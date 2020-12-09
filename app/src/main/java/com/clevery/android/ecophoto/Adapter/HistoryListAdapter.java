package com.clevery.android.ecophoto.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.clevery.android.ecophoto.Models.StudentModel;
import com.clevery.android.ecophoto.R;
import com.clevery.android.ecophoto.Utils.Utils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Acer on 01/12/2016.
 */

public class HistoryListAdapter extends BaseAdapter
{
    ArrayList<StudentModel> arrayList;
    Context context;

    HistoryListAdapter() {
        arrayList = null;
        context = null;
    }
    public HistoryListAdapter(Context _context, ArrayList<StudentModel> _arrayList) {
        arrayList = _arrayList;
        context = _context;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        StudentModel model = arrayList.get(i);
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.cell_history, null);
        }
        ImageView img_person = view.findViewById(R.id.img_person);
        TextView txt_studentID = view.findViewById(R.id.txt_studentID);
        TextView txt_schoolCode = view.findViewById(R.id.txt_schoolCode);
        TextView txt_classroom = view.findViewById(R.id.txt_classroom);
        TextView txt_date = view.findViewById(R.id.txt_date);
        TextView txt_type = view.findViewById(R.id.txt_type);
        Glide.with(context).load(Utils.decodeBase64(model.photo))
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.person).centerCrop().dontAnimate()).into(img_person);
        txt_studentID.setText(model.student_id);
        txt_schoolCode.setText(model.school_code);
        txt_classroom.setText(model.classroom);
        txt_date.setText(model.date);
        txt_type.setText(model.type);
        return view;
    }

}
