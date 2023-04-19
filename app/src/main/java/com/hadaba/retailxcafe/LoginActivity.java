package com.hadaba.retailxcafe;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hadaba.retailxcafe.module.LoginResponse;
import com.hadaba.retailxcafe.retrofit.ApiClient;
import com.hadaba.retailxcafe.retrofit.ApiInterface;
import com.hadaba.retailxcafe.utils.AppPreference;
import com.hadaba.retailxcafe.utils.ObjectFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import info.hoang8f.android.segmented.SegmentedGroup;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText edtusername;
    @SuppressLint("StaticFieldLeak")
    public static EditText edtpassword;
    @SuppressLint("StaticFieldLeak")
    public static Button login;
    public static String username_ = null;
    public static String password_ = null;
    @SuppressLint("StaticFieldLeak")
    public static ProgressBar mProgressIndicator1;
    @SuppressLint("StaticFieldLeak")
    public static TextView txtdownloading;
    String username, password, flag;
    MyApp globalVariable;
    public String uid;
    CheckBox check_show_password;
    FloatingActionButton settings_login;
    AppPreference appPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mProgressIndicator1 = findViewById(R.id.progressBar1);
            appPreference = ObjectFactory.getInstance(LoginActivity.this).getAppPreference();

            globalVariable = (MyApp) getApplicationContext();
            edtusername = findViewById(R.id.edtusername);
            edtusername.setHintTextColor(getResources().getColor(R.color.white));
            txtdownloading = findViewById(R.id.dwdtxt);

            edtpassword = findViewById(R.id.edtpassword);
            edtpassword.setHintTextColor(getResources().getColor(R.color.white));
            login = findViewById(R.id.button1);
            check_show_password = findViewById(R.id.check_show_password);
            settings_login = findViewById(R.id.settings_login);

            username = appPreference.getUserName(globalVariable.getUser_id());
            password = appPreference.getPassword();
            flag = appPreference.getInitially();
            edtusername.setText(username);


//            edtusername.setText("admin");
//            edtpassword.setText("123");


            checkAndRequestPermissions();

            settings_login.setOnClickListener(v -> {
                MaterialDialog dialogs = new MaterialDialog.Builder(LoginActivity.this)
                        .title("Settings")
                        .customView(R.layout.setting_dialog,true)
                        .backgroundColorRes(R.color.white)
                        .titleColorRes(R.color.black)
                        .contentColorRes(R.color.black)
                        .canceledOnTouchOutside(false)
                        .build();
                dialogs.show();
                Button btn_sub = (Button) dialogs.findViewById(R.id.submit_btn);
                Button cancel_btn = (Button) dialogs.findViewById(R.id.cancel_btn);
                SegmentedGroup orientation_segments = (SegmentedGroup) dialogs.findViewById(R.id.orientation_segments);
                orientation_segments.setTintColor(Color.DKGRAY);

                EditText edt_tabname = (EditText) dialogs.findViewById(R.id.edit_tabname);
                EditText edt_url = (EditText) dialogs.findViewById(R.id.edit_url);
                edt_url.setText(appPreference.getUrl());//"hadabaoffice.dyndns.tv:98"
                edt_tabname.setText(appPreference.getTab_name());//tab1
                btn_sub.setOnClickListener(v1 -> {
                    appPreference.setTab_name(edt_tabname.getText().toString());
                    appPreference.setUrl(edt_url.getText().toString());
                    dialogs.dismiss();

                });
                cancel_btn.setOnClickListener(v1 -> dialogs.dismiss());

                orientation_segments.setOnCheckedChangeListener((radioGroup, i) -> {
                    switch (i) {
                        case R.id.landscape:
                       //     setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            appPreference.setOrientation("LANDSCAPE");
                            break;
                        case R.id.portrait:
//                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            appPreference.setOrientation("PORTRAIT");
                            break;
                        default:
                            // Nothing to do
                    }
                });


            });
            check_show_password.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    // show password
                    edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    // hide password
                    edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            });

            login.setOnClickListener(v -> {

                if (!checkAndRequestPermissions()) //  only allow all permission sucess
                    return;

               String url = appPreference.getUrl();
               String tab_name = appPreference.getTab_name();
               if(url.equals("") || tab_name.equals("") )
               {
                   MaterialDialog dialogs = new MaterialDialog.Builder(LoginActivity.this)
                           .title("Settings")
                           .content(" Please enter public url and tab name before login")
                           .positiveText("OK")
                           .backgroundColorRes(R.color.white)
                           .titleColorRes(R.color.black)
                           .contentColorRes(R.color.black)
                           .canceledOnTouchOutside(false)
                           .onPositive((dialog, which) -> dialog.dismiss())
                           .build();
                   dialogs.show();
                   return;

               }

                if (!isConnectingToInternet()) {

                    Toast.makeText(LoginActivity.this, "Internet Connection not exist", Toast.LENGTH_SHORT)
                            .show();
                    return;
                }
                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    assert imm != null;
                    imm.hideSoftInputFromWindow(Objects.requireNonNull(getCurrentFocus()).getWindowToken(),
                            0);
                } catch (Exception ignored) {

                }


                login.setEnabled(false);
                edtpassword.setEnabled(false);
                edtusername.setEnabled(false);

                if (edtusername.getText().toString().equals("") || edtpassword.getText().toString().equals("")) {
                        Toast.makeText(LoginActivity.this, "Please enter the credentials", Toast.LENGTH_SHORT).show();

                    login.setEnabled(true);
                    edtusername.setEnabled(true);
                    edtpassword.setEnabled(true);

                } else {
                    try {
                        uid = getUniqueID();
                    } catch (Exception e) {
                        uid = "0";
                        e.printStackTrace();
                    }
                    username_ = edtusername.getText().toString().trim();
                    password_ = edtpassword.getText().toString().trim();
                    if (uid == null)
                        uid = "0";

                    postLogin();

                }

            });
        } catch (Exception e) {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setMessage("Error Message : " + e.getLocalizedMessage() + ". Please contact application team.").setTitle("Error Found").setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }

    private static final int PERMISSION_ALL = 2;

    private boolean checkAndRequestPermissions() {

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);
        }
        if (ActivityCompat.checkSelfPermission(LoginActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(LoginActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
            return false;
        }
        return true;
    }

    private ProgressDialog dialog;
    AlertDialog shows;

    private void postLogin() {

        dialog = new ProgressDialog(LoginActivity.this,
                ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        ApiInterface apiService = ApiClient.getClient(getApplicationContext()).create(ApiInterface.class);

        Call<LoginResponse> call;
        JSONObject jsonObj_ = new JSONObject();
        try {
            jsonObj_.put("userid", username_);
            jsonObj_.put("password", password_);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObject gsonObject;
        JsonParser jsonParser = new JsonParser();
        gsonObject = (JsonObject) jsonParser.parse(jsonObj_.toString());

        call = apiService.postLogin(gsonObject);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                login.setEnabled(true);
                edtusername.setEnabled(true);
                edtpassword.setEnabled(true);

                if (response.isSuccessful()) {
                    if (response.body().getResult() != null) {
                        if(response.body().getResult().equals("success")) {
                            appPreference.setUserName(username_);
                            appPreference.setPassword(password_);
                            appPreference.setEmployee_id(response.body().getEmployee_id());
                            appPreference.setInitially("done");
                            appPreference.setLogout("no");

                            Intent i = new Intent(LoginActivity.this, TableActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), "Login Failed ,Please check UserName and Password", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        showAlertDialog(LoginActivity.this, "Alert", "Empty Response");
                    }
                } else {
                    Toast.makeText(getApplicationContext(), response.message(), Toast.LENGTH_LONG).show();
                }
                dialog.cancel();

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                dialog.cancel();
                login.setEnabled(true);
                edtusername.setEnabled(true);
                edtpassword.setEnabled(true);
            }
        });

    }


    @SuppressLint({"MissingPermission", "HardwareIds"})
    public String getUniqueID() {
        String myAndroidDeviceId;
        TelephonyManager mTelephony = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        assert mTelephony != null;
        if (mTelephony.getDeviceId() != null) {
            myAndroidDeviceId = mTelephony.getDeviceId();
        } else {
            myAndroidDeviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return myAndroidDeviceId;

    }

    public boolean isConnectingToInternet() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }

        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();

    }


    public void showAlertDialog(Context context, String title, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        // Set Dialog Title
        alertDialog.setTitle(title);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);

        // Set OK Button
        alertDialog.setButton("OK", (dialog, which) -> {
            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);

    }

}
