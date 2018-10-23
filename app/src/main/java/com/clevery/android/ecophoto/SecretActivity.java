package com.clevery.android.ecophoto;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;

public class SecretActivity extends AppCompatActivity {
    EditText edit1, edit2, edit3, edit4;
    String secret = "0000";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                overridePendingTransition(0, R.anim.fade_out);
            }
        });
        Button btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkCode();
            }
        });
        Button btn_clear = (Button)findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit1.setText("");edit2.setText("");edit3.setText("");edit4.setText("");
                edit1.requestFocus();
            }
        });
        edit1 = (EditText)findViewById(R.id.edit1);
        edit2 = (EditText)findViewById(R.id.edit2);
        edit3 = (EditText)findViewById(R.id.edit3);
        edit4 = (EditText)findViewById(R.id.edit4);

        edit1.setFocusable(true);

        edit1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edit1.getText().toString().length()==1)     //size as per your requirement
                {
                    edit2.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edit2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edit2.getText().toString().length()==1)     //size as per your requirement
                {
                    edit3.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edit3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edit3.getText().toString().length()==1)     //size as per your requirement
                {
                    edit4.requestFocus();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        edit4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edit4.getText().toString().length()==1)     //size as per your requirement
                {
                    checkCode();
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void checkCode() {
        App.hideKeyboard(SecretActivity.this);
        if (edit1.getText().toString().length()*edit2.getText().toString().length()*edit3.getText().toString().length()*edit4.getText().toString().length() == 0) {
            new AlertDialog.Builder(SecretActivity.this)
                    .setTitle("Warning")
                    .setMessage("Please complete your secret code!")
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return;
        }
        secret = edit1.getText().toString() + edit2.getText().toString() + edit3.getText().toString() + edit4.getText().toString();
        checkSecretRequest();
    }
    ProgressDialog mDialog;
    private void checkSecretRequest() {
        mDialog = new ProgressDialog(SecretActivity.this);
        mDialog.setMessage("Checking secrets...");
        mDialog.setCancelable(false);
        mDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "check_secret")
                .addParam("secret", secret)
                .addParam("id", App.readPreference(App.MY_ID, ""))
                .sendRequest(callback);
    }

    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            mDialog.hide();
            switch (code) {
                case 200:
                    App.secret_checked = true;
//                    Toast.makeText(SecretActivity.this, element.getErrorData(), Toast.LENGTH_SHORT).show();
                    SecretActivity.this.finish();
                    break;
                case 500:
                    new AlertDialog.Builder(SecretActivity.this)
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
                    new AlertDialog.Builder(SecretActivity.this)
                            .setTitle("Error")
                            .setMessage(element.getErrorData())
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
            }
        }

    };
}
