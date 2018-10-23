package com.clevery.android.ecophoto;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;
import com.hbb20.CountryCodePicker;

public class LoginActivity extends AppCompatActivity {
    String country_code, number, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final CountryCodePicker txt_countryCode = (CountryCodePicker)findViewById(R.id.txt_countryCode);
        final EditText edit_number = (EditText)findViewById(R.id.edit_number);
        final EditText edit_password = (EditText)findViewById(R.id.edit_password);
        Button btn_login = (Button)findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                country_code = txt_countryCode.getSelectedCountryCode();
                number = edit_number.getText().toString();
                password = edit_password.getText().toString();
                if (number.length()*password.length() == 0) {
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Warning")
                            .setMessage("Please fill in all fields")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    return;
                }
                loginRequest();
            }
        });

        if (App.readPreference(App.MY_ID, "").length() > 0) {
            goMainScreen();
        }
    }
    void goMainScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    ProgressDialog mDialog;
    private void loginRequest() {
        mDialog = new ProgressDialog(LoginActivity.this);
        mDialog.setMessage("Log in...");
        mDialog.setCancelable(false);
        mDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "login")
                .addParam("country_code", country_code)
                .addParam("number", number)
                .addParam("password", password)
                .sendRequest(callback);
    }

    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            mDialog.hide();
            switch (code) {
                case 200:
                    String id = element.getData("id");
                    App.setPreference(App.MY_ID, id);
                    goMainScreen();
                    break;
                case 400:
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error")
                            .setMessage("Phone number or password is invalid!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
                case 500:
                    new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("Error")
                            .setMessage("Can't connect to server!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
                default:
                    break;
            }
        }

    };
}
