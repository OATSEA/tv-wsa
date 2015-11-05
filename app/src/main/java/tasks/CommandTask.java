package tasks;


import android.content.Context;
import android.os.AsyncTask;

import org.teachervirus.Constants;
import org.teachervirus.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;

public class CommandTask extends AsyncTask<String, String, String> {

    public final static String CHANGE_PERMISSION = "/system/bin/chmod 755 ";
    public final static String COMMAND_EXECUTED = "command_executed";

    private List<String> mCommandList;
    private boolean enableSU;
    private int command_executed;
    private OnCommandTaskExecutedListener onCommandTaskExecutedListener;

    private Context mContext;

    public interface OnCommandTaskExecutedListener{
        public void onTaskCompleted();
    }

    public CommandTask() {

    }

    public CommandTask(Context mContext) {
        this.mContext = mContext;
    }

    /* public CommandTask(Context context) {
            super(context);
        }

        public CommandTask(Context context, String title, String message) {
            super(context, title, message);
        }

        public CommandTask(Context context, String title, String message, boolean createDialog) {
            super(context, title, message, createDialog);
        }

        public CommandTask(Context context, int titleResId, int messageResId) {
            super(context, context.getString(titleResId), context.getString(messageResId));
        }

        public CommandTask(Context context, int titleResId, int messageResId, boolean createDialog) {
            super(context, context.getString(titleResId), context.getString(messageResId), createDialog);
        }
    */
    public static CommandTask createForConnect(final Context c, final String execName, final String bindPort) {
    	return createForConnect(c, execName, bindPort, true);
    }
    public static CommandTask createForConnect(final Context c, final String execName, final String bindPort, final boolean createDialog) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(CHANGE_PERMISSION.concat(Constants.INTERNAL_LOCATION + "/scripts/server-sh.sh"));
                add(String.format("%s/scripts/server-sh.sh %s %s", Constants.INTERNAL_LOCATION, execName, bindPort));
            }
        });
        CommandTask task = new CommandTask(c);
        task.addCommand(command);
        task.setNotification(R.string.web_server_is_running);
        return task;
    }

    public static CommandTask createForDisconnect(Context c) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(Constants.INTERNAL_LOCATION + "/scripts/shutdown-sh.sh");
            }
        });
        CommandTask task = new CommandTask(c);
        task.addCommand(command);
        return task;
    }

    public static CommandTask createForUninstall(final Context c) {
        List<String> command = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(String.format("rm -R %s", Constants.INTERNAL_LOCATION.concat("/")));
            }
        });
        CommandTask task = new CommandTask(c);
        task.addCommand(command);
        task.setNotification(R.string.core_apps_uninstalled);
        return task;
    }

    @Override
    protected String doInBackground(String... cmdArgs) {
        checkFilesystem();
        String command[] = mCommandList.toArray(new String[mCommandList.size()]);
        String shell = enableSU ? "su" : "sh";
        List<String> res = Shell.run(shell, command, null, true);
        for (String queryRes : res)
            publishProgress(queryRes);
        publishProgress(COMMAND_EXECUTED);
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //dismissProgress();
        if(onCommandTaskExecutedListener!=null){
            onCommandTaskExecutedListener.onTaskCompleted();
        }
    }

    @Override
    protected void onProgressUpdate(String... queryRes) {
        super.onProgressUpdate(queryRes);
       // setMessage(queryRes[0]);
        if (queryRes[0].equals(COMMAND_EXECUTED)) {
            command_executed = (command_executed == 0) ? R.string.command_executed : command_executed;

        }
    }

    public CommandTask addCommand(List<String> commandList) {
        this.mCommandList = commandList;
        return this;
    }

    public boolean isEnabledSU() {
        return enableSU;
    }

    public CommandTask enableSU(final boolean enableSU) {
        this.enableSU = enableSU;
        return this;
    }

    public CommandTask toggleEnableSU() {
        return enableSU(!enableSU);
    }

    protected CommandTask setNotification(int resId) {
        command_executed = resId;
        return this;
    }


    public void setOnCommandTaskExecutedListener(OnCommandTaskExecutedListener onCommandTaskExecutedListener) {
        this.onCommandTaskExecutedListener = onCommandTaskExecutedListener;
    }

    protected void checkFilesystem() {
        List<String> listFiles = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("/logs");
                add("/conf");
                add("/conf/nginx/logs");
                add("/hosts/nginx");
                add("/hosts/lighttpd");
                add("/sessions");
                add("/packages");
                add("/htdocs");
                add("/tmp");
            }
        });

        for (String filePath : listFiles) {
            File f = new File(Constants.PROJECT_LOCATION.concat(filePath));
            if (!f.exists()) f.mkdirs();
        }
    }

}