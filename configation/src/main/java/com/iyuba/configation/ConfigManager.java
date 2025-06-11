package com.iyuba.configation;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

/**
 * @author gyx
 *         <p>
 *         功能：配置文件管理
 */
public class ConfigManager {
    private volatile static ConfigManager instance;

    public static final String CONFIG_NAME = "config";

    private Context context;

    private SharedPreferences.Editor editor;

    private SharedPreferences preferences;

    public static ConfigManager Instance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private ConfigManager() {

        this.context = RuntimeManager.getContext();

        openEditor();
    }

    private ConfigManager(Context context) {

        this.context = context;

        openEditor();
    }

    // 创建或修改配置文件
    public void openEditor() {
        int mode = Activity.MODE_PRIVATE;
        preferences = context.getSharedPreferences(CONFIG_NAME, mode);
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
        putString(name, productBase64);
        oos.close();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }
    public boolean loadBoolean(String key,boolean value) {
        return preferences.getBoolean(key, value);
    }
    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public float loadFloat(String key, float value) {
        return preferences.getFloat(key, value);
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

    public String loadString(String key, String name) {
        return preferences.getString(key, name);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
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


    //设置动态域名
    private final static String short1 = "short1";
    private final static String short2 = "short2";

    public void setDomainShort(String domainShort){
        putString(short1,domainShort);
    }

    public String getDomainShort(){
        return loadString(short1,"iyuba.cn");
    }

    public void setDomainLong(String domainLong){
        putString(short2,domainLong);
    }

    public String getDomainLong(){
        return loadString(short2,"iyuba.com.cn");
    }

    //设置qq支持和群组支持
    private final static String QQ_EDITOR = "qq_editor";
    private final static String QQ_TECHNICIAN = "qq_technician";
    private final static String QQ_MANAGER = "qq_manager";

    private static final String QQ_GROUP_NUMBER = "sp_qq_group_number";
    private static final String QQ_GROUP_KEY = "sp_qq_group_key";

    public String getQQEditor() {
        return loadString(QQ_EDITOR, "3099007489");
    }

    public void setQQEditor(String qqName) {
        putString(QQ_EDITOR, qqName);
    }

    public String getQQTechnician() {
        return loadString(QQ_TECHNICIAN, "2926711810");
    }

    public void setQQTechnician(String qqName) {
        putString(QQ_TECHNICIAN, qqName);
    }

    public String getQQManager() {
        return loadString(QQ_MANAGER, "572828703");
    }

    public void setQQManager(String qqName) {
        putString(QQ_MANAGER, qqName);
    }

    public String getQQGroupNumber(){
        return loadString(QQ_GROUP_NUMBER,"553959124");
    }

    public void setQQGroupNumber(String qqGroupNumber){
        putString(QQ_GROUP_NUMBER,qqGroupNumber);
    }

    public String getQQGroupKey(){
        return loadString(QQ_GROUP_KEY,"1ez-53Qiy0Q5086C6uwxMdms2AQ2ZmK6");
    }

    public void setQQGroupKey(String qqGroupKey){
        putString(QQ_GROUP_KEY,qqGroupKey);
    }

    private final String banner_Img="banner_Img";
    private final String splash_Img="splash_Img";

    //http://app.iyuba.cn/dev/upload/1679381438179.jpg
    public String getBannerImg() {
        return "http://app."+Constant.IYBHttpHead()+"/dev/upload/1679381438179.jpg";
    }

    public void setBannerImg(String bannerImg) {
        putString(banner_Img,bannerImg);
    }

    //http://app.iyuba.cn/dev/upload/1679379374314.jpg
    public String getSplashImg() {
        return "http://app."+Constant.IYBHttpHead()+"/dev/upload/1679379374314.jpg";
    }

    public void setSplashImg(String splashImg) {
        putString(splash_Img,splashImg);
    }
}
