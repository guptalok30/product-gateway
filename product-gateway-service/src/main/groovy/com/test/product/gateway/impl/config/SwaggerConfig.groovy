package com.test.product.gateway.impl.config
import com.mangofactory.swagger.configuration.SpringSwaggerConfig
import com.mangofactory.swagger.models.dto.ApiInfo
import com.mangofactory.swagger.plugin.EnableSwagger
import com.mangofactory.swagger.plugin.SwaggerSpringMvcPlugin
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.LocalDateTime
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

import javax.inject.Inject

@Slf4j
@CompileStatic
@Configuration
@EnableSwagger
class SwaggerConfig {

    private static final String TITLE = 'Product Gateway Service'
    private static final String DESC = 'API to manage product information.'
    private static final String[] INCLUDE = ['^/products*.*$']

    private SpringSwaggerConfig springSwaggerConfig

    @Inject
    void setSpringSwaggerConfig(SpringSwaggerConfig springSwaggerConfig) {
        this.springSwaggerConfig = springSwaggerConfig
    }

    @Bean
    SwaggerSpringMvcPlugin customImplementation() {
        return new SwaggerSpringMvcPlugin(this.springSwaggerConfig)
                .apiInfo(apiInfo())
                .includePatterns(INCLUDE)
                .ignoredParameterTypes(LocalDateTime)
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(TITLE, DESC, null, null, null, null)
    }
}

