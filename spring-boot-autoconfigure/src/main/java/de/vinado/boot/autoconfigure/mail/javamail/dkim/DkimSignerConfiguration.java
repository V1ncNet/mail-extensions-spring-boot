package de.vinado.boot.autoconfigure.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Configuration component for the {@link DkimSigner}.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(DkimSignerProperties.class)
@ConditionalOnProperty(prefix = "javamail.dkim", name = {"selector", "signing-domain", "private-key"})
class DkimSignerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    DkimSigner dkimSigner(DkimSignerProperties properties) throws IOException,
        InvalidKeySpecException, NoSuchAlgorithmException {
        Resource resource = properties.getPrivateKey();
        DkimSigner signer = new DkimSigner(properties.getSigningDomain(),
            properties.getSelector(),
            resource.getInputStream());
        applyProperties(properties, signer);
        return signer;
    }

    private void applyProperties(DkimSignerProperties dkimProperties, DkimSigner signer) {
        DkimSignerProperties.SignerProperties properties = dkimProperties.getSigner();
        signer.setIdentity(properties.getIdentity());
        signer.setHeaderCanonicalization(properties.getHeaderCanonicalization());
        signer.setBodyCanonicalization(properties.getBodyCanonicalization());
        signer.setCheckDomainKey(properties.isCheckDomainKey());
        signer.setSigningAlgorithm(properties.getSigningAlgorithm());
        signer.setLengthParam(properties.isLengthParam());
        signer.setCopyHeaderFields(properties.isCopyHeaderFields());
    }
}
