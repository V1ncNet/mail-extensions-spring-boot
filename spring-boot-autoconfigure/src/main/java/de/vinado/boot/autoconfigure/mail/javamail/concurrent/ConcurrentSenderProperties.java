package de.vinado.boot.autoconfigure.mail.javamail.concurrent;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for {@link de.vinado.spring.mail.javamail.concurrent.ConcurrentJavaMailSender} support.
 *
 * @author Vincent Nadoll
 */
@Getter
@Setter
@ConfigurationProperties("javamail.concurrent")
public class ConcurrentSenderProperties {

    private boolean enabled;
    private int batchSize = 20;
    private int cooldownMillis = 20 * 1000;
}
