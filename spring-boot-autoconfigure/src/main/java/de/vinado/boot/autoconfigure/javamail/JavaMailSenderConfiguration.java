package de.vinado.boot.autoconfigure.javamail;

import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSender;
import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSenderDecoratorFactory;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;

/**
 * {@link Configuration Configuration} for DKIM signed email support. Configures the
 * {@link DkimJavaMailSender Mail Sender} based on properties' configuration.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MailProperties.class)
class JavaMailSenderConfiguration {

    @Bean
    @ConditionalOnBean(DkimSigner.class)
    DkimJavaMailSender mailSender(MailProperties mailProperties, DkimSigner dkimSigner) {
        DkimJavaMailSenderDecoratorFactory factory = new DkimJavaMailSenderDecoratorFactory(dkimSigner);
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
