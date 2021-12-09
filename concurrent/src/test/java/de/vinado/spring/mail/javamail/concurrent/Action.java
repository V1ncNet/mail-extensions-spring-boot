package de.vinado.spring.mail.javamail.concurrent;

import org.springframework.mail.javamail.JavaMailSender;

import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @author Vincent Nadoll
 */
public interface Action<T> extends BiConsumer<JavaMailSender, T[]> {

    static <T> Action<T> noop() {
        return (sender, messages) -> {
        };
    }

    static <T> Action<T> appendTo(Appendable appendable, Function<T, String> extractor) {
        return (sender, messages) -> {
            for (T message : messages) {
                try {
                    appendable.append(extractor.apply(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
