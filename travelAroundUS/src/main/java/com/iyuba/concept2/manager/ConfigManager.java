package com.iyuba.concept2.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author gyx
 * 
 *         功能：配置文件管理
 * 
 */
public class ConfigManager {
	private volatile static ConfigManager instance;

	//public static final String CONFIG_NAME = "config";

	//private Context context;

	private SharedPreferences.Editor editor;

	private SharedPreferences preferences;

	public static ConfigManager Instance(Context context) {
		if (instance == null) {
			synchronized (ConfigManager.class) {
				if (instance == null) {
					instance = new ConfigManager(context);
				}
			}
		}
		return instance;
	}

//	private ConfigManager() {
//
//	
//		//this.context = crashapplication.getContext();
//
//		openEditor();
//	}
	private ConfigManager(Context context) {

		openEditor(context);
	}

	// 创建或修改配置文件
	public void openEditor(Context context) {
		int mode = Activity.MODE_PRIVATE;
		if (context==null) {
			return ;
		}
		preferences =PreferenceManager.getDefaultSharedPreferences(context);
		//preferences = context.getSharedPreferences(CONFIG_NAME, mode);
		editor = preferences.edit();
	}

	public void putBoolean(String name, boolean value) {

		editor.putBoolean(name, value);
		editor.commit();
	}

	public void putFloat(String name, float value) {

		editor.putFloat(name, value);
		editor.commit();
	}

	public void putInt(String name, int value) {

		editor.putInt(name, value);
		editor.commit();
	}

	public void putLong(String name, long value) {

		editor.putLong(name, value);
		editor.commit();
	}

	public void putString(String name, String value) {

		editor.putString(name, value);
		editor.commit();
	}
	public void remove(String name) {
		editor.remove(name);
		editor.commit();
	}

	/**
	 * 对象存储
	 * 
	 * @param name
	 * @param value
	 * @throws IOException
	 */
	public void putString(String name, Object value) throws IOException {
		// 把值对象以流的形式转化为字符串。
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(value);
		String productBase64 = new String(Base64.encodeBase64(baos
				.toByteArray()));
		// Log.e("ConfigManager", productBase64);
		putString(name, productBase64);
		oos.close();
	}

	public boolean loadBoolean(String key) {
		return preferences.getBoolean(key, false);
	}

	public float loadFloat(String key) {
		return preferences.getFloat(key, 0);
	}

	public int loadInt(String key) {
		return preferences.getInt(key, 0);
	}

	public long loadLong(String key) {
		return preferences.getLong(key, 0);
	}

	public String loadString(String key) {
		return preferences.getString(key, "");
	}

	/**
	 * 读取对象格式
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws StreamCorruptedException
	 * @throws ClassNotFoundException
	 */
	public Object loadObject(String key) throws StreamCorruptedException,
			IOException, ClassNotFoundException {
		String objBase64String = loadString(key);
		byte[] b = Base64.decodeBase64(objBase64String.getBytes());
		InputStream bais = new ByteArrayInputStream(b);
		ObjectInputStream ois = new ObjectInputStream(bais); // something wrong
		Object obj = ois.readObject();
		ois.close();
		return obj;
	}
	public static List<String> list=new ArrayList<String>(20);
	{
		list.add("autonotice");
		list.add("textColor");
		list.add("screenLit");
		list.add("media_saving_path");
		list.add("applanguage");
		list.add("bell_is_on");
		list.add("autonotice");
		list.add("showChinese");
		list.add("mode");
		list.add("syncho");
		list.add("textSize");
		list.add("autoLogin");
		list.add("backColor");
	}

}

