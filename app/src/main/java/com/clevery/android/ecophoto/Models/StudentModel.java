package com.clevery.android.ecophoto.Models;

import java.io.Serializable;

/**
 * Created by Acer on 01/12/2016.
 */

public class StudentModel implements Serializable {
    public int _id;
    public String student_id;
    public String school_code;
    public String classroom;
    public String photo;
    public String date;
    public String type;

    public StudentModel(int _id, String student_id, String school_code, String classroom, String photo, String date, String type)
    {
        this._id = _id;
        this.student_id = student_id;
        this.school_code = school_code;
        this.classroom = classroom;
        this.photo = photo;
        this.date = date;
        this.type = type;
    }
    public StudentModel()
    {
        this._id = 0;
        this.student_id = "";
        this.school_code = "";
        this.classroom = "";
        this.photo = "";
        this.date = "";
        this.type = "";
    }
}
