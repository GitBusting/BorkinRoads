package com.bitirme.gitbusters.borkinroads.uiactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bitirme.gitbusters.borkinroads.R;
import com.bitirme.gitbusters.borkinroads.data.DoggoRecord;
import com.bitirme.gitbusters.borkinroads.data.UserRecord;
import com.bitirme.gitbusters.borkinroads.uihelpers.DogButtonAdapter;
import com.bitirme.gitbusters.borkinroads.uihelpers.Notifications;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.bitirme.gitbusters.borkinroads.uihelpers.Notifications.zonedDateTimeDifference;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DogButtonAdapter.ItemClickListener {
    private final int GET_FROM_GALLERY = 4;
    private static final String TAG = "MainActivity";
    private TextView walkdate;
    private ImageButton ppbutton;
    private TextView name;
    private TextView breed;
    private TextView gender;
    private TextView birthdate;
    private TextView age;
    private TextView vetdate;
    private TextView bathdate;
    private ProgressBar walkbar;
    private ProgressBar vetbar;
    private ProgressBar bathbar;
    private ImageButton walkbutton;
    private ImageButton vetbutton;
    private ImageButton bathbutton;

    private DoggoRecord currentDoggo;

    private DogButtonAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (UserRecord.activeUser.getPets() != null)
            for (DoggoRecord doggo : UserRecord.activeUser.getPets()) {
                Notifications tmp = new Notifications();
                tmp.checkEverthing(doggo, this);
            }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        name = findViewById(R.id.name);
        breed = findViewById(R.id.breed);
        gender = findViewById(R.id.gender);
        birthdate = findViewById(R.id.birth_date);
        age = findViewById(R.id.age);
        walkdate = findViewById(R.id.walkDate);
        vetdate = findViewById(R.id.vetDate);
        bathdate = findViewById(R.id.bathDate);
        walkbar = findViewById(R.id.walkBar);
        vetbar = findViewById(R.id.vetBar);
        bathbar = findViewById(R.id.bathBar);
        walkbutton = findViewById(R.id.walkButton);
        walkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDoggo.getName().equals("Add New Pet"))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Gone for a walk?");
                builder.setMessage("Have you walked " + currentDoggo.getName() + "?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        currentDoggo.setLast_walk_date(ZonedDateTime.now());
                        setValues();
                        setBars();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        vetbutton = findViewById(R.id.vetButton);
        vetbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDoggo.getName().equals("Add New Pet"))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Vet Visit");
                builder.setMessage("Have you visited the vet?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        currentDoggo.setLast_vet_date(ZonedDateTime.now());
                        setValues();
                        setBars();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        bathbutton = findViewById(R.id.bathButton);
        bathbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentDoggo.getName().equals("Add New Pet"))
                    return;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Bath Time");
                builder.setMessage("Has " + currentDoggo.getName() + "taken a bath?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        currentDoggo.setLast_bath_date(ZonedDateTime.now());
                        setValues();
                        setBars();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        ppbutton = findViewById(R.id.p_button);
        ppbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentDoggo.getName().equals("Add New Pet")) {
                    Intent i = new Intent(MainActivity.this, BreedDoggos.class);
                    startActivity(i);
                } else
                    startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });
        DoggoRecord temp = null;
        if (UserRecord.activeUser.getPets().size() == 0) {
            temp = new DoggoRecord("Add New Pet", "Breed", ZonedDateTime.now(ZoneId.systemDefault()), DoggoRecord.gender.Gender);
//            UserRecord.activeUser.getPets().add(temp);
            Log.v(TAG, "did it!");
        }

        if (UserRecord.activeUser.getPets().size() == 0)
            currentDoggo = temp;
        else
            currentDoggo = UserRecord.activeUser.getPets().get(0);

        setValues();
        setBars();

        // set up the RecyclerView
        RecyclerView recyclerView = findViewById(R.id.doggo_bar);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManager);
        adapter = new DogButtonAdapter(MainActivity.this);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

    }

    private void setValues() {
        ZonedDateTime rn = ZonedDateTime.now(ZoneId.systemDefault());
        long monthOld = ChronoUnit.MONTHS.between(currentDoggo.getBirth_date(), rn);
        name.setText(currentDoggo.getName());
        breed.setText(currentDoggo.getBreed());
        gender.setText(currentDoggo.getSex().toString());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        birthdate.setText(currentDoggo.getBirth_date().format(formatter));
        walkdate.setText(currentDoggo.getLast_walk_date().format(formatter));
        vetdate.setText(currentDoggo.getLast_vet_date().format(formatter));
        bathdate.setText(currentDoggo.getLast_bath_date().format(formatter));
        String plural = (monthOld == 1) ? " month old" : " months old";
        String mo = monthOld + "";
        age.setText(mo.concat(plural));
        setImage();
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
                saveImage(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_addPet) {
            Intent i = new Intent(this, BreedDoggos.class);
            startActivity(i);

        } else if (id == R.id.nav_walk) {
            Intent i = new Intent(this, MapActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_my_routes) {
            Intent i = new Intent(this, DisplayRoutesActivity.class);
            startActivity(i);
        }else if (id == R.id.nav_friend_list) {
            Intent i = new Intent(this, FriendListActivity.class);
            startActivity(i);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemClick(View view, int position) {
        currentDoggo = adapter.getItem(position);
        Log.v(TAG, "You clicked " + adapter.getItem(position) + " on row number " + position);
        currentDoggo = UserRecord.activeUser.getPets().get(position);
        setValues();
        setBars();

    }

    private void setBars() {
        double daysSinceLastWalk = zonedDateTimeDifference(currentDoggo.getLast_walk_date(), ZonedDateTime.now(ZoneId.systemDefault())).getDays();
        int walkProg = (int) (daysSinceLastWalk / 14.0 * 100);
        if (walkProg > 100) walkProg = 100;
        double monthsSinceLastVetVisit = zonedDateTimeDifference(currentDoggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getDays();
        int vetProg = (int) (monthsSinceLastVetVisit / 180 * 100);
        if (vetProg > 100) vetProg = 100;
        double monthsSinceLastBath = zonedDateTimeDifference(currentDoggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getDays();
        int bathProg = (int) (monthsSinceLastBath / 90 * 100);
        if (bathProg > 100) bathProg = 100;

        walkbar.setProgress(walkProg);
        vetbar.setProgress(vetProg);
        bathbar.setProgress(bathProg);
    }

    private void setImage() {
        String path = getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + currentDoggo.getName() + ".jpg";
        try {
            File tmp = new File(path);
            if (tmp.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                ppbutton.setImageBitmap(bitmap);
            } else {
                Resources r = getResources();
                ppbutton.setImageDrawable(ResourcesCompat.getDrawable(r, R.drawable.plus_icon, this.getTheme()));
            }

        } catch (Exception e) {
            e.printStackTrace();
            //ppbutton.setImageBitmap(BitmapFactory.decodeFile(getExternalFilesDir(Environment.DIRECTORY_DCIM) + "/" + "SysTemp.jpg"));
        }
    }

    @SuppressWarnings({"ConstantConditions", "ResultOfMethodCallIgnored"})
    private void saveImage(Bitmap finalBitmap) {
        File myDir = new File(getExternalFilesDir(Environment.DIRECTORY_DCIM).toString());
        myDir.mkdirs();
        String fname = currentDoggo.getName() + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
