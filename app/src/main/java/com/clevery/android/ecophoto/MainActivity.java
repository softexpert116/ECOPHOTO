package com.clevery.android.ecophoto;

import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.ecophoto.Fragments.HistoryFragment;
import com.clevery.android.ecophoto.Fragments.PhotoFragment;
import com.clevery.android.ecophoto.Fragments.ProfileFragment;
import com.clevery.android.ecophoto.Fragments.WaitingFragment;
import com.clevery.android.ecophoto.Service.PingService;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction transaction;
    RelativeLayout tab_photo, tab_waiting, tab_history, tab_profile;
    ImageView img_camera, img_wait, img_history, img_profile;
    TextView txt_photo, txt_wait, txt_history, txt_profile;
    int index_cur;
    String color_sel = "#293c9c";
    static ImageView img_online;
    TextView txt_title;
    public boolean check_secret = false;
    public static boolean online = false;
    // PHOTO fragment variables
    public String frag_student_id, frag_school_code, frag_classroom;
    public Uri frag_selUri;
    public Uri[] frag_imgUri = new Uri[3];
    public int frag_photo_index = 0;
    public static MainActivity mainActivity;
    //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        img_online = (ImageView)findViewById(R.id.img_online);
        img_camera = (ImageView)findViewById(R.id.img_photo);
        img_wait = (ImageView)findViewById(R.id.img_wait);
        img_history = (ImageView)findViewById(R.id.img_history);
        img_profile = (ImageView)findViewById(R.id.img_profile);
        txt_title = (TextView)findViewById(R.id.txt_title);
        txt_photo = (TextView)findViewById(R.id.txt_photo);
        txt_wait = (TextView)findViewById(R.id.txt_wait);
        txt_history = (TextView)findViewById(R.id.txt_history);
        txt_profile = (TextView)findViewById(R.id.txt_profile);

        tab_photo = (RelativeLayout) findViewById(R.id.ly_photo);
        tab_waiting = (RelativeLayout) findViewById(R.id.ly_waiting);
        tab_history = (RelativeLayout) findViewById(R.id.ly_history);
        tab_profile = (RelativeLayout) findViewById(R.id.ly_profile);

        Button btn_photo = (Button)findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_activity(0);
            }
        });
        Button btn_waiting = (Button)findViewById(R.id.btn_wait);
        btn_waiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_activity(1);
            }
        });
        Button btn_history = (Button)findViewById(R.id.btn_history);
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_activity(2);
            }
        });
        Button btn_profile = (Button)findViewById(R.id.btn_profile);
        btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh_activity(3);
            }
        });
        refresh_activity(0);
        App.startMyService(PingService.class);
    }
    static public void setNetStatus(boolean flag) {
        online = flag;
        if (flag) {
            Glide.with(mainActivity)
                    .asGif()
                    .load(R.drawable.online).apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(img_online);
        } else {
//            Glide.with(mainActivity).load(R.drawable.offline)
//                    .apply(new RequestOptions()
//                            .placeholder(R.drawable.offline).centerCrop().dontAnimate()).into(img_online);
            img_online.setImageResource(R.drawable.offline);
        }
    }
    public void refresh_activity(int index) {
        index_cur = index;
        tab_photo.setBackgroundColor(Color.parseColor("#00000000"));
        tab_waiting.setBackgroundColor(Color.parseColor("#00000000"));
        tab_history.setBackgroundColor(Color.parseColor("#00000000"));
        tab_profile.setBackgroundColor(Color.parseColor("#00000000"));
        img_camera.setImageDrawable(getDrawable(R.drawable.ic_camera_gray));
        img_wait.setImageDrawable(getDrawable(R.drawable.ic_waiting_gray));
        img_history.setImageDrawable(getDrawable(R.drawable.ic_history_gray));
        img_profile.setImageDrawable(getDrawable(R.drawable.ic_profile_gray));
        txt_photo.setTextColor(Color.parseColor("#aaaaaa"));
        txt_wait.setTextColor(Color.parseColor("#aaaaaa"));
        txt_history.setTextColor(Color.parseColor("#aaaaaa"));
        txt_profile.setTextColor(Color.parseColor("#aaaaaa"));

        switch (index) {
            case 0:
                selectFragment(new PhotoFragment());
                tab_photo.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                img_camera.setImageDrawable(getDrawable(R.drawable.ic_camera_white));
                txt_photo.setTextColor(Color.parseColor("#ffffff"));
                txt_title.setText("Take Photo");
                break;
            case 1:
                selectFragment(new WaitingFragment());
                tab_waiting.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                img_wait.setImageDrawable(getDrawable(R.drawable.ic_waiting_white));
                txt_wait.setTextColor(Color.parseColor("#ffffff"));
                txt_title.setText("Waiting");
                break;
            case 2:
                selectFragment(new HistoryFragment());
                tab_history.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                img_history.setImageDrawable(getDrawable(R.drawable.ic_history_white));
                txt_history.setTextColor(Color.parseColor("#ffffff"));
                txt_title.setText("History");
                break;
            case 3:
                selectFragment(new ProfileFragment());
                tab_profile.setBackgroundColor(getColor(R.color.colorPrimaryDark));
                img_profile.setImageDrawable(getDrawable(R.drawable.ic_profile_white));
                txt_profile.setTextColor(Color.parseColor("#ffffff"));
                txt_title.setText("My Profile");
                break;
            default: break;
        }
    }
    private void selectFragment(Fragment fragment) {
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to finish app?");
        builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                App.stopMyService(PingService.class);
                ActivityCompat.finishAffinity(MainActivity.this);
                System.exit(0);
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
