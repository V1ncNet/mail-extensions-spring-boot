package de.vinado.spring.mail.javamail.concurrent;

import org.junit.jupiter.api.Test;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * @author Vincent Nadoll
 */
class BatchConsumerTest {

    @Test
    void enqueueingSimpleMessages_shouldProcessDuringExecution() throws InterruptedException {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        StringBuilder builder = new StringBuilder();
        LinkedBlockingQueue<Batch> queue = new LinkedBlockingQueue<>();
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        simpleMessage.setFrom("test@example.com");
        simpleMessage.setTo("john.doe@example.com");
        simpleMessage.setSubject("Ping");
        simpleMessage.setText("Lorem Ipsum");
        Batch batch = Batches.create(0, Action.appendTo(builder, SimpleMailMessage::getFrom), simpleMessage);
        queue.put(batch);

        JavaMailSender sender = mock(JavaMailSender.class);
        BatchConsumer consumer = new BatchConsumer(queue, sender);

        threadPool.execute(consumer);
        threadPool.shutdown();
        threadPool.awaitTermination(10, TimeUnit.MILLISECONDS);

        assertEquals("test@example.com", builder.toString());
    }
}
