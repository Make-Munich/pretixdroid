package eu.pretix.pretixdroid.net.server;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import eu.pretix.pretixdroid.PretixDroid;
import eu.pretix.pretixdroid.R;
import eu.pretix.pretixdroid.ui.MainActivity;

public class ServerService extends Service {
    public static final int NOTIFICATION_ID = 0x00001;
    public static final int PORT = 8765;

    public static boolean RUNNING = false;
    public static String SECRET = "";

    private WebServer server;

    @Override
    public void onCreate() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences sp = getSharedPreferences("server",
                Context.MODE_PRIVATE);
        SECRET = sp.getString("secret", null);

        try {
            server = new WebServer(this, PretixDroid.KEYSTORE_PASSWORD, PORT);
            server.start();
        } catch (Exception e) {
            stopSelf();
            e.printStackTrace();
            return START_REDELIVER_INTENT;
        }
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(
                        PendingIntent.getActivity(this, 0, new Intent(this,
                                MainActivity.class), 0))
                .setContentText(getString(R.string.server_running))
                .setSmallIcon(R.drawable.ic_logo).build();
        startForeground(NOTIFICATION_ID, notification);

        Log.i("server", "starting");

        RUNNING = true;
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        RUNNING = false;
        if (server != null) {
            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
