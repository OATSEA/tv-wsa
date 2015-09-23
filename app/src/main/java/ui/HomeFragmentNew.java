package ui;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.teachervirus.Constants;
import org.teachervirus.R;

import java.util.List;

import common.utils.FileUtils;
import dialogs.DialogHelpers;
import eu.chainfire.libsuperuser.Shell;
import listeners.OnInflationListener;
import services.ServerService;
import tasks.CommandTask;
import tasks.RepoInstallerTask;

@android.annotation.TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class HomeFragmentNew extends Fragment implements View.OnClickListener, CommandTask.OnCommandTaskExecutedListener {
    private static final String TAG = HomeFragmentNew.class.getSimpleName();
    private static OnInflationListener sInflateCallback;
    private Switch sEnableServer;
    private SharedPreferences preferences;
    private Context context;

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
        preferences = PreferenceManager.
                getDefaultSharedPreferences(getActivity());

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
        if (!FileUtils.checkIfExecutableExists()) {
            RepoInstallerTask task = new RepoInstallerTask(context);
            task.setOnRepoInstalledListener(new RepoInstallerTask.OnRepoInstalledListener() {
                @Override
                public void repoInstalled() {
                    Log.e(TAG,"Repo installed");
                    new ConnectionListenerTask().execute();
                }
            });
            task.execute("", Constants.INTERNAL_LOCATION.concat("/"));
        }else{
            new ConnectionListenerTask().execute();
        }

    }




    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onStart() {
        super.onStart();

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
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_uninstall) {
            CommandTask task = CommandTask.createForUninstall(context);
            task.execute();
        } else
            sInflateCallback.setOnViewIdReceived(view.getId());
    }

    @Override
    public void onTaskCompleted() {

    }


    @Override
    public void onDetach() {
        disableServer();
        super.onDetach();

    }


    private void disableServer(){
        boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForDisconnect(context);
        task.enableSU(enableSU);
        task.execute();

        NotificationManager notify = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notify.cancel(143);
        context.stopService(new Intent(context,ServerService.class));
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

    private class ConnectionListenerTask extends AsyncTask<Void, String, Boolean> {

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
                context.startService(new Intent(context, ServerService.class));
                executeSu();
            /*try {
                Thread.sleep(3000);
                Intent intent = new Intent(context,FullscreenActivity.class);
                context.startActivity(intent);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            Intent intent = new Intent(context,FullscreenActivity.class);
            context.startActivity(intent);
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


    private void executeSu(){
        boolean enableSU = preferences.getBoolean("run_as_root", false);
        String execName = preferences.getString("use_server_httpd", "lighttpd");
        String bindPort = preferences.getString("server_port", "8080");
        CommandTask task = CommandTask.createForConnect(context, execName, bindPort);
        task.enableSU(enableSU);
        task.setOnCommandTaskExecutedListener(this);
        task.execute();
    }
}