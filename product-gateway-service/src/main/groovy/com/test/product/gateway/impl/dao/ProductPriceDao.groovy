package com.test.product.gateway.impl.dao

import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Row
import com.datastax.driver.core.Statement
import com.datastax.driver.core.querybuilder.Insert
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.querybuilder.Select
import com.fasterxml.jackson.databind.ObjectMapper
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import com.test.product.gateway.api.domain.Price
import com.test.product.gateway.impl.config.cassandra.CassandraTemplate
import com.test.product.gateway.impl.exception.PersistenceException
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.joda.time.DateTimeZone
import org.joda.time.LocalDateTime
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import javax.inject.Inject

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq

@CompileStatic
@Slf4j
@Component
class ProductPriceDao {

    static final Logger LOG = LoggerFactory.getLogger(ProductPriceDao)
    static final String PRODUCT_KEYSPACE = "product"
    static final String PRODUCT_PRICE_TABLE = "product_price"
    static final String PRODUCT_ID = "pid"
    static final String PRODUCT_PRICE = "price"
    static final String CURRENCY = "currency"
    static final String LAST_UPDATE_TIME = "update_time"

    ObjectMapper objectMapper
    CassandraTemplate cassandraTemplate

    @Inject
    ProductPriceDao(ObjectMapper objectMapper, CassandraTemplate cassandraTemplate) {
        this.objectMapper = objectMapper
        this.cassandraTemplate = cassandraTemplate
    }

    Price getProductPrice(Long pid) {
        LOG.info("fetching product price: ${pid}")
        Select select = QueryBuilder.select()
                .all()
                .from(PRODUCT_KEYSPACE, PRODUCT_PRICE_TABLE)

        select.where(eq(PRODUCT_ID, pid))
        select.setConsistencyLevel(cassandraTemplate.getDefaultQueryConsistency())

        ResultSet rs = handleExecuteStatement(select, pid)

        return transformResultSet(rs)

    }

    UpdateProductPriceResponse saveProductPrice(UpdateProductPriceRequest request) {
        LOG.info('saving product price: {}', request.id)

        LocalDateTime currentTime = LocalDateTime.now(DateTimeZone.UTC)

        Insert insert = QueryBuilder
                .insertInto(PRODUCT_KEYSPACE, PRODUCT_PRICE_TABLE)
                .value(PRODUCT_ID, request.id)
                .value(PRODUCT_PRICE, request.current_price.value.toString())
                .value(CURRENCY, request.current_price.currency_code)
                .value(LAST_UPDATE_TIME, toUtcDate(currentTime))
        insert.setConsistencyLevel(cassandraTemplate.getDefaultUpdateConsistency())
        //using timestamp for upsert
        insert.using(QueryBuilder.timestamp(toMicros(currentTime)))

        handleExecuteStatement(insert, request.id)

        UpdateProductPriceResponse response = objectMapper.convertValue(request, UpdateProductPriceResponse)
        response.setUpdate_time(currentTime)
        return response
    }

    Price transformResultSet(ResultSet rs) {

        Price price = new Price()

        rs.iterator().each { Row row ->
            price.value = new BigDecimal(row.getString(PRODUCT_PRICE))
            price.currency_code = row.getString(CURRENCY)
        }
        return price
    }

    ResultSet handleExecuteStatement(Statement statement, Long entityId) {
        try {
            ResultSetFuture future = cassandraTemplate.executeAsync(statement)
            return future.get()
        } catch (Exception ex) {
            LOG.error("An exception occurred while processing request for a statement with " +
                    "product id:{} \n Exception Type:{} \n Exception Message:{}",
                    entityId, ex.getClass(), ex.getMessage())
            throw new PersistenceException("Exception caught while executing cassandra statements")

        }
    }

    static final Date toUtcDate(LocalDateTime localDateTime) {
        return localDateTime.toDateTime(DateTimeZone.UTC).toDate()
    }

    static final long toMicros(LocalDateTime localDateTime) {
        return toUtcDate(localDateTime).time * 1000
    }
}
