package li.fyun.commons.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper implements Serializable {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private long timestamp;
    private int status;
    private String error;
    private String exception;
    private String message;
    private String path;
    private Object data;

    private ResponseWrapper(Throwable ex, int status, String message, String path) {
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            httpStatus = HttpStatus.valueOf(status);
        } catch (Exception e) {
            //
        }
        this.setTimestamp(System.currentTimeMillis());
        this.setStatus(status);
        this.setError(httpStatus.getReasonPhrase());
        this.setException(ex.getClass().getName());
        this.setMessage(message);
        this.setPath(path);
    }

    public static ResponseWrapper success(Object data, String path) {
        ResponseWrapper wrapper = new ResponseWrapper();
        wrapper.setTimestamp(System.currentTimeMillis());
        wrapper.setStatus(HttpStatus.OK.value());
        wrapper.setMessage("SUCCESS");
        wrapper.setPath(path);
        wrapper.setData(data);
        return wrapper;
    }

    public static ResponseWrapper error(Throwable ex, int status, String path) {
        return new ResponseWrapper(ex, status, ex.getMessage(), path);
    }

    public static ResponseWrapper error(Throwable ex, int status, String message, String path) {
        return new ResponseWrapper(ex, status, message, path);
    }

    public void print(HttpServletResponse response) throws IOException {
        log.warn(this.getMessage());
        String json = OBJECT_MAPPER.writeValueAsString(this);
        response.setStatus(this.getStatus());
        response.setContentType("application/json;charset=" + Charsets.UTF_8.name());
        response.setContentLength(json.getBytes().length);
        PrintWriter out = response.getWriter();
        out.write(json);
        out.flush();
        out.close();
    }

}
