
package com.example.tbmanager;
public class Patients {
    String fullname,gender,age,residence,next_of_kin,contact;


    public Patients() {
    }

    public Patients(String fullname, String gender, String age, String residence, String next_of_kin, String contact) {
        this.fullname = fullname;
        this.gender = gender;
        this.age = age;
        this.residence = residence;
        this.next_of_kin = next_of_kin;
        this.contact = contact;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getResidence() {
        return residence;
    }

    public void setResidence(String residence) {
        this.residence = residence;
    }

    public String getNext_of_kin() {
        return next_of_kin;
    }

    public void setNext_of_kin(String next_of_kin) {
        this.next_of_kin = next_of_kin;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}
