package com.example.kjh.shakeit.beforelogin.callback;

public interface Callback {
    void onSuccess(String body);
    void onFailure(String errorMsg);
}