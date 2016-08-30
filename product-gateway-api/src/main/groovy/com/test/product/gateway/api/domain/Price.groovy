package com.test.product.gateway.api.domain

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull

@CompileStatic
@JsonIgnoreProperties(ignoreUnknown = true)
class Price {
    @NotNull
    BigDecimal value
    @NotNull
    String currency_code
}
