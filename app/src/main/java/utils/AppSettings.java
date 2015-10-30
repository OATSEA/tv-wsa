package utils;

import android.content.Context;

import org.teachervirus.BuildConfig;

import java.io.File;

import common.utils.FileUtils;
import common.utils.PrefUtil;

/**
 * Created by warlock on 30-10-2015.
 */
public class AppSettings {

    public static final String KEY_SETTINGS = "Settings";
    public static final String KEY_VERSION_NAME = "version_name";
    public static final String KEY_INSTALLATION_DIR = "installation_dir";
    public static final String KEY_CROSSWALk = "crosswalk";

    public static final String DROID_PHP_DIR = "/mnt/sdcard/droidphp";
    public static final String INSTALLATION_DEFAULT_DIR = "/mnt/sdcard/TeacherVirus";
    public static final String PLAY_URL = "/play";
    public static final String GETINFECTED_URL = "/getinfected.php";

    public static String getDefaultInstallationPath(Context context){
        String defaultDir = INSTALLATION_DEFAULT_DIR;
        String pathFromPrefs = PrefUtil.getString(context,KEY_SETTINGS,KEY_INSTALLATION_DIR);

        if(FileUtils.ensureLighttpdConfigExists() && pathFromPrefs.equals(FileUtils.getPathToRootDir())){
            defaultDir = pathFromPrefs;
        }else if(!pathFromPrefs.equals("")){
            defaultDir = pathFromPrefs;
        }
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

    public static File getDefaultInstallFile(){
        File file = new File(INSTALLATION_DEFAULT_DIR);
        return file;
    }


    //user installation dir
    public static boolean installationDirExists(Context context){
        String dir = PrefUtil.getString(context,KEY_SETTINGS,INSTALLATION_DEFAULT_DIR);
        File file = new File(dir);
        return file.exists();
    }

    public static File getInstalledFile(Context context){
        String dir = PrefUtil.getString(context,KEY_SETTINGS,INSTALLATION_DEFAULT_DIR);
        File file = new File(dir);
        return file;
    }

    //get root directory
    public static File getRootDir(Context context){
        return new File(getDefaultInstallationPath(context));
    }

    public static void deletePreviousInstallation(Context context){
        if(droidPhpExists()){
            getDroidPhpFile().delete();
        }
        if(defaultInstallationDirExists()){
            getDefaultInstallFile().delete();
        }

        if(installationDirExists(context)){
            getInstalledFile(context).delete();
        }
    }

    //update version name
    public static void updateVersionName(Context context){
        PrefUtil.putString(context, KEY_SETTINGS, KEY_VERSION_NAME, BuildConfig.VERSION_NAME);
    }

    //update installation directoryPath()
    public static void updateInstallationDirectory(Context context,String path){
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

}
