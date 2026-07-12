package org.example.parnasservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ProblemDetails {

    private String type;
    private String title;
    private int status;
    private String code;
    private String detail;
    private String instance;
    private String requestId;
    private Instant timestamp;
    private List<FieldViolation> violations;
    private boolean retryable;
    private Integer retryAfterSeconds;

    public ProblemDetails(int status, String code, String title, String detail, String instance, String requestId) {
        this.type = "https://api.parnas.example/problems/" + code.toLowerCase().replace("_", "-");
        this.title = title;
        this.status = status;
        this.code = code;
        this.detail = detail;
        this.instance = instance;
        this.requestId = requestId;
        this.timestamp = Instant.now();
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldViolation {
        private String field;
        private String message;
        private Object rejectedValue;
    }
}
