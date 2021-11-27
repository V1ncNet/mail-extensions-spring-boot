package de.vinado.spring.mail.javamail.dkim;

import de.vinado.spring.test.mail.javamail.MockJavaMailSender;
import lombok.SneakyThrows;
import net.markenwerk.utils.mail.dkim.DkimMessage;
import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * This suite calls every {@code #send(...)}-methods and tests if the a signing attempt happened.
 *
 * @author Vincent Nadoll
 */
class DkimJavaMailSenderTest {

    private MockJavaMailSender delegate;
    private DkimJavaMailSender sender;

    @BeforeEach
    void setUp() {
        DkimSigner dkimSigner = new DkimSignerBuilder().defaultSigner().build();
        delegate = MockJavaMailSender.defaultSender().build();
        sender = spy(new DkimJavaMailSender(delegate, dkimSigner));
    }

    @Test
    @SneakyThrows
    void sendMimeMessage_shouldAttemptToSign() {
        MimeMessage mimeMessage = sender.createMimeMessage();
        mimeMessage.setFrom("test@example.com");
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("john.doe@example.com"));
        mimeMessage.setSubject("Ping");
        mimeMessage.setText("Lorem Ipsum");

        sender.send(mimeMessage);

        assertSignedMessages(1);
    }

    @Test
    @SneakyThrows
    void sendMimeMessages_shouldAttemptToSign() {
        MimeMessage mimeMessage1 = sender.createMimeMessage();
        mimeMessage1.setFrom("test@example.com");
        mimeMessage1.setRecipient(Message.RecipientType.TO, new InternetAddress("john.doe@example.com"));
        mimeMessage1.setSubject("Ping");
        mimeMessage1.setText("Lorem Ipsum");
        MimeMessage mimeMessage2 = sender.createMimeMessage();
        mimeMessage2.setFrom("test@example.com");
        mimeMessage2.setRecipient(Message.RecipientType.TO, new InternetAddress("jane.doe@example.com"));
        mimeMessage1.setSubject("Ping");
        mimeMessage2.setText("Lorem Ipsum");

        sender.send(mimeMessage1, mimeMessage2);

        assertSignedMessages(2);
    }

    @Test
    void sendSimpleMessage_shouldAttemptToSign() {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("test@example.com");
        simpleMessage.setTo("john.doe@example.com");
        simpleMessage.setSubject("Ping");
        simpleMessage.setText("Lorem Ipsum");

        sender.send(simpleMessage);

        assertSignedMessages(1);
    }

    @Test
    void sendSimpleMessages_shouldAttemptToSign() {
        SimpleMailMessage simpleMessage1 = new SimpleMailMessage();
        simpleMessage1.setFrom("test@example.com");
        simpleMessage1.setTo("john.doe@example.com");
        simpleMessage1.setSubject("Ping");
        simpleMessage1.setText("Lorem Ipsum");
        SimpleMailMessage simpleMessage2 = new SimpleMailMessage();
        simpleMessage2.setFrom("test@example.com");
        simpleMessage2.setTo("jane.doe@example.com");
        simpleMessage2.setSubject("Ping");
        simpleMessage2.setText("Lorem Ipsum");

        sender.send(simpleMessage1, simpleMessage2);

        assertSignedMessages(2);
    }

    @Test
    void sendMimeMessagePreparator_shouldAttemptToSign() {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("john.doe@example.com"));
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };

        sender.send(mimeMessagePreparator);

        assertSignedMessages(1);
    }

    @Test
    void sendMimeMessagePreparators_shouldAttemptToSign() {
        MimeMessagePreparator preparator1 = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("john.doe@example.com"));
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };
        MimeMessagePreparator preparator2 = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("jane.doe@example.com"));
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };

        sender.send(preparator1, preparator2);

        assertSignedMessages(2);
    }

    @Test
    void sendArtificiallyBadSubject_shouldThrowException() throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        mimeMessage.setFrom("test@example.com");
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress("john.doe@example.com"));
        mimeMessage.setSubject("fail");
        mimeMessage.setText("Lorem Ipsum");

        try {
            sender.send(mimeMessage);
        } catch (MailSendException e) {
            assertEquals(1, e.getFailedMessages().size());
            assertEquals(DkimMessage.class, e.getFailedMessages().keySet().iterator().next().getClass());
            Object nested = e.getFailedMessages().values().iterator().next();
            assertTrue(nested instanceof MessagingException);
            assertEquals("failed", ((MessagingException) nested).getMessage());
        }

        verify(sender, times(1)).createSignedMimeMessage(any());
    }

    private void assertSignedMessages(int expectedAmount) {
        assertEquals(expectedAmount, delegate.getSentMessages().size());

        for (int i = 0; i < expectedAmount; i++) {
            assertEquals(DkimMessage.class, delegate.getSentMessage(i).getClass());
        }

        verify(sender, times(expectedAmount)).createSignedMimeMessage(any());
    }
}
