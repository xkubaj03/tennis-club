package com.inqool.tennisclub.aspects;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LogManager.getLogger(LoggingAspect.class);
    private static final ThreadLocal<Boolean> isLogging = new ThreadLocal<>();

    @Around("execution(* com.inqool.tennisclub.CourtService.*(..)) || "
            + "execution(* com.inqool.tennisclub.CourtSurfaceService.*(..)) || "
            + "execution(* com.inqool.tennisclub.CustomerService.*(..)) || "
            + "execution(* com.inqool.tennisclub.ReservationService.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        if (Boolean.TRUE.equals(isLogging.get())) {
            return joinPoint.proceed();
        }

        try {
            isLogging.set(true);

            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            Object[] args = joinPoint.getArgs();

            log.info("[{}] {} → {}", className, methodName, formatArgs(args));

            Object result = joinPoint.proceed();

            log.info("[{}] {} ← {}", className, methodName, formatResult(result));

            return result;
        } finally {
            isLogging.remove();
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "()";
        }
        return Arrays.stream(args).map(this::formatValue).collect(Collectors.joining(", ", "(", ")"));
    }

    private String formatResult(Object result) {
        if (result == null) {
            return "null";
        }
        return formatValue(result);
    }

    private String formatValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String) {
            return "\"" + value + "\"";
        }
        if (value instanceof Collection<?>) {
            return "[" + ((Collection<?>) value).size() + " items]";
        }
        return value.toString();
    }
}
