package mao.cartoonapp.application;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mao.cartoonapp.net.RestfulHTTP;
import mao.cartoonapp.net.SimpleRestfulHTTPImpl;
import mao.cartoonapp.service.CartoonService;
import mao.cartoonapp.service.CartoonServiceImpl;

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


    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
        mainApplication = this;
        threadPool = Executors.newFixedThreadPool(6);
        http = new SimpleRestfulHTTPImpl();
        http.setReadTimeout(10000);
        http.setThreadPool(threadPool);

        cartoonService = new CartoonServiceImpl(http);
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
}
