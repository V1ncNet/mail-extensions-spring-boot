package de.vinado.boot.autoconfigure.dkim;

import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for DKIM signing support.
 *
 * @author Vincent Nadoll
 */
@ConfigurationProperties(prefix = "dkim")
public class DkimProperties {

    private String signingDomain;
    private String selector;
    private String privateKey;
    private SignerProperties signer = new SignerProperties();

    public String getSigningDomain() {
        return signingDomain;
    }

    public void setSigningDomain(String signingDomain) {
        this.signingDomain = signingDomain;
    }

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public SignerProperties getSigner() {
        return signer;
    }

    public void setSigner(SignerProperties signer) {
        this.signer = signer;
    }

    /**
     * Configuration properties for the {@link net.markenwerk.utils.mail.dkim.DkimSigner} itself.
     *
     * @author Vincent Nadoll
     */
    public static class SignerProperties {

        private String identity = null;
        private Canonicalization headerCanonicalization = Canonicalization.SIMPLE;
        private Canonicalization bodyCanonicalization = Canonicalization.RELAXED;
        private boolean checkDomainKey = true;
        private SigningAlgorithm signingAlgorithm = SigningAlgorithm.SHA256_WITH_RSA;
        private boolean lengthParam = true;
        private boolean copyHeaderFields = false;

        public String getIdentity() {
            return identity;
        }

        public void setIdentity(String identity) {
            this.identity = identity;
        }

        public Canonicalization getHeaderCanonicalization() {
            return headerCanonicalization;
        }

        public void setHeaderCanonicalization(Canonicalization headerCanonicalization) {
            this.headerCanonicalization = headerCanonicalization;
        }

        public Canonicalization getBodyCanonicalization() {
            return bodyCanonicalization;
        }

        public void setBodyCanonicalization(Canonicalization bodyCanonicalization) {
            this.bodyCanonicalization = bodyCanonicalization;
        }

        public boolean isCheckDomainKey() {
            return checkDomainKey;
        }

        public void setCheckDomainKey(boolean checkDomainKey) {
            this.checkDomainKey = checkDomainKey;
        }

        public SigningAlgorithm getSigningAlgorithm() {
            return signingAlgorithm;
        }

        public void setSigningAlgorithm(SigningAlgorithm signingAlgorithm) {
            this.signingAlgorithm = signingAlgorithm;
        }

        public boolean isLengthParam() {
            return lengthParam;
        }

        public void setLengthParam(boolean lengthParam) {
            this.lengthParam = lengthParam;
        }

        public boolean isCopyHeaderFields() {
            return copyHeaderFields;
        }

        public void setCopyHeaderFields(boolean copyHeaderFields) {
            this.copyHeaderFields = copyHeaderFields;
        }
    }
}
