package services;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import common.utils.FileUtils;
import common.utils.PrefUtil;
import tasks.CommandTask;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            PrefUtil.putBoolean(context, "restart", "restart", false);
            FileUtils.writeIpAddress("http://"+"localhost:8080");
        	final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            if (preferences.getBoolean("enable_server_on_boot", false)) {
                
            	Intent i = new Intent(context, ServerService.class);
                context.startService(i);
                
                final boolean enableSU = preferences.getBoolean("run_as_root", false);
                final String execName = preferences.getString("use_server_httpd", "lighttpd");
                final String bindPort = preferences.getString("server_port", "8080");
            	
                CommandTask task = CommandTask.createForConnect(context, execName, bindPort, false);
                task.enableSU(enableSU);
                task.execute();
                
            }
        }
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            BackgroundIntentService.performAction(context, BackgroundIntentService.ACTION_PACKAGE_REMOVED);
        }
    }


}