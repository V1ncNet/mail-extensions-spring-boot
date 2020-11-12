package de.vinado.boot.dkim;

import lombok.Getter;
import lombok.Setter;
import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for DKIM signing support.
 *
 * @author Vincent Nadoll
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "dkim")
public class DkimProperties {

    private String signingDomain;
    private String selector;
    private String privateKey;
    private SignerProperties signer = new SignerProperties();

    /**
     * Configuration properties for the {@link net.markenwerk.utils.mail.dkim.DkimSigner} itself.
     *
     * @author Vincent Nadoll
     */
    @Getter
    @Setter
    public static class SignerProperties {

        private Canonicalization headerCanonicalization = Canonicalization.RELAXED;
        private Canonicalization bodyCanonicalization = Canonicalization.RELAXED;
        private boolean checkDomainKey = true;
        private SigningAlgorithm signingAlgorithm = SigningAlgorithm.SHA256_WITH_RSA;
    }
}
