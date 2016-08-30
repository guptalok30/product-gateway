package com.test.product.gateway.impl.service

import com.test.product.gateway.api.GetProductRequest
import com.test.product.gateway.api.GetProductResponse
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import com.test.product.gateway.api.domain.Price
import com.test.product.gateway.impl.dao.ProductPriceDao
import com.test.product.gateway.impl.external.productapi.ProductApi
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.inject.Inject

@CompileStatic
@Service
class ProductService {
    @Value('${productApi.fields}')
    String fields
    @Value('${productApi.apiKey}')
    String apiKey
    @Value('${productApi.idType}')
    String idType

    HashMap<String, String> queryParams = [:]
    private ProductPriceDao productPriceDao
    private EntityValidator entityValidator
    private ProductApi productApi

    @Inject
    ProductService(ProductPriceDao productPriceDao, EntityValidator entityValidator, ProductApi productApi) {
        this.productPriceDao = productPriceDao
        this.entityValidator = entityValidator
        this.productApi = productApi
    }

    GetProductResponse getProduct(GetProductRequest request) {
        entityValidator.validate(request)
        return new GetProductResponse(
                name: getProductDescription(request.id),
                id: request.id,
                current_price: getProductPrice(request.id))
    }

    private String getProductDescription(Long id) {
        HashMap<String, String> queryParams = [:]
        queryParams.put('fields', fields)
        queryParams.put('id_type', idType)
        queryParams.put('key', apiKey)

        //Using map for simplicity ..typically we have a strongly typed domain api provided by the client lib
        Map<String, Object> response = productApi.getProductDetails(id, queryParams)
        List items = (List) ((Map) response.get('product_composite_response')).get('items')

        return ((Map) items.get(0)).get('general_description') ?: 'Product description not found'
    }


    private Price getProductPrice(Long id) {
        productPriceDao.getProductPrice(id)
    }

    UpdateProductPriceResponse updateProductPrice(Long id, UpdateProductPriceRequest request) {
        entityValidator.validate(request)
        if (id != request.id) {
            throw new IllegalArgumentException('Product id is not valid')
        }
        //verify if the product id is valid. Not sure if the provided endpoint is meant for that.
        productPriceDao.saveProductPrice(request)
    }

}
