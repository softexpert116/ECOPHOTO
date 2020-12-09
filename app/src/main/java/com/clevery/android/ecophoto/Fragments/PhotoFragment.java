package com.clevery.android.ecophoto.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.ecophoto.App;
import com.clevery.android.ecophoto.MainActivity;
import com.clevery.android.ecophoto.Models.StudentModel;
import com.clevery.android.ecophoto.R;
import com.clevery.android.ecophoto.Utils.Utils;
import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class PhotoFragment extends Fragment {
    MainActivity activity;
    EditText edit_student_id, edit_school_code, edit_classroom;
    ImageView sel_photo;
    ImageView[] img_photo = new ImageView[3];
    Button[] btn_photo = new Button[3];
    RadioGroup radioGroup;
    private ProgressDialog mDialog;
    String student_id, school_code, classroom, type;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_photo, container, false);
        App.hideKeyboard(activity);
        edit_student_id = (EditText)v.findViewById(R.id.edit_student_id);
        edit_school_code = (EditText)v.findViewById(R.id.edit_school_code);
        edit_classroom = (EditText)v.findViewById(R.id.edit_classroom);
        sel_photo = (ImageView)v.findViewById(R.id.img_photo);
        img_photo[0] = (ImageView)v.findViewById(R.id.img_photo1);
        img_photo[1] = (ImageView)v.findViewById(R.id.img_photo2);
        img_photo[2] = (ImageView)v.findViewById(R.id.img_photo3);
        btn_photo[0] = (Button)v.findViewById(R.id.btn_photo1);
        btn_photo[1] = (Button)v.findViewById(R.id.btn_photo2);
        btn_photo[2] = (Button)v.findViewById(R.id.btn_photo3);

        edit_student_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activity.frag_student_id = edit_student_id.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_school_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activity.frag_school_code = edit_school_code.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_classroom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                activity.frag_classroom = edit_classroom.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        for (int i = 0; i < 3; i++) {
            final int j = i;
            img_photo[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectPhoto(j);
                }
            });
        }
        for (int i = 0; i < 3; i++) {
            final int j = i;
            btn_photo[j].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(activity, PhotoFragment.this);
                }
            });
        }
        radioGroup = (RadioGroup)v.findViewById(R.id.radio_group);
        RadioButton radio_free = (RadioButton) v.findViewById(R.id.radio_card);
        RadioButton radio_paid = (RadioButton) v.findViewById(R.id.radio_normal);
        initPhotoType();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton rb= (RadioButton) v.findViewById(checkedId);
                activity.frag_photo_type = rb.getText().toString();
            }
        });
        Button btn_save = (Button)v.findViewById(R.id.btn_save);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                student_id = edit_student_id.getText().toString().trim().toLowerCase();
                school_code = edit_school_code.getText().toString().trim().toLowerCase();
                classroom = edit_classroom.getText().toString().trim().toLowerCase();
                type = activity.frag_photo_type;
                if (student_id.length()*school_code.length()*classroom.length() == 0) {
                    Toast.makeText(activity, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (activity.frag_selUri == null) {
                    Toast.makeText(activity, "Please capture Image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // check school code on backend
                RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
                requestBuilder
                        .addParam("type", "check_info")
                        .addParam("student_id", student_id)
                        .addParam("school_code", school_code)
                        .sendRequest(callback);
                mDialog = new ProgressDialog(activity);
                mDialog.setTitle("Wait");
                mDialog.setMessage("Checking school code and student ID...");
                mDialog.setCancelable(false);
                mDialog.show();
                // --------------------------


            }
        });
        return v;
    }
    RunanbleCallback callback = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            mDialog.dismiss();
            int code = element.getStatusCode();
            switch (code) {
                case 200:
                    Bitmap selectedImage = null;
                    try {
                        final InputStream imageStream = activity.getContentResolver().openInputStream(activity.frag_selUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    String encodedImage = Utils.encodeToBase64(selectedImage);
                    StudentModel model = new StudentModel(0, student_id, school_code, classroom, encodedImage, "", type);
                    ArrayList<StudentModel> arrayList = App.readPreference_waiting_array();
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    for (int i = 0; i < arrayList.size(); i++) {
                        StudentModel model1 = arrayList.get(i);
                        if (model1.student_id.equals(student_id)) {

                            Toast.makeText(activity, "This student's photo already exist! If you want to change, please remove in waiting list.", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    arrayList.add(model);
                    App.setPreference_waiting_array(arrayList);
                    Toast.makeText(activity, "Successfully saved!", Toast.LENGTH_SHORT).show();
                    // initialize -------------------
                    edit_student_id.setText("");
                    edit_school_code.setText("");
                    edit_classroom.setText("");
                    activity.frag_student_id = ""; activity.frag_classroom = ""; activity.frag_school_code = "";
                    activity.frag_selUri = null;
                    activity.frag_photo_type = "ID Card";
                    for (int i = 0; i < 3; i++) {
                        activity.frag_imgUri[i] = null;
                        setPersonPhoto(activity.frag_imgUri[i], img_photo[i]);
                    }
                    setPersonPhoto(activity.frag_selUri, sel_photo);
                    break;
                case 301:
                    new AlertDialog.Builder(activity)
                            .setTitle("Warning")
                            .setMessage("Invalid school code!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
                case 300:
                    new AlertDialog.Builder(activity)
                            .setTitle("Warning")
                            .setMessage("Invalid student ID!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
                case 400:
                    new AlertDialog.Builder(activity)
                            .setTitle("Error")
                            .setMessage("Server error!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    break;
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

    private void initPhotoType() {
        if (activity.frag_photo_type.equals("ID Card")) {
            radioGroup.check(R.id.radio_card);
        } else {
            radioGroup.check(R.id.radio_normal);
        }
    }
    private void deselectPhotos() {
        for (int i = 0; i < 3; i++) {
            final int j = i;
            img_photo[j].setBackgroundColor(Color.parseColor("#00000000"));
            btn_photo[j].setVisibility(View.GONE);
        }
    }
    private void selectPhoto(int j) {
        activity.frag_photo_index = j;
        deselectPhotos();
        img_photo[j].setBackgroundDrawable(activity.getDrawable(R.drawable.frame_circle_border));
        btn_photo[j].setVisibility(View.VISIBLE);
        activity.frag_selUri = activity.frag_imgUri[j];
        setPersonPhoto(activity.frag_selUri, sel_photo);
    }
    private void setPersonPhoto(Uri uri, ImageView imageView) {
        Glide.with(this).load(uri)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.person).centerCrop().dontAnimate()).into(imageView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                activity.frag_selUri = result.getUri();
                activity.frag_imgUri[activity.frag_photo_index] = activity.frag_selUri;
                setPersonPhoto(activity.frag_selUri, img_photo[activity.frag_photo_index]);
                setPersonPhoto(activity.frag_selUri, sel_photo);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(activity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public void onResume() {
        selectPhoto(activity.frag_photo_index);
        edit_student_id.setText(activity.frag_student_id);
        edit_school_code.setText(activity.frag_school_code);
        edit_classroom.setText(activity.frag_classroom);
        initPhotoType();
        setPersonPhoto(activity.frag_selUri, sel_photo);
        for (int i = 0; i < 3; i++) {
            setPersonPhoto(activity.frag_imgUri[i], img_photo[i]);
        }
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
