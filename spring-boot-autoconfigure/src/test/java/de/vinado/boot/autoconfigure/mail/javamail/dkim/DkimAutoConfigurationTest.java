package de.vinado.boot.autoconfigure.mail.javamail.dkim;

import de.vinado.boot.autoconfigure.mail.javamail.Properties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class DkimAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(DkimAutoConfiguration.class));

    @Test
    void configuringWithoutDkimPropertiesAndDkimSigner_shouldNotCreateConfiguration() {
        contextRunner
            .withClassLoader(new FilteredClassLoader("net.markenwerk.utils.mail.dkim"))
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimAutoConfiguration.class));
    }

    @Test
    void configuringWithDkimPropertiesAndDkimSigner_shouldCreateConfiguration() {
        contextRunner
            .withPropertyValues(Properties.dkimPropertyPairs())
            .run(context -> assertThat(context)
                .hasSingleBean(DkimAutoConfiguration.class));
    }

    @Test
    void configuringWithEmptyPrivateKeyAndDkimSigner_shouldNotCreateConfiguration() {
        contextRunner
            .withPropertyValues("javamail.dkim.signing-domain=")
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimAutoConfiguration.class));
    }

    @Test
    void configuringWithEmptyPrivateKeyButWithoutDkimSigner_shouldNotCreateConfiguration() {
        contextRunner
            .withClassLoader(new FilteredClassLoader("net.markenwerk.utils.mail.dkim"))
            .withPropertyValues("javamail.dkim.signing-domain=")
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimAutoConfiguration.class));
    }
}
