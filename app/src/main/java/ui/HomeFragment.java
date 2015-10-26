package ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;




import org.teachervirus.Constants;
import org.teachervirus.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import common.utils.FileUtils;
import dialogs.DialogHelpers;
import eu.chainfire.libsuperuser.Shell;

import listeners.OnInflationListener;
import services.ServerService;
import tasks.CommandTask;

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeFragment extends Fragment implements View.OnClickListener {

    private static OnInflationListener sInflateCallback;
    private Switch sEnableServer;
    private SharedPreferences preferences;
    private Context context;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private static String pathToConfig = Environment.getExternalStorageDirectory()
            +File.separator+"droidphp"+File.separator+"conf"+File.separator+"lighttpd.conf";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        prepareView(rootView);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            sInflateCallback = (OnInflationListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnInflateViewListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                new IntentFilter("path"));
        preferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity());
        new AsyncSystemRequirement().execute();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        new ConnectionListenerTask().execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (preferences.getBoolean("enable_server_on_app_startup", false)) {
            
        	Context context = getActivity();
        	context.startService(new Intent(context, ServerService.class));
            
            final boolean enableSU = preferences.getBoolean("run_as_root", false);
            final String execName = preferences.getString("use_server_httpd", "lighttpd");
            final String bindPort = preferences.getString("server_port", "8080");
        	
            CommandTask task = CommandTask.createForConnect(context, execName, bindPort);
            task.enableSU(enableSU);
            task.execute();
        }
        new ConnectionListenerTask().execute();
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }

    protected void prepareView(View view) {
        sEnableServer = (Switch) view.findViewById(R.id.sw_enable_server);
        sEnableServer.setEnabled(true);
        sEnableServer.setOnCheckedChangeListener(new ServerListener());
        view.findViewById(R.id.ll_mysql_shell).setOnClickListener(this);
        view.findViewById(R.id.ll_package).setOnClickListener(this);
        view.findViewById(R.id.ll_vhost).setOnClickListener(this);
        //view.findViewById(R.id.ll_update).setOnClickListener(this);
        view.findViewById(R.id.ll_about).setOnClickListener(this);
        view.findViewById(R.id.ll_uninstall).setOnClickListener(this);
        view.findViewById(R.id.ll_teacher_virus).setOnClickListener(this);
        view.findViewById(R.id.ll_exit).setOnClickListener(this);
        view.findViewById(R.id.ll_dir).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_uninstall) {
            CommandTask task = CommandTask.createForUninstall(context);
            task.execute();
        } else if(view.getId() == R.id.ll_teacher_virus){
            Intent fullScreenIntent = new Intent(getActivity(), FullscreenActivity.class);
            fullScreenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            getActivity().startActivity(fullScreenIntent);
        }else if(view.getId()== R.id.ll_exit){
            disableServer();
            Intent intent = new Intent("stop");
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
            getActivity().finish();

        }else
            sInflateCallback.setOnViewIdReceived(view.getId());
    }

    protected void confirmDialogForInstall() {
        DialogFragment fragment = DialogHelpers.factoryForInstall(context);
        fragment.show(getFragmentManager(), getClass().getSimpleName());
    }


    private String getPathToRootDir(){
        try {
            List<String>  lines= org.apache.commons.io.FileUtils.readLines(new File(pathToConfig),"UTF-8");
            for(String line : lines){

                if(line.startsWith("server.document-root")){
                    Log.e(TAG, line);
                    int startIndex = line.indexOf("\"");
                    String path = line.substring(startIndex + 1, line.length() - 1);
                    return path;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }



    private class AsyncSystemRequirement extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (!FileUtils.checkIfExecutableExists()) {
                confirmDialogForInstall();
                return null;
            }
            return null;
        }
    }

    protected class ServerListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isEnable) {

            boolean enableSU = preferences.getBoolean("run_as_root", false);
            String execName = preferences.getString("use_server_httpd", "lighttpd");
            String bindPort = preferences.getString("server_port", "8080");

            if (isEnable) {
                context.startService(new Intent(context, ServerService.class));
                CommandTask task = CommandTask.createForConnect(context, execName, bindPort);
                task.enableSU(enableSU);
                task.execute();
            } else {
                CommandTask task = CommandTask.createForDisconnect(context);
                task.enableSU(enableSU);
                task.execute();

                NotificationManager notify = (NotificationManager) context.
                        getSystemService(Context.NOTIFICATION_SERVICE);
                notify.cancel(143);
            }
        }
    }

    private class ConnectionListenerTask extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

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
                publishProgress("OK");
            } else {
                publishProgress("ERROR");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("OK")) {
                sEnableServer.setChecked(true);
            }
            if (values[0].equals("ERROR")) {
                sEnableServer.setChecked(false);
            }
        }
    }


    private void disableServer(){
        boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForDisconnect(getActivity());
        task.enableSU(enableSU);
        task.execute();

        NotificationManager notify = (NotificationManager)context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notify.cancel(143);
        context.stopService(new Intent(getActivity(), ServerService.class));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String newPath = intent.getStringExtra("path");
            if(!newPath.equalsIgnoreCase(getPathToRootDir())){

                askToCopy(newPath);
            }

        }
    };

    private void askToCopy(final String newPath){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle("Copy Data");
        builder.setMessage("Would you like to copy all data to new directory.");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                File mOldDir = new File(getPathToRootDir());
                if(mOldDir.isDirectory()) {
                    File[] mFiles = mOldDir.listFiles();
                    for (File file : mFiles) {
                        try {
                            InputStream in = new FileInputStream(file);
                            File outFile = new File(new File(newPath), file.getName());
                            OutputStream out = new FileOutputStream(outFile);
                            copyFile(in, out);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    setNewPath(newPath);
                    askToRestart();
                }



            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               File mOldDir = new File(getPathToRootDir());
                if(mOldDir.isDirectory()){
                    File[] mFiles = mOldDir.listFiles();
                    for(File file:mFiles){

                        String filename = file.getName();
                        if (filename.contains("getinfected.php") || filename.contains("loading_spinner.gif")) {
                            try {
                                InputStream in = new FileInputStream(file);
                                File outFile = new File(new File(newPath), filename);
                                OutputStream out = new FileOutputStream(outFile);
                                copyFile(in, out);

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                    setNewPath(newPath);
                    askToRestart();
                }

            }
        });
        builder.show();
    }



    private void coprFiles(){

    }


    private void setNewPath(String newPath){
        String content = null;

        try {
            content = org.apache.commons.io.FileUtils.readFileToString(new File(pathToConfig), "UTF-8");
            content = content.replaceAll(getPathToRootDir(),newPath);
            File tempFile = new File(pathToConfig);
            org.apache.commons.io.FileUtils.writeStringToFile(tempFile, content, "UTF-8");
        } catch (IOException e) {
            //Simple exception handling, replace with what's necessary for your use case!
            throw new RuntimeException("Generating file failed", e);
        }
    }
    private void askToRestart(){
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity());
        builder.setTitle("Restart Device");
        builder.setMessage("Your device need to Restart to apply changes.");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        builder.show();
    }

    private boolean isGetInfectedExists() {
        File file = new File(getPathToRootDir() + File.separator + "getinfected.php");
        return file.exists();
    }

    private boolean isLoadingSpinnerExists() {
        File file = new File(getPathToRootDir() + File.separator + "loading_spinner.gif");
        return file.exists();
    }

    private void copyAssets() {
        AssetManager assetManager = getActivity().getAssets();
        String[] files = null;
        try {
            files = assetManager.list("");
        } catch (IOException e) {
            Log.e("tag", "Failed to get asset file list.", e);
        }
        if (files != null) for (String filename : files) {
            if (filename.contains("getinfected.php") || filename.contains("loading_spinner.gif")) {
                InputStream in = null;
                OutputStream out = null;
                try {
                    in = assetManager.open(filename);
                    File outFile = new File(new File(getPathToRootDir()), filename);
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
}