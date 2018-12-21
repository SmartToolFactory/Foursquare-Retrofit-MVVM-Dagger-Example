package com.test.foursquaresingle.application;

import com.test.foursquaresingle.di.AppComponent;
import com.test.foursquaresingle.di.DaggerAppComponent;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class MyApplication extends DaggerApplication {


    @Override
    public void onCreate() {
        super.onCreate();

        // AppInjector.init(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        AppComponent appComponent = DaggerAppComponent.builder().application(this).build();
        return appComponent;
    }

}