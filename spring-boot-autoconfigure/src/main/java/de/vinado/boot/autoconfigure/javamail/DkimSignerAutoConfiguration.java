package de.vinado.boot.autoconfigure.javamail;

import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSender;
import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSenderDecoratorFactory;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;
import java.util.Properties;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto configuration} for DKIM signed email
 * support. Configures the {@link DkimSigner DKIM signer} as well as the {@link DkimJavaMailSender Mail Sender} based on
 * properties configuration.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties({DkimProperties.class, MailProperties.class})
@ConditionalOnProperty(prefix = "javamail.dkim", name = {"selector", "signing-domain", "private-key"})
class DkimSignerAutoConfiguration {

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

    @Bean
    @ConditionalOnBean(DkimSigner.class)
    DkimJavaMailSender mailSender(MailProperties mailProperties, DkimSigner signer) {
        DkimJavaMailSenderDecoratorFactory factory = new DkimJavaMailSenderDecoratorFactory(signer);
        JavaMailSenderImpl delegate = mailSender(mailProperties);
        return factory.decorate(delegate);
    }

    JavaMailSenderImpl mailSender(MailProperties properties) {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        applyProperties(properties, sender);
        return sender;
    }

    private void applyProperties(MailProperties properties, JavaMailSenderImpl sender) {
        sender.setHost(properties.getHost());
        if (properties.getPort() != null) {
            sender.setPort(properties.getPort());
        }
        sender.setUsername(properties.getUsername());
        sender.setPassword(properties.getPassword());
        sender.setProtocol(properties.getProtocol());
        if (properties.getDefaultEncoding() != null) {
            sender.setDefaultEncoding(properties.getDefaultEncoding().name());
        }
        if (!properties.getProperties().isEmpty()) {
            sender.setJavaMailProperties(asProperties(properties.getProperties()));
        }
    }

    private Properties asProperties(Map<String, String> source) {
        Properties properties = new Properties();
        properties.putAll(source);
        return properties;
    }
}
