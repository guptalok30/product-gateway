package com.test.product.gateway.api

import com.test.product.gateway.api.domain.Price
import groovy.transform.CompileStatic

import javax.validation.Valid
import javax.validation.constraints.NotNull

@CompileStatic
class UpdateProductPriceRequest {
    @NotNull
    Long id
    String name
    @Valid
    Price current_price
}
