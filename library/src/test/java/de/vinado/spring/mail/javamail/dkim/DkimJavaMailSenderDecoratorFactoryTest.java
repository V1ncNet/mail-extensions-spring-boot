package de.vinado.spring.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Vincent Nadoll
 */
class DkimJavaMailSenderDecoratorFactoryTest {

    @Test
    void decoratingDelegate_shouldCreateNewDecorator() {
        DkimSigner signer = mock(DkimSigner.class);
        JavaMailSender delegate = new JavaMailSenderImpl();
        DkimJavaMailSenderDecoratorFactory factory = new DkimJavaMailSenderDecoratorFactory(signer);

        DkimJavaMailSender sender = factory.decorate(delegate);

        assertNotNull(sender);
    }
}
