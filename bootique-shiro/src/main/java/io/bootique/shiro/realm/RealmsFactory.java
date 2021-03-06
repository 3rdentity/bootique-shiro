package io.bootique.shiro.realm;

import com.google.inject.Injector;
import io.bootique.annotation.BQConfig;
import io.bootique.annotation.BQConfigProperty;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.joining;

@BQConfig
public class RealmsFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(RealmsFactory.class);

    private List<RealmFactory> realms;

    @BQConfigProperty
    public void setRealms(List<RealmFactory> realms) {
        this.realms = realms;
    }

    public Realms createRealms(Injector injector, Set<Realm> diRealms) {

        // no configured realms, use DI Realms
        if (realms == null || realms.isEmpty()) {
            return new Realms(new ArrayList<>(diRealms));
        }

        // use configured realms...

        // ignoring DI Realms if at least one config realm exists. This allows to fully override and/or order realms
        // without recompiling

        if (!diRealms.isEmpty() && LOGGER.isInfoEnabled()) {
            String realmNames = diRealms.stream()
                    .map(r -> r.getName() != null ? r.getName() : r.getClass().getSimpleName()).collect(joining(", "));
            LOGGER.info("Ignoring DI-originated Realms: " + realmNames + ". Using Realms from configuration instead.");
        }

        List<Realm> orderedRealms = new ArrayList<>(diRealms.size());
        realms.forEach(rf -> orderedRealms.add(rf.createRealm(injector)));

        return new Realms(orderedRealms);
    }
}
