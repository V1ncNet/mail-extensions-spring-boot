package de.vinado.spring.mail.javamail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.spy;

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
}
