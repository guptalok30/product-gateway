package com.test.product.gateway.fixtures

import com.test.product.gateway.api.GetProductResponse
import com.test.product.gateway.api.domain.Price

class GetProductResponseFixtureBuilder {
    Long id = 1234L
    String name = 'My Test Product'
    Price price = new Price(value: 12.00, currency_code: 'USD')

    GetProductResponseFixtureBuilder withId(Long id) {
        this.id = id
        return this
    }

    GetProductResponseFixtureBuilder withName(String name) {
        this.name = name
        return this
    }

    GetProductResponseFixtureBuilder withPrice(Price price) {
        this.price = price
        return this
    }

    GetProductResponse build() {
        return  new GetProductResponse(
                id: id,
                name: name,
                current_price: price
        )
    }
}
