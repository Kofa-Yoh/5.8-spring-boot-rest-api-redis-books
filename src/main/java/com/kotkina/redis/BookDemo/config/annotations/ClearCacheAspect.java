package com.kotkina.redis.BookDemo.config.annotations;

import com.kotkina.redis.BookDemo.services.CacheService;
import com.kotkina.redis.BookDemo.web.models.requests.BookRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class ClearCacheAspect {

    private final CacheService cacheService;

    @Before("@annotation(cacheEvictByPrefix) && args(.., bookRequest)")
    public void bookRequestCacheEvictByPrefix(CacheEvictByPrefix cacheEvictByPrefix, BookRequest bookRequest) {
        String prefix = cacheEvictByPrefix.cacheName() + "::" +
                evaluateSpelExpression(cacheEvictByPrefix.key(), bookRequest, "bookRequest") +
                cacheEvictByPrefix.prefixEnd();

        cacheService.evictCachesByPrefix(prefix);
    }

    private String evaluateSpelExpression(String expression, Object param, String paramName) {
        try {
            SpelExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(expression);
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable(paramName, param);

            return exp.getValue(context, String.class);
        } catch (Exception e) {
            return "";
        }
    }
}
