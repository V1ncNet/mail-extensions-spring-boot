package de.vinado.spring.mail.javamail;

import lombok.Getter;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.util.Assert;

import java.io.InputStream;
import javax.mail.internet.MimeMessage;

/**
 * Basic implementation of the decorator pattern proxying every method to its delegate.
 *
 * @author Vincent Nadoll
 */
public abstract class JavaMailSenderDecorator implements JavaMailSender {

    @Getter
    private final JavaMailSender delegate;

    public JavaMailSenderDecorator(JavaMailSender delegate) {
        Assert.notNull(delegate, "Delegate JavaMailSender must not be null");

        this.delegate = delegate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessage createMimeMessage() {
        return delegate.createMimeMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessage createMimeMessage(InputStream contentStream) throws MailException {
        return delegate.createMimeMessage(contentStream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        delegate.send(mimeMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        delegate.send(mimeMessages);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        delegate.send(mimeMessagePreparator);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        delegate.send(mimeMessagePreparators);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        delegate.send(simpleMessage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        delegate.send(simpleMessages);
    }
}
