package de.vinado.spring.mail.javamail.concurrent;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;

/**
 * @author Vincent Nadoll
 */
public final class Batches {

    public static Batch create(SimpleMailMessage... simpleMessages) {
        return create(0, Action.noop(), simpleMessages);
    }

    public static Batch create(MimeMessage... mimeMessages) {
        return create(0, Action.noop(), mimeMessages);
    }

    public static Batch create(MimeMessagePreparator... mimeMessagePreparators) {
        return create(0, Action.noop(), mimeMessagePreparators);
    }

    public static Batch create(long time, Action<SimpleMailMessage> action, SimpleMailMessage... mailMessages) {
        return new Batch(time, mailMessages) {
            @Override
            void dispatch(JavaMailSender sender) {
                action.accept(sender, (SimpleMailMessage[]) getMessages());
            }
        };
    }

    public static Batch create(long time, Action<MimeMessage> action, MimeMessage... mimeMessages) {
        return new Batch(time, mimeMessages) {
            @Override
            void dispatch(JavaMailSender sender) {
                action.accept(sender, (MimeMessage[]) getMessages());
            }
        };
    }

    public static Batch create(long time,
                               Action<MimeMessagePreparator> action,
                               MimeMessagePreparator... mimeMessagePreparators) {
        return new Batch(time, mimeMessagePreparators) {
            @Override
            void dispatch(JavaMailSender sender) {
                action.accept(sender, (MimeMessagePreparator[]) getMessages());
            }
        };
    }
}
