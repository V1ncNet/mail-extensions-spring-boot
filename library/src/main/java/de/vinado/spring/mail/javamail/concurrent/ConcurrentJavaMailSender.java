package de.vinado.spring.mail.javamail.concurrent;

import de.vinado.spring.mail.javamail.JavaMailSenderDecorator;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * {@link JavaMailSender} implementation that queues any messages and delays. The amount of concurrently enqueued
 * emails as well as the cooldown between dequeue operations can be configured using the appropriate setter. Also, this
 * sender SMTP transport operation doesn't block the main thread which becomes handy in case your SMTP provider provides
 * a darn slow server. You can also overcome any rate limit or limit involving the amount of MIME messages to be present
 * in an SMTP session.
 *
 * @author Vincent Nadoll
 */
@Slf4j
public class ConcurrentJavaMailSender extends JavaMailSenderDecorator implements JavaMailSender {

    @Getter
    @Setter
    private int batchSize = 20;

    @Getter
    @Setter
    private int cooldownMillis = 20 * 1000;

    private final BlockingQueue<Batch> queue;
    private final AtomicLong delay;

    ConcurrentJavaMailSender(JavaMailSender delegate,
                             ExecutorService threadPool) {
        super(delegate);
        this.queue = new DelayQueue<>();
        this.delay = new AtomicLong(System.currentTimeMillis());

        startConsumer(threadPool, delegate);
    }

    private void startConsumer(ExecutorService threadPool, JavaMailSender delegate) {
        BatchConsumer consumer = new BatchConsumer(queue, delegate);
        threadPool.execute(consumer);
    }

    @Override
    public void send(MimeMessage mimeMessage) throws MailException {
        send(new MimeMessage[]{mimeMessage});
    }

    @Override
    public void send(MimeMessage... mimeMessages) throws MailException {
        doSend(mimeMessages);
    }

    @Override
    public void send(MimeMessagePreparator mimeMessagePreparator) throws MailException {
        send(new MimeMessagePreparator[]{mimeMessagePreparator});
    }

    @Override
    public void send(MimeMessagePreparator... mimeMessagePreparators) throws MailException {
        doSend(mimeMessagePreparators);
    }

    @Override
    public void send(SimpleMailMessage simpleMessage) throws MailException {
        send(new SimpleMailMessage[]{simpleMessage});
    }

    @Override
    public void send(SimpleMailMessage... simpleMessages) throws MailException {
        doSend(simpleMessages);
    }

    private void doSend(Object[] messages) throws MailException {
        resetDelayIfQueueIsEmpty();

        Batch[] batches = createBatches(messages);
        enqueue(batches);
    }

    private void resetDelayIfQueueIsEmpty() {
        long time = System.currentTimeMillis();
        int queueSize = queue.size();
        long delay = this.delay.get();

        if (log.isTraceEnabled()) {
            log.trace("Queue length is: {}", queueSize);
            log.trace("Delay is at: {}", Instant.ofEpochMilli(delay).atZone(ZoneId.systemDefault()).toLocalDateTime());
            log.trace("Cooldown expires in: {}", (delay + cooldownMillis - time));
        }

        if (queueSize == 0 && delay + cooldownMillis < time) {
            if (log.isTraceEnabled()) log.trace("Resetting delay");
            this.delay.set(time);
        }
    }

    private Batch[] createBatches(Object[] original) {
        return partition(original, batchSize)
            .map(messages -> {
                long time = delay.getAndAdd(cooldownMillis);
                return new Batch(time, messages);
            })
            .toArray(Batch[]::new);
    }

    private static <T> Stream<T[]> partition(T[] original, int batchSize) {
        return IntStream.iterate(0, i -> i + batchSize)
            .limit((long) Math.ceil((double) original.length / batchSize))
            .mapToObj(i -> Arrays.copyOfRange(original, i, Math.min(i + batchSize, original.length)));
    }

    private void enqueue(Batch[] batches) throws MailException {
        for (Batch batch : batches) {
            try {
                if (log.isDebugEnabled()) log.debug("Enqueueing {}", batch);
                queue.put(batch);
            } catch (InterruptedException e) {
                if (log.isErrorEnabled()) log.error("An error occurred while waiting for {} to be enqueued.", batch);
                throw new MailQueueException("Could not enqueue email batch", e);
            }
        }
    }
}
