package com.test.product.gateway.api.common

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic

@CompileStatic
@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ErrorResponse {
    String message
}
