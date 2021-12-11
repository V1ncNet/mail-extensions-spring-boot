package de.vinado.boot.autoconfigure.javamail;

import de.vinado.boot.autoconfigure.javamail.JavaMailUtilsAutoConfiguration.JavaMailUtilsCondition;
import org.springframework.boot.autoconfigure.condition.AnyNestedCondition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration Auto configuration} for advanced {@link
 * JavaMailSender}s.
 *
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(JavaMailSender.class)
@Conditional(JavaMailUtilsCondition.class)
@Import({DkimSignerConfiguration.class, JavaMailSenderConfiguration.class})
public class JavaMailUtilsAutoConfiguration {

    static class JavaMailUtilsCondition extends AnyNestedCondition {

        JavaMailUtilsCondition() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @Conditional(PrivateKeyNotEmpty.class)
        static class PrivateKeyProperty {
        }

        @ConditionalOnProperty(prefix = "javamail.concurrent", name = "enabled", havingValue = "true")
        static class ConcurrentSenderEnabledProperty {
        }
    }
}
