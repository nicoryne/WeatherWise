package com.example.weatherwise.api;

public interface DataCallback<T> {

    void onCallback(T data);
    void onError(Throwable t);
}
