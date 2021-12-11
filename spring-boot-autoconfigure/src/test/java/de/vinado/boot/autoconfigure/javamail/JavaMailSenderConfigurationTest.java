package de.vinado.boot.autoconfigure.javamail;

import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSender;
import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSender;
import lombok.SneakyThrows;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static de.vinado.boot.autoconfigure.javamail.Properties.DKIM_PRIVATE_KEY_LOCATION;
import static de.vinado.boot.autoconfigure.javamail.Properties.DKIM_SELECTOR;
import static de.vinado.boot.autoconfigure.javamail.Properties.DKIM_SIGNING_DOMAIN;
import static de.vinado.boot.autoconfigure.javamail.Properties.dkimPropertyPairs;
import static de.vinado.boot.autoconfigure.javamail.Properties.propertyPairs;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class JavaMailSenderConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(JavaMailSenderConfiguration.class);

    @Test
    void configuringWithoutDkimProperties_shouldNotCreateDkimSender() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimJavaMailSender.class));
    }

    @Test
    void configuringWithDkimProperties_shouldCreateDkimSender() {
        contextRunner
            .withBean(DkimSigner.class, this::createDkimSigner)
            .withPropertyValues(dkimPropertyPairs())
            .run(context -> {
                assertThat(context).hasSingleBean(DkimJavaMailSender.class);
                DkimJavaMailSender mailSender = (DkimJavaMailSender) context.getBean("mailSender");
                assertTrue(mailSender.getDelegate() instanceof JavaMailSenderImpl);
            });
    }

    @Test
    void configuringDkimAndConcurrent_shouldCreateDecoratedDkimSender() {
        contextRunner
            .withBean(DkimSigner.class, this::createDkimSigner)
            .withPropertyValues(propertyPairs(true))
            .run(context -> {
                assertThat(context).hasSingleBean(DkimJavaMailSender.class);
                DkimJavaMailSender mailSender = (DkimJavaMailSender) context.getBean("mailSender");
                assertTrue(mailSender.getDelegate() instanceof ConcurrentJavaMailSender);
            });
    }

    @Test
    void configuringDkimAndConcurrentDisabled_shouldCreateDkimSender() {
        contextRunner
            .withBean(DkimSigner.class, this::createDkimSigner)
            .withPropertyValues(propertyPairs(false))
            .run(context -> {
                assertThat(context).hasSingleBean(DkimJavaMailSender.class);
                DkimJavaMailSender mailSender = (DkimJavaMailSender) context.getBean("mailSender");
                assertTrue(mailSender.getDelegate() instanceof JavaMailSenderImpl);
            });
    }

    @Test
    void configuringWithConcurrentPropertyEnabled_shouldCreateConcurrentSender() {
        contextRunner
            .withPropertyValues("javamail.concurrent.enabled=true")
            .run(context -> assertThat(context)
                .hasSingleBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithConcurrentPropertyDisabled_shouldNotCreateConcurrentSender() {
        contextRunner
            .withPropertyValues("javamail.concurrent.enabled=false")
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithoutConcurrentProperty_shouldNotCreateConcurrentSender() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @SneakyThrows
    private DkimSigner createDkimSigner() {
        ClassPathResource privateKey = new ClassPathResource(DKIM_PRIVATE_KEY_LOCATION);
        return new DkimSigner(DKIM_SIGNING_DOMAIN, DKIM_SELECTOR, privateKey.getInputStream());
    }
}
