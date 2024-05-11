package org.example;

import java.io.Serializable;

public class PersonelData implements Serializable {
    private String name;
    private String username;
    private String dateOfBirth;
    public PersonelData(String name, String username,String dateOfBirth) {
        this.name = name;
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
    public String getName(){
        return name;
    }
}
