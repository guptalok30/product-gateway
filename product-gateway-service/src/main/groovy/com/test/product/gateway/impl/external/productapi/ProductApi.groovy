package com.test.product.gateway.impl.external.productapi

import retrofit.http.GET
import retrofit.http.Headers
import retrofit.http.Path
import retrofit.http.QueryMap

interface ProductApi {

   //Note: Response type would typically be provided by the dependent library.
   //Using map for simplicity here

    @Headers(['Accept: application/json'])
    @GET('/products/v3/{id}')
    Map<String, Object> getProductDetails(@Path('id') Long id, @QueryMap Map<String, String> params)

}