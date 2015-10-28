package common.utils;





import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import org.teachervirus.Constants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

/**
 * Static method for helping user to validates servers configurations
 */

public class FileUtils {
    public static String pathToConfig = Environment.getExternalStorageDirectory()
            +File.separator+"droidphp"+File.separator+"conf"+File.separator+"lighttpd.conf";
    /**
     * Check if required file exist
     *
     * @return boolean
     */
    public static boolean checkIfExecutableExists() {
        return new File(Constants.LIGHTTPD_SBIN_LOCATION).exists() &&
                new File(Constants.PHP_SBIN_LOCATION).exists() &&
                new File(Constants.MYSQL_DAEMON_SBIN_LOCATION).exists() &&
                new File(Constants.MYSQL_MONITOR_SBIN_LOCATION).exists();
    }



    public static  String getPathToRootDir(){
        try {
            List<String> lines= org.apache.commons.io.FileUtils.readLines(new File(pathToConfig),"UTF-8");
            for(String line : lines){

                if(line.startsWith("server.document-root")){
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

    public static  boolean ensureLighttpdConfigExists(){
        File mFile = new File(pathToConfig);
        return mFile.exists();
    }


    public static void writeIpAddress(String ipAddress) {

        File ipFile = new File(FileUtils.getPathToRootDir(), "IP.txt");
        try {
            FileOutputStream f = new FileOutputStream(ipFile);
            PrintWriter pw = new PrintWriter(f);
            pw.print(ipAddress);
            pw.flush();
            pw.close();
            f.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyAssets(Context mContext) {
        AssetManager assetManager = mContext.getAssets();
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
                    File outFile = new File(new File(FileUtils.getPathToRootDir()), filename);
                    out = new FileOutputStream(outFile);
                    copyFile(in, out);
                } catch (IOException e) {
                    Log.e("tag", "Failed to copy asset file: " + filename, e);
                } catch (NullPointerException e){
                    Log.e("Null","null pointer exception");
                }finally {
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

    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }


    public static boolean isInstalled() {
        boolean playExists = false, tempExists = false;
        File parentDir = new File(getPathToRootDir());
        File[] allFiles = parentDir.listFiles();

        for (File file : allFiles) {

            if (file.isDirectory()) {
                if (file.getName().equalsIgnoreCase("Play")) {
                    playExists = true;
                }
                if (file.getName().startsWith("unzip_temp")) {
                    tempExists = true;
                }
            }
        }

        if (playExists && !tempExists) {
            return true;
        }
        return false;
    }


    public static void setServerRootDir(File mServerRootDir){


        String content = null;

        try {
            content = org.apache.commons.io.FileUtils.readFileToString(new File(pathToConfig), "UTF-8");
            content = content.replaceAll(getPathToRootDir(), mServerRootDir.getPath());
            File tempFile = new File(pathToConfig);
            org.apache.commons.io.FileUtils.writeStringToFile(tempFile, content, "UTF-8");
        } catch (IOException e) {
            //Simple exception handling, replace with what's necessary for your use case!
            throw new RuntimeException("Generating file failed", e);
        }
    }
}
