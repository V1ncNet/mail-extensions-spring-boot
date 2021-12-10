package de.vinado.boot.autoconfigure.javamail;

import lombok.Getter;
import lombok.Setter;
import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * Configuration properties for DKIM signing support.
 *
 * @author Vincent Nadoll
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "javamail.dkim")
public class DkimProperties {

    private String signingDomain;
    private String selector;
    private Resource privateKey;
    private SignerProperties signer = new SignerProperties();


    /**
     * Configuration properties for the {@link net.markenwerk.utils.mail.dkim.DkimSigner} itself.
     *
     * @author Vincent Nadoll
     */
    @Getter
    @Setter
    public static class SignerProperties {

        private String identity = null;
        private Canonicalization headerCanonicalization = Canonicalization.SIMPLE;
        private Canonicalization bodyCanonicalization = Canonicalization.RELAXED;
        private boolean checkDomainKey = true;
        private SigningAlgorithm signingAlgorithm = SigningAlgorithm.SHA256_WITH_RSA;
        private boolean lengthParam = true;
        private boolean copyHeaderFields = false;

        public String getIdentity() {
            return StringUtils.hasText(identity) ? identity : null;
        }
    }
}
