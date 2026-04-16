package com.github.tskorupka.keycloak.policy;

import org.jboss.logging.Logger;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.policy.PasswordPolicyProvider;
import org.keycloak.policy.PolicyError;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class HaveIBeenPwnedPasswordPolicyProvider implements PasswordPolicyProvider {

    private static final Logger logger = Logger.getLogger(HaveIBeenPwnedPasswordPolicyProvider.class);
    private static final String HIBP_URL = "https://api.pwnedpasswords.com/range/";
    private static final HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    private final KeycloakSession session;

    public HaveIBeenPwnedPasswordPolicyProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public PolicyError validate(RealmModel realm, UserModel user, String password) {
        RealmModel effectiveRealm = realm != null ? realm : session.getContext().getRealm();
        int threshold = effectiveRealm.getPasswordPolicy()
                .getPolicyConfig(HaveIBeenPwnedPasswordPolicyProviderFactory.ID);

        try {
            String hash = sha1Hex(password).toUpperCase();
            String prefix = hash.substring(0, 5);
            String suffix = hash.substring(5);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(HIBP_URL + prefix))
                    .header("Add-Padding", "true")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String body = response.body();
                for (String line : body.split("\r?\n")) {
                    String[] parts = line.split(":");
                    if (parts.length == 2 && parts[0].equals(suffix)) {
                        int count = Integer.parseInt(parts[1].trim());
                        if (count > threshold) {
                            logger.warnf("Password rejected: found %d time(s) in Have I Been Pwned database (threshold: %d)", count, threshold);
                            return new PolicyError("passwordIsPwned", count);
                        }
                    }
                }
            } else {
                logger.errorf("Have I Been Pwned API returned unexpected status code: %d", response.statusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to check password against Have I Been Pwned API, allowing password", e);
        }

        return null;
    }

    @Override
    public PolicyError validate(String user, String password) {
        return validate(null, null, password);
    }

    @Override
    public Object parseConfig(String value) {
        return value != null ? Integer.parseInt(value) : 0;
    }

    @Override
    public void close() {
    }

    private String sha1Hex(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] result = md.digest(input.getBytes());
        return HexFormat.of().formatHex(result);
    }
}
