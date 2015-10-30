package org.teachervirus;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import common.utils.FileUtils;
import common.utils.PrefUtil;
import eu.chainfire.libsuperuser.Shell;
import services.ServerService;
import tasks.CommandTask;
import tasks.RepoInstallerTask;
import utils.AppSettings;
import utils.Utils;

public class WebActivity extends AppCompatActivity {

    private static final String TAG = WebActivity.class.getSimpleName();
    private static final int DEFAULT_COUNTDOWN_TIME = 60 * 1000; // one minute
    private static final int DEFAULT_INTERVAL = 5000; // 5 second
    private WebView webView;
    private SharedPreferences preferences;
    private CountDownTimer countDownTimer;
    private ProgressDialog pDialog;
    private boolean trackTimer = false;

    private void initialise() {

        webView = (WebView) findViewById(R.id.webView);
        countDownTimer = new MyTimer(DEFAULT_COUNTDOWN_TIME, DEFAULT_INTERVAL);
    }


    private void installAndCheckRepo() {

        if (preferences.getBoolean("enable_server_on_app_startup", false)) {
            startService(new Intent(WebActivity.this, ServerService.class));
            final boolean enableSU = preferences.getBoolean("run_as_root", false);
            final String execName = preferences.getString("use_server_httpd", "lighttpd");
            final String bindPort = preferences.getString("server_port", "8080");

            CommandTask task = CommandTask.createForConnect(WebActivity.this, execName, bindPort);
            task.enableSU(enableSU);
            task.execute();
        }
        if (!FileUtils.checkIfExecutableExists()) {
            RepoInstallerTask task = new RepoInstallerTask(WebActivity.this);
            task.setOnRepoInstalledListener(new RepoInstallerTask.OnRepoInstalledListener() {
                @Override
                public void repoInstalled() {
                    Log.e(TAG, "Repo installed");
                    new ConnectionListenerTask().execute();
                }
            });
            task.execute("", Constants.INTERNAL_LOCATION.concat("/"));
        } else {
            new ConnectionListenerTask().execute();
        }
    }

    private void executeSu() {
        startService(new Intent(WebActivity.this, ServerService.class));
        final boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForConnect(this, execName, bindPort);
        task.enableSU(enableSU);
        task.setOnCommandTaskExecutedListener(new CommandTask.OnCommandTaskExecutedListener() {
            @Override
            public void onTaskCompleted() {

                if (FileUtils.ensureLighttpdConfigExists()) {

                    String path  = getIntent().getStringExtra("path");
                    if(!path.equals(AppSettings.getDefaultInstallationPath(WebActivity.this))){
                        Log.e(TAG, "Path from intent : " + path);
                        AppSettings.updateInstallationDirectory(WebActivity.this, path);
                        Log.e(TAG, "path from config : " + FileUtils.getPathToRootDir());
                    }

                    if(!AppSettings.getRootDir(WebActivity.this).exists()){
                        openCheckMedia();
                    }else{
                        Log.e(TAG,"configure server");
                        configServer();
                    }

                }
            }
        });
        task.execute();
    }

    private void configServer(){
        showDialog();
        String ipAddress = Utils.getIPAddress(true);
        if (ipAddress.trim().isEmpty()) {
            FileUtils.writeIpAddress("http://" + "localhost" + ":8080");
        } else {
            FileUtils. writeIpAddress("http://" + Utils.getIPAddress(true) + ":8080");
        }
        if(!FileUtils.isGetInfectedExists() || ! FileUtils.isLoadingSpinnerExists()){
            FileUtils.copyAssets(WebActivity.this);
        }
        countDownTimer.start();
        trackTimer = true;
    }

    private void openCheckMedia(){
        Intent intent = new Intent();
        intent.putExtra("retry",true);
        setResult(RESULT_OK, intent);
        finish();
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
    private class ConnectionListenerTask extends AsyncTask<Void, String, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Boolean doInBackground(Void... voids) {

            String FIND_PROCESS = String.format(
                    "%s ps | %s grep \"components\"",
                    Constants.BUSYBOX_SBIN_LOCATION,
                    Constants.BUSYBOX_SBIN_LOCATION);

            List<String> rc = Shell.SH.run(FIND_PROCESS);

            boolean serverListing;
            boolean phpListing;
            boolean mysqlListing;

            String shellOutput = "";
            for (String buf : rc.toArray(new String[rc.size()])) {
                shellOutput += buf;
            }

            serverListing = (shellOutput.contains("lighttpd") || shellOutput.contains("nginx"));
            phpListing = shellOutput.contains("php-cgi");
            mysqlListing = shellOutput.contains("mysqld");

            if (serverListing && phpListing && mysqlListing) {

                //publishProgress("OK");
                return true;
            } else {
                // publishProgress("ERROR");
                return false;
            }
            //return null;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            Log.e(TAG, "Server started :" + aBoolean);
            executeSu();
        }
    }





    public class MyTimer extends CountDownTimer {


        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            Log.e(TAG,"on tick");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (serverWorking()) {
                        countDownTimer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                dismissDialog();
                                if (FileUtils.isInstalled()) {
                                    openPage(AppSettings.PLAY_URL);
                                    Toast.makeText(WebActivity.this, "installation found", Toast.LENGTH_SHORT).show();
                                } else {
                                    openPage(AppSettings.GETINFECTED_URL);
                                    Toast.makeText(WebActivity.this, "installation not found", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                        PrefUtil.putBoolean(WebActivity.this, "restart", "restart", false);
                    }
                }
            }).start();

        }

        @Override
        public void onFinish() {
            trackTimer = false;
            dismissDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (serverWorking()) {
                        countDownTimer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                if (FileUtils.isInstalled()) {
                                    openPage(AppSettings.PLAY_URL);
                                    Toast.makeText(WebActivity.this, "installation found", Toast.LENGTH_SHORT).show();
                                } else {
                                    openPage(AppSettings.GETINFECTED_URL);
                                    Toast.makeText(WebActivity.this, "installation not found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        PrefUtil.putBoolean(WebActivity.this, "restart", "restart", false);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (PrefUtil.getBoolean(WebActivity.this, "restart", "restart")) {
                                        askToRestart();
                                    } else {
                                        unknownError();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }
                        });

                    }
                }
            }).start();
        }
    }


    private boolean serverWorking() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(FileUtils.getIpAddress()+AppSettings.GETINFECTED_URL).openConnection();
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return false;
            }
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (ProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void openPage(String url) {

        String mainurl = "http://localhost:8080"+url;
        Log.e(TAG,"current url : "+mainurl);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(mainurl);
        Log.e(TAG, "Webview");
    }

    private void askToRestart(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(WebActivity.this);
        builder.setTitle("Restart Device");
        builder.setMessage("Your device need to Restart to apply changes.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefUtil.putBoolean(WebActivity.this, "restart", "restart", true);
                disableServer();
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }

    private void unknownError(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(WebActivity.this);
        builder.setTitle("Restart Device");
        builder.setMessage("Your device need to Restart to apply changes.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                disableServer();
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }


    private void showDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.cancel();
            pDialog = null;
        }

        pDialog = ProgressDialog.show(WebActivity.this, "Loading Resource", "Please wait..", true);
    }

    private void dismissDialog() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
                pDialog = null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        preferences = PreferenceManager.
                getDefaultSharedPreferences(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("reboot"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("stop"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("crosswalk"));
        initialise();
        installAndCheckRepo();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

        if(trackTimer){
            outState.putBoolean("track",true);
        }else{
            outState.putBoolean("track", false);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        boolean state = savedInstanceState.getBoolean("track");
        if(!trackTimer && state){
            unknownError();
        }
        super.onRestoreInstanceState(savedInstanceState);
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

                finish();
            }else if(intent.getAction().equals("crosswalk")){

                finish();
            }else if(intent.getAction().equals("stop")){
                disableServer();
                finish();
            }
        }
    };
}
