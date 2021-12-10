package de.vinado.spring.mail.javamail.concurrent;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Vincent Nadoll
 */
class BatchTest {

    @Test
    void initializingUnsupportedContent_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Batch(0, new String[]{}));
    }

    @Test
    void initializingBatchWithSimpleMailMessage_shouldReturnMessagesAsSimpleMailMessage() {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        Batch batch = Batches.create(simpleMessage);

        Object[] messages = batch.getMessages();

        assertTrue(messages instanceof SimpleMailMessage[]);
    }

    @Test
    void initializingBatchWithMimeMessage_shouldReturnMessagesAsSimpleMailMessage() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        Batch batch = Batches.create(mimeMessage);

        Object[] messages = batch.getMessages();

        assertTrue(messages instanceof MimeMessage[]);
    }

    @Test
    void initializingBatchWithMimeMessagePreparator_shouldReturnMessagesAsMimeMessagePreparator() {
        MimeMessagePreparator mimeMessagePreparator = mimeMessage -> {
        };
        Batch batch = Batches.create(mimeMessagePreparator);

        Object[] messages = batch.getMessages();

        assertTrue(messages instanceof MimeMessagePreparator[]);
    }

    @Test
    void initializingSingleMessage_shouldReturnMessagesOfSizeOne() {
        SimpleMailMessage simpleMessages = new SimpleMailMessage();
        Batch batch = Batches.create(simpleMessages);

        Object[] messages = batch.getMessages();

        assertEquals(1, messages.length);
    }

    @Test
    void initializingTwoMessage_shouldReturnMessagesOfSizeTwo() {
        SimpleMailMessage simpleMessages_1 = new SimpleMailMessage();
        SimpleMailMessage simpleMessages_2 = new SimpleMailMessage();
        Batch batch = Batches.create(simpleMessages_1, simpleMessages_2);

        Object[] messages = batch.getMessages();

        assertEquals(2, messages.length);
    }
}
