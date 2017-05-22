package com.example.locationservices.starter;

import android.app.Application;

import com.example.locationservices.R;

public class WochApp extends Application {

  private static WochApp instance;
  private static final String TAG = "Starter";

  @Override
  public void onCreate() {
    super.onCreate();

    instance = this;
  }

  public static WochApp getInstance(){
    return instance;
  }
}
