package de.vinado.boot.autoconfigure.javamail;

import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSender;
import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSenderFactory;
import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSender;
import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSenderDecoratorFactory;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
 * {@link Configuration Configuration} for advanced {@link JavaMailSender} support. Depending which combination of
 * properties is set, different mail senders are bering instantiated.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(DkimSignerConfiguration.class)
@EnableConfigurationProperties({MailProperties.class, ConcurrentJavaMailSenderProperties.class})
class JavaMailSenderConfiguration {

    @Bean("mailSender")
    @ConditionalOnProperty(prefix = "javamail.concurrent", name = "enabled", havingValue = "false", matchIfMissing = true)
    @ConditionalOnBean(DkimSigner.class)
    DkimJavaMailSender dkimJavaMailSender(MailProperties mailProperties, DkimSigner dkimSigner) {
        DkimJavaMailSenderDecoratorFactory factory = new DkimJavaMailSenderDecoratorFactory(dkimSigner);
        JavaMailSenderImpl delegate = mailSender(mailProperties);
        return factory.decorate(delegate);
    }

    @Bean("mailSender")
    @ConditionalOnProperty(prefix = "javamail.concurrent", name = "enabled", havingValue = "true")
    @ConditionalOnBean(DkimSigner.class)
    DkimJavaMailSender concurrentDkimJavaMailSender(MailProperties mailProperties,
                                                    ConcurrentJavaMailSenderProperties concurrentSenderProperties,
                                                    DkimSigner dkimSigner) {
        DkimJavaMailSenderDecoratorFactory dkimSenderFactory = new DkimJavaMailSenderDecoratorFactory(dkimSigner);

        JavaMailSenderImpl rootSender = mailSender(mailProperties);
        ConcurrentJavaMailSender concurrentJavaMailSender = mailSender(concurrentSenderProperties, rootSender);

        return dkimSenderFactory.decorate(concurrentJavaMailSender);
    }

    @Bean("mailSender")
    @ConditionalOnProperty(prefix = "javamail.concurrent", name = "enabled", havingValue = "true")
    @ConditionalOnMissingBean(DkimSigner.class)
    ConcurrentJavaMailSender concurrentJavaMailSender(MailProperties mailProperties,
                                                      ConcurrentJavaMailSenderProperties concurrentSenderProperties) {
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

    ConcurrentJavaMailSender mailSender(ConcurrentJavaMailSenderProperties concurrentSenderProperties, JavaMailSender delegate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        ConcurrentJavaMailSenderFactory concurrentSenderFactory = new ConcurrentJavaMailSenderFactory(executor);

        ConcurrentJavaMailSender concurrentJavaMailSender = concurrentSenderFactory.decorate(delegate);
        applyProperties(concurrentSenderProperties, concurrentJavaMailSender);
        return concurrentJavaMailSender;
    }

    private void applyProperties(ConcurrentJavaMailSenderProperties properties, ConcurrentJavaMailSender sender) {
        sender.setBatchSize(properties.getBatchSize());
        sender.setCooldownMillis(properties.getCooldownMillis());
    }
}
