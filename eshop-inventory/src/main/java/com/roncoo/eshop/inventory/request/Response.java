package com.roncoo.eshop.inventory.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response {
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final Integer SUCCESS_CODE = 200;
    private String status;
    private String message;
    private Integer code;

    public Response(String status, Integer code) {
        this.status = status;
        this.message = "";
        this.code = code;
    }

    public Response(String status, String message, Integer code) {
        this.status = status;
        this.message = message;
        this.code = code;
    }
}
