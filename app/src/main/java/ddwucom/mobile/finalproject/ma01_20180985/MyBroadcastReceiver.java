package ddwucom.mobile.finalproject.ma01_20180985;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String id = intent.getStringExtra("channelId");
        Log.d("yyj", "intent-title = " + title);

        intent = new Intent(context, AllScheduleActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, id)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        int notiId = 100;
        notificationManagerCompat.notify(notiId, builder.build());
    }
}
