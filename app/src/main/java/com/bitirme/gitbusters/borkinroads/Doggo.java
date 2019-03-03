package com.bitirme.gitbusters.borkinroads;

import com.bitirme.gitbusters.borkinroads.data.RestRecordImpl;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Doggo extends RestRecordImpl {
    static final ArrayList<Doggo> doggos = new ArrayList<>();
    private String name;
    private String breed;
    private ZonedDateTime birth_date;
    private ZonedDateTime last_walk_date;
    private ZonedDateTime last_bath_date;
    private ZonedDateTime last_vet_date;
    private int entryID;


    public Doggo() {
        super();
    }

    public Doggo(JSONObject jso) {
        super(jso);
        parseRecordFromJSON(jso);
    }

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

    private void parseRecordFromJSON(JSONObject jso) {
        try {
            this.entryID = jso.getInt("id");
            this.name = jso.getString("name");
            this.breed = jso.getString("breed");
            String jsoGender = jso.getString("gender");
            if (jsoGender.equals("female"))
                this.sex = gender.Female;
            else
                this.sex = gender.Male;
            this.birth_date = parseSTRtoZTD(jso.getString("birthDate"));
            this.last_walk_date = parseSTRtoZTD(jso.getString("lastWalkDate"));
            this.last_bath_date = parseSTRtoZTD(jso.getString("lastBathDate"));
            this.last_vet_date = parseSTRtoZTD(jso.getString("lastVetDate"));
        } catch (JSONException jse) {
            jse.printStackTrace();
        }
    }

    private ZonedDateTime parseSTRtoZTD(String date) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return ZonedDateTime.parse(date, formatter);
    }

    private String parseZTDtoSTR(ZonedDateTime birthDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return birthDate.format(formatter);
    }

    @Override
    public String getURL() {
        return "https://shielded-cliffs-47552.herokuapp.com/doggos";
    }

    @Override
    public JSONObject getJSON() {
        JSONObject jso = new JSONObject();
        try {
            jso.put("name", this.name);
            jso.put("breed", this.breed);
            if (this.sex == gender.Female)
                jso.put("gender", "female");
            else
                jso.put("gender", "male");
            jso.put("birthDate", parseZTDtoSTR(this.birth_date));
            jso.put("lastWalkDate", parseZTDtoSTR(this.last_walk_date));
            jso.put("lastBathDate", parseZTDtoSTR(this.last_bath_date));
            jso.put("lastVetDate", parseZTDtoSTR(this.last_vet_date));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jso;
    }

    @Override
    public int getEntryID() {
        return entryID;
    }

}
