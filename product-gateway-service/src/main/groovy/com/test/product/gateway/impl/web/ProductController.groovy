package com.test.product.gateway.impl.web
import com.test.product.gateway.api.GetProductRequest
import com.test.product.gateway.api.GetProductResponse
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import com.test.product.gateway.impl.service.ProductService
import groovy.transform.CompileStatic
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

import javax.inject.Inject
import javax.validation.Valid

@CompileStatic
@RestController
@RequestMapping("/products")
class ProductController {

    private final ProductService productService

    @Inject
    ProductController(ProductService productService) {
        this.productService = productService
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.GET, produces = ['application/json'])
    GetProductResponse getProduct(GetProductRequest request){
        productService.getProduct(request)
    }

    @RequestMapping(value = '/{id}', method = RequestMethod.PUT, produces = ['application/json'])
    UpdateProductPriceResponse updateProductPrice(@PathVariable Long id, @Valid @RequestBody UpdateProductPriceRequest request) {
        productService.updateProductPrice(id, request)
    }

}
