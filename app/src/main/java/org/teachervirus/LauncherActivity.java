package org.teachervirus;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import common.utils.FileUtils;
import services.ServerService;
import tasks.CommandTask;
import utils.AppSettings;
import utils.Utils;

public class LauncherActivity extends AppCompatActivity {

    private static final String TAG = LauncherActivity.class.getSimpleName();
    public static final int REQ_SELECT_INSTALL_PATH = 100;
    public static final int REQ_CROSS_WALK = 101;
    public static final int REQ_DELETE_OLD = 102;
    public static final int REQ_WEB_VIEW = 103;
    private SharedPreferences preferences;

    private void openInstallationPathChooser(boolean retry){
        Intent mIntent = new Intent(LauncherActivity.this,ChooseInstallationActivity.class);
        /*mIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/

        mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mIntent.putExtra("retry",retry);
        startActivityForResult(mIntent,REQ_SELECT_INSTALL_PATH);
    }


    private void openBrowser(){
        if(AppSettings.crosswalkEnabled(LauncherActivity.this)){
            openCrosswalk();
        }else{
            openWebView();
        }
    }

    private void openCrosswalk(){
        Intent intent = new Intent(LauncherActivity.this,CrosswalkActivity.class);
       /* intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent,REQ_CROSS_WALK);
    }

    private void openWebView(){
        Intent intent = new Intent(LauncherActivity.this,WebActivity.class);
       /* intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent,REQ_WEB_VIEW);
    }

    private void openDeleteAndInstall(){
        Intent intent = new Intent(LauncherActivity.this,InstallationDeleteActivity.class);
        /*intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);*/

        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityForResult(intent,REQ_DELETE_OLD);
    }

    private void disableServer() {
        boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForDisconnect(this);
        task.enableSU(enableSU);
        task.execute();

        NotificationManager notify = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notify.cancel(143);
        stopService(new Intent(this, ServerService.class));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        preferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("reboot"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("stop"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("crosswalk"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("open"));

        String ipAddress = Utils.getIPAddress(true);
        if (ipAddress.trim().isEmpty()) {
            FileUtils.writeIpAddress("http://" + "localhost" + ":8080");
        } else {
            FileUtils.writeIpAddress("http://" + Utils.getIPAddress(true) + ":8080");
        }
        if (!FileUtils.isGetInfectedExists() || !FileUtils.isLoadingSpinnerExists()) {
            FileUtils.copyAssets(LauncherActivity.this);
        }
        if(AppSettings.applicationUpdated(LauncherActivity.this)
                && AppSettings.previousInstallationFound(LauncherActivity.this)){
            //App updated or freshly installed  && installation found.
            Log.e(TAG,"Ask to delete old one");
            openDeleteAndInstall();
        }else{
            if(AppSettings.previousInstallationFound(LauncherActivity.this)){
                Log.e(TAG,"run server related code");
                openBrowser();
            }else{
                Log.e(TAG,"Ask for install location");
                openInstallationPathChooser(false);
            }
        }


        AppSettings.updateVersionName(LauncherActivity.this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){

            case REQ_SELECT_INSTALL_PATH:
                    if(resultCode == RESULT_OK){
                        openBrowser();
                    }else{
                        finish();
                    }
                break;

            case REQ_DELETE_OLD:
                if(resultCode == RESULT_OK){
                    if(data.getBooleanExtra("deleted",false)){
                        openInstallationPathChooser(false);
                    }else{
                        openBrowser();
                    }
                }else{
                    finish();
                }
                break;

            case REQ_CROSS_WALK:
                if(resultCode == RESULT_OK){
                    if(data.getBooleanExtra("retry",false)){
                        openInstallationPathChooser(true);
                    }else{
                        finish();
                    }
                }else{
                    finish();
                }
                break;

            case REQ_WEB_VIEW:
                if(resultCode == RESULT_OK){
                    if(data.getBooleanExtra("retry",false)){
                        openInstallationPathChooser(true);
                    }else{
                        finish();
                    }
                }else{
                    finish();
                }
                break;

            default:
                finish();
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("reboot")){

                openBrowser();
            }else if(intent.getAction().equals("crosswalk")){

                openBrowser();
            }else if(intent.getAction().equals("stop")){
                disableServer();
                finish();
            }else if(intent.getAction().equals("open")){
                openBrowser();
            }
        }
    };
}
