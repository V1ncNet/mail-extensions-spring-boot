package de.vinado.spring.mail.javamail.concurrent;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.StringJoiner;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.mail.internet.MimeMessage;

/**
 * A generic implementation of {@link Delayed} holding supported MIME messages of any type.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@EqualsAndHashCode
public class Batch implements Delayed {

    private final long time;

    @Getter(AccessLevel.PACKAGE)
    private final Object[] messages;

    private final Derivate derivate;

    protected Batch(long time, Object[] messages) {
        this.time = time;
        this.messages = messages;
        this.derivate = Derivate.forClass(messages.getClass());
    }

    /**
     * Sends this messages using the given {@link JavaMailSender}.
     *
     * @param sender must not be null
     * @implNote The implementation should use one of {@link JavaMailSender}s <em>#send</em> methods.
     */
    void dispatch(JavaMailSender sender) {
        derivate.send(sender, messages);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long diff = this.time - System.currentTimeMillis();
        return unit.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return saturatedCast(this.time - ((Batch) other).time);
    }

    private static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Batch.class.getSimpleName() + "[", "]")
            .add("time=" + Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime())
            .add("delay=" + getDelay(TimeUnit.MILLISECONDS) + "ms")
            .add("messages=" + Arrays.toString(messages))
            .add("derivate=" + derivate)
            .toString();
    }


    @RequiredArgsConstructor
    private enum Derivate {
        // CHECKSTYLE.OFF: LineLength - Much more readable
        SIMPLE_MAIL_MESSAGE(SimpleMailMessage[].class, (sender, messages) -> sender.send((SimpleMailMessage[]) messages)),
        MIME_MESSAGE(MimeMessage[].class, (sender, messages) -> sender.send((MimeMessage[]) messages)),
        MIME_MESSAGE_PREPARATOR(MimeMessagePreparator[].class, (sender, messages) -> sender.send((MimeMessagePreparator[]) messages)),
        ;
        // CHECKSTYLE.ON: LineLength

        private final Class<?> messageType;
        private final BiConsumer<JavaMailSender, Object[]> dispatcher;

        private void send(JavaMailSender sender, Object[] messages) {
            dispatcher.accept(sender, messages);
            if (log.isDebugEnabled()) log.debug("Dispatched using {}", sender);
        }

        private static Derivate forClass(Class<?> messageType) {
            return Arrays.stream(values())
                .filter(by(messageType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("[" + messageType + "] ist an unsupported type"));
        }

        private static Predicate<Derivate> by(Class<?> type) {
            return value -> value.messageType.equals(type);
        }
    }
}
