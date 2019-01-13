package com.bitirme.gitbusters.borkinroads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Date;

public class DoggoActivity extends AppCompatActivity {

    private TextView name, breed, gender, birthdate, age;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggo);

        name      = findViewById(R.id.name);
        breed     = findViewById(R.id.breed);
        gender    = findViewById(R.id.gender);
        birthdate = findViewById(R.id.birthdate);
        age       = findViewById(R.id.age);

        Doggo temp =  new Doggo("SysTemp","Terrier",new Date(2000,1,1),new Date(2019,1,13), Doggo.gender.male);
        //Doggo selected doggo.

        name.setText(temp.getName());
        breed.setText(temp.getBreed());
        birthdate.setText(temp.getBirth_date().toString());
        age.setText(temp.getBirth_date().toString()); //broken on purpose
    }
}
