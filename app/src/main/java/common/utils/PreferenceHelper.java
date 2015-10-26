package common.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Win124
 * 
 * PreferenceHelper.class
 * 
 * Used to store and retrieve key value pare for various data type
 * Static methods for storing & retrieving values.
 *
 */
public class PreferenceHelper {

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="preference file name resourceId"
	 * @param key="resourceId of key"
	 * @param value="String value you want to store"
	 */
	public static void putString(Context mContext, String fileName, String key,
                                String value) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putString(key, value);
		mEditor.commit();
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="preference file name resourceId"
	 * @param key="resourceId of key"
	 * @param value="int value you want to store"
	 */
	public static void putInt(Context mContext, String fileName, String key,
                                int value) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="preference file name resourceId"
	 * @param key="resourceId of key"
	 * @param value="float value you want to store"
	 */
	public static void putFloat(Context mContext, String fileName, String key,
                                float value) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putFloat(key, value);
		mEditor.commit();
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="preference file name resourceId"
	 * @param key="resourceId of key"
	 * @param value="long value you want to store"
	 */
	public static void putLong(Context mContext, String fileName, String key,
                                long value) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putLong(key, value);
		mEditor.commit();
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="preference file name resourceId"
	 * @param key="resourceId of key"
	 * @param value="boolean value you want to store"
	 */
	public static void putBoolean(Context mContext, String fileName, String key,
                                boolean value) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}
	
	
	
	

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="resource id of filename"
	 * @param key="resource id of key"
	 * @return="string value"
	 */
	public static String getString(Context mContext, String fileName,
                                        String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);

		return mSharedPreferences.getString(key,
				null);
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="resource id of filename"
	 * @param key="resource id of key"
	 * @return="int value"
	 */
	public static int getInt(Context mContext, String fileName, String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);

		return mSharedPreferences.getInt(key, -1);
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="resource id of filename"
	 * @param key="resource id of key"
	 * @return="boolean value"
	 */
	public static boolean getBoolean(Context mContext, String fileName,
			String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		return mSharedPreferences.getBoolean(key,
				false);
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="resource id of filename"
	 * @param key="resource id of key"
	 * @return="float value"
	 */
	public static float getFloat(Context mContext, String fileName,
			String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		return mSharedPreferences.getFloat(key, -1);
	}

	/**
	 * @param mContext="context of calling class"
	 * @param fileName="resource id of filename"
	 * @param key="resource id of key"
	 * @return="long value"
	 */
	public static long getLong(Context mContext, String fileName, String key) {

		SharedPreferences mSharedPreferences = mContext.getSharedPreferences(
				fileName, Context.MODE_PRIVATE);
		return mSharedPreferences.getLong(key, -1);
	}



    public static void  remove(Context context,String filename,String key){

        SharedPreferences mSharedPreferences = context.getSharedPreferences(
                filename, Context.MODE_PRIVATE);

        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(key);
        mEditor.commit();
    }

}
