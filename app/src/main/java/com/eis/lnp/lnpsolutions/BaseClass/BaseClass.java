package com.eis.lnp.lnpsolutions.BaseClass;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by HP on 12/8/2016.
 */

public class BaseClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration configuration=new RealmConfiguration.Builder(this)
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
