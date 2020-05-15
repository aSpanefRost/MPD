package com.rajdeepdas.mpd;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by rajdeepdas on 29/04/20.
 */

public class Notification_reciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        Bundle bundle = intent.getExtras();
        String enWord= bundle.getString("en_word");

        Intent repeating_intent = new Intent(context,WordMeaningActivity.class);
        Bundle bundle1 = new Bundle();
        bundle1.putString("en_word",enWord);
        repeating_intent.putExtras(bundle1);



        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(context,100,repeating_intent,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder builder= new NotificationCompat.Builder(context,"MyNotification")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_import_contacts)
                .setContentTitle("Today's Word is")
                .setContentText(enWord)
                .setAutoCancel(true);

        notificationManager.notify(100, builder.build());


    }
}
