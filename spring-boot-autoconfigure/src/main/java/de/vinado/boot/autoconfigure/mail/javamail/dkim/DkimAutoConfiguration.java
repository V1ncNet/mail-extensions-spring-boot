package de.vinado.boot.autoconfigure.mail.javamail.dkim;

import net.markenwerk.utils.mail.dkim.DkimSigner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author Vincent Nadoll
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(DkimSigner.class)
@Conditional(PrivateKeyNotEmpty.class)
@Import({DkimSignerConfiguration.class, DkimSenderConfiguration.class})
public class DkimAutoConfiguration {
}
