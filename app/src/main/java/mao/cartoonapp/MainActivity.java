package mao.cartoonapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity
{

    /**
     * 标签
     */
    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startActivity(new Intent(this, ContentActivity.class));
    }


    /**
     * 从指定路径的图片文件中读取位图数据
     *
     * @param path 路径
     * @return {@link Bitmap}
     */
    public Bitmap openImage(String path)
    {
        // 声明一个位图对象
        Bitmap bitmap = null;
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fileInputStream = new FileInputStream(path))
        {
            // 从文件输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
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


    /**
     * 从指定路径的图片文件中读取位图数据
     *
     * @param file File对象
     * @return {@link Bitmap}
     */
    public Bitmap openImage(File file)
    {
        // 声明一个位图对象
        Bitmap bitmap = null;
        // 根据指定的文件路径构建文件输入流对象
        try (FileInputStream fileInputStream = new FileInputStream(file))
        {
            // 从文件输入流中解码位图数据
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 把位图数据保存到指定路径的图片文件
     *
     * @param path   路径
     * @param bitmap Bitmap对象
     */
    public boolean saveImage(String path, Bitmap bitmap)
    {
        // 根据指定的文件路径构建文件输出流对象
        try (FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            // 把位图数据压缩到文件输出流中
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }
}