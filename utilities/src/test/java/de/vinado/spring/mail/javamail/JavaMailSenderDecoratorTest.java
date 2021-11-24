package de.vinado.spring.mail.javamail;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Vincent Nadoll
 */
class JavaMailSenderDecoratorTest {

    @Test
    void initializingNullArgument_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new JavaMailSenderDecorator(null) {
        });
    }
}
