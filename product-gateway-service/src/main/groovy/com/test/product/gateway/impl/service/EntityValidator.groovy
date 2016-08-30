package com.test.product.gateway.impl.service

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

import javax.inject.Inject
import javax.validation.ConstraintViolation
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@CompileStatic
@Service
@Slf4j
class EntityValidator {
    final Validator validator

    @Inject
    EntityValidator(Validator validator) {
        this.validator = validator
    }

    void validate(Object obj) {
        Set<ConstraintViolation<Object>> violations = validator.validate(obj)
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations)
        }
    }
}
