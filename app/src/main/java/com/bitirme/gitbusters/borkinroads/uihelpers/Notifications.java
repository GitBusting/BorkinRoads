package com.bitirme.gitbusters.borkinroads.uihelpers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.bitirme.gitbusters.borkinroads.R;
import com.bitirme.gitbusters.borkinroads.data.DoggoRecord;

import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;

public class Notifications {

    private Context context;

    public static Period zonedDateTimeDifference(ZonedDateTime d1, ZonedDateTime d2) {
        return Period.between(d1.toLocalDate(), d2.toLocalDate());
    }

    public void checkEverthing(DoggoRecord doggo, Context context) {
        this.context = context;
        checkLastWalk(doggo);
        checkLastVetVisit(doggo);
        checkLastBath(doggo);
    }

    private void checkLastWalk(DoggoRecord doggo) {
        int daysSinceLastWalk = zonedDateTimeDifference(doggo.getLast_walk_date(), ZonedDateTime.now(ZoneId.systemDefault())).getDays();
        if (daysSinceLastWalk >= 14) {
            assembleNotification(reason.walk, doggo, daysSinceLastWalk);
        } else
            anotherNotification(reason.walk, doggo, daysSinceLastWalk);
    }

    private void checkLastVetVisit(DoggoRecord doggo) {
        int monthsSinceLastVetVisit = zonedDateTimeDifference(doggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getMonths();
        if (monthsSinceLastVetVisit >= 6) {
            assembleNotification(reason.vet, doggo, monthsSinceLastVetVisit);
        } else
            anotherNotification(reason.walk, doggo, monthsSinceLastVetVisit);
    }

    private void checkLastBath(DoggoRecord doggo) {
        int monthsSinceLastBath = zonedDateTimeDifference(doggo.getLast_vet_date(), ZonedDateTime.now(ZoneId.systemDefault())).getMonths();
        if (monthsSinceLastBath >= 3) {
            assembleNotification(reason.bath, doggo, monthsSinceLastBath);
        } else
            anotherNotification(reason.walk, doggo, monthsSinceLastBath);
    }

    private void assembleNotification(reason reason, DoggoRecord doggo, int forHowMany) {
        String textTitle = "";
        String textContent = "";

        switch (reason) {
            case walk:
                textTitle = doggo.getName() + " wants to walk!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't walked for " + forHowMany + " days.";
                break;
            case vet:
                textTitle = doggo.getName() + " should visit" + ((doggo.getSex().equals(DoggoRecord.gender.Male)) ? " his" : " her") + " vet soon!";
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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        mBuilder.setWhen(cal.getTimeInMillis());
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
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private enum reason {walk, vet, bath}

    private void anotherNotification(reason reason, DoggoRecord doggo, int forHowMany) {
        String textTitle = "";
        String textContent = "";
        Calendar cal = Calendar.getInstance();

        switch (reason) {
            case walk:
                cal.add(Calendar.DAY_OF_YEAR, 14 - forHowMany);
                textTitle = doggo.getName() + " wants to walk!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't walked for 14 days.";
                break;
            case vet:
                cal.add(Calendar.MONTH, 6 - forHowMany);
                textTitle = doggo.getName() + " should visit" + ((doggo.getSex().equals(DoggoRecord.gender.Male)) ? " his" : " her") + " vet soon!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't gone to vet for 6 months.";
                break;
            case bath:
                cal.add(Calendar.MONTH, 3 - forHowMany);
                textTitle = doggo.getName() + " should bathe" + " soon!";
                textContent = "Your " + doggo.getBreed() + " " + doggo.getName() + " " + "hasn't bathed for 3 months.";
                break;
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "default")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setWhen(cal.getTimeInMillis());

        mBuilder.setWhen(cal.getTimeInMillis());
    }
}

