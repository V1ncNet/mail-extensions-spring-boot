package de.vinado.spring.mail.javamail.concurrent;

import org.springframework.mail.MailException;

/**
 * Exception to be thrown if a mail batch could not be properly en- or dequeued.
 *
 * @author Vincent Nadoll
 */
public class MailQueueException extends MailException {

    public MailQueueException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
