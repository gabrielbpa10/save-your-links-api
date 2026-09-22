package com.save.link.api.util.response;

public class CustomResponse {
    public Object object;
    public Integer status;
    public String message;

    public CustomResponse(Object object, Integer status, String message) {
        this.object = object;
        this.status = status;
        this.message = message;
    }
}
