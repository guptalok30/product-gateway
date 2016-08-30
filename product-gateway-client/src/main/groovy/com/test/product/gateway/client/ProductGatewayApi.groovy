package com.test.product.gateway.client

import com.test.product.gateway.api.GetProductResponse
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import retrofit.http.Body
import retrofit.http.GET
import retrofit.http.Headers
import retrofit.http.PUT
import retrofit.http.Path

interface ProductGatewayApi {

    @Headers(['Accept: application/json'])
    @GET('/products/{id}')
    GetProductResponse getProduct(@Path('id') Long id)

    @Headers(['Accept: application/json'])
    @PUT('/products/{id}')
    UpdateProductPriceResponse updateProductPrice(@Path('id') Long id, @Body UpdateProductPriceRequest request)
}