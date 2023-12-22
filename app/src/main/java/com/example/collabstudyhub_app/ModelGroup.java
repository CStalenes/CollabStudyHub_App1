package com.example.collabstudyhub_app;

public class ModelGroup {

    private String name, level, field, admin;

    public ModelGroup() {
    }

    public ModelGroup(String admin, String field,String level,String name) {
        this.name = name;
        this.level = level;
        this.field = field;
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
