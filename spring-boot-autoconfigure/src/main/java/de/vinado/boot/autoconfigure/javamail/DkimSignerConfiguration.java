package de.vinado.boot.autoconfigure.javamail;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Configuration component for the {@link DkimSigner}.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties(DkimProperties.class)
@ConditionalOnProperty(prefix = "javamail.dkim", name = {"selector", "signing-domain", "private-key"})
class DkimSignerConfiguration {

    @Bean
    @Conditional(PrivateKeyNotEmpty.class)
    @ConditionalOnMissingBean
    DkimSigner dkimSigner(DkimProperties properties) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        Resource resource = properties.getPrivateKey();
        DkimSigner signer = new DkimSigner(properties.getSigningDomain(), properties.getSelector(), resource.getInputStream());
        applyProperties(properties, signer);
        return signer;
    }

    private void applyProperties(DkimProperties dkimProperties, DkimSigner signer) {
        DkimProperties.SignerProperties properties = dkimProperties.getSigner();
        signer.setIdentity(properties.getIdentity());
        signer.setHeaderCanonicalization(properties.getHeaderCanonicalization());
        signer.setBodyCanonicalization(properties.getBodyCanonicalization());
        signer.setCheckDomainKey(properties.isCheckDomainKey());
        signer.setSigningAlgorithm(properties.getSigningAlgorithm());
        signer.setLengthParam(properties.isLengthParam());
        signer.setCopyHeaderFields(properties.isCopyHeaderFields());
    }
}
