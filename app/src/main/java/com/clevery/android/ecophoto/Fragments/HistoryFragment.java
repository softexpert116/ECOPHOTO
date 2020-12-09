package com.clevery.android.ecophoto.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.clevery.android.ecophoto.Adapter.HistoryListAdapter;
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

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    MainActivity activity;
    HistoryListAdapter historyListAdapter;
    ArrayList<StudentModel> arrayList = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, container, false);
        ListView listView = (ListView) v.findViewById(R.id.list_history);
        swipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.ly_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRequest();
//                swipeRefreshLayout.setRefreshing(false);
            }
        });

        historyListAdapter = new HistoryListAdapter(activity, arrayList);
        listView.setAdapter(historyListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(activity, "Student ID: xxx\nSchool Code: yyy\nClassroom: zzz", Toast.LENGTH_SHORT).show();
            }
        });
        getRequest();
//        for (int i = 0; i < 50; i++) {
//            StudentModel model = new StudentModel(i, "0000" + String.valueOf(i), "school1", "classroom1", "", "03/21/2018");
//            arrayList.add(model);
//        }
        historyListAdapter.notifyDataSetChanged();
        return v;
    }
    private void initArray(JSONArray array) {
        arrayList.clear();
        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                int id = object.getInt("id");
                String student_id = object.getString("student_id");
                String school_code = object.getString("school_code");
                String classroom = object.getString("classroom");
                String photo = object.getString("photo");
                String date = object.getString("date");
                String type = object.getString("type");
                StudentModel model = new StudentModel(id, student_id, school_code, classroom, photo, date, type);
                arrayList.add(model);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        if (arrayList.size() == 0) {
            swipeRefreshLayout.setVisibility(View.INVISIBLE);
        }
        historyListAdapter.notifyDataSetChanged();
    }
    private void getRequest() {
        swipeRefreshLayout.setRefreshing(true);
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "get_students")
                .addParam("user_id", App.readPreference(App.MY_ID, ""))
                .sendRequest(callback);
    }

    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            swipeRefreshLayout.setRefreshing(false);
            switch (code) {
                case 200:
                    initArray(element.getArray("data"));
                    break;
                case 400:
//                    break;
                case 500:
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
    @Override
    public void onResume() {
        super.onResume();
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
