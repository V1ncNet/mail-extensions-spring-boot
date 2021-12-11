package de.vinado.boot.autoconfigure.javamail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static de.vinado.boot.autoconfigure.javamail.Properties.dkimPropertyPairs;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class JavaMailUtilsAutoConfigurationTest {

    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JavaMailUtilsAutoConfiguration.class));
    }

    @Test
    void configuringWithoutAnyProperties_shouldOnlyCreateSenderConfiguration() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimSignerConfiguration.class)
                .hasSingleBean(JavaMailSenderConfiguration.class));
    }

    @Test
    void configuringWithDkimProperties_shouldCreateAllImportedConfigurations() {
        contextRunner
            .withPropertyValues(dkimPropertyPairs())
            .run(context -> assertThat(context)
                .hasSingleBean(DkimSignerConfiguration.class)
                .hasSingleBean(JavaMailSenderConfiguration.class));
    }
}
