package com.mishail.country_search.aspect;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;


@Aspect
@Component
public class CountrySearchAspect {
    private final Logger logger = LoggerFactory
            .getLogger(CountrySearchAspect.class);

    @Pointcut("execution(* com.mishail.country_search.controller.*.*(..))")
    public void callControllers() {
    }

    @Before("callControllers()")
    public void beforeCallMethod(final JoinPoint jp) {
        String args = Arrays.stream(jp.getArgs())
                .map(Object::toString)
                .collect(Collectors.joining(","));
        if (logger.isInfoEnabled()) {
            logger.info("Before {}, args={}", jp, args);
        }
    }

    @After("callControllers()")
    public void afterCallMethod(final JoinPoint jp) {
        if (logger.isInfoEnabled()) {
            logger.info("After {}", jp.toString());
        }
    }

    @AfterReturning("callControllers()")
    public void afterReturningCallMethod(final JoinPoint jp) {
        if (logger.isInfoEnabled()) {
            logger.info("After returning {}", jp.toString());
        }
    }

    @AfterThrowing(value = "callControllers()", throwing = "exception")
    public void afterThrowingCallMethod(final JoinPoint jp,
                                        final Exception exception) {
        if (logger.isErrorEnabled()) {
            logger.error("After throwing {}, exception: {}",
                    jp.toString(), exception.getMessage());
        }
    }

    @PostConstruct
    public void initAspect() {
        if (logger.isInfoEnabled()) {
            logger.info("Aspect is initialized");
        }
    }

    @PreDestroy
    public void destroyAspect() {
        if (logger.isInfoEnabled()) {
            logger.info("Aspect is destroyed");
        }
    }
}
