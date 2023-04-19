package com.hadaba.retailxcafe;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

public class SplashScreenActivity extends Activity {
    AppPreference appPreference;
    String flag,logout;
    private static final int SPLASH_DURATION = 2000; // 2 seconds
    MyApp globalVariable;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        appPreference = ObjectFactory.getInstance(SplashScreenActivity.this).getAppPreference();
        globalVariable = (MyApp) getApplicationContext();
        flag = appPreference.getInitially();
        logout=appPreference.getLogout("yes");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if(logout.equals("yes"))
            {
                Intent intent = new Intent(SplashScreenActivity.this,
                        LoginActivity.class);
                finish();
               startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(SplashScreenActivity.this, TableActivity.class);
                finish();
                startActivity(intent);
            }
        }, SPLASH_DURATION);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}
