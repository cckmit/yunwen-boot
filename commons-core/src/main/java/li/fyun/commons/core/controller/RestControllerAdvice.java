package li.fyun.commons.core.controller;

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import li.fyun.commons.core.utils.DateEditor;
import li.fyun.commons.core.utils.ResponseWrapper;
import li.fyun.commons.core.utils.YunwenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.util.NestedServletException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Order
@ControllerAdvice
@Slf4j
public class RestControllerAdvice extends ResponseEntityExceptionHandler {

    @Value("${app.group-name:'li.fyun'}")
    private String appGroupName;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(false));
        binder.registerCustomEditor(Date.class, new DateEditor(true));
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ObjectNotFoundException.class)
    public ResponseEntity<Object> handleObjectNotFoundException(
            ObjectNotFoundException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.NOT_FOUND, "没有找到对象", request);

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.NOT_FOUND, "数据不存在", request);

    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = FileNotFoundException.class)
    public ResponseEntity<Object> handleOFileNotFoundException(
            FileNotFoundException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.NOT_FOUND, "没有找到文件", request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR, "数据约束性异常", request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = SecurityException.class)
    public ResponseEntity<Object> handleSecurityException(
            SecurityException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.BAD_REQUEST, request);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = ValidationException.class)
    public ResponseEntity<Object> handleValidationException(
            ValidationException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.BAD_REQUEST, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(
            IllegalStateException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = IOException.class)
    public ResponseEntity<Object> handleIOException(
            IOException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(
            NullPointerException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = NestedServletException.class)
    public ResponseEntity<Object> handleNestedServletException(
            NestedServletException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = InvalidDefinitionException.class)
    public ResponseEntity<Object> handleInvalidDefinitionException(
            InvalidDefinitionException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = TransactionSystemException.class)
    public ResponseEntity<Object> handleTransactionSystemException(
            TransactionSystemException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR, "保存数据失败", request);
    }

    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.PAYLOAD_TOO_LARGE, "上传文件超出了限制大小", request);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = YunwenException.class)
    public ResponseEntity<Object> handleYunwenException(YunwenException ex, WebRequest request, HttpServletRequest httpServletRequest) {
        return handleErrorInfo(ex, httpServletRequest.getRequestURI(),
                HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);

    }

    protected ResponseEntity<Object> handleErrorInfo(Exception ex, String path, HttpStatus status, WebRequest request) {
        this.logTrace(ex);
        ResponseWrapper responseWrapper = ResponseWrapper.error(ex, status.value(), path);
        return handleExceptionInternal(ex, responseWrapper, new HttpHeaders(), HttpStatus.valueOf(responseWrapper.getStatus()), request);
    }

    protected ResponseEntity<Object> handleErrorInfo(Exception ex, String path, HttpStatus status, String message, WebRequest request) {
        this.logTrace(ex);
        ResponseWrapper responseWrapper = ResponseWrapper.error(ex, status.value(), message, path);
        return handleExceptionInternal(ex, responseWrapper, new HttpHeaders(), HttpStatus.valueOf(responseWrapper.getStatus()), request);
    }

    private void logTrace(Exception ex) {
        if (ex instanceof SecurityException) {
            logger.warn(ex.getMessage());
        } else {
            StackTraceElement[] traceElements = ex.getStackTrace();
            StringBuffer traceMessage = new StringBuffer();
            for (StackTraceElement traceElement : traceElements) {
                if (traceElement.getClassName().startsWith(appGroupName)) {
                    traceMessage.append("\t - ");
                    traceMessage.append(traceElement.toString());
                    traceMessage.append("\n");
                }
            }
            log.error("{}\n{}", ex.toString(), traceMessage.toString());
        }
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        BindingResult result = ex.getBindingResult();
        final List<FieldError> fieldErrors = result.getFieldErrors();
        StringBuffer sb = new StringBuffer();
        if (CollectionUtils.isNotEmpty(fieldErrors)) {
            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getField());
                sb.append(": ");
                sb.append(fieldError.getDefaultMessage());
                sb.append(";");
            }
        }
        String uri = request.getDescription(false)
                .replace("uri=", "")
                .replace(request.getContextPath(), "");
        return handleErrorInfo(ex, uri, status, sb.toString(), request);
    }

}