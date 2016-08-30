package com.test.product.gateway.impl.web

import com.test.product.gateway.api.common.ErrorResponse
import com.test.product.gateway.impl.exception.EntityNotFoundException
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import retrofit.RetrofitError
import retrofit.mime.TypedInput

import javax.servlet.ServletException
import javax.validation.ConstraintViolationException

@CompileStatic
@ControllerAdvice
@Slf4j
class ErrorHandler {
    @ExceptionHandler(Exception)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ErrorResponse handle(Exception e) {
        log.error('Exception: {}', e.message, e)
        return new ErrorResponse(message: e.message)
    }

    @ExceptionHandler(RetrofitError)
    @ResponseBody
    ResponseEntity<ErrorResponse> handle(RetrofitError re) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR
        StringWriter sw = new StringWriter()
        sw << "Unexpected exception from retrofit endpoint: ${re.url ?: re.response?.url}"
        if (re.response) {
            status = HttpStatus.valueOf(re.response.status)
            sw << " [${re.response.status}], reason: ${re.response.reason}"
            TypedInput body = re.response.body
            if (body) {
                String cr = ''
                sw << ", body: "
                body.in()?.eachLine { line ->
                    sw << "${line}${cr}"
                    cr = '\n'
                }
            }
        }
        log.error sw.toString()

        return new ResponseEntity<ErrorResponse>(new ErrorResponse(message: sw.toString()), status)
    }

    @ExceptionHandler(IllegalArgumentException)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    ErrorResponse handle(IllegalArgumentException e) {
        String message = e.message
        return new ErrorResponse(message: message)
    }

    /** Requested entity could not be found
     * Returns a 404 for a missing root entity
     * Returns a 422 for a missing child entity */
    @ExceptionHandler(EntityNotFoundException)
    @ResponseBody
    ResponseEntity<ErrorResponse> handle(EntityNotFoundException e) {
        HttpStatus status = HttpStatus.NOT_FOUND
        return new ResponseEntity<ErrorResponse>(new ErrorResponse(message: e.message), status)
    }

    /** This happens when a null is returned to any of the controllers */
    @ExceptionHandler(ServletException)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    ErrorResponse handle(ServletException e) {
        log.warn('Null result error: {}', e.message)
        return new ErrorResponse(message: e.message)
    }

    @ExceptionHandler(ConstraintViolationException)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    ErrorResponse handle(ConstraintViolationException e) {
        return new ErrorResponse(
                message: 'Error saving entity'
        )
    }

}
