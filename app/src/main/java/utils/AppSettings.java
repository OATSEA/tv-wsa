package utils;

import android.content.Context;
import android.util.Log;

import org.teachervirus.BuildConfig;

import java.io.File;
import java.io.IOException;
import java.util.List;

import common.utils.FileUtils;
import common.utils.PrefUtil;

/**
 * Created by warlock on 30-10-2015.
 */
public class AppSettings {
    private static final String TAG = AppSettings.class.getSimpleName();

    public static final String KEY_SETTINGS = "Settings";
    public static final String KEY_VERSION_NAME = "version_name";
    public static final String KEY_INSTALLATION_DIR = "installation_dir";
    public static final String KEY_CROSSWALk = "crosswalk";
    public static final String KEY_GRANT_ACCESS = "grant_access";

    public static final String DROID_PHP_DIR = "/mnt/sdcard/droidphp";
    public static final String INSTALLATION_DEFAULT_DIR = "/mnt/sdcard/TeacherVirus";
    public static final String PLAY_URL = "/play";
    public static final String GETINFECTED_URL = "/getinfected.php";

    public static String getRootDirPath(Context context){
        String defaultDir = INSTALLATION_DEFAULT_DIR;
        String pathFromPrefs = PrefUtil.getString(context,KEY_SETTINGS,KEY_INSTALLATION_DIR);

        if(FileUtils.ensureLighttpdConfigExists() && pathFromPrefs.equals(FileUtils.getPathToRootDir())){
            defaultDir = pathFromPrefs;
        }else if(!pathFromPrefs.equals("")){
            defaultDir = pathFromPrefs;
        }
        Log.e(TAG,"currentRootPath : "+defaultDir);
        return defaultDir;
    }

    //check for droid php dir.
    public static boolean droidPhpExists(){
        File file = new File(DROID_PHP_DIR);
        return file.exists();
    }

    public  static File getDroidPhpFile(){
        return new File(DROID_PHP_DIR);
    }


    //check for default installation dir
    public static boolean defaultInstallationDirExists(){
        File file = new File(INSTALLATION_DEFAULT_DIR);
        return file.exists();
    }

    public static File getDefaultRootFile(){
        File file = new File(INSTALLATION_DEFAULT_DIR);
        return file;
    }


    //user installation dir
    public static boolean installationDirExists(Context context){
        String dir = PrefUtil.getString(context,KEY_SETTINGS,INSTALLATION_DEFAULT_DIR);
        File file = new File(dir);
        return file.exists();
    }


    //get root directory
    public static File getRootDir(Context context){
        return new File(getRootDirPath(context));
    }

    public static void deletePreviousInstallation(Context context){
        if(droidPhpExists()){
            deleteRecursive(getDroidPhpFile());
        }
        if(defaultInstallationDirExists()){
           deleteRecursive( getDefaultRootFile());
        }

        if(getRootDir(context).exists()){

            deleteRecursive(getRootDir(context));
        }
    }

    public  static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    //update version name
    public static void updateVersionName(Context context){
        PrefUtil.putString(context, KEY_SETTINGS, KEY_VERSION_NAME, BuildConfig.VERSION_NAME);
    }

    //update installation directoryPath()
    public static void updateRootDirPath(Context context, String path){
        Log.e(TAG,"updatedPath : "+path);
        PrefUtil.putString(context,KEY_SETTINGS,KEY_INSTALLATION_DIR,path);
        FileUtils.setServerRootDir(context, new File(path));
    }

    // check for application is updated or not
    public static boolean applicationUpdated(Context context){
        boolean updated = false;
        String storedVersion = PrefUtil.getString(context, KEY_SETTINGS, KEY_VERSION_NAME);
        if(!storedVersion.equals(BuildConfig.VERSION_NAME)){
            //updated or fresh install
            updated = true;
        }
        return updated;
    }

    //check if droidphp or teacher virus exists or not
    public static boolean previousInstallationFound(Context context){
        boolean found = false;

            // check for installed  || default location || droid php
            if(installationDirExists(context) || defaultInstallationDirExists() || droidPhpExists()){
                found = true;
            }

        return found;
    }

    //check crosswalk enabled or not
    public static boolean crosswalkEnabled(Context context){
        boolean return_value = true;
        return_value = PrefUtil.getBoolean(context,KEY_SETTINGS,KEY_CROSSWALk);
        return  return_value;
    }

    //set crosswalk enable or disabled
    public static void setCrosswalkEnable(Context context,boolean enable){
        PrefUtil.putBoolean(context,KEY_SETTINGS,KEY_CROSSWALk,enable);
    }


    public static void grantAccess(Context context,boolean allowAccess){

        PrefUtil.putBoolean(context,KEY_SETTINGS,KEY_GRANT_ACCESS,allowAccess);
        grantAccess(allowAccess);
        FileUtils.writeIpAddress(context,Utils.getIPAddress(true));
    }

    public static boolean isAccessGranted(Context context){
        boolean return_value = false;
        return_value = PrefUtil.getBoolean(context,KEY_SETTINGS,KEY_GRANT_ACCESS);
        return return_value;
    }




    public static  void  grantAccess(boolean enable){
        try {
            List<String> lines= org.apache.commons.io.FileUtils.readLines(new File(FileUtils.pathToConfig),"UTF-8");

            for(int i = 0; i<lines.size();i++){

                if(i == 152 || i == 153 || i == 154){
                    boolean commented =lines.get(i).startsWith("#");
                    if(enable){

                        if(!commented){
                            String toreplace = "#"+lines.get(i);
                            Log.e(TAG,"disable "+toreplace);
                            lines.remove(i);
                            lines.add(i,toreplace);
                        }

                    }else{

                        if(commented){
                            String toreplace = lines.get(i).replace("#","");
                            Log.e(TAG,"enable "+toreplace);
                            lines.remove(i);
                            lines.add(i,toreplace);
                        }

                    }
                }
            }

            org.apache.commons.io.FileUtils.writeLines(new File(FileUtils.pathToConfig), "UTF-8", lines);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
