package com.test.product.gateway.impl.service
import com.test.product.gateway.api.GetProductRequest
import com.test.product.gateway.api.UpdateProductPriceRequest
import com.test.product.gateway.api.domain.Price
import com.test.product.gateway.impl.dao.ProductPriceDao
import com.test.product.gateway.impl.external.productapi.ProductApi
import spock.lang.Specification

class ProductServiceSpec extends Specification {
    ProductPriceDao productPriceDao
    ProductService service
    private EntityValidator entityValidator
    private ProductApi productApi


    void setup() {
        productPriceDao = Mock(ProductPriceDao)
        entityValidator = Mock(EntityValidator)
        productApi = Mock(ProductApi)

        service = new ProductService(productPriceDao, entityValidator, productApi)
    }

    void 'should get product by id'() {
        given:
        Long id = 1234L
        GetProductRequest request = new GetProductRequest(id: id)

        HashMap<String, Object> detailMap = [:]
        detailMap.put('product_composite_response', ['items': [['general_description': 'My test product']]])
        when:
        service.getProduct(request)

        then:
        1 * productPriceDao.getProductPrice(request.id)
        1 * entityValidator.validate(request)
        1 * productApi.getProductDetails(request.id, _) >> detailMap
        0 * _

    }

    void 'should save product price'() {
        given:
        Long id = 1234L
        UpdateProductPriceRequest request = new UpdateProductPriceRequest(
                id: id,
                name: 'My Test product',
                current_price: new Price(value: 12.00, currency_code: 'USD')
        )
        when:
        service.updateProductPrice(id, request)

        then:
        1 * productPriceDao.saveProductPrice(request)
        1 * entityValidator.validate(request)
        0 * _
    }

}
