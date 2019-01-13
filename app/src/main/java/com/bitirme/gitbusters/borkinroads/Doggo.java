package com.bitirme.gitbusters.borkinroads;

import java.util.Date;

public class Doggo {
    private String name;
    private String breed;
    private Date birth_date;
    private Date last_walk_date;
    private enum gender{male,female};
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

    public Date getBirth_date() {
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public Date getLast_walk_date() {
        return last_walk_date;
    }

    public void setLast_walk_date(Date last_walk_date) {
        this.last_walk_date = last_walk_date;
    }

}
