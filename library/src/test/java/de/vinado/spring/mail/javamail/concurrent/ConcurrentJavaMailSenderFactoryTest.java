package de.vinado.spring.mail.javamail.concurrent;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

/**
 * @author Vincent Nadoll
 */
class ConcurrentJavaMailSenderFactoryTest {

    @Test
    void decoratingDelegate_shouldCreateNewDecorator() {
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        JavaMailSender delegate = mock(JavaMailSender.class);
        ConcurrentJavaMailSenderFactory factory = new ConcurrentJavaMailSenderFactory(threadPool);

        ConcurrentJavaMailSender sender = factory.decorate(delegate);

        assertNotNull(sender);

        threadPool.shutdownNow();
    }
}
