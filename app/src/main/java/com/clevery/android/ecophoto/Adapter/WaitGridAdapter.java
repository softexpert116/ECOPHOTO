package com.clevery.android.ecophoto.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.ecophoto.App;
import com.clevery.android.ecophoto.Fragments.WaitingFragment;
import com.clevery.android.ecophoto.MainActivity;
import com.clevery.android.ecophoto.Models.StudentModel;
import com.clevery.android.ecophoto.R;
import com.clevery.android.ecophoto.Utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Acer on 01/12/2016.
 */

public class WaitGridAdapter extends BaseAdapter
{
    ArrayList<StudentModel> arrayList;
    Context context;
    WaitingFragment fragment;

    WaitGridAdapter() {
        arrayList = null;
        context = null;
    }
    public WaitGridAdapter(Context _context, ArrayList<StudentModel> _arrayList, WaitingFragment _fragment) {
        arrayList = _arrayList;
        context = _context;
        fragment = _fragment;
    }
    @Override
    public int getCount() {

        if (arrayList == null)
            return 0;
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        StudentModel model = arrayList.get(i);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_grid, null);
        }
        Button btn_delete = (Button)view.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Do you want to remove this item?");
                builder.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        arrayList.remove(i);
                        App.setPreference_waiting_array(arrayList);
                        fragment.refreshGridView();
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
        ImageView img_person = view.findViewById(R.id.img_person);
        TextView txt_id = view.findViewById(R.id.txt_id);
        Glide.with(context).load(Utils.decodeBase64(model.photo))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.person).centerCrop().dontAnimate()).into(img_person);
        txt_id.setText(model.student_id);
        return view;
    }

}
