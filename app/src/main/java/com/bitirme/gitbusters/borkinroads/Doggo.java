package com.bitirme.gitbusters.borkinroads;

import java.time.ZonedDateTime;

public class Doggo {
    private String name;
    private String breed;
    private ZonedDateTime birth_date;
    private ZonedDateTime last_walk_date;

    Doggo(String name, String breed, ZonedDateTime birth_date, ZonedDateTime last_walk_date, gender sex) {
        this.name = name;
        this.breed = breed;
        this.birth_date = birth_date;
        this.last_walk_date = last_walk_date;
        this.sex = sex;
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

    protected enum gender {Male, Female}

}
