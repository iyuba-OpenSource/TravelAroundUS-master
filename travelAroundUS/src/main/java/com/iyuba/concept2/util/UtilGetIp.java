package com.iyuba.concept2.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class UtilGetIp {

	public static String getLocalIpAddress() {  
        try {  
            String ipv4;  
            List<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
            for (NetworkInterface ni: nilist)   
            {  
                List<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
                for (InetAddress address: ialist){  
                    if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
                    {   
                        return ipv4;  
                    }  
                }  
   
            }  
   
        } catch (SocketException ex) {  
            Log.e("SocketException", ex.toString());  
        }  
        return null;  
    }  
	
	/**
	 * 获取客户端IP？
	 */
	public static String GetNetIp()  
    {  
        URL infoUrl = null;  
        InputStream inStream = null;  
        
        try
        {  
            //http://iframe.ip138.com/ic.asp  
            //infoUrl = new URL("http://city.ip138.com/city0.asp");  
            infoUrl = new URL("http://www.cz88.net/ip/viewip778.aspx");  
            URLConnection connection = infoUrl.openConnection();  
            HttpURLConnection httpConnection = (HttpURLConnection)connection;  
            
            int responseCode = httpConnection.getResponseCode();  
            if(responseCode == HttpURLConnection.HTTP_OK)  
            {   
                inStream = httpConnection.getInputStream();   
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,"utf-8"));  
                StringBuilder strber = new StringBuilder();  
                String line = null;  
                
                while ((line = reader.readLine()) != null)   
                    strber.append(line + "\n");  
                inStream.close();  
                
                //从反馈的结果中提取出IP地址  
                int start = strber.indexOf("IPMessage")+11;  
                int end = strber.indexOf("</span>", start);  
                line = strber.substring(start, end);  
                
                return line;   
            }  
        }  
        catch(MalformedURLException e) {  
            e.printStackTrace();  
        }  
        catch (IOException e) {  
            e.printStackTrace();  
        }  
        return null;  
    }  

}
