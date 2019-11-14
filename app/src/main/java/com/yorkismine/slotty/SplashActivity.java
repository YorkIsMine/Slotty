package com.yorkismine.slotty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.WindowManager;

import com.yorkismine.slotty.utils.Constants;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

import static com.yorkismine.slotty.utils.Constants.CASINO_URL;

public class SplashActivity extends AppCompatActivity {

    private ArrayList<Response> responses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(CASINO_URL)
                .build();
        final Response response = null;
        responses.add(response);


        new Thread(){
            @Override
            public void run() {
                try {
                    responses.set(0, client.newCall(request).execute());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        int i = 0;
            while (Thread.currentThread().isAlive()){
                if (i == 2) break;
                if (responses.get(0) != null) break;
                try {
                    Thread.sleep(1000);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        final Response copyOfResponse = responses.get(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent welcomeIntent = null;
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if (manager.getSimState() == TelephonyManager.SIM_STATE_ABSENT ||
                            manager.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN ||
                            copyOfResponse == null) {
                        welcomeIntent = new Intent(SplashActivity.this, MainActivity.class);
                    } else if (copyOfResponse.isSuccessful()){
                        welcomeIntent = new Intent(SplashActivity.this, CasinoWebActivity.class);
                    }


                    startActivity(welcomeIntent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    finish();
            }
        }, Constants.WELCOME_TIMEOUT);
    }

}
