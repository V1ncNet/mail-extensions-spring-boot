package de.vinado.boot.autoconfigure.mail.javamail.concurrent;

import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSender;
import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSenderFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JavaMailSender.class)
@EnableConfigurationProperties({MailProperties.class, ConcurrentSenderProperties.class})
public class ConcurrentSenderAutoConfiguration {

    @Bean("mailSender")
    @ConditionalOnProperty(prefix = "javamail.concurrent", name = "enabled", havingValue = "true")
    @ConditionalOnMissingClass("net.markenwerk.utils.mail.dkim.DkimSigner")
    ConcurrentJavaMailSender concurrentJavaMailSender(MailProperties mailProperties,
                                                      ConcurrentSenderProperties concurrentSenderProperties) {
        JavaMailSenderImpl delegate = mailSender(mailProperties);
        return mailSender(concurrentSenderProperties, delegate);
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

    ConcurrentJavaMailSender mailSender(ConcurrentSenderProperties concurrentSenderProperties, JavaMailSender delegate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrentJavaMailSenderFactory concurrentSenderFactory = new ConcurrentJavaMailSenderFactory(executor);

        ConcurrentJavaMailSender concurrentJavaMailSender = concurrentSenderFactory.decorate(delegate);
        applyProperties(concurrentSenderProperties, concurrentJavaMailSender);
        return concurrentJavaMailSender;
    }

    private void applyProperties(ConcurrentSenderProperties properties, ConcurrentJavaMailSender sender) {
        sender.setBatchSize(properties.getBatchSize());
        sender.setCooldownMillis(properties.getCooldownMillis());
    }
}
