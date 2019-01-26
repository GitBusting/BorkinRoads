package com.bitirme.gitbusters.borkinroads;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

public class Doggo {
    static final ArrayList<Doggo> doggos = new ArrayList<>();
    private String name;
    private String breed;
    private ZonedDateTime birth_date;
    private ZonedDateTime last_walk_date;
    private ZonedDateTime last_bath_date;
    private ZonedDateTime last_vet_date;


    Doggo(String name, String breed, ZonedDateTime birth_date, gender sex) {
        this.name = name;
        this.breed = breed;
        this.birth_date = birth_date;
        this.sex = sex;
        this.last_walk_date = ZonedDateTime.now(ZoneId.systemDefault());
        this.last_bath_date = ZonedDateTime.now(ZoneId.systemDefault());
        this.last_vet_date = ZonedDateTime.now(ZoneId.systemDefault());
    }

    ZonedDateTime getBirth_date() {
        return birth_date;
    }

    private gender sex;

    public void setBirth_date(ZonedDateTime birth_date) {
        this.birth_date = birth_date;
    }

    public void setSex(gender sex) {
        this.sex = sex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    gender getSex() {
        return sex;
    }

    public ZonedDateTime getLast_walk_date() {
        return last_walk_date;
    }

    public void setLast_walk_date(ZonedDateTime last_walk_date) {
        this.last_walk_date = last_walk_date;
    }

    protected enum gender {Male, Female, Gender}

    public ZonedDateTime getLast_bath_date() {
        return last_bath_date;
    }

    public void setLast_bath_date(ZonedDateTime last_bath_date) {
        this.last_bath_date = last_bath_date;
    }

    public ZonedDateTime getLast_vet_date() {
        return last_vet_date;
    }

    public void setLast_vet_date(ZonedDateTime last_vet_date) {
        this.last_vet_date = last_vet_date;
    }


}
