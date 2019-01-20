package com.bitirme.gitbusters.borkinroads;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

public class BreedDoggos extends AppCompatActivity {

    EditText nametext, breedtext;
    TextView warning;
    Button create;
    ToggleButton gender;
    EditText birthtext;
    DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_doggos);

        final Calendar myCalendar = Calendar.getInstance();

        nametext = findViewById(R.id.Name);
        breedtext = findViewById(R.id.Breed);
        birthtext = findViewById(R.id.Birth);
        create = findViewById(R.id.Create);
        gender = findViewById(R.id.Gender);
        warning = findViewById(R.id.Warning);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                birthtext.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birthtext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new DatePickerDialog(BreedDoggos.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nametext.getText().equals("") | !breedtext.getText().equals("") | !birthtext.getText().equals("")) {
                    warning.setText("You need to fill all the input spaces");
                } else {
                    ZonedDateTime zdt = ZonedDateTime.parse(birthtext.getText(), DateTimeFormatter.ofPattern("dd MMM yyyy"));
                    Doggo newPet = new Doggo(nametext.getText().toString(), breedtext.getText().toString(), zdt, gender.isChecked() ? Doggo.gender.Male : Doggo.gender.Female);
                    Doggo.doggos.add(newPet);
                    Toast.makeText(BreedDoggos.this, "Pet Added!", Toast.LENGTH_SHORT);
                    Intent i = new Intent(view.getContext(), DoggoActivity.class);
                    startActivity(i);
                }
            }
        });

    }
}
