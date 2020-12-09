package com.clevery.android.ecophoto.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.clevery.android.ecophoto.Adapter.WaitGridAdapter;
import com.clevery.android.ecophoto.App;
import com.clevery.android.ecophoto.MainActivity;
import com.clevery.android.ecophoto.Models.StudentModel;
import com.clevery.android.ecophoto.R;
import com.clevery.android.ecophoto.SecretActivity;
import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class WaitingFragment extends Fragment {
    MainActivity activity;
    WaitGridAdapter waitGridAdapter;
    ArrayList<StudentModel> arrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    Button btn_clear, btn_send;
    GridView gridView;
    boolean flag_send = false;
    String send_option = "Print";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_waiting, container, false);
        gridView = (GridView)v.findViewById(R.id.gridView);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.ly_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                arrayList = App.readPreference_waiting_array();
                refreshGridView();
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                StudentModel model = arrayList.get(i);
                Toast.makeText(activity, "Type: "+ model.type + "\nStudent ID: "+ model.student_id + "\nSchool Code: " + model.school_code + "\nClassroom: " + model.classroom, Toast.LENGTH_SHORT).show();
            }
        });

        btn_send = (Button)v.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity.online) {
                    openSubmitDialog();
                } else {
                    Toast.makeText(activity, "Couldn't send photos under offline!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_clear = (Button)v.findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Do you want to clear all waiting?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        App.setPreference_waiting_array(new ArrayList());
                        arrayList.clear();
                        refreshGridView();
                        Toast.makeText(activity, "Successfully cleared!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        AsyncTaskRunner runner = new AsyncTaskRunner();
        runner.execute("loading async task");
        return v;
    }
    private void openSubmitDialog() {
        final Dialog dlg = new Dialog(activity);
        Window window = dlg.getWindow();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        final View view = getLayoutInflater().inflate(R.layout.dialog_send_option, null);
        dlg.setContentView(view);
        window.setGravity(Gravity.CENTER);
        RadioGroup type = (RadioGroup)view.findViewById(R.id.type);
        send_option = "Print";
        type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton b = (RadioButton) group.findViewById(checkedId);
                send_option = b.getText().toString();
            }
        });
        Button btn_submit = (Button)view.findViewById(R.id.btn_send);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(activity, SecretActivity.class);
                startActivity(i);
                activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                dlg.dismiss();
            }
        });
        dlg.show();
        dlg.getWindow().setLayout((int)(width*0.95f), ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void refreshGridView() {
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        btn_clear.setEnabled(true);
        btn_send.setEnabled(true);
        btn_send.setBackgroundColor(activity.getColor(R.color.colorPrimaryDark));
        btn_clear.setBackgroundColor(activity.getColor(R.color.colorAccent));
        if (arrayList == null) {
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
            btn_clear.setEnabled(false);
            btn_send.setEnabled(false);
            btn_send.setBackgroundColor(activity.getColor(R.color.colorInactive));
            btn_clear.setBackgroundColor(activity.getColor(R.color.colorInactive));
        } else {
            if (arrayList.size() == 0) {
                swipeRefreshLayout.setVisibility(View.INVISIBLE);
                btn_clear.setEnabled(false);
                btn_send.setEnabled(false);
                btn_send.setBackgroundColor(activity.getColor(R.color.colorInactive));
                btn_clear.setBackgroundColor(activity.getColor(R.color.colorInactive));
            }
        }
        waitGridAdapter = new WaitGridAdapter(activity, arrayList, this);
        gridView.setAdapter(waitGridAdapter);
    }
    private ProgressDialog mDialog;

    private void sendRequest() {
        StudentModel model = new StudentModel();
        if (arrayList.size() > 0) {
            model = arrayList.get(0);
            String date = new SimpleDateFormat(App.DATE_FORMAT).format(Calendar.getInstance().getTime());
            RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
            requestBuilder
                    .addParam("type", "add_student")
                    .addParam("student_id", model.student_id)
                    .addParam("school_code", model.school_code)
                    .addParam("classroom", model.classroom)
                    .addParam("photo", model.photo)
                    .addParam("send_option", send_option)
                    .addParam("photo_type", model.type)
                    .addParam("user_id", App.readPreference(App.MY_ID, ""))
                    .addParam("date", date)
                    .sendRequest(callback);
            arrayList.remove(0);
        } else {
            Toast.makeText(activity, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
            mDialog.dismiss();
        }
    }
    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            switch (code) {
                case 200:
//                    arrayList.clear();
                    refreshGridView();
                    App.setPreference_waiting_array(arrayList);
                    if (flag_send) {
                        sendRequest();
                    } else {
                        Toast.makeText(activity, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 400:
//                    break;
                case 500:
                    mDialog.dismiss();
                    new AlertDialog.Builder(activity)
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

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

        private String resp;

        @Override
        protected String doInBackground(String... params) {
            arrayList.clear();
            arrayList = App.readPreference_waiting_array();
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            refreshGridView();
            swipeRefreshLayout.setRefreshing(false);
        }


        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (App.secret_checked) {
            App.secret_checked = false;
            mDialog = new ProgressDialog(activity);
            mDialog.setTitle("Wait");
            mDialog.setMessage("Uploading photos...");
            mDialog.setCancelable(false);
            mDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    flag_send = false;
                }
            });
            mDialog.show();
            flag_send = true;
            sendRequest();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
