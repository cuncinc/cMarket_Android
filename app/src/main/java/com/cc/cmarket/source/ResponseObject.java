package com.cc.cmarket.source;

import java.io.Serializable;

public class ResponseObject<T> implements Serializable
{

    private static final long serialVersionUID = 1111013L;

    private String code;//状态码
    private String message; //响应消息
    private T data; //响应的数据，视情况决定

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    @Override
    public String toString()
    {
        return "ResponeObject{" + "code='" + code + '\'' + ", message='" + message + '\'' + ", data=" + data + '}';
    }
}
