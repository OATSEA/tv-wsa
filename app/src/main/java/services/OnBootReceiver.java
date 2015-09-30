package services;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import tasks.CommandTask;
import utils.Utils;

public class OnBootReceiver extends BroadcastReceiver {

    private static final String TAG = OnBootReceiver.class.getSimpleName();

    private static String htdocs = Environment.getExternalStorageDirectory() + File.separator + "htdocs";
    @Override
    public void onReceive(Context context, Intent intent) {
    	
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            checkHtdocsOK();
            writeIpAddress("http://"+"localhost:8080");
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


    public boolean checkHtdocsOK() {
        File file = new File(htdocs);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    } // END checkHtdocsOK

    private void writeIpAddress(String ipAddress){

        File ipFile = new File(htdocs,"IP.txt");
        try {
            FileOutputStream f = new FileOutputStream(ipFile);
            PrintWriter pw = new PrintWriter(f);
            pw.print(ipAddress);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "******* File not found. Did you" +
                    " add a WRITE_EXTERNAL_STORAGE permission to the   manifest?");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}