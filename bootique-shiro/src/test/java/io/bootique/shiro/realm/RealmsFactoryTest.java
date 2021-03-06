package io.bootique.shiro.realm;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.shiro.realm.Realm;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RealmsFactoryTest {

    @Test
    public void testCreateRealms_NoConfig() {

        Realm r1 = Mockito.mock(Realm.class);
        Realm r2 = Mockito.mock(Realm.class);

        Set<Realm> diRealms = new HashSet<>(asList(r1, r2));

        RealmsFactory realmsFactory = new RealmsFactory();

        Realms realms = realmsFactory.createRealms(Guice.createInjector(), diRealms);
        Assert.assertNotNull(realms);
        assertEquals(2, realms.getRealms().size());
        assertTrue(realms.getRealms().contains(r1));
        assertTrue(realms.getRealms().contains(r2));
    }

    @Test
    public void testCreateRealms_NoDi() {
        Injector injector = Guice.createInjector();

        Realm r1 = Mockito.mock(Realm.class);
        Realm r2 = Mockito.mock(Realm.class);

        RealmFactory rf1 = Mockito.mock(RealmFactory.class);
        Mockito.when(rf1.createRealm(injector)).thenReturn(r1);

        RealmFactory rf2 = Mockito.mock(RealmFactory.class);
        Mockito.when(rf2.createRealm(injector)).thenReturn(r2);

        List<RealmFactory> configFactories = asList(rf1, rf2);

        RealmsFactory realmsFactory = new RealmsFactory();
        realmsFactory.setRealms(configFactories);

        Realms realms = realmsFactory.createRealms(injector, Collections.emptySet());
        Assert.assertNotNull(realms);
        assertEquals(2, realms.getRealms().size());
        assertEquals("Realm ordering got lost", r1,  realms.getRealms().get(0));
        assertEquals("Realm ordering got lost", r2, realms.getRealms().get(1));
    }

    @Test
    public void testCreateRealms_DiAndConfig() {
        Injector injector = Guice.createInjector();

        Realm rdi1 = Mockito.mock(Realm.class);
        Realm rdi2 = Mockito.mock(Realm.class);

        Set<Realm> diRealms = new HashSet<>(asList(rdi1, rdi2));

        Realm r1 = Mockito.mock(Realm.class);
        Realm r2 = Mockito.mock(Realm.class);

        RealmFactory rf1 = Mockito.mock(RealmFactory.class);
        Mockito.when(rf1.createRealm(injector)).thenReturn(r1);

        RealmFactory rf2 = Mockito.mock(RealmFactory.class);
        Mockito.when(rf2.createRealm(injector)).thenReturn(r2);

        List<RealmFactory> configFactories = asList(rf1, rf2);

        RealmsFactory realmsFactory = new RealmsFactory();
        realmsFactory.setRealms(configFactories);

        Realms realms = realmsFactory.createRealms(injector, diRealms);
        Assert.assertNotNull(realms);
        assertEquals(2, realms.getRealms().size());
        assertEquals("Wrong Realms or Realm ordering got lost", r1,  realms.getRealms().get(0));
        assertEquals("Wrong Realms or Realm ordering got lost", r2, realms.getRealms().get(1));
    }
}
