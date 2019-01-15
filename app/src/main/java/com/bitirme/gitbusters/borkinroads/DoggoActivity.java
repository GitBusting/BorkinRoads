package com.bitirme.gitbusters.borkinroads;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.GregorianCalendar;

public class DoggoActivity extends AppCompatActivity {

    private TextView name, breed, gender, birthdate, age;
    private final int GET_FROM_GALLERY = 4;
    private ImageButton ppbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doggo);

        name      = findViewById(R.id.name);
        breed     = findViewById(R.id.breed);
        gender    = findViewById(R.id.gender);
        birthdate = findViewById(R.id.birthdate);
        age       = findViewById(R.id.age);
        ppbutton = findViewById(R.id.pbutton);
        // TODO: fix the ratio of the button picture

        ppbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        Doggo temp = new Doggo("SysTemp", "Terrier", new GregorianCalendar(2019, 1, 1), new GregorianCalendar(2019, 1, 1), Doggo.gender.male);
        //  TODO: Swap out TestDoggo to selected doggo.

        name.setText(temp.getName());
        breed.setText(temp.getBreed());
        birthdate.setText(temp.getBirth_date().toString());
        age.setText(temp.getBirth_date().toString()); //broken on purpose
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ppbutton.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
