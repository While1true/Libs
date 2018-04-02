package com.master.rxlib.Rx;

/**
 * Created by 不听话的好孩子 on 2018/4/2.
 */

public class Base<T> {
    int code;
    String message;
    T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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

    public void setData(T date) {
        this.data = date;
    }
}
