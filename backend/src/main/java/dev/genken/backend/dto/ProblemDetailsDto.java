package dev.genken.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;


// RFC 7807
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemDetailsDto {
    private String type;
    private String title;
    private int status;
    private String detail;
    private String instance;
    private Map<String, String> errors;

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    public String getDetail() { return detail; }

    public void setDetail(String detail) { this.detail = detail; }

    public String getInstance() { return instance; }

    public void setInstance(String instance) { this.instance = instance; }

    public Map<String, String> getErrors() { return errors; }

    public void setErrors(Map<String, String> errors) { this.errors = errors; }

    public ProblemDetailsDto(String title, int status, String detail, String instance) {
        setTitle(title);
        setStatus(status);
        setDetail(detail);
        setInstance(instance);
    }

    public ProblemDetailsDto(String title, int status, String detail, String instance,  Map<String, String> errors) {
        setTitle(title);
        setStatus(status);
        setDetail(detail);
        setInstance(instance);
        setErrors(errors);
    }

    public ProblemDetailsDto(String type, String title, int status, String detail, String instance, Map<String, String> errors) {
        setType(type);
        setTitle(title);
        setStatus(status);
        setDetail(detail);
        setInstance(instance);
        setErrors(errors);
    }
}
