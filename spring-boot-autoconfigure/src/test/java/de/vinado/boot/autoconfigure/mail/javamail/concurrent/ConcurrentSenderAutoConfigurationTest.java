package de.vinado.boot.autoconfigure.mail.javamail.concurrent;

import de.vinado.boot.autoconfigure.mail.javamail.Properties;
import de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class ConcurrentSenderAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(ConcurrentSenderAutoConfiguration.class));

    @Test
    void configuringWithoutConcurrentSenderProperties_shouldNotCreateConcurrentSender() {
        contextRunner
            .withClassLoader(new FilteredClassLoader("net.markenwerk.utils.mail.dkim"))
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithoutConcurrentSenderPropertiesButDkimSigner_shouldNotCreateConcurrentSender() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithConcurrentSenderPropertiesEnabledAndWithoutDkimSigner_shouldCreateConcurrentSender() {
        contextRunner
            .withClassLoader(new FilteredClassLoader("net.markenwerk.utils.mail.dkim"))
            .withPropertyValues(Properties.propertyPairs(true))
            .run(context -> assertThat(context)
                .hasSingleBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithConcurrentSenderPropertiesEnabledButDkimSigner_shouldNotCreateConcurrentSender() {
        contextRunner
            .withPropertyValues(Properties.propertyPairs(true))
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithConcurrentSenderPropertiesDisabledAndWithDkimSigner_shouldNotCreateConcurrentSender() {
        contextRunner
            .withClassLoader(new FilteredClassLoader("net.markenwerk.utils.mail.dkim"))
            .withPropertyValues(Properties.propertyPairs(false))
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }

    @Test
    void configuringWithConcurrentSenderPropertiesDisabledButDkimSigner_shouldNotCreateConcurrentSender() {
        contextRunner
            .withPropertyValues(Properties.propertyPairs(false))
            .run(context -> assertThat(context)
                .doesNotHaveBean(ConcurrentJavaMailSender.class));
    }
}
