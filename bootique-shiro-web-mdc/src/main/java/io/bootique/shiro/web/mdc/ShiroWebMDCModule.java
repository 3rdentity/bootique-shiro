package io.bootique.shiro.web.mdc;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import io.bootique.jetty.JettyModule;
import io.bootique.jetty.MappedListener;
import io.bootique.shiro.ShiroModule;
import io.bootique.shiro.mdc.PrincipalMDC;
import io.bootique.shiro.web.ShiroWebModule;

/**
 * @since 0.25
 */
public class ShiroWebMDCModule implements Module {

    // make sure we wrap request timer listener whose order is defined in
    // InstrumentedJettyModule.REQUEST_TIMER_LISTENER_ORDER
    public static final int MDC_LISTENER_ORDER = Integer.MIN_VALUE + 900;

    @Override
    public void configure(Binder binder) {
        JettyModule.extend(binder).addMappedListener(new TypeLiteral<MappedListener<ShiroWebMDCCleaner>>() {
        });
        ShiroModule.extend(binder).addAuthListener(OnAuthMDCInitializer.class);
        ShiroWebModule.extend(binder).setFilter("mdc", SubjectMDCInitializer.class);
    }

    @Singleton
    @Provides
    MappedListener<ShiroWebMDCCleaner> providePrincipalMDCCleaner(PrincipalMDC principalMDC) {
        ShiroWebMDCCleaner cleaner = new ShiroWebMDCCleaner(principalMDC);
        return new MappedListener<>(cleaner, MDC_LISTENER_ORDER);
    }

    @Singleton
    @Provides
    OnAuthMDCInitializer providePrincipalMDCInitializer(PrincipalMDC principalMDC) {
        return new OnAuthMDCInitializer(principalMDC);
    }

    @Singleton
    @Provides
    SubjectMDCInitializer provideSubjectMDCInitializer(PrincipalMDC principalMDC) {
        return new SubjectMDCInitializer(principalMDC);
    }
}
