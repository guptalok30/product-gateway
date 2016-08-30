package com.test.product.gateway.impl.web

import com.test.product.gateway.api.GetProductRequest
import com.test.product.gateway.api.GetProductResponse
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import com.test.product.gateway.api.domain.Price
import com.test.product.gateway.impl.service.ProductService
import org.joda.time.LocalDateTime
import spock.lang.Specification

class ProductControllerSpec extends Specification {
    ProductController controller
    ProductService productService

    void setup() {
        productService = Mock(ProductService)
        controller = new ProductController(productService)
    }

    void 'get product info by product id'() {
        given:
        Long id = 1234L
        GetProductRequest request = new GetProductRequest(id: id)
        GetProductResponse expected = new GetProductResponse(
                id: id,
                name: 'My Test product',
                current_price: new Price(value: 12.00, currency_code: 'USD')
        )

        when:
        GetProductResponse response = controller.getProduct(request)

        then:
        1 * productService.getProduct(request) >> expected
        0 * _
        expected == response
    }


    void 'save product price'() {
        given:
        Long id = 1234L
        UpdateProductPriceRequest request = new UpdateProductPriceRequest(
                id: id,
                name: 'My Test product',
                current_price: new Price(value: 12.00, currency_code: 'USD')
        )
        UpdateProductPriceResponse expected = new UpdateProductPriceResponse (
                id: id,
                name: 'My Test product',
                current_price: new Price(value: 12.00, currency_code: 'USD'),
                update_time: LocalDateTime.now()
        )

        when:
        UpdateProductPriceResponse response = controller.updateProductPrice(id, request)

        then:
        1 * productService.updateProductPrice(id, request) >> expected
        0 * _
        expected == response
    }

}
