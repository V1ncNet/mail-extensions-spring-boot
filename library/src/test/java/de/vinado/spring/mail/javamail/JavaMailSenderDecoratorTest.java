package de.vinado.spring.mail.javamail;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Vincent Nadoll
 */
class JavaMailSenderDecoratorTest {

    private MockJavaMailSender delegate;
    private JavaMailSenderDecorator sender;

    @BeforeEach
    void setUp() {
        delegate = spy(new MockJavaMailSender());
        sender = new JavaMailSenderDecorator(delegate) {
        };
    }

    @Test
    void initializingNullArgument_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new JavaMailSenderDecorator(null) {
        });
    }

    @Test
    void initializingDelegate_shouldAlsoBeReturned() {
        assertEquals(delegate, sender.getDelegate());
    }

    @Test
    void creatingMimeMessage_shouldDelegate() {
        sender.createMimeMessage();

        verify(delegate, times(1)).createMimeMessage();
    }

    @Test
    void creatingMimeMessageFromContentStream_shouldDelegate() {
        InputStream contentStream = mock(InputStream.class);

        sender.createMimeMessage(contentStream);

        verify(delegate, times(1)).createMimeMessage(eq(contentStream));
    }

    @Test
    void sendingSimpleMessage_shouldDelegate() {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("test@example.com");
        simpleMessage.setTo("john.doe@example.com");
        simpleMessage.setSubject("Ping");
        simpleMessage.setText("Lorem Ipsum");

        sender.send(simpleMessage);

        verify(delegate, times(1)).send(eq(simpleMessage));
    }

    @Test
    void sendingSimpleMessages_shouldDelegate() {
        SimpleMailMessage simpleMessage_1 = new SimpleMailMessage();
        simpleMessage_1.setFrom("test@example.com");
        simpleMessage_1.setTo("john.doe@example.com");
        simpleMessage_1.setSubject("Ping");
        simpleMessage_1.setText("Lorem Ipsum");
        SimpleMailMessage simpleMessage_2 = new SimpleMailMessage();
        simpleMessage_2.setFrom("test@example.com");
        simpleMessage_2.setTo("jane.doe@example.com");
        simpleMessage_2.setSubject("Ping");
        simpleMessage_2.setText("Lorem Ipsum");

        sender.send(simpleMessage_1, simpleMessage_2);

        verify(delegate, times(1)).send(ArgumentMatchers.<SimpleMailMessage[]>any());
    }

    @Test
    @SneakyThrows
    void sendingMimeMessage_shouldDelegate() {
        MimeMessage mimeMessage = sender.createMimeMessage();
        mimeMessage.setFrom("test@example.com");
        mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
        mimeMessage.setSubject("Ping");
        mimeMessage.setText("Lorem Ipsum");

        sender.send(mimeMessage);

        verify(delegate, times(1)).send(eq(mimeMessage));
    }

    @Test
    @SneakyThrows
    void sendingMimeMessages_shouldDelegate() {
        MimeMessage mimeMessage_1 = sender.createMimeMessage();
        mimeMessage_1.setFrom("test@example.com");
        mimeMessage_1.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
        mimeMessage_1.setSubject("Ping");
        mimeMessage_1.setText("Lorem Ipsum");
        MimeMessage mimeMessage_2 = sender.createMimeMessage();
        mimeMessage_2.setFrom("test@example.com");
        mimeMessage_2.setRecipients(Message.RecipientType.TO, "jane.doe@example.com");
        mimeMessage_2.setSubject("Ping");
        mimeMessage_2.setText("Lorem Ipsum");

        sender.send(mimeMessage_1, mimeMessage_2);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessage[]>any());
    }

    @Test
    void sendingMimeMessagePreparator_shouldDelegate() {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };

        sender.send(preparator);

        verify(delegate, times(1)).send(eq(preparator));
    }

    @Test
    void sendingMimeMessagePreparators_shouldDelegate() {
        MimeMessagePreparator preparator_1 = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };
        MimeMessagePreparator preparator_2 = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipients(Message.RecipientType.TO, "jane.doe@example.com");
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };

        sender.send(preparator_1, preparator_2);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessagePreparator[]>any());
    }
}
