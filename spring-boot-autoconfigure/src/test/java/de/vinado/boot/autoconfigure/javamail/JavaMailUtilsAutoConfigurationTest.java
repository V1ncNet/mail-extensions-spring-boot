package de.vinado.boot.autoconfigure.javamail;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.assertj.ApplicationContextAssert;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;

import static de.vinado.boot.autoconfigure.javamail.Properties.dkimPropertyPairs;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class JavaMailUtilsAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(JavaMailUtilsAutoConfiguration.class);

    @Test
    void configuringWithoutAnyProperties_shouldNotImportConfigurations() {
        contextRunner
            .run(this::assertNoneImported);
    }

    @Test
    void configuringWithoutDkimAndConcurrentSenderDisabled_shouldNotImportConfigurations() {
        contextRunner
            .withPropertyValues("javamail.concurrent.enabled=false")
            .run(this::assertNoneImported);
    }

    @Test
    void configuringDkimPropertiesAndWithoutConcurrentSender_shouldCreateAllImportedConfigurations() {
        contextRunner
            .withPropertyValues(dkimPropertyPairs())
            .run(this::assertAllImported);
    }

    @Test
    void configuringDkimPropertiesAndConcurrentSenderDisabled_shouldImportAllConfigurations() {
        contextRunner
            .withPropertyValues(Properties.propertyPairs(false))
            .run(this::assertAllImported);
    }

    @Test
    void configuringWithoutDkimPropertiesAndConcurrentSenderEnabled_shouldImportSenderConfigurations() {
        contextRunner
            .withPropertyValues("javamail.concurrent.enabled=true")
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimSignerConfiguration.class)
                .hasSingleBean(JavaMailSenderConfiguration.class));
    }

    @Test
    void configuringDkimPropertiesAndConcurrentSenderEnabled_shouldImportAllConfigurations() {
        contextRunner
            .withPropertyValues(Properties.propertyPairs(true))
            .run(this::assertAllImported);
    }

    private ApplicationContextAssert<ConfigurableApplicationContext> assertAllImported(
        AssertableApplicationContext context) {
        return assertThat(context)
            .hasSingleBean(DkimSignerConfiguration.class)
            .hasSingleBean(JavaMailSenderConfiguration.class);
    }

    private ApplicationContextAssert<ConfigurableApplicationContext> assertNoneImported(
        AssertableApplicationContext context) {
        return assertThat(context)
            .doesNotHaveBean(DkimSignerConfiguration.class)
            .doesNotHaveBean(JavaMailSenderConfiguration.class);
    }
}
