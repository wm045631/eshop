package com.roncoo.eshop.cache.pojo;

public class ApiResponse<T> {

    private Integer status = 200;
    private String message = "success";
    private T data;

    public ApiResponse() {
        super();
    }

    public ApiResponse(Integer status, String message) {
        super();
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
