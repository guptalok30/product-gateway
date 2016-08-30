package com.test.product.gateway.impl.config

import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Slf4jReporter
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter
import groovy.util.logging.Slf4j
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

import java.util.concurrent.TimeUnit

@Configuration
@ConditionalOnClass(MetricRegistry)
@EnableMetrics(proxyTargetClass = true)
@EnableConfigurationProperties
@Slf4j
class MetricsConfig extends MetricsConfigurerAdapter {

    @Override
    void configureReporters(MetricRegistry metricRegistry) {
        Slf4jReporter.forRegistry(metricRegistry)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
                .start(60, TimeUnit.SECONDS)
    }
}
