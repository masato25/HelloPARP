package com.masato25.hellopapp;


import android.provider.Settings;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by masato on 2017/6/27.
 */

public class NetWorkingTmp {

    protected StringBuffer request(String urlString) {
        StringBuffer chaine = new StringBuffer("");
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setRequestProperty("User-Agent", "PARP-test");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("auth-token", "parp-tester");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }
        return chaine;
    }

    protected StringBuffer requestAvatar(String urlString, Avatar avatar) {
        StringBuffer chaine = new StringBuffer("");
        JSONObject avaobj = new JSONObject();
        JSONObject avajs = new JSONObject();

        try{
            avajs.put("name", avatar.GetName());
            avajs.put("address", avatar.GetAddr());
            avajs.put("bluetooth_status", avatar.GetStatus());
            avajs.put("bluetooth_type", avatar.GetType());
            avajs.put("coordinate", avatar.GetCoordinate());
            avaobj.put("avatar", avajs);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("User-Agent", "PARP-test");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("auth-token", "parp-tester");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            OutputStream sbody= connection.getOutputStream();
            sbody.write(avaobj.toString().getBytes("UTF-8"));
            sbody.close();
            connection.connect();
            connection.getResponseCode();
            Log.v("PARPServer", connection.getResponseMessage());

//            InputStream inputStream = connection.getInputStream();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
                Log.v("PARPServer", line);
            }
            rd.close();
            connection.disconnect();
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return chaine;
    }

    protected StringBuffer requestLeaveAvatar(String urlString, String address) {
        StringBuffer chaine = new StringBuffer("");
        JSONObject avajs = new JSONObject();

        try{
            avajs.put("address", address);
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("User-Agent", "PARP-test");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("auth-token", "parp-tester");
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            OutputStream sbody= connection.getOutputStream();
            sbody.write(avajs.toString().getBytes("UTF-8"));
            sbody.close();
            connection.connect();
            connection.getResponseCode();
            Log.v("PARPServer", connection.getResponseMessage());

//          InputStream inputStream = connection.getInputStream();
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
                Log.v("PARPServer", line);
            }
            rd.close();
            connection.disconnect();
        }
        catch (IOException e) {
            // Writing exception to log
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {

        }
        return chaine;
    }
}