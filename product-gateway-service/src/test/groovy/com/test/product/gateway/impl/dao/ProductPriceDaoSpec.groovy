package com.test.product.gateway.impl.dao
import com.datastax.driver.core.ResultSet
import com.datastax.driver.core.ResultSetFuture
import com.datastax.driver.core.Row
import com.fasterxml.jackson.databind.ObjectMapper
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.UpdateProductPriceResponse
import com.test.product.gateway.api.domain.Price
import com.test.product.gateway.impl.config.cassandra.CassandraTemplate
import org.spockframework.runtime.GroovyRuntimeUtil
import spock.lang.Specification

class ProductPriceDaoSpec extends Specification {

    ProductPriceDao dao
    ObjectMapper objectMapper
    CassandraTemplate cassandraTemplate
    ResultSetFuture future
    ResultSet rs
    Row row1
    Row row2

    void setup() {
        objectMapper = Mock(ObjectMapper)
        cassandraTemplate = Mock(CassandraTemplate)
        dao = new ProductPriceDao(objectMapper, cassandraTemplate)
        future = Mock()
        rs = Mock(ResultSet)
        row1 = Mock(Row)
        row2 = Mock(Row)
   }


    void 'should save product price'(){
        given:
        Long id = 1234L
        UpdateProductPriceRequest request = new UpdateProductPriceRequest(
                id: id,
                name: 'My Test product',
                current_price: new Price(value: 12.00, currency_code: 'USD')
        )
        when:
        dao.saveProductPrice(request)

        then:
        1 * cassandraTemplate.executeAsync(_) >> future
        1 * future.get() >> rs
        1 * cassandraTemplate.getDefaultUpdateConsistency()
        1 * objectMapper.convertValue(request, UpdateProductPriceResponse) >> new UpdateProductPriceResponse()
        0 * _
    }

    void 'fetch product price'() {
        given:
        Long id = 1234L
        Iterator<Row> iterator = GroovyRuntimeUtil.asIterator([row1])

        when:
        dao.getProductPrice(id)

        then:
        1 * cassandraTemplate.executeAsync(_) >> future
        1 * future.get() >> rs
        1 * rs.iterator() >> iterator
        interaction {
            1 * row1.getString(ProductPriceDao.PRODUCT_PRICE) >> '12.34'
            1 * row1.getString(ProductPriceDao.CURRENCY) >> 'USD'
        }
        1 * cassandraTemplate.getDefaultQueryConsistency()
        0 * _

    }
}
