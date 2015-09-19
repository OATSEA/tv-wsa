package ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import org.teachervirus.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

// Credits:
// https://gist.github.com/rduplain/2638913
// https://developer.chrome.com/multidevice/webview/gettingstarted

// ** TO DO ***
// 0. Download getinfected.php from github and unzip into /mnt/sdcard/htdocs
//  - with option to download from alternative address if unable to access github
// 1. Start Webserver (DroidPHP) via INTENT?? (and confirm success)
// 2. Test can access web pages on local host
// 3. Provide network IP address of this device so can infect


public class FullscreenActivity extends Activity {

    private WebView myWebView;
    private static final String TAG = FullscreenActivity.class.getSimpleName();

    // Progress Dialog
    private ProgressDialog pDialog;
    public static final int progress_bar_type = 0;
    private static final int DEFAULT_COUNTDOWN_TIME = 60 * 1000; // one minute
    private static final int DEFAULT_INTERVAL = 5000; // 5 second

    private CountDownTimer countDownTimer;

    private static String file_url = "https://www.github.com/OATSEA/getinfected/zipball/master";  // File url to download - github
    private static String getinfected_url = "http://localhost:8080/getinfected.php"; // getinfected installer


    private static String htdocs = Environment.getExternalStorageDirectory() + File.separator + "htdocs";
    private static String up = htdocs + File.separator + "play" + File.separator + "up.html";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);


        countDownTimer = new MyTimer(DEFAULT_COUNTDOWN_TIME, DEFAULT_INTERVAL);
        countDownTimer.start();
        showDialog();

        checkHtdocsOK();
        copyAssets();
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        // Hide Everything but Web Page:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            // Hide the bottom bar and prevent it from reactivating
            // Requires minimym of 11
            // Source: http://stackoverflow.com/questions/11027193/maintaining-lights-out-mode-view-setsystemuivisibility-across-restarts
            final View rootView = getWindow().getDecorView();
            // rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);

            // New Immersive config: https://developer.android.com/training/system-ui/immersive.htm
            rootView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE);

            rootView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if (visibility == View.SYSTEM_UI_FLAG_VISIBLE) {
                        /* rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                        rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                        rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
                        */
                        // New Immersive config: https://developer.android.com/training/system-ui/immersive.html

                        rootView.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                    }
                }
            });
        } // END Hide Everything


    } // END onCreate


    @Override
    protected void onPause() {
        super.onPause();
        dismissDialog();
    }

    private void copyAssets() {
        AssetManager assetManager = getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            if (filename.contains("getinfected.php")) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(new File(htdocs), filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            // NOOP
                        }
                    }
                }
            }
        }
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    private void openPage(String url) {
        myWebView.loadUrl(url);
    }


    public boolean checkHtdocsOK() {
        File file = new File(htdocs);
        if (file.exists()) {
            return true;
        }
        return file.mkdirs();

    } // END checkHtdocsOK


    @Override
    public void onResume() {
        super.onResume();

        // Hide Everything:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            View rootView = getWindow().getDecorView();
            rootView.setSystemUiVisibility(View.STATUS_BAR_VISIBLE);
            rootView.setSystemUiVisibility(View.STATUS_BAR_HIDDEN);
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            rootView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }

    } // END onResume


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


    private void showDialog(){
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.cancel();
            pDialog = null;
        }

        pDialog = ProgressDialog.show(FullscreenActivity.this,"Loading Resource","Please wait..",true);
    }

    private void dismissDialog(){
        if(pDialog!=null && pDialog.isShowing()){
            pDialog.dismiss();
            pDialog=null;
        }
    }


    // Make the back button go "back" in browser rather than back to launcher
    @Override
    public void onBackPressed() {

        if (myWebView.canGoBack()) {
            myWebView.goBack();
        } else {
            super.onBackPressed();
        }
    } // END onBackPressed


    public class MyTimer extends CountDownTimer {


        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(serverWorking()){
                        countDownTimer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                openPage(getinfected_url);
                                Toast.makeText(FullscreenActivity.this,"Get Infected Loaded",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).start();

        }

        @Override
        public void onFinish() {
            dismissDialog();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(serverWorking()){
                        countDownTimer.cancel();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                openPage(getinfected_url);
                                Toast.makeText(FullscreenActivity.this,"Get Infected Loaded",Toast.LENGTH_LONG).show();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(FullscreenActivity.this,"Please restart you device",Toast.LENGTH_LONG).show();
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
            connection = (HttpURLConnection) new URL(getinfected_url).openConnection();
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

    } // END CLASS FullScreenActivity

