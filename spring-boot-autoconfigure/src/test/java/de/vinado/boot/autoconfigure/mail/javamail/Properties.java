package de.vinado.boot.autoconfigure.mail.javamail;

/**
 * @author Vincent Nadoll
 */
public class Properties {

    public static final String DKIM_SIGNING_DOMAIN = "domain.tld";
    public static final String DKIM_PRIVATE_KEY_LOCATION = "test.key.der";
    public static final String DKIM_SELECTOR = "default";

    public static String[] propertyPairs(boolean concurrent) {
        return new String[]{
            "javamail.dkim.signing-domain=" + DKIM_SIGNING_DOMAIN,
            "javamail.dkim.private-key=classpath:" + DKIM_PRIVATE_KEY_LOCATION,
            "javamail.dkim.selector=" + DKIM_SELECTOR,
            "javamail.concurrent.enabled=" + concurrent,
        };
    }

    public static String[] dkimPropertyPairs() {
        return new String[]{
            "javamail.dkim.signing-domain=" + DKIM_SIGNING_DOMAIN,
            "javamail.dkim.private-key=classpath:" + DKIM_PRIVATE_KEY_LOCATION,
            "javamail.dkim.selector=" + DKIM_SELECTOR,
        };
    }
}
