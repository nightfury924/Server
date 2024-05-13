package com.example.demo;

import java.io.Serializable;

public class PersonelData implements Serializable {
    private String username;
    private String dateOfBirth;
    public PersonelData(String username,String dateOfBirth) {
        this.username = username;
        this.dateOfBirth = dateOfBirth;
    }
    public PersonelData(){

    }
    // public void setUsername(String  username){this.username=username;}
    public void setdateOfBirth(String  dateOfBirth){this.dateOfBirth=dateOfBirth;}
    public String getUsername( ) { return username ;}
    public String getDateOfBirth() {
        return dateOfBirth;
    }
}