package com.uii.academico.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.uii.academico.FCM.FcmInstanceIDListenerService;
import com.uii.academico.R;
import com.uii.academico.Utility.Utils;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT",false)){
            finish();

        }else {

            setContentView(R.layout.activity_splash_screen);

            Utils.init(this);

            Thread timerThread = new Thread() {
                public void run() {
                    try {
                        // 3000 = 3 detik
                        sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {

                        if (Utils.sharedPreferences.contains("id_user")) {

                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }


                    }
                }
            };

            timerThread.start();

        }

    }





}
