package com.example.collabstudyhub_app;

public class ModelStudent {

    private String studentID,studentemail,studentetablissement,studentfiliere,studentname,studentniveau,studentphone,uriImageStudent;

    public ModelStudent() {
    }

    public ModelStudent(String studentID, String studentemail, String studentetablissement, String studentfiliere, String studentname, String studentniveau, String studentphone, String uriImageStudent) {
        this.studentID = studentID;
        this.studentemail = studentemail;
        this.studentetablissement = studentetablissement;
        this.studentfiliere = studentfiliere;
        this.studentname = studentname;
        this.studentniveau = studentniveau;
        this.studentphone = studentphone;
        this.uriImageStudent = uriImageStudent;
    }

    public String getUriImageStudent() {
        return uriImageStudent;
    }

    public void setUriImageStudent(String uriImageStudent) {
        this.uriImageStudent = uriImageStudent;
    }

    public String getStudentID() {
        return studentID;
    }

    public String getStudentemail() {
        return studentemail;
    }

    public String getStudentetablissement() {
        return studentetablissement;
    }

    public String getStudentfiliere() {
        return studentfiliere;
    }

    public String getStudentname() {
        return studentname;
    }

    public String getStudentniveau() {
        return studentniveau;
    }

    public String getStudentphone() {
        return studentphone;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setStudentemail(String studentemail) {
        this.studentemail = studentemail;
    }

    public void setStudentetablissement(String studentetablissement) {
        this.studentetablissement = studentetablissement;
    }

    public void setStudentfiliere(String studentfiliere) {
        this.studentfiliere = studentfiliere;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public void setStudentniveau(String studentniveau) {
        this.studentniveau = studentniveau;
    }

    public void setStudentphone(String studentphone) {
        this.studentphone = studentphone;
    }
}
