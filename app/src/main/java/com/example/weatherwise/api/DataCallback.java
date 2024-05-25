package com.example.weatherwise.api;

public interface DataCallback<T> {

    void onSuccess(T data);
    void onFailure(Throwable t);
}
