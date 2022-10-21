package mao.cartoonapp.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mao.cartoonapp.R;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.ImageLoadResult;
import mao.cartoonapp.entity.VersionInfo;
import mao.cartoonapp.net.HTTP;
import mao.cartoonapp.net.RestfulHTTP;
import mao.cartoonapp.net.SimpleHTTPImpl;
import mao.cartoonapp.net.SimpleRestfulHTTPImpl;
import mao.cartoonapp.service.CartoonService;
import mao.cartoonapp.service.CartoonServiceImpl;
import mao.cartoonapp.service.UpdateService;
import mao.cartoonapp.service.UpdateServiceImpl;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.application
 * Class(类名): MainApplication
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 13:42
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class MainApplication extends Application
{
    /**
     * 标签
     */
    private static final String TAG = "MainApplication";

    /**
     * 实例，单例模式
     */
    private static volatile MainApplication mainApplication;

    public Map<String, Object> data = new HashMap<>();

    /**
     * 线程池
     */
    private ExecutorService threadPool;

    /**
     * http
     */
    private RestfulHTTP http;


    private CartoonService cartoonService;

    /**
     * 更新服务
     */
    private UpdateService updateService;

    /**
     * 版本信息
     */
    private VersionInfo versionInfo;

    /**
     * 更新线程
     */
    private Thread updateThread;

    /**
     * 获取更新失败计数
     */
    private int count;


    public ExecutorService getThreadPool()
    {
        return threadPool;
    }

    public RestfulHTTP getHttp()
    {
        return http;
    }

    public CartoonService getCartoonService()
    {
        return cartoonService;
    }

    public static MainApplication getInstance()
    {
        return mainApplication;
    }

    public UpdateService getUpdateService()
    {
        return updateService;
    }

    public Thread getUpdateThread()
    {
        return updateThread;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mainApplication = this;
        threadPool = new ThreadPoolExecutor(2, 7,
                100L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
        http = new SimpleRestfulHTTPImpl();
        http.setConnectTimeout(16000);
        http.setReadTimeout(10000);
        http.setThreadPool(threadPool);

        cartoonService = new CartoonServiceImpl(http);

        HTTP http = new SimpleHTTPImpl();
        http.setThreadPool(threadPool);
        http.setConnectTimeout(30000);
        http.setReadTimeout(20000);

        updateService = new UpdateServiceImpl(http);
    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     */
    @Override
    public void onTerminate()
    {
        super.onTerminate();
        Log.d(TAG, "onTerminate: ");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged: ");
    }


    public Thread runUpdateThread(Activity activity)
    {
        if (versionInfo != null)
        {
            checkVersion(activity);
            return updateThread;
        }

        if (updateThread != null && (updateThread.getState() == Thread.State.RUNNABLE
                || updateThread.getState() == Thread.State.BLOCKED ||
                updateThread.getState() == Thread.State.WAITING ||
                updateThread.getState() == Thread.State.TIMED_WAITING ||
                updateThread.getState() == Thread.State.NEW))
        {
            //线程正在运行
            toastShow(activity, "更新线程还在运行");
        }
        else
        {
            //更新线程运行完毕或者更新线程没有运行过
            updateThread = new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    getVersionInfo();
                    activity.runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            checkVersion(activity);
                        }
                    });
                }
            });
            updateThread.start();
        }
        return updateThread;
    }


    private void getVersionInfo()
    {
        VersionInfo versionInfo = updateService.getVersionInfo();
        if (versionInfo == null)
        {
            if (count > 100)
            {
                return;
            }
            count++;
            getVersionInfo();
        }
        else
        {
            this.versionInfo = versionInfo;
        }
    }

    private void checkVersion(Activity activity)
    {
        if (versionInfo.getVersion() == null)
        {
            toastShow(activity, "无法获取版本信息");
            return;
        }
        if (getString(R.string.version).equals(versionInfo.getVersion()))
        {
            toastShow(activity, "当前的版本是最新的版本");
            return;
        }
        if (versionInfo.getVersionUpdateInfo() == null)
        {
            new AlertDialog.Builder(activity)
                    .setTitle("更新提示")
                    .setMessage("发现新版本!!!\n当前版本是" + getString(R.string.version) + "\n最新版本是" + versionInfo.getVersion())
                    .setPositiveButton("去更新", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            Uri uri = Uri.parse(URLConstant.projectUrl);
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
            return;
        }
        List<String> versionUpdateInfo = versionInfo.getVersionUpdateInfo();
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : versionUpdateInfo)
        {
            stringBuilder.append(s).append("\n");
        }
        new AlertDialog.Builder(activity)
                .setTitle("更新提示")
                .setMessage("发现新版本!!!\n当前版本是" + getString(R.string.version) + "\n最新版本是" + versionInfo.getVersion() + "\n\n" +
                        versionInfo.getVersion() + "版本更新信息：\n" +
                        stringBuilder)
                .setPositiveButton("去更新", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Uri uri = Uri.parse(URLConstant.projectUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }


    /**
     * 加载图片
     *
     * @param cartoon 卡通
     * @return {@link Bitmap}
     */
    public Bitmap loadImage(Cartoon cartoon)
    {
        String imgPath = getExternalCacheDir().toString() + "/" + cartoon.getId() + ".jpg";
        Log.d(TAG, "loadImage: imgPath" + imgPath);
        //从本地加载
        Bitmap bitmap = openImage(imgPath);
        if (bitmap != null)
        {
            //本地存在，直接返回
            Log.d(TAG, "loadImage: 本地存在图片：" + cartoon.getId());
            return bitmap;
        }
        //本地不存在,从网络上加载
        ImageLoadResult imageLoadResult = openImageByHTTP(cartoon.getImgUrl());
        if (imageLoadResult.isStatus())
        {
            Log.d(TAG, "loadImage: 从网络上加载图片成功：" + cartoon.getId());
            //从网络上成功加载，保存到本地
            saveImage(imgPath, imageLoadResult.getBitmap());
            return imageLoadResult.getBitmap();
        }
        //从网络上加载失败，是默认的图片
        return imageLoadResult.getBitmap();
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
            //e.printStackTrace();
        }
        return bitmap;
    }


    public ImageLoadResult openImageByHTTP(String imgUrl)
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
            return new ImageLoadResult(true, bitmap);
        }
        catch (Exception e)
        {
            //加载失败，直接加载默认的图片
            Log.e(TAG, "openImageByHTTP: ", e);
            bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher_round);
            return new ImageLoadResult(false, bitmap);
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
            //e.printStackTrace();
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

    /**
     * 显示消息
     *
     * @param message 消息
     */
    private void toastShow(Activity activity, String message)
    {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
