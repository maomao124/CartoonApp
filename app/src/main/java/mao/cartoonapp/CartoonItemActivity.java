package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import mao.cartoonapp.application.MainApplication;

public class CartoonItemActivity extends AppCompatActivity
{

    private static final String TAG = "CartoonItemActivity";

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartoon_item);

        listView = findViewById(R.id.ListView);

        Intent intent = getIntent();
        if (intent == null)
        {
            toastShow("无法获取数据");
            return;
        }
        Bundle bundle = intent.getExtras();
        String id = bundle.getString("id");
        String name = bundle.getString("name");
        String author = bundle.getString("author");
        String imgUrl = bundle.getString("imgUrl");
        if (id == null)
        {
            toastShow("无法获取数据");
            return;
        }
        //toastShow(name);
        TextView textView_name = findViewById(R.id.name);
        TextView textView_author = findViewById(R.id.author);
        ImageView imageView = findViewById(R.id.ImageView);
        textView_name.setText(name);
        textView_author.setText(author);
        MainApplication.getInstance().getThreadPool().submit(new Runnable()
        {
            @Override
            public void run()
            {
                Bitmap bitmap = openImageByHTTP(imgUrl);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(String message)
    {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 从网络上加载图片
     *
     * @param imgUrl img url
     * @return {@link Bitmap}
     */
    public Bitmap openImageByHTTP(String imgUrl)
    {
        Bitmap bitmap;
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try
        {
            URL url = new URL(imgUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            inputStream = httpURLConnection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }
        catch (Exception e)
        {
            //加载失败，直接加载默认的图片
            Log.e(TAG, "openImageByHTTP: ", e);
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round);
            return bitmap;
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            if (httpURLConnection != null)
            {
                httpURLConnection.disconnect();
            }
        }
    }
}