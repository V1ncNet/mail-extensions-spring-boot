package de.vinado.boot.autoconfigure.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static de.vinado.boot.autoconfigure.mail.javamail.Properties.DKIM_PRIVATE_KEY_LOCATION;
import static de.vinado.boot.autoconfigure.mail.javamail.Properties.DKIM_SIGNING_DOMAIN;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class DkimSignerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(DkimSignerConfiguration.class);

    @Test
    void missingProperty_shouldNotCreateDkimSignerBean() {
        this.contextRunner
            .withPropertyValues("javamail.dkim.signing-domain=" + DKIM_SIGNING_DOMAIN)
            .run(context -> assertThat(context).doesNotHaveBean(DkimSigner.class));
    }

    @Test
    void fileUri_shouldCreateDkimSignerBean() {
        assertSuccessfulDkimBeanCreation("file:src/test/resources/" + DKIM_PRIVATE_KEY_LOCATION);
    }

    @Test
    void classpathUri_shouldCreateDkimSignerBean() {
        assertSuccessfulDkimBeanCreation("classpath:" + DKIM_PRIVATE_KEY_LOCATION);
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
