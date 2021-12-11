package de.vinado.boot.autoconfigure.javamail;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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
@Import({DkimSignerConfiguration.class, JavaMailSenderConfiguration.class})
class JavaMailUtilsAutoConfiguration {
}
