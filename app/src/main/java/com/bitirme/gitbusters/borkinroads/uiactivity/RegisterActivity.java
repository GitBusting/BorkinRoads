package com.bitirme.gitbusters.borkinroads.uiactivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bitirme.gitbusters.borkinroads.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private UserRegisterTask mAuthTask;

    //UI
    private EditText email;
    private EditText name;
    private EditText password1;
    private EditText password2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.editTextMail);
        name = findViewById(R.id.editTextName);
        password1 = findViewById(R.id.editTextPass1);
        password2 = findViewById(R.id.editTextPass2);

        Button button = findViewById(R.id.registerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    public void attemptRegister () {
        String emailString = email.getText().toString();
        String nameString = name.getText().toString();
        String passwordString = password1.getText().toString();
        String password2String = password2.getText().toString();

        if (!password2String.equals(passwordString)) {
            Toast.makeText(this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(passwordString) || TextUtils.isEmpty(password2String)) {
            Toast.makeText(this, "Password fields cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuthTask = new UserRegisterTask(emailString, passwordString, nameString);
        mAuthTask.execute((Void) null);
    }




    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private final String mName;

        UserRegisterTask(String email, String password, String name) {
            mEmail = email;
            mPassword = password;
            mName = name;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                URL url = new URL("https://shielded-cliffs-47552.herokuapp.com/auth/register");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-type", "application/json");
                OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
                JSONObject jso = new JSONObject();
                jso.put("email", mEmail);
                jso.put("password", mPassword);
                jso.put("name", mName);
                out.write(jso.toString());
                out.flush();
                out.close();


                if (connection.getResponseCode() == 201)
                    return true;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                startMainActivity();
            } else {
                password1.setError(getString(R.string.error_incorrect_password));
                password1.requestFocus();
            }


        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
