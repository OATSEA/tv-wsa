package ui;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.teachervirus.Constants;
import org.teachervirus.FilePickerActivity;
import org.teachervirus.R;
import org.xwalk.core.XWalkNavigationHistory;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkView;

import java.io.File;
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
import utils.Utils;

// Credits:
// https://gist.github.com/rduplain/2638913
// https://developer.chrome.com/multidevice/webview/gettingstarted

public class FullscreenActivity extends AppCompatActivity {


    private XWalkView xWalkWebView;
    private static final String TAG = FullscreenActivity.class.getSimpleName();

    private static final int REQ_DIR = 101;
    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private static final int DEFAULT_COUNTDOWN_TIME = 60 * 1000; // one minute
    private static final int DEFAULT_INTERVAL = 5000; // 5 second
    private static String file_url = "https://www.github.com/OATSEA/getinfected/zipball/master";  // File url to download - github
    private static String getinfected_url = "/getinfected.php"; // getinfected installer
    private static String play_url = "/play";

    private SharedPreferences preferences;
    private CountDownTimer countDownTimer;
    private RadioButton mDefaultRadio,mSelectedRadio;
    private LinearLayout layoutDirChooser;
    private AppCompatButton btnGo,btnChangePath,btnRetry;
    private AppCompatTextView txtDefaultPath , txtSelectedPath,txtInstalledPath,txtTitle;
    private WebView webView;
    private String selectedPath = "";
    private boolean trackTimer = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
       // hideSystemUI();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("stop"));
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("crosswalk"));

        preferences = PreferenceManager.
                getDefaultSharedPreferences(this);

        layoutDirChooser=(LinearLayout)findViewById(R.id.layout_config);

        mDefaultRadio=(RadioButton)findViewById(R.id.radioDefault);
        mSelectedRadio=(RadioButton)findViewById(R.id.radioSelectPath);
        btnGo=(AppCompatButton)findViewById(R.id.btnGO);
        btnChangePath=(AppCompatButton)findViewById(R.id.btnChangePath);
        txtDefaultPath=(AppCompatTextView)findViewById(R.id.txtDefaultPath);
        txtSelectedPath=(AppCompatTextView)findViewById(R.id.txtSelectedPath);
        btnRetry=(AppCompatButton)findViewById(R.id.btnRetry);
        txtInstalledPath=(AppCompatTextView)findViewById(R.id.txtInstalledPath);
        txtTitle=(AppCompatTextView)findViewById(R.id.txtTitle);

        btnGo.setOnClickListener(mOnClickListener);
        btnChangePath.setOnClickListener(mOnClickListener);
        btnRetry.setOnClickListener(mOnClickListener);
        mDefaultRadio.setOnCheckedChangeListener(onCheckedChangeListener);
        mSelectedRadio.setOnCheckedChangeListener(onCheckedChangeListener);

        countDownTimer = new MyTimer(DEFAULT_COUNTDOWN_TIME, DEFAULT_INTERVAL);
        xWalkWebView = (XWalkView) findViewById(R.id.xwalkWebView);
        webView = (WebView)findViewById(R.id.webView);


       installAndCheckRepo();

    }

    private void forceTOexit(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(FullscreenActivity.this);
        builder.setTitle("Restart Device");
        builder.setMessage("Restart is required to apply changes made to installation directory.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                disableServer();
                finish();
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            final int id = buttonView.getId();

            switch (id){
                case R.id.radioDefault:
                    if(isChecked){
                        mSelectedRadio.setChecked(false);
                    }
                    break;

                case R.id.radioSelectPath:
                    if(isChecked){
                        mDefaultRadio.setChecked(false);
                    }
                    break;
            }
        }
    };

    private void installAndCheckRepo(){

        if (preferences.getBoolean("enable_server_on_app_startup", false)) {
            startService(new Intent(FullscreenActivity.this, ServerService.class));
            final boolean enableSU = preferences.getBoolean("run_as_root", false);
            final String execName = preferences.getString("use_server_httpd", "lighttpd");
            final String bindPort = preferences.getString("server_port", "8080");

            CommandTask task = CommandTask.createForConnect(FullscreenActivity.this, execName, bindPort);
            task.enableSU(enableSU);
            task.execute();
        }
        if (!FileUtils.checkIfExecutableExists()) {
            RepoInstallerTask task = new RepoInstallerTask(FullscreenActivity.this);
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


    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final int id = v.getId();
            switch (id){
                case R.id.btnGO:
                    if(mDefaultRadio.isChecked()){
                        String dir = FileUtils.getPathToRootDir();
                        if(dir.equals("/mnt/sdcard/htdocs")){
                            dir = "/mnt/sdcard/TeacherVirus";
                        }
                        onDirSelected(dir);
                    }else if(mSelectedRadio.isChecked()){
                        if(selectedPath.length()>0){
                            onDirSelected(selectedPath);
                        }else{
                            Toast.makeText(FullscreenActivity.this, "Please select path", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;

                case R.id.btnChangePath:
                    Intent mIntent = new Intent(FullscreenActivity.this,FilePickerActivity.class);
                    mIntent.putExtra("path",selectedPath);
                    startActivityForResult(mIntent,REQ_DIR);
                    break;

                case R.id.btnRetry:
                    if (FileUtils.ensureLighttpdConfigExists()) {
                        File rootWebDir = new File(FileUtils.getPathToRootDir());
                        if(rootWebDir.exists()){
                            layoutDirChooser.setVisibility(View.GONE);
                            xWalkWebView.setVisibility(View.VISIBLE);
                            configServer();
                        }else{
                            txtInstalledPath.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                            Toast.makeText(FullscreenActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
            }
        }
    };


    private void onDirSelected(String dir){

        disableServer();

        PrefUtil.putBoolean(FullscreenActivity.this, "restart", "restart", true);
        File mFile = new File(dir);
        if(!mFile.exists()){
            mFile.mkdirs();
            FileUtils.setServerRootDir(FullscreenActivity.this,mFile);
            executeSu();
        }else{
            if (!mFile.getPath().equals(FileUtils.getPathToRootDir())){
                FileUtils.setServerRootDir(FullscreenActivity.this, mFile);
               executeSu();
            } else{
               executeSu();
            }

        }
        layoutDirChooser.setVisibility(View.GONE);
        xWalkWebView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQ_DIR){
            if(resultCode == RESULT_OK){
                selectedPath = data.getStringExtra("path");
                txtSelectedPath.setText(selectedPath);
                mSelectedRadio.setChecked(true);
            } else if (resultCode == RESULT_CANCELED){
                mDefaultRadio.setChecked(true);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void askToRestart(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(FullscreenActivity.this);
        builder.setTitle("Restart Device");
        builder.setMessage("Your device need to Restart to apply changes.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PrefUtil.putBoolean(FullscreenActivity.this, "restart", "restart", true);
                disableServer();
                dialog.dismiss();
                finish();
            }
        });

        builder.show();
    }
    private void hideSystemUI() {
        // Hide Everything but Web Page:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        } // END Hide Everything*/
    }




  /*  @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {

            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }*/

    @Override
    protected void onPause() {
        super.onPause();
        if (xWalkWebView != null) {
            xWalkWebView.pauseTimers();
            xWalkWebView.onHide();
        }

    }

    @Override
    protected void onDestroy() {
        if (xWalkWebView != null) {
            xWalkWebView.onDestroy();
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }



    @Override
    public void onResume() {
        super.onResume();
        if (xWalkWebView != null) {
            xWalkWebView.resumeTimers();
            xWalkWebView.onShow();
        }
        // Hide Everything:
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View rootView = getWindow().getDecorView();
            rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
            rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }*/



       /* if(PreferenceHelper.getBoolean(FullscreenActivity.this,"restart","restart")){

            executeSu();
        }*/

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

    /**
     * Showing Dialog
     */

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case progress_bar_type: // we set this to 0
                pDialog = new ProgressDialog(this);
                pDialog.setMessage("Downloading getinfected - Please wait...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(true);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }




    @Override
    public void onBackPressed() {

        if(xWalkWebView.getVisibility() == View.VISIBLE){
            if(xWalkWebView.getNavigationHistory().canGoBack()){
                xWalkWebView.getNavigationHistory().navigate(XWalkNavigationHistory.Direction.BACKWARD,1);
            }else{
                countDownTimer.cancel();
                super.onBackPressed();
            }
        }else if(webView.getVisibility()==View.VISIBLE){
            if(webView.canGoBack()){
                webView.goBack();
            }else{
                countDownTimer.cancel();
                super.onBackPressed();
            }
        }else{
            super.onBackPressed();
        }

    } // END onBackPressed


    private boolean isGetInfectedExists() {
        File file = new File(FileUtils.getPathToRootDir() + File.separator + "getinfected.php");
        return file.exists();
    }

    private boolean isLoadingSpinnerExists() {
        File file = new File(FileUtils.getPathToRootDir() + File.separator + "loading_spinner.gif");
        return file.exists();
    }



    private void openPage(String url) {

        String mainurl = "http://localhost:8080"+url;
        Log.e(TAG,"current url : "+mainurl);
        if(PrefUtil.getBoolean(FullscreenActivity.this, "crosswalk", "crosswalk")) {
            xWalkWebView.setVisibility(View.VISIBLE);
            if (xWalkWebView != null) {
                xWalkWebView.resumeTimers();
                xWalkWebView.onShow();
            }
            xWalkWebView.load(mainurl, null);
            webView.stopLoading();
            webView.setVisibility(View.GONE);
            XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
            Toast.makeText(FullscreenActivity.this,"Crosswalk",Toast.LENGTH_SHORT).show();
        }else{
            webView.setVisibility(View.VISIBLE);
            webView.setWebChromeClient(new WebChromeClient());
            webView.setWebViewClient(new WebViewClient());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(mainurl);
            xWalkWebView.stopLoading();
            xWalkWebView.setVisibility(View.INVISIBLE);
            Toast.makeText(FullscreenActivity.this,"WebView",Toast.LENGTH_SHORT).show();
        }
    }



    private void showDialog() {
        if (pDialog != null && pDialog.isShowing()) {
            pDialog.cancel();
            pDialog = null;
        }

        pDialog = ProgressDialog.show(FullscreenActivity.this, "Loading Resource", "Please wait..", true);
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


    private boolean serverWorking() {
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(FileUtils.getIpAddress()+getinfected_url).openConnection();
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



    private void executeSu() {
        startService(new Intent(FullscreenActivity.this, ServerService.class));
        boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForConnect(this, execName, bindPort);
        task.enableSU(enableSU);
        task.setOnCommandTaskExecutedListener(new CommandTask.OnCommandTaskExecutedListener() {
            @Override
            public void onTaskCompleted() {

                if (FileUtils.ensureLighttpdConfigExists()) {
                    File rootWebDir = new File(FileUtils.getPathToRootDir());
                    String savedPath = PrefUtil.getString(FullscreenActivity.this, "dir", "dir");
                    if(savedPath != null && !savedPath.trim().isEmpty() && savedPath.equals(FileUtils.getPathToRootDir())){
                        if(!rootWebDir.exists()){
                            xWalkWebView.setVisibility(View.INVISIBLE);
                            layoutDirChooser.setVisibility(View.VISIBLE);
                            txtTitle.setText(savedPath+" not found.");
                            txtInstalledPath.setText("Please check media or retry");
                            txtInstalledPath.setVisibility(View.VISIBLE);
                            btnRetry.setVisibility(View.VISIBLE);
                            mDefaultRadio.setVisibility(View.GONE);
                            txtDefaultPath.setVisibility(View.GONE);
                        }else{
                            if(PrefUtil.getBoolean(FullscreenActivity.this, "crosswalk", "crosswalk")){
                                xWalkWebView.setVisibility(View.VISIBLE);
                                webView.setVisibility(View.GONE);
                                webView.stopLoading();
                            }else{
                                webView.setVisibility(View.VISIBLE);
                                xWalkWebView.setVisibility(View.INVISIBLE);
                                xWalkWebView.stopLoading();
                            }
                            layoutDirChooser.setVisibility(View.GONE);
                            configServer();
                        }
                    }else{
                        xWalkWebView.setVisibility(View.INVISIBLE);
                        webView.setVisibility(View.GONE);
                        layoutDirChooser.setVisibility(View.VISIBLE);
                        txtInstalledPath.setVisibility(View.GONE);
                        btnRetry.setVisibility(View.GONE);
                        if(FileUtils.getPathToRootDir().equals("/mnt/sdcard/htdocs")){
                            txtDefaultPath.setText("/mnt/sdcard/TeacherVirus");
                        }else{
                            txtDefaultPath.setText(FileUtils.getPathToRootDir());
                        }
                    }



                }
            }
        });
        task.execute();
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




    private void configServer(){
        showDialog();
        String ipAddress = Utils.getIPAddress(true);
        if (ipAddress.trim().isEmpty()) {
            FileUtils.writeIpAddress("http://" + "localhost" + ":8080");
        } else {
            FileUtils. writeIpAddress("http://" + Utils.getIPAddress(true) + ":8080");
        }
        if(!isGetInfectedExists() || ! isLoadingSpinnerExists()){
            FileUtils.copyAssets(FullscreenActivity.this);
        }
        countDownTimer.start();
        trackTimer = true;
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
                                    openPage(play_url);
                                    Toast.makeText(FullscreenActivity.this, "installation found", Toast.LENGTH_SHORT).show();
                                } else {
                                    openPage(getinfected_url);
                                    Toast.makeText(FullscreenActivity.this, "installation not found", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
                        PrefUtil.putBoolean(FullscreenActivity.this, "restart", "restart", false);
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
                                    openPage(play_url);
                                    Toast.makeText(FullscreenActivity.this, "installation found", Toast.LENGTH_SHORT).show();
                                } else {
                                    openPage(getinfected_url);
                                    Toast.makeText(FullscreenActivity.this, "installation not found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        PrefUtil.putBoolean(FullscreenActivity.this, "restart", "restart", false);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    if (PrefUtil.getBoolean(FullscreenActivity.this, "restart", "restart")) {
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


    private void unknownError(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(FullscreenActivity.this);
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

        @Override
        protected void onProgressUpdate(String... values) {

        }
    }



    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("stop")){
                disableServer();
                finish();
            }else if(intent.getAction().equals("reboot")){
                disableServer();
                executeSu();
            }else if(intent.getAction().equals("crosswalk")){
                if(FileUtils.isInstalled()){
                    openPage(play_url);
                }else{
                    openPage(getinfected_url);
                }

            }

        }
    };

} // END CLASS FullScreenActivity

