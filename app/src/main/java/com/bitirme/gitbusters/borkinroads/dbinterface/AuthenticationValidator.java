package com.bitirme.gitbusters.borkinroads.dbinterface;

import android.content.Context;
import android.content.Intent;

import com.auth0.android.jwt.JWT;
import com.bitirme.gitbusters.borkinroads.uiactivity.LoginActivity;

public class AuthenticationValidator {
    private Context context;
    private String token;

    public AuthenticationValidator(Context context) {
        this.context = context;
    }

    public String getAuthenticationToken() {
        token = context.getSharedPreferences("auth", Context.MODE_PRIVATE).getString("token", "invalid");
        if (token.equals("invalid")) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }

        JWT jwt = new JWT(token);
        if (jwt.isExpired(60)) {
            Intent intent = new Intent(context, LoginActivity.class);
            context.startActivity(intent);
        }

        return token;
    }

}
