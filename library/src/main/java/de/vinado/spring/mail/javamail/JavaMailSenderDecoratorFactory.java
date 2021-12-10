package de.vinado.spring.mail.javamail;

import org.springframework.mail.javamail.JavaMailSender;

/**
 * An interface for decorating any {@link JavaMailSender}.
 *
 * @author Vincent Nadoll
 */
public interface JavaMailSenderDecoratorFactory {

    /**
     * Decorated the given delegate with a new {@link JavaMailSender}.
     *
     * @param delegate the sender to be decorated
     * @return a new instance of {@link JavaMailSender}
     */
    JavaMailSender decorate(JavaMailSender delegate);
}
