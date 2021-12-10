package de.vinado.spring.mail.javamail.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.BlockingQueue;

/**
 * A {@link Runnable} consumer dequeuing batches and handing them over to the actual {@link JavaMailSender}.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@RequiredArgsConstructor
class BatchConsumer implements Runnable {

    private final BlockingQueue<Batch> queue;
    private final JavaMailSender sender;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        while (true) {
            try {
                Batch batch = queue.take();
                if (log.isDebugEnabled()) log.debug("Dequeued {}", batch);
                batch.dispatch(sender);
            } catch (InterruptedException e) {
                if (queue.isEmpty()) break;

                if (log.isErrorEnabled()) log.error(""
                    + "Mail sender thread was interrupted but queue was not empty. "
                    + "Some Emails were not sent.");
                throw new MailQueueException("Could not dequeue email batch", e);
            }
        }
    }
}
