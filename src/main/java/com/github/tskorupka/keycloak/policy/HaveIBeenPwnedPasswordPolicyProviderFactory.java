package com.github.tskorupka.keycloak.policy;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PasswordPolicyProviderFactory;

public class HaveIBeenPwnedPasswordPolicyProviderFactory implements PasswordPolicyProviderFactory {

    public static final String ID = "haveIBeenPwned";

    @Override
    public PasswordPolicyProvider create(KeycloakSession session) {
        return new HaveIBeenPwnedPasswordPolicyProvider(session);
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return ID;
    }

    @Override
    public String getConfigType() {
        return PasswordPolicyProvider.INT_CONFIG_TYPE;
    }

    @Override
    public String getDefaultConfigValue() {
        return "0";
    }

    @Override
    public boolean isMultiplSupported() {
        return false;
    }
}
