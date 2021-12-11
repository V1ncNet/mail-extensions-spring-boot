package de.vinado.boot.autoconfigure.javamail;

import de.vinado.spring.mail.javamail.dkim.DkimJavaMailSender;
import lombok.SneakyThrows;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.ClassPathResource;

import static de.vinado.boot.autoconfigure.javamail.Properties.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vincent Nadoll
 */
class JavaMailSenderConfigurationTest {


    private ApplicationContextRunner contextRunner;

    @BeforeEach
    void setUp() {
        contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(JavaMailSenderConfiguration.class));
    }

    @Test
    void configuringWithoutMandatoryProperties_shouldNotCreateSender() {
        contextRunner
            .run(context -> assertThat(context)
                .doesNotHaveBean(DkimJavaMailSender.class));
    }

    @Test
    void configuringWithDkimProperties_shouldCreateDkimSender() {
        contextRunner
            .withBean(DkimSigner.class, this::createDkimSigner)
            .withPropertyValues(dkimPropertyPairs())
            .run(context -> assertThat(context)
                .hasSingleBean(DkimJavaMailSender.class));
    }

    @SneakyThrows
    private DkimSigner createDkimSigner() {
        ClassPathResource privateKey = new ClassPathResource(DKIM_PRIVATE_KEY_LOCATION);
        return new DkimSigner(DKIM_SIGNING_DOMAIN, DKIM_SELECTOR, privateKey.getInputStream());
    }
}
