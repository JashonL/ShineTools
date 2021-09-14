package com.growatt.shinetools.base;

public interface BaseView {

    void startView();

    void loadingView();

    void successView(String json);

    void errorView(String errorMsg);
}
