package tasks;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class RepoInstallerTask extends AsyncTask<String, String, Boolean> {

    public static final String INSTALL_DONE = "org.opendroidphp.repository.INSTALLED";
    public static final String INSTALL_ERROR = "org.opendroidphp.repository.INSTALL_ERROR";
    private static final String TAG = RepoInstallerTask.class.getSimpleName();
    private String repositoryName;
    private String repositoryPath;
    private  OnRepoInstalledListener  onRepoInstalledListener;
    private Context context;

    public interface OnRepoInstalledListener{
        public void repoInstalled();
    }


    public RepoInstallerTask() {
    }

    public RepoInstallerTask(Context context) {
        this.context = context;
    }

    /* public RepoInstallerTask(Context context) {
        super(context);
    }

    public RepoInstallerTask(Context context, String title, String message) {
        super(context, title, message);
    }
*/

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... file) {
        repositoryName = file[0];
        repositoryPath = file[1];
        boolean isSuccess = true;

        ZipInputStream zipInputStream = null;
        createDirectory("");
        try {
            if (repositoryName == null || repositoryName.equals("")) {
                zipInputStream = new ZipInputStream(context.getAssets().open("data.zip"));
            } else if (new File(repositoryName).exists()) {
                zipInputStream = new ZipInputStream(new FileInputStream(repositoryName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ZipEntry zipEntry;
        try {
            FileOutputStream fout;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (zipEntry.isDirectory()) {
                    createDirectory(zipEntry.getName());
                } else {
                    fout = new FileOutputStream(repositoryPath + "/" + zipEntry.getName());
                    publishProgress(zipEntry.getName());
                    byte[] buffer = new byte[4096 * 10];
                    int length;
                    while ((length = zipInputStream.read(buffer)) != -1) {
                        fout.write(buffer, 0, length);
                    }
                    zipInputStream.closeEntry();
                    fout.close();
                }
            }
            zipInputStream.close();
        } catch (Exception e) {
            isSuccess = false;
            e.printStackTrace();
        }
        publishProgress(isSuccess ? INSTALL_DONE : INSTALL_ERROR);

        return isSuccess;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.e(TAG, "Success  :" + aBoolean);
        if(aBoolean){
            if(onRepoInstalledListener!=null){
                onRepoInstalledListener.repoInstalled();
            }
        }else{

        }

    }

    @Override
    protected void onProgressUpdate(String... values) {
        //setMessage(values[0]);


        if(values[0].equals(INSTALL_DONE) ){

            if(onRepoInstalledListener!=null){
                onRepoInstalledListener.repoInstalled();
            }
        }
       /* if (values[0].equals(INSTALL_DONE) || values[0].equals(INSTALL_ERROR)) {
            int resId = values[0].equals(INSTALL_DONE) ? R.string.core_apps_installed :
                    R.string.core_apps_not_installed;
            AppController.toast(getContext(), getContext().getString(resId));
        }*/
    }

    /**
     * Responsible for creating directory inside application's data directory
     *
     * @param dirName Directory to create during extracting
     */
    protected void createDirectory(String dirName) {
        File file = new File(repositoryPath + dirName);
        if (!file.isDirectory()) file.mkdirs();
    }


    public void setOnRepoInstalledListener(OnRepoInstalledListener onRepoInstalledListener) {
        this.onRepoInstalledListener = onRepoInstalledListener;
    }
}
