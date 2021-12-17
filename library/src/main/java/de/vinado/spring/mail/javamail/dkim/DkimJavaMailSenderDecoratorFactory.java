package de.vinado.spring.mail.javamail.dkim;

import de.vinado.spring.mail.javamail.JavaMailSenderDecoratorFactory;
import lombok.RequiredArgsConstructor;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Factory implementation for decorating any {@link JavaMailSender}.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class DkimJavaMailSenderDecoratorFactory implements JavaMailSenderDecoratorFactory {

    private final DkimSigner signer;

    /**
     * Decorates the given {@link JavaMailSender} with a new instance of {@link DkimJavaMailSender}.
     *
     * @param delegate the sender to be decorated
     * @return a new {@link DkimJavaMailSender} instance
     */
    @Override
    public DkimJavaMailSender decorate(JavaMailSender delegate) {
        return new DkimJavaMailSender(delegate, signer);
    }
}
