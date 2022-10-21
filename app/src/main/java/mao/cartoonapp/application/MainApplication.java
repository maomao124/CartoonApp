package mao.cartoonapp.application;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import mao.cartoonapp.CartoonItemActivity;
import mao.cartoonapp.FavoritesActivity;
import mao.cartoonapp.R;
import mao.cartoonapp.constant.OtherConstant;
import mao.cartoonapp.constant.URLConstant;
import mao.cartoonapp.dao.CartoonFavoritesDao;
import mao.cartoonapp.dao.CartoonUpdateDao;
import mao.cartoonapp.entity.Cartoon;
import mao.cartoonapp.entity.CartoonItem;
import mao.cartoonapp.entity.CartoonUpdate;
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
        threadPool = new ThreadPoolExecutor(2, 198,
                100L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());
        http = new SimpleRestfulHTTPImpl();
        http.setConnectTimeout(16000);
        http.setReadTimeout(10000);
        http.setThreadPool(threadPool);

        createNotificationChannel("1", "漫画更新通知", NotificationManager.IMPORTANCE_HIGH);
        createNotificationChannel("2", "普通通知", NotificationManager.IMPORTANCE_HIGH);

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
        String imgPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + cartoon.getId() + ".jpg";
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
     * 删除一个目录里面的所有文件
     *
     * @param dir dir
     * @return boolean
     */
    @SuppressWarnings("all")
    public boolean deleteDirFile(File dir)
    {
        if (dir == null || !dir.exists() || !dir.isDirectory())
        {
            return false;
        }
        File[] listFiles = dir.listFiles();
        if (listFiles == null)
        {
            return false;
        }
        for (File file : listFiles)
        {
            if (file.isFile())
            {
                file.delete(); // 删除所有文件
            }
            else if (file.isDirectory())
            {
                deleteDirFile(file); // 递规的方式删除文件夹
            }
        }
        return true;
    }


    public void startCartoonUpdate(Activity activity)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (isToday(activity))
                    {
                        Log.d(TAG, "run: 今天已经检查过漫画更新");
                        return;
                    }
                    Log.d(TAG, "run: 今天还没有开始更新，即将检查漫画更新");
                    cartoonUpdate(activity);
                    saveToday();
                }
                catch (Exception e)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("错误")
                            .setMessage("异常内容：\n" + e)
                            .setPositiveButton("我知道了", null)
                            .create()
                            .show();
                }
            }
        }).start();
    }

    /**
     * 漫画更新
     *
     * @param activity 活动
     */
    private void cartoonUpdate(Activity activity)
    {
        CartoonFavoritesDao cartoonFavoritesDao = CartoonFavoritesDao.getInstance(this);
        List<Cartoon> cartoonList = cartoonFavoritesDao.queryAll();
        if (cartoonList.size() == 0)
        {
            return;
        }

        List<Cartoon> updateList = new ArrayList<>();

        for (Cartoon cartoon : cartoonList)
        {
            List<CartoonItem> cartoonItem = cartoonService.getCartoonItem(Integer.parseInt(cartoon.getId()));
            if (cartoonItem == null || cartoonItem.size() == 0)
            {
                continue;
            }
            CartoonUpdateDao cartoonUpdateDao = CartoonUpdateDao.getInstance(activity);
            CartoonUpdate cartoonUpdate = cartoonUpdateDao.queryById(cartoon.getId());
            if (cartoonUpdate == null)
            {
                Log.d(TAG, "cartoonUpdate: cartoonUpdate为空，发送更新通知：" + cartoon.getName());
                //这里不应该发送更新通知
//                activity.runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        Intent intent = new Intent(activity, CartoonItemActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("id", cartoon.getId());
//                        bundle.putString("name", cartoon.getName());
//                        bundle.putString("author", cartoon.getAuthor());
//                        bundle.putString("imgUrl", cartoon.getImgUrl());
//                        intent.putExtras(bundle);
//                        PendingIntent pendingIntent = PendingIntent.
//                                getActivity(activity, Integer.parseInt(cartoon.getId()),
//                                        intent, 0);
//                        sendNotification("1", Integer.parseInt(cartoon.getId()), "漫画更新通知",
//                                "您收藏的漫画\"" + cartoon.getName() + "\"已更新，当前最新章节为\"" + cartoonItem.get(0).getName() + "\"",
//                                CartoonItemActivity.class, pendingIntent, R.mipmap.ic_launcher_round, loadImage(cartoon));
//                    }
//                });
                cartoonUpdateDao.insert(new CartoonUpdate().setId(cartoon.getId()).setItemCount(cartoonItem.size()));
                //更新标记
                updateList.add(cartoon.setRemarks("漫画已更新：" + cartoonItem.get(0).getName()));
                continue;
            }
            if (cartoonUpdate.getItemCount() < cartoonItem.size())
            {
                Log.d(TAG, "cartoonUpdate: 漫画：" + cartoon.getName() + "已更新，发送更新通知");
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Intent intent = new Intent(activity, CartoonItemActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", cartoon.getId());
                        bundle.putString("name", cartoon.getName());
                        bundle.putString("author", cartoon.getAuthor());
                        bundle.putString("imgUrl", cartoon.getImgUrl());
                        intent.putExtras(bundle);
                        PendingIntent pendingIntent = PendingIntent.
                                getActivity(activity, Integer.parseInt(cartoon.getId()),
                                        intent, 0);
                        sendNotification("1", Integer.parseInt(cartoon.getId()), "漫画更新通知",
                                "您收藏的漫画\"" + cartoon.getName() + "\"已更新"
                                        + (cartoonItem.size() - cartoonUpdate.getItemCount()) +
                                        "章，当前最新章节为\"" + cartoonItem.get(0).getName() + "\"",
                                CartoonItemActivity.class, pendingIntent, R.drawable.run, loadImage(cartoon));
                    }
                });
                cartoonUpdateDao.update(cartoonUpdate.setItemCount(cartoonItem.size()));
                //更新标记
                updateList.add(cartoon.setRemarks("漫画已更新：" + cartoonItem.get(0).getName()));
            }
            else
            {
                Log.d(TAG, "cartoonUpdate: 漫画：" + cartoon.getName() + "没有更新");
            }
        }
        for (Cartoon cartoon : updateList)
        {
            cartoonFavoritesDao.update(cartoon);
        }
    }


    private void saveToday()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences = getSharedPreferences("date", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("year", year);
        editor.putInt("month", month);
        editor.putInt("day", day);

        editor.apply();
    }


    private boolean isToday(Activity activity)
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        SharedPreferences sharedPreferences = getSharedPreferences("date", Context.MODE_PRIVATE);

        int year1 = sharedPreferences.getInt("year", -1);
        int month1 = sharedPreferences.getInt("month", -1);
        int day1 = sharedPreferences.getInt("day", -1);

        Log.d(TAG, "isToday: 当前：" + year + "/" + month + "/" + day);
        Log.d(TAG, "isToday: 文件：" + year1 + "/" + month1 + "/" + day1);


        if (year1 == -1)
        {
            Log.d(TAG, "isToday: 未写入过，先保存");
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    sendBaseNotification("2", 1, "您好",
                            "欢迎使用！");
                }
            });
            saveToday();
            return false;
        }
        if (year != year1)
        {
            Log.d(TAG, "isToday: 不是当前天");
            return false;
        }
        if (month != month1)
        {
            Log.d(TAG, "isToday: 不是当前天");
            return false;
        }
        if (day != day1)
        {
            Log.d(TAG, "isToday: 不是当前天");
            return false;
        }
        Log.d(TAG, "isToday: 当前天");
        return true;
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


    /**
     * 创建通知渠道，通知的重要程度默认为NotificationManager.IMPORTANCE_HIGH
     *
     * @param id   id
     * @param name 名字
     */
    private void createNotificationChannel(String id, String name)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    /**
     * 创建通知渠道
     *
     * @param id    id
     * @param name  名字
     * @param level 通知水平,比如：NotificationManager.IMPORTANCE_HIGH
     */
    private void createNotificationChannel(String id, String name, int level)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(id, name, level);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }


    /**
     * 发送基本通知
     *
     * @param channelId 通道标识
     * @param id        id
     * @param title     标题
     * @param content   内容
     */
    private void sendBaseNotification(String channelId, int id, String title, String content)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(id, notification);
    }

    /**
     * 发送基本通知
     *
     * @param channelId 通道标识
     * @param id        id
     * @param title     标题
     * @param content   内容
     * @param cls       点击后要跳转到的Activity类
     */
    private void sendBaseNotification(String channelId, int id, String title, String content, Class<?> cls)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0,
                        new Intent(this, cls), 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(id, notification);
    }


    /**
     * 发送通知
     *
     * @param channelId 通道标识
     * @param id        id
     * @param title     标题
     * @param content   内容
     * @param cls       点击后要跳转到的Activity类
     * @param smallIcon 小图标
     * @param largeIcon 大图标
     */
    private void sendNotification(String channelId, int id, String title, String content,
                                  Class<?> cls, int smallIcon, Bitmap largeIcon)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0,
                        new Intent(this, cls), 0);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(id, notification);
    }


    /**
     * 发送通知
     *
     * @param channelId     通道标识
     * @param id            id
     * @param title         标题
     * @param content       内容
     * @param cls           cls
     * @param pendingIntent 悬而未决意图
     * @param smallIcon     小图标
     * @param largeIcon     大图标
     */
    private void sendNotification(String channelId, int id, String title, String content,
                                  Class<?> cls, PendingIntent pendingIntent, int smallIcon, Bitmap largeIcon)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(id, notification);
    }
}
