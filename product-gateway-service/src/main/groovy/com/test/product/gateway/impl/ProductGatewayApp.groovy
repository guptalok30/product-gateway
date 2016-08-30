package com.test.product.gateway.impl

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.squareup.okhttp.OkHttpClient
import com.test.product.gateway.impl.external.productapi.ProductApi
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import retrofit.RestAdapter
import retrofit.client.OkClient
import retrofit.converter.Converter
import retrofit.converter.JacksonConverter

import javax.validation.Validator
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@CompileStatic
@ComponentScan(basePackages = ['com.target.product.gateway'])
@SpringBootApplication
class ProductGatewayApp {
    @Value('${productApi.productApiUri}')
    String productApiUri

    static void main(final String[] args) {
        SpringApplication.run(ProductGatewayApp, args)
    }

    @Bean
    ObjectMapper objectMapper() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        return new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(new JodaModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, true)
                .configure(JsonParser.Feature.ALLOW_COMMENTS, false)
                .configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true)
                .configure(SerializationFeature.INDENT_OUTPUT, false)
                .setDateFormat(dateFormat)
    }

    @Bean
    ProductApi productApi() {
        OkHttpClient okHttpClient = new OkHttpClient()
        okHttpClient.setConnectTimeout(30L, TimeUnit.SECONDS)
        okHttpClient.setWriteTimeout(30L, TimeUnit.SECONDS)
        okHttpClient.setReadTimeout(30L, TimeUnit.SECONDS)

        Converter converter = new JacksonConverter(objectMapper())
        OkClient clientProvider = new OkClient(okHttpClient)
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setClient(clientProvider)
                .setConverter(converter)
                .setEndpoint(productApiUri)
        RestAdapter restAdapter = builder.build()
        return restAdapter.create(ProductApi)
    }

    @Bean
    Validator validator() {
        new LocalValidatorFactoryBean()
    }
}
