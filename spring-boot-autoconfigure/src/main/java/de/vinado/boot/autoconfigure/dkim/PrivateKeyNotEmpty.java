package de.vinado.boot.autoconfigure.dkim;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * Condition which ensures an existing <em>dkim.private-key</em> has a non-empty value.
 *
 * @author Vincent Nadoll
 */
class PrivateKeyNotEmpty implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String privateKey = context.getEnvironment().getProperty("dkim.private-key");
        return !StringUtils.isEmpty(privateKey);
    }
}
