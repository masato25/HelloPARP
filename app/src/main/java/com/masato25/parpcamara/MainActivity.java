package com.masato25.parpcamara;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {
    private ImageView imgFavorite;
    private EditText et1;
    private Button bu1;
    private String encoded;
    private NetWorkingTmp httcli = new NetWorkingTmp();
    private Toast toast;
    private Context context;
    private int duration;
    TessBaseAPI baseApi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this.context;
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        context = this.getBaseContext();
        duration = Toast.LENGTH_SHORT;

        toast = Toast.makeText(context, "hello", duration);

        //test detection
        //训练数据路径，必须包含tesseract文件夹
        File directory = new File("/storage/CDF9-840C/tesseract/");
        //识别语言英文
        final String TESSBASE_PATH = directory.toString();
        final String DEFAULT_LANGUAGE = "eng";
        baseApi = new TessBaseAPI();
        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);


        // get permission from  android 6.0
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.INTERNET}, 1);
        }
        if(!(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED)){
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        }
        imgFavorite = (ImageView)findViewById(R.id.imageView1);
        imgFavorite.setAdjustViewBounds(true);
        et1 = (EditText)findViewById(R.id.editText1);
        bu1 = (Button)findViewById(R.id.button1);
        // 開啟camara
        final Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toast.show();
                startActivityForResult(intent,0);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        bu1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                JSONObject jsobj = new JSONObject();
                try {
                    jsobj.put("name", et1.getText().toString());
                    jsobj.put("img", encoded);
                    jsobj.put("parking_no", et1.getText().toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                WebAccessThread at = new WebAccessThread("http://${myserver}/api/v1/uploadimg", httcli, "POST", jsobj);
                at.setToast(toast);
                at.setContext(context);
                Thread t = new Thread(at);
                t.run();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        if(bp != null) {
            imgFavorite.getLayoutParams().width = bp.getWidth();
            imgFavorite.getLayoutParams().height = bp.getHeight();
            imgFavorite.setImageBitmap(bp);
            baseApi.setImage(bp);
            toast = Toast.makeText(context, "show: " + baseApi.getUTF8Text(), duration);
            toast.show();
            baseApi.clear();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.v("test", encoded);
        }else{
            toast = Toast.makeText(context, "bitmap is null", duration);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
