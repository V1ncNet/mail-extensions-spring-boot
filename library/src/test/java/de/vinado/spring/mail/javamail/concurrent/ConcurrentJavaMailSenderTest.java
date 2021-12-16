package de.vinado.spring.mail.javamail.concurrent;

import de.vinado.spring.mail.javamail.MockJavaMailSender;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Vincent Nadoll
 */
class ConcurrentJavaMailSenderTest {

    private static final long EXECUTION_TIMEOUT_MILLIS = 50;
    private static final int COOLDOWN_MILLIS = 500;
    private static final long COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS = EXECUTION_TIMEOUT_MILLIS + COOLDOWN_MILLIS;

    private ExecutorService executor;
    private MockJavaMailSender delegate;
    private ConcurrentJavaMailSender sender;

    @BeforeEach
    void setUp() {
        executor = Executors.newSingleThreadExecutor();
        delegate = spy(new MockJavaMailSender());
        sender = new ConcurrentJavaMailSender(delegate, executor);

        applyProperties(sender);
    }

    private void applyProperties(ConcurrentJavaMailSender sender) {
        sender.setBatchSize(2);
        sender.setCooldownMillis(COOLDOWN_MILLIS);
    }

    @Test
    void creatingInitialMimeMessage_shouldUseDelegateSender() {
        sender.createMimeMessage();

        verify(delegate, times(1)).createMimeMessage();
    }

    @Test
    void creatingMimeMessage_shouldUseDelegateSender() {
        InputStream contentStream = mock(InputStream.class);

        sender.createMimeMessage(contentStream);

        verify(delegate, times(1)).createMimeMessage(eq(contentStream));
    }

    @Test
    @SneakyThrows
    void sendingSimpleMessage_shouldSendUsingDelegate() {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("test@example.com");
        simpleMessage.setTo("john.doe@example.com");
        simpleMessage.setSubject("Ping");
        simpleMessage.setText("Lorem Ipsum");

        sender.send(simpleMessage);

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<SimpleMailMessage[]>any());
    }

    @Test
    @SneakyThrows
    void sendingSimpleMessages_shouldSendUsingDelegate() {
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

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<SimpleMailMessage[]>any());
    }

    @Test
    @SneakyThrows
    void sendingMimeMessage_shouldSendUsingDelegate() {
        MimeMessage mimeMessage = sender.createMimeMessage();
        mimeMessage.setFrom("test@example.com");
        mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
        mimeMessage.setSubject("Ping");
        mimeMessage.setText("Lorem Ipsum");

        sender.send(mimeMessage);

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessage[]>any());
    }

    @Test
    @SneakyThrows
    void sendingMimeMessages_shouldSendUsingDelegate() {
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

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessage[]>any());
    }

    @Test
    @SneakyThrows
    void sendingMimeMessagePreparator_shouldSendUsingDelegate() {
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setFrom("test@example.com");
            mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
            mimeMessage.setSubject("Ping");
            mimeMessage.setText("Lorem Ipsum");
        };

        sender.send(preparator);

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessagePreparator[]>any());
    }

    @Test
    @SneakyThrows
    void sendingMimeMessagePreparators_shouldSendUsingDelegate() {
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

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        verify(delegate, times(1)).send(ArgumentMatchers.<MimeMessagePreparator[]>any());
    }

    @Test
    @SneakyThrows
    void sendingInitialMessage_shouldDispatchImmediately() {
        MimeMessage mimeMessage = sender.createMimeMessage();
        mimeMessage.setFrom("test@example.com");
        mimeMessage.setRecipients(Message.RecipientType.TO, "john.doe@example.com");
        mimeMessage.setSubject("Ping");
        mimeMessage.setText("Lorem Ipsum");

        sender.send(mimeMessage);

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        assertEquals(mimeMessage, delegate.getSentMessage(0));
    }

    @Test
    @SneakyThrows
    void sendingUndersized_shouldDispatchImmediately() {
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

        executor.shutdown();
        executor.awaitTermination(EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        assertEquals(mimeMessage_1, delegate.getSentMessage(0));
        assertEquals(mimeMessage_2, delegate.getSentMessage(1));
    }

    @Test
    @SneakyThrows
    void sendingOversized_shouldPartitionThenDispatchSequentially() {
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
        MimeMessage mimeMessage_3 = sender.createMimeMessage();
        mimeMessage_3.setFrom("test@example.com");
        mimeMessage_3.setRecipients(Message.RecipientType.TO, "max.mustermann@example.com");
        mimeMessage_3.setSubject("Ping");
        mimeMessage_3.setText("Lorem Ipsum");

        sender.send(mimeMessage_1, mimeMessage_2, mimeMessage_3);

        Thread.sleep(EXECUTION_TIMEOUT_MILLIS);

        List<Message> sentMessages = delegate.getSentMessages();
        assertEquals(2, sentMessages.size());
        assertEquals(mimeMessage_1, sentMessages.get(0));
        assertEquals(mimeMessage_2, sentMessages.get(1));

        executor.shutdown();
        executor.awaitTermination(COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        sentMessages = delegate.getSentMessages();
        assertEquals(1, sentMessages.size());
        assertEquals(mimeMessage_3, sentMessages.get(0));
    }

    @Test
    @SneakyThrows
    void sendingTwoUndersized_shouldDispatchSequentiallyAfterCooldown() {
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

        sender.send(mimeMessage_1);
        sender.send(mimeMessage_2);

        Thread.sleep(EXECUTION_TIMEOUT_MILLIS);

        List<Message> sentMessages = delegate.getSentMessages();
        assertEquals(1, sentMessages.size());
        assertEquals(mimeMessage_1, sentMessages.get(0));

        executor.shutdown();
        executor.awaitTermination(COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        sentMessages = delegate.getSentMessages();
        assertEquals(1, sentMessages.size());
        assertEquals(mimeMessage_2, sentMessages.get(0));
    }

    @Test
    @SneakyThrows
    void sendingTwoOversized_shouldPartitionThenDispatchSequentially() {
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
        MimeMessage mimeMessage_3 = sender.createMimeMessage();
        mimeMessage_3.setFrom("test@example.com");
        mimeMessage_3.setRecipients(Message.RecipientType.TO, "max.mustermann@example.com");
        mimeMessage_3.setSubject("Ping");
        mimeMessage_3.setText("Lorem Ipsum");
        MimeMessage mimeMessage_4 = sender.createMimeMessage();
        mimeMessage_4.setFrom("test@example.com");
        mimeMessage_4.setRecipients(Message.RecipientType.TO, "erika.musterfrau@example.com");
        mimeMessage_4.setSubject("Ping");
        mimeMessage_4.setText("Lorem Ipsum");
        MimeMessage mimeMessage_5 = sender.createMimeMessage();
        mimeMessage_5.setFrom("test@example.com");
        mimeMessage_5.setRecipients(Message.RecipientType.TO, "uncle.bob@example.com");
        mimeMessage_5.setSubject("Ping");
        mimeMessage_5.setText("Lorem Ipsum");
        MimeMessage mimeMessage_6 = sender.createMimeMessage();
        mimeMessage_6.setFrom("test@example.com");
        mimeMessage_6.setRecipients(Message.RecipientType.TO, "eric.evans@example.com");
        mimeMessage_6.setSubject("Ping");
        mimeMessage_6.setText("Lorem Ipsum");

        sender.send(mimeMessage_1, mimeMessage_2, mimeMessage_3);
        sender.send(mimeMessage_4, mimeMessage_5, mimeMessage_6);

        Thread.sleep(EXECUTION_TIMEOUT_MILLIS);

        List<Message> sentMessages = delegate.getSentMessages();
        assertEquals(2, sentMessages.size());
        assertEquals(mimeMessage_1, sentMessages.get(0));
        assertEquals(mimeMessage_2, sentMessages.get(1));

        Thread.sleep(COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS);

        sentMessages = delegate.getSentMessages();
        assertEquals(1, sentMessages.size());
        assertEquals(mimeMessage_3, sentMessages.get(0));

        Thread.sleep(COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS);

        sentMessages = delegate.getSentMessages();
        assertEquals(2, sentMessages.size());
        assertEquals(mimeMessage_4, sentMessages.get(0));
        assertEquals(mimeMessage_5, sentMessages.get(1));

        executor.shutdown();
        executor.awaitTermination(COOLED_DOWN_EXECUTION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);

        sentMessages = delegate.getSentMessages();
        assertEquals(1, sentMessages.size());
        assertEquals(mimeMessage_6, sentMessages.get(0));
    }

    @AfterEach
    void tearDown() {
        executor.shutdownNow();
    }
}
