package tech.iwish.onhome.Notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import tech.iwish.onhome.R;

import static tech.iwish.onhome.Notification.tech.iwish.onhome.AppController.CHANNEL_1_ID;


public class NotificationModule extends AppCompatActivity {
    private NotificationManagerCompat notificationManager;


    Context context;
    Bitmap bitmap;
    Drawable drawable;

    public NotificationModule(Context context){
        this.context=context;
        notificationManager = NotificationManagerCompat.from(context);


    }



    public void fire(String title ,String message) {
        drawable= ContextCompat.getDrawable(context,R.drawable.om);

        bitmap = ((BitmapDrawable)drawable).getBitmap();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_1_ID,"001",NotificationManager.IMPORTANCE_DEFAULT);
//            notificationChannel.setDescription("hello this is my");
//             notificationManager= (NotificationManagerCompat)getSystemService(NOTIFICATION_SERVICE);
//            notificationChannel.createNotificationChannel(notificationChannel);
//
//            Notification.Builder builder = new Notification.Builder(context,CHANNEL_1_ID);
//            builder.setSmallIcon(R.drawable.noti)
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
//                    .setLargeIcon(bitmap);
//
//            notificationManager.notify(1,builder.build());
        }
        else {
            Notification notification = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                    .setSmallIcon(R.drawable.noti)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                    .setLargeIcon(bitmap)
                    .build();

            notificationManager.notify(1,notification);

        }









    }
}
