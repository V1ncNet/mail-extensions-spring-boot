package de.vinado.boot.autoconfigure.javamail;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class DkimSignerAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(DkimSignerAutoConfiguration.class));

    @Test
    void missingProperty_shouldNotCreateDkimSignerBean() {
        this.contextRunner
            .withPropertyValues("javamail.dkim.signing-domain=domain.tld")
            .run(context -> assertThat(context).doesNotHaveBean(DkimSigner.class));
    }

    @Test
    void fileUri_shouldCreateDkimSignerBean() {
        assertSuccessfulDkimBeanCreation("file:src/test/resources/test.key.der");
    }

    @Test
    void classpathUri_shouldCreateDkimSignerBean() {
        assertSuccessfulDkimBeanCreation("classpath:test.key.der");
    }

    private void assertSuccessfulDkimBeanCreation(String privateKeyLocation) {
        String[] properties = {
            "javamail.dkim.signing-domain=domain.tld",
            "javamail.dkim.private-key=" + privateKeyLocation,
            "javamail.dkim.selector=default"
        };

        this.contextRunner
            .withPropertyValues(properties)
            .run(context -> assertThat(context).hasSingleBean(DkimSigner.class));
    }
}
