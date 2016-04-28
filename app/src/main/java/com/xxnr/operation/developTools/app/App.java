package com.xxnr.operation.developTools.app;

import com.xxnr.operation.RndApplication;


public class App {
    private static RndApplication app;

    public static void setApp(RndApplication application) {
        app = application;
    }

    public static RndApplication getApp() {
        return app;
    }


}
