package com.masato25.parpcamara;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by masato on 2017/7/6.
 */

public class NetWorkingTmp {

    protected String request(String urlString, String method, JSONObject jsobj) {
        StringBuffer chaine = new StringBuffer("");
        String result = "";
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setRequestProperty("User-Agent", "PARP-test");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("auth-token", "parp-tester");
            connection.setRequestMethod(method);
            connection.setDoInput(true);
            OutputStream sbody= connection.getOutputStream();
            sbody.write(jsobj.toString().getBytes("UTF-8"));
            sbody.close();
            connection.connect();
            connection.getResponseCode();

            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
                Log.d("test-net", line);
                result += result + line.toString() + "\n";
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
        Log.d("dddd-result", result);
        return result;
    }
}