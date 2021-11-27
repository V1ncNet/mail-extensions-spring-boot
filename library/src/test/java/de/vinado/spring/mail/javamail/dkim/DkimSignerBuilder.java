package de.vinado.spring.mail.javamail.dkim;

import lombok.SneakyThrows;
import net.markenwerk.utils.mail.dkim.Canonicalization;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import net.markenwerk.utils.mail.dkim.SigningAlgorithm;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

/**
 * @author Vincent Nadoll
 */
public class DkimSignerBuilder {

    private Resource privateKey;
    private String signingDomain;
    private String selector;

    private String identity = null;
    private Canonicalization headerCanonicalization = Canonicalization.SIMPLE;
    private Canonicalization bodyCanonicalization = Canonicalization.RELAXED;
    private boolean checkDomainKey = true;
    private SigningAlgorithm signingAlgorithm = SigningAlgorithm.SHA256_WITH_RSA;
    private boolean lengthParam = true;
    private boolean copyHeaderFields = false;

    public DkimSignerBuilder privateKey(Resource privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public DkimSignerBuilder signingDomain(String signingDomain) {
        this.signingDomain = signingDomain;
        return this;
    }

    public DkimSignerBuilder selector(String selector) {
        this.selector = selector;
        return this;
    }

    public DkimSignerBuilder identity(String identity) {
        this.identity = identity;
        return this;
    }

    public DkimSignerBuilder headerCanonicalization(Canonicalization canonicalization) {
        this.headerCanonicalization = canonicalization;
        return this;
    }

    public DkimSignerBuilder bodyCanonicalization(Canonicalization canonicalization) {
        this.bodyCanonicalization = canonicalization;
        return this;
    }

    public DkimSignerBuilder checkDomainKey(boolean checkDomainKey) {
        this.checkDomainKey = checkDomainKey;
        return this;
    }

    public DkimSignerBuilder signingAlgorithm(SigningAlgorithm signingAlgorithm) {
        this.signingAlgorithm = signingAlgorithm;
        return this;
    }

    public DkimSignerBuilder lengthParam(boolean lengthParam) {
        this.lengthParam = lengthParam;
        return this;
    }

    public DkimSignerBuilder copyHeaderFields(boolean copyHeaderFields) {
        this.copyHeaderFields = copyHeaderFields;
        return this;
    }

    @SneakyThrows
    public DkimSigner build() {
        DkimSigner signer = new DkimSigner(signingDomain, selector, privateKey.getInputStream());
        signer.setIdentity(identity);
        signer.setHeaderCanonicalization(headerCanonicalization);
        signer.setBodyCanonicalization(bodyCanonicalization);
        signer.setCheckDomainKey(checkDomainKey);
        signer.setSigningAlgorithm(signingAlgorithm);
        signer.setLengthParam(lengthParam);
        signer.setCopyHeaderFields(copyHeaderFields);
        return signer;
    }

    @SneakyThrows
    public DkimSignerBuilder defaultSigner() {
        return new DkimSignerBuilder()
            .privateKey(new FileUrlResource("src/test/resources/test.key.der"))
            .signingDomain("example.com")
            .selector("dkim1")
            .checkDomainKey(false)
            ;
    }
}
