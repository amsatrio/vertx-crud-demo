package io.github.amsatrio.vertx_crud_demo.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.jackson.DatabindCodec;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Response<T> implements Serializable {
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    public Date getTimestamp() {
        return Optional.ofNullable(this.timestamp).orElse(null);
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = (timestamp != null)
                ? new Date(timestamp.getTime())
                : null;
    }

    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private StackTraceElement[] trace;

    public StackTraceElement[] getTrace() {
        return trace != null
                ? Arrays.copyOf(trace, trace.length)
                : null;
    }

    public void setTrace(StackTraceElement[] originalTrace) {
        if (originalTrace != null) {
            // Limit stack trace to prevent potential information leakage
            this.trace = Arrays.copyOfRange(
                    originalTrace,
                    0,
                    Math.min(originalTrace.length, 5));
        } else {
            this.trace = null;
        }
    }

    public Buffer toBuffer() {
        ObjectMapper objectMapper = DatabindCodec.mapper().registerModule(new JavaTimeModule());
        try {
            byte[] byteArray = objectMapper.writeValueAsBytes(this);
            return Buffer.buffer(byteArray);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize response to Buffer", e);
        }
    }

    public String toString() {
        ObjectMapper objectMapper = DatabindCodec.mapper().registerModule(new JavaTimeModule());
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize response to String", e);
        }
    }

    // === CUSTOM

    public static <T> Response<T> ok(T data) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(200);
        response.setData(data);
        response.setMessage("success");
        return response;
    }

    public static <T> Response<T> success(int status, String path, T data) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setData(data);
        response.setMessage("success");
        response.setPath(path);
        return response;
    }

    public static <T> Response<T> success(int status, String path) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setMessage("success");
        response.setPath(path);
        return response;
    }

    public static <T> Response<T> error(int status, String message, StackTraceElement[] errorTrace) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setTrace(errorTrace);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> error(int status, String message, String path) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setPath(path);
        response.setMessage(message);
        return response;
    }

    public static <T> Response<T> error(int status, String message, T data, StackTraceElement[] trace) {
        Response<T> response = new Response<>();
        response.setTimestamp(new Date());
        response.setStatus(status);
        response.setTrace(trace);
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
