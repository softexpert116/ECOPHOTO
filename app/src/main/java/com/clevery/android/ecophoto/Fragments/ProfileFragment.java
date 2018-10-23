package com.clevery.android.ecophoto.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.ecophoto.App;
import com.clevery.android.ecophoto.LoginActivity;
import com.clevery.android.ecophoto.MainActivity;
import com.clevery.android.ecophoto.R;
import com.clevery.android.ecophoto.Service.PingService;
import com.clevery.android.ecophoto.Utils.Utils;
import com.clevery.android.ecophoto.httpModule.RequestBuilder;
import com.clevery.android.ecophoto.httpModule.ResponseElement;
import com.clevery.android.ecophoto.httpModule.RunanbleCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    MainActivity activity;
    CircleImageView img_photo;
    Uri imgUri;
    EditText edit_name, edit_email, edit_phone;
    String encodedImage = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        App.hideKeyboard(activity);
        img_photo = (CircleImageView)v.findViewById(R.id.img_photo);
        edit_email = (EditText)v.findViewById(R.id.edit_email);
        edit_name = (EditText)v.findViewById(R.id.edit_name);
        edit_phone = (EditText)v.findViewById(R.id.edit_phone);
        Button btn_photo = (Button)v.findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(activity, ProfileFragment.this);
            }
        });

        Button btn_save = (Button)v.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (encodedImage == null) {
                    new AlertDialog.Builder(activity)
                            .setTitle("Warning")
                            .setMessage("Please update your profile photo!")
                            .setCancelable(false)
                            .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                    return;
                }
                updateProfileRequest();
            }
        });
        Button btn_logout = (Button)v.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.setPreference(App.MY_ID, "");
                App.stopMyService(PingService.class);
                Intent intent = new Intent(activity, LoginActivity.class);
                startActivity(intent);
                activity.finish();
            }
        });
        readProfileRequest();
        return v;
    }
    ProgressDialog mDialog;
    private void readProfileRequest() {
        mDialog = new ProgressDialog(activity);
        mDialog.setMessage("Loading profile...");
        mDialog.setCancelable(false);
        mDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "get_profile")
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
                    Glide.with(activity).load(element.getData("photo"))
                            .apply(new RequestOptions()
                                    .placeholder(R.drawable.person).centerCrop().dontAnimate()).into(img_photo);
                    edit_name.setText(element.getData("name"));
                    edit_email.setText(element.getData("email"));
                    edit_phone.setText("+" + element.getData("country_code") + " " + element.getData("number"));
                    break;
                case 400:
                    new AlertDialog.Builder(activity)
                            .setTitle("Error")
                            .setMessage("Profile is invalid!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    private void updateProfileRequest() {
        mDialog = new ProgressDialog(activity);
        mDialog.setMessage("Updating profile...");
        mDialog.setCancelable(false);
        mDialog.show();
        RequestBuilder requestBuilder = new RequestBuilder(App.serverUrl);
        requestBuilder
                .addParam("type", "update_profile")
                .addParam("photo", encodedImage)
                .addParam("id", App.readPreference(App.MY_ID, ""))
                .sendRequest(callback1);
    }

    RunanbleCallback callback1 = new RunanbleCallback() {
        @Override
        public void finish(ResponseElement element) {
            int code = element.getStatusCode();
            mDialog.hide();
            switch (code) {
                case 200:
                    Toast.makeText(activity, "Successfully updated!", Toast.LENGTH_SHORT).show();
                    break;
                case 400:
                    new AlertDialog.Builder(activity)
                            .setTitle("Error")
                            .setMessage("Profile is invalid!")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imgUri = result.getUri();
                Glide.with(this).load(result.getUri())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.person).centerCrop().dontAnimate()).into(img_photo);
                Bitmap selectedImage = null;
                try {
                    final InputStream imageStream = activity.getContentResolver().openInputStream(imgUri);
                    selectedImage = BitmapFactory.decodeStream(imageStream);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                encodedImage = Utils.encodeToBase64(selectedImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(activity, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }
    }
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
