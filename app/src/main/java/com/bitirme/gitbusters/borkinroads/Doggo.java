package com.bitirme.gitbusters.borkinroads;

import java.util.GregorianCalendar;

public class Doggo {
    private String name;
    private String breed;
    private GregorianCalendar birth_date;
    private GregorianCalendar last_walk_date;

    public Doggo(String name, String breed, GregorianCalendar birth_date, GregorianCalendar last_walk_date, gender sex) {
        this.name = name;
        this.breed = breed;
        this.birth_date = birth_date;
        this.last_walk_date = last_walk_date;
        this.sex = sex;
    }

    public GregorianCalendar getBirth_date() {
        return birth_date;
    }

    private gender sex;

    public gender getSex() {
        return sex;
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

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public void setBirth_date(GregorianCalendar birth_date) {
        this.birth_date = birth_date;
    }

    public GregorianCalendar getLast_walk_date() {
        return last_walk_date;
    }

    public void setLast_walk_date(GregorianCalendar last_walk_date) {
        this.last_walk_date = last_walk_date;
    }

    protected enum gender {male, female}

}
