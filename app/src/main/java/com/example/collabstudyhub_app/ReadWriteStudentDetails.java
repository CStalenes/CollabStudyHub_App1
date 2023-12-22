package com.example.collabstudyhub_app;

import android.net.Uri;

public class ReadWriteStudentDetails {

    public String studentID,studentname,studentemail,studentphone,studentniveau,studentfiliere,studentetablissement,uriImageStudent;

    public ReadWriteStudentDetails(String studentID, String studentname, String studentemail, String studentphone, String studentniveau, String studentfiliere, String studentetablissement, String uriImageStudent) {
        this.studentID = studentID;
        this.studentname = studentname;
        this.studentemail = studentemail;
        this.studentphone = studentphone;
        this.studentniveau = studentniveau;
        this.studentfiliere = studentfiliere;
        this.studentetablissement = studentetablissement;
        this.uriImageStudent = uriImageStudent;
    }

    public ReadWriteStudentDetails(String uriImageStudent) {
        this.uriImageStudent = uriImageStudent;
    }

    public ReadWriteStudentDetails() {
    }
}
