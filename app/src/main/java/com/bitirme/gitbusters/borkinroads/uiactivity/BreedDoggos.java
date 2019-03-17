package com.bitirme.gitbusters.borkinroads.uiactivity;

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

import com.bitirme.gitbusters.borkinroads.R;
import com.bitirme.gitbusters.borkinroads.data.DoggoRecord;
import com.bitirme.gitbusters.borkinroads.data.UserRecord;
import com.bitirme.gitbusters.borkinroads.dbinterface.RestPusher;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;

public class BreedDoggos extends AppCompatActivity {

    private EditText nameText;
    private EditText breedText;
    private TextView warning;
    private ToggleButton gender;
    private EditText birthText;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breed_doggos);

        final Calendar myCalendar = Calendar.getInstance();

        nameText = findViewById(R.id.Name);
        breedText = findViewById(R.id.Breed);
        birthText = findViewById(R.id.Birth);
        Button create = findViewById(R.id.Create);
        gender = findViewById(R.id.Gender);
        warning = findViewById(R.id.Warning);
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {
                String myFormat = "dd MMM yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                birthText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birthText.setOnClickListener(new View.OnClickListener() {

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
                if (nameText.getText().toString().equals("") | breedText.getText().toString().equals("") | birthText.getText().toString().equals("")) {
                    warning.setText(R.string.warning);
                } else {
                    ZonedDateTime zdt = ZonedDateTime.ofInstant(myCalendar.toInstant(), ZoneId.systemDefault());
                    DoggoRecord newPet = new DoggoRecord(nameText.getText().toString(), breedText.getText().toString(), zdt, gender.isChecked() ? DoggoRecord.gender.Male : DoggoRecord.gender.Female);
//                    if (DoggoRecord.doggos.get(0).getName().equals("Add New Pet")) {
//                        DoggoRecord.doggos.remove(0);
//                    }
//                    DoggoRecord.doggos.add(newPet);
                    pushNewDogToDatabase(newPet);
                    Toast.makeText(BreedDoggos.this, "Pet Added!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(view.getContext(), MainActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    private void pushNewDogToDatabase(DoggoRecord newPet) {
        RestPusher rp = new RestPusher(newPet, getApplicationContext());
        rp.start();
        UserRecord.activeUser.getPets().add(newPet);


    }
}
