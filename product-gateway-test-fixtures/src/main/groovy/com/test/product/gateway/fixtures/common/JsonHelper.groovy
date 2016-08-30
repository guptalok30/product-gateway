package com.test.product.gateway.fixtures.common
import com.fasterxml.jackson.databind.ObjectMapper

class JsonHelper {

    static ObjectMapper objectMapper = new ObjectMapper()

    static <T> T fromJson(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz)
    }

    static String toJson(Object object) {
        return objectMapper.writeValueAsString(object)
    }

    static String jsonFromFixture(String fixture) {
        String path = "/fixtures/${fixture}.json"
        return jsonFromResource(path)
    }

    static String jsonFromResource(String resourcePath) {
        InputStream inputStream = this.class.getResourceAsStream(resourcePath)
        if (inputStream) {
            return stripWhiteSpace(inputStream.text)
        }
        throw new FileNotFoundException(resourcePath)
    }

    static String stripWhiteSpace(String str) {
        StringBuffer out = new StringBuffer()
        str.eachLine {
            out << it.replaceFirst(/": /, '":').replaceFirst(/" : /, '":').trim()
        }
        return out.toString()
    }

}

