package services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.teachervirus.R;

import java.io.File;

import common.utils.FileUtils;
import ui.ConsoleActivity;
import utils.Utils;


public class ServerService extends Service {

    private static final String TAG = ServerService.class.getSimpleName();
    private final IBinder mBinder = new ServerBinder();
    private PowerManager.WakeLock wakeLock = null;

    private SharedPreferences preferences;
    private Handler handler = new Handler(Looper.getMainLooper());

    private static String htdocs = Environment.getExternalStorageDirectory() + File.separator + "htdocs";
    private static String pathToConfig = Environment.getExternalStorageDirectory()
            +File.separator+"droidphp"+File.separator+"conf"+File.separator+"lighttpd.conf";

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);

        registerReceiver(mConnectionReceiver,
                mIntentFilter);
        initialize();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return (START_NOT_STICKY);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mConnectionReceiver);
        destroyService();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    protected void initialize() {
        Context context = getApplicationContext();
        NotificationManager noti = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("DroidPHP service started")
                        .setContentText("Web Service started");

        Intent notificationIntent = new Intent(this, ConsoleActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, 143, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        mBuilder.setContentIntent(contentIntent);
        Notification mNotificationCompat = mBuilder.build();
        mNotificationCompat.flags |= Notification.FLAG_AUTO_CANCEL;
        mNotificationCompat.flags |= Notification.FLAG_INSISTENT;
        startForeground(143, mNotificationCompat);
        noti.notify(143, mNotificationCompat);

        if (preferences.getBoolean("enable_screen_on", false)) {
            PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "DPScreenLock");
            wakeLock.acquire();
        }

        if (preferences.getBoolean("enable_lock_wifi", false)) {
            /*  not implemented */
        }
    }

    protected void destroyService() {
        NotificationManager notify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notify.cancel(143);
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public class ServerBinder extends Binder {

        ServerService getService() {
            return ServerService.this;
        }
    }


  private BroadcastReceiver mConnectionReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {

          String ipAddress = Utils.getIPAddress(true);
          FileUtils.writeIpAddress(context,ipAddress);
      }
  };

}