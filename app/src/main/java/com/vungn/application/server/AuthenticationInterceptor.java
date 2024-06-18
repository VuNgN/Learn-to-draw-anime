package com.vungn.application.server;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {
    private final String authToken;

    public AuthenticationInterceptor(String token) {
        this.authToken = token;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .header("Authorization", authToken)
                .header("X-APP-VERSION", ServiceGenerator.VERSION_NAME)
                .header("X-APP-ID", ServiceGenerator.APPLICATION_ID);
        Request request = builder.build();
        return chain.proceed(request);
    }
}