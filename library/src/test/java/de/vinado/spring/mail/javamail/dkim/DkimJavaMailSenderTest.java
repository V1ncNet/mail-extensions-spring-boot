package de.vinado.spring.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailParseException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * This suite calls every {@code #send(...)}-methods and tests if the a signing attempt happened.
 *
 * @author Vincent Nadoll
 */
class DkimJavaMailSenderTest {

    private DkimJavaMailSender sender;

    @BeforeEach
    void setUp() {
        DkimSigner dkimSigner = mock(DkimSigner.class);
        JavaMailSenderImpl delegate = new JavaMailSenderImpl();
        sender = spy(new DkimJavaMailSender(delegate, dkimSigner));
    }

    @Test
    void sendMimeMessage_shouldAttemptToSign() {
        MimeMessage mimeMessage = sender.createMimeMessage();

        assertThrows(MailParseException.class, () -> sender.send(mimeMessage));
        verify(sender, atLeastOnce()).createSignedMimeMessage(mimeMessage);
    }

    @Test
    void sendMimeMessages_shouldAttemptToSign() {
        MimeMessage[] mimeMessages = new MimeMessage[]{
            sender.createMimeMessage(),
        };

        assertThrows(MailParseException.class, () -> sender.send(mimeMessages));
        verify(sender, atLeastOnce()).createSignedMimeMessage(mimeMessages[0]);
    }

    @Test
    void sendSimpleMessage_shouldAttemptToSign() {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        assertThrows(MailParseException.class, () -> sender.send(simpleMailMessage));
        verify(sender, atLeastOnce()).createSignedMimeMessage(any());
    }

    @Test
    void sendSimpleMessages_shouldAttemptToSign() {
        SimpleMailMessage[] simpleMailMessages = new SimpleMailMessage[]{
            new SimpleMailMessage(),
        };

        assertThrows(MailParseException.class, () -> sender.send(simpleMailMessages));
        verify(sender, atLeastOnce()).createSignedMimeMessage(any());
    }

    @Test
    void sendMimeMessagePreparator_shouldAttemptToSign() {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
        };

        assertThrows(MailParseException.class, () -> sender.send(mimeMessagePreparator));
        verify(sender, atLeastOnce()).createSignedMimeMessage(any());
    }

    @Test
    void sendMimeMessagePreparators_shouldAttemptToSign() {
        MimeMessagePreparator[] mimeMessagePreparators = new MimeMessagePreparator[]{
            mimeMessage -> {
            },
        };

        assertThrows(MailParseException.class, () -> sender.send(mimeMessagePreparators));
        verify(sender, atLeastOnce()).createSignedMimeMessage(any());
    }
}
