package com.pregrad.aprilandroid.Model;

public class Students {
    String id, name, Class, age, roll_No;

    public Students(String id, String name, String Class, String age, String roll_No) {
        this.id = id;
        this.name = name;
        this.Class = Class;
        this.age = age;
        this.roll_No = roll_No;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassData() {
        return Class;
    }

    public void setClass(String aClass) {
        Class = aClass;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getRoll_No() {
        return roll_No;
    }

    public void setRoll_No(String roll_No) {
        this.roll_No = roll_No;
    }
}
