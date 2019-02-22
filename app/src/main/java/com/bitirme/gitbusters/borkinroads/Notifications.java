package com.bitirme.gitbusters.borkinroads;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Notifications {

    private Context context;

    static Period zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2) {
        return Period.between(d1.toLocalDate(), d2.toLocalDate());
    }

    public void checkEverthing(Doggo doggo, Context context) {
        this.context = context;
        checkLastWalk(doggo);
        checkLastVetVisit(doggo);
        checkLastBath(doggo);
    }

    private void checkLastWalk(Doggo doggo) {
        int daysSinceLastWalk = zonedDateTimeDifference(doggo.getLast_walk_date(), ZonedDateTime.now(ZoneId.systemDefault())).getDays();
        if (daysSinceLastWalk >= 14) {
            assembleNotification(reason.walk, doggo, daysSinceLastWalk);
        }
    }

    private void checkLastVetVisit(Doggo doggo) {
        int monthsSinceLastVetVisit = zonedDateTimeDifference(doggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getMonths();
        if (monthsSinceLastVetVisit >= 6) {
            assembleNotification(reason.vet, doggo, monthsSinceLastVetVisit);
        }
    }

    private void checkLastBath(Doggo doggo) {
        int monthsSinceLastBath = zonedDateTimeDifference(doggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getMonths();
        if (monthsSinceLastBath >= 3) {
            assembleNotification(reason.bath, doggo, monthsSinceLastBath);
        }
    }

    private void assembleNotification(reason reason, Doggo doggo, int forHowMany) {
        String textTitle = "";
        String textContent = "";

        switch (reason) {
            case walk:
                textTitle = doggo.getName() + " wants to walk!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't walked for " + forHowMany + " days.";
                break;
            case vet:
                textTitle = doggo.getName() + " should visit" + ((doggo.getSex().equals(Doggo.gender.Male)) ? " his" : " her") + " vet soon!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't gone to vet for " + forHowMany + " months.";
                break;
            case bath:
                textTitle = doggo.getName() + " should bathe" + " soon!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't bathed for " + forHowMany + " months.";
                break;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "default";
            CharSequence name = "default";
            String description = "TODO"; //TODO name them
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private enum reason {walk, vet, bath}
}