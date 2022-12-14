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
import java.util.concurrent.locks.LockSupport;

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
 * Project name(????????????)???CartoonApp
 * Package(??????): mao.cartoonapp.application
 * Class(??????): MainApplication
 * Author(?????????: mao
 * Author QQ???1296193245
 * GitHub???https://github.com/maomao124/
 * Date(????????????)??? 2022/10/12
 * Time(????????????)??? 13:42
 * Version(??????): 1.0
 * Description(??????)??? ???
 */


public class MainApplication extends Application
{
    /**
     * ??????
     */
    private static final String TAG = "MainApplication";

    /**
     * ?????????????????????
     */
    private static volatile MainApplication mainApplication;

    public Map<String, Object> data = new HashMap<>();

    /**
     * ?????????
     */
    private ExecutorService threadPool;

    /**
     * http
     */
    private RestfulHTTP http;


    private CartoonService cartoonService;

    /**
     * ????????????
     */
    private UpdateService updateService;

    /**
     * ????????????
     */
    private VersionInfo versionInfo;

    /**
     * ????????????
     */
    private Thread updateThread;

    /**
     * ????????????????????????
     */
    private int count;

    /**
     * ????????????????????????
     */
    private volatile boolean isCartoonUpdate;


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

        createNotificationChannel("1", "??????????????????", NotificationManager.IMPORTANCE_HIGH);
        createNotificationChannel("2", "????????????", NotificationManager.IMPORTANCE_HIGH);

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
            //??????????????????
            toastShow(activity, "????????????????????????");
        }
        else
        {
            //?????????????????????????????????????????????????????????
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
            toastShow(activity, "????????????????????????");
            return;
        }
        if (getString(R.string.version).equals(versionInfo.getVersion()))
        {
            toastShow(activity, "?????????????????????????????????");
            return;
        }
        if (versionInfo.getVersionUpdateInfo() == null)
        {
            new AlertDialog.Builder(activity)
                    .setTitle("????????????")
                    .setMessage("???????????????!!!\n???????????????" + getString(R.string.version) + "\n???????????????" + versionInfo.getVersion())
                    .setPositiveButton("?????????", new DialogInterface.OnClickListener()
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
                    .setNegativeButton("??????", null)
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
                .setTitle("????????????")
                .setMessage("???????????????!!!\n???????????????" + getString(R.string.version) + "\n???????????????" + versionInfo.getVersion() + "\n\n" +
                        versionInfo.getVersion() + "?????????????????????\n" +
                        stringBuilder)
                .setPositiveButton("?????????", new DialogInterface.OnClickListener()
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
                .setNegativeButton("??????", null)
                .create()
                .show();
    }


    /**
     * ????????????
     *
     * @param cartoon ??????
     * @return {@link Bitmap}
     */
    public Bitmap loadImage(Cartoon cartoon)
    {
        String imgPath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + "/" + cartoon.getId() + ".jpg";
        Log.d(TAG, "loadImage: imgPath" + imgPath);
        //???????????????
        Bitmap bitmap = openImage(imgPath);
        if (bitmap != null)
        {
            //???????????????????????????
            Log.d(TAG, "loadImage: ?????????????????????" + cartoon.getId());
            return bitmap;
        }
        //???????????????,??????????????????
        ImageLoadResult imageLoadResult = openImageByHTTP(cartoon);
        if (imageLoadResult.isStatus())
        {
            Log.d(TAG, "loadImage: ?????????????????????????????????" + cartoon.getId());
            //??????????????????????????????????????????
            saveImage(imgPath, imageLoadResult.getBitmap());
            return imageLoadResult.getBitmap();
        }
        //?????????????????????????????????????????????
        return imageLoadResult.getBitmap();
    }


    /**
     * ???????????????????????????????????????????????????
     *
     * @param path ??????
     * @return {@link Bitmap}
     */
    public Bitmap openImage(String path)
    {
        // ????????????????????????
        Bitmap bitmap = null;
        // ??????????????????????????????????????????????????????
        try (FileInputStream fileInputStream = new FileInputStream(path))
        {
            // ???????????????????????????????????????
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * ????????????????????????
     *
     * @param imgUrl img url
     * @return {@link ImageLoadResult}
     */
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
            //??????????????????????????????????????????
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
     * ????????????????????????
     *
     * @param cartoon ??????
     * @return {@link ImageLoadResult}
     */
    public ImageLoadResult openImageByHTTP(Cartoon cartoon)
    {
        ImageLoadResult imageLoadResult = openImageByHTTP(cartoon.getImgUrl());
        //????????????????????????
        if (imageLoadResult.isStatus())
        {
            //?????????????????????
            return imageLoadResult;
        }
        //????????????
        //??????????????????
        Cartoon cartoon1 = cartoonService.getCartoonById(cartoon.getId());
        //??????????????????
        if (cartoon1 == null)
        {
            return imageLoadResult;
        }
        //???????????????
        cartoon.setImgUrl(cartoon1.getImgUrl());
        //??????????????????????????????
        return openImageByHTTP(cartoon.getImgUrl());
    }


    /**
     * ???????????????????????????????????????????????????
     *
     * @param file File??????
     * @return {@link Bitmap}
     */
    public Bitmap openImage(File file)
    {
        // ????????????????????????
        Bitmap bitmap = null;
        // ??????????????????????????????????????????????????????
        try (FileInputStream fileInputStream = new FileInputStream(file))
        {
            // ???????????????????????????????????????
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        }
        catch (Exception e)
        {
            //e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param path   ??????
     * @param bitmap Bitmap??????
     */
    public boolean saveImage(String path, Bitmap bitmap)
    {
        // ??????????????????????????????????????????????????????
        try (FileOutputStream fileOutputStream = new FileOutputStream(path))
        {
            // ??????????????????????????????????????????
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
     * ???????????????????????????????????????
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
                file.delete(); // ??????????????????
            }
            else if (file.isDirectory())
            {
                deleteDirFile(file); // ??????????????????????????????
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
                        Log.d(TAG, "run: ?????????????????????????????????");
                        return;
                    }
                    Log.d(TAG, "run: ??????????????????????????????????????????????????????");
                    cartoonUpdate(activity);
                    saveToday();
                }
                catch (Exception e)
                {
                    new AlertDialog.Builder(activity)
                            .setTitle("??????")
                            .setMessage("???????????????\n" + e)
                            .setPositiveButton("????????????", null)
                            .create()
                            .show();
                }
            }
        }).start();
    }

    /**
     * ????????????
     *
     * @param activity ??????
     */
    private void cartoonUpdate(Activity activity)
    {
        Thread currentThread = Thread.currentThread();
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
                Log.d(TAG, "cartoonUpdate: cartoonUpdate??????????????????????????????" + cartoon.getName());
                //?????????????????????????????????
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
//                        sendNotification("1", Integer.parseInt(cartoon.getId()), "??????????????????",
//                                "??????????????????\"" + cartoon.getName() + "\"?????????????????????????????????\"" + cartoonItem.get(0).getName() + "\"",
//                                CartoonItemActivity.class, pendingIntent, R.mipmap.ic_launcher_round, loadImage(cartoon));
//                    }
//                });
                cartoonUpdateDao.insert(new CartoonUpdate().setId(cartoon.getId()).setItemCount(cartoonItem.size()));
                //????????????
                updateList.add(cartoon.setRemarks("??????????????????" + cartoonItem.get(0).getName()));
                continue;
            }
            if (cartoonUpdate.getItemCount() < cartoonItem.size())
            {
                Log.d(TAG, "cartoonUpdate: ?????????" + cartoon.getName() + "??????????????????????????????");
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
                                        intent, PendingIntent.FLAG_IMMUTABLE);
                        sendNotification("1", Integer.parseInt(cartoon.getId()), "??????\"" + cartoon.getName() + "\"????????????",
                                "??????????????????\"" + cartoon.getName() + "\"?????????"
                                        + (cartoonItem.size() - cartoonUpdate.getItemCount()) +
                                        "???????????????????????????\"" + cartoonItem.get(0).getName() + "\"",
                                CartoonItemActivity.class, pendingIntent, R.drawable.run, loadImage(cartoon));
                        LockSupport.unpark(currentThread);
                    }
                });
                LockSupport.park();
                //????????????
                cartoonUpdateDao.update(cartoonUpdate.setItemCount(cartoonItem.size()));
                updateList.add(cartoon.setRemarks("??????????????????" + cartoonItem.get(0).getName()));
            }
            else
            {
                Log.d(TAG, "cartoonUpdate: ?????????" + cartoon.getName() + "????????????");
            }
        }
        for (Cartoon cartoon : updateList)
        {
            cartoonFavoritesDao.update(cartoon);
        }
    }

    /**
     * ????????????
     *
     * @param activity ??????
     */
    public void cartoonUpdateByButton(Activity activity)
    {
        if (isCartoonUpdate)
        {
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    toastShow(activity, "????????????????????????????????????");
                }
            });
        }
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                toastShow(activity, "?????????????????????????????????????????????????????????????????????");
            }
        });
        isCartoonUpdate = true;
        Thread currentThread = Thread.currentThread();
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
                Log.d(TAG, "cartoonUpdate: cartoonUpdate??????????????????????????????" + cartoon.getName());
                //?????????????????????????????????
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
//                        sendNotification("1", Integer.parseInt(cartoon.getId()), "??????????????????",
//                                "??????????????????\"" + cartoon.getName() + "\"?????????????????????????????????\"" + cartoonItem.get(0).getName() + "\"",
//                                CartoonItemActivity.class, pendingIntent, R.mipmap.ic_launcher_round, loadImage(cartoon));
//                    }
//                });
                cartoonUpdateDao.insert(new CartoonUpdate().setId(cartoon.getId()).setItemCount(cartoonItem.size()));
                //????????????
                updateList.add(cartoon.setRemarks("??????????????????" + cartoonItem.get(0).getName()));
                continue;
            }
            if (cartoonUpdate.getItemCount() < cartoonItem.size())
            {
                Log.d(TAG, "cartoonUpdate: ?????????" + cartoon.getName() + "??????????????????????????????");
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
                                        intent, PendingIntent.FLAG_IMMUTABLE);
                        sendNotification("1", Integer.parseInt(cartoon.getId()), "??????\"" + cartoon.getName() + "\"????????????",
                                "??????????????????\"" + cartoon.getName() + "\"?????????"
                                        + (cartoonItem.size() - cartoonUpdate.getItemCount()) +
                                        "???????????????????????????\"" + cartoonItem.get(0).getName() + "\"",
                                CartoonItemActivity.class, pendingIntent, R.drawable.run, loadImage(cartoon));
                        LockSupport.unpark(currentThread);
                    }
                });
                LockSupport.park();
                //????????????
                cartoonUpdateDao.update(cartoonUpdate.setItemCount(cartoonItem.size()));
                updateList.add(cartoon.setRemarks("??????????????????" + cartoonItem.get(0).getName()));
            }
            else
            {
                Log.d(TAG, "cartoonUpdate: ?????????" + cartoon.getName() + "????????????");
            }
        }
        for (Cartoon cartoon : updateList)
        {
            cartoonFavoritesDao.update(cartoon);
        }
        isCartoonUpdate = false;
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                toastShow(activity, "???????????????????????????");
            }
        });
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

        Log.d(TAG, "isToday: ?????????" + year + "/" + month + "/" + day);
        Log.d(TAG, "isToday: ?????????" + year1 + "/" + month1 + "/" + day1);


        if (year1 == -1)
        {
            Log.d(TAG, "isToday: ????????????????????????");
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    sendBaseNotification("2", 1, "??????",
                            "???????????????\n\n" + OtherConstant.softwareDocumentation);
                }
            });
            saveToday();
            return false;
        }
        if (year != year1)
        {
            Log.d(TAG, "isToday: ???????????????");
            return false;
        }
        if (month != month1)
        {
            Log.d(TAG, "isToday: ???????????????");
            return false;
        }
        if (day != day1)
        {
            Log.d(TAG, "isToday: ???????????????");
            return false;
        }
        Log.d(TAG, "isToday: ?????????");
        return true;
    }


    /**
     * ????????????
     *
     * @param message ??????
     */
    private void toastShow(Activity activity, String message)
    {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * ???????????????????????????????????????????????????NotificationManager.IMPORTANCE_HIGH
     *
     * @param id   id
     * @param name ??????
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
     * ??????????????????
     *
     * @param id    id
     * @param name  ??????
     * @param level ????????????,?????????NotificationManager.IMPORTANCE_HIGH
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
     * ??????????????????
     *
     * @param channelId ????????????
     * @param id        id
     * @param title     ??????
     * @param content   ??????
     */
    private void sendBaseNotification(String channelId, int id, String title, String content)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(id, notification);
    }

    /**
     * ??????????????????
     *
     * @param channelId ????????????
     * @param id        id
     * @param title     ??????
     * @param content   ??????
     * @param cls       ????????????????????????Activity???
     */
    private void sendBaseNotification(String channelId, int id, String title, String content, Class<?> cls)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0,
                        new Intent(this, cls), PendingIntent.FLAG_IMMUTABLE);
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
     * ????????????
     *
     * @param channelId ????????????
     * @param id        id
     * @param title     ??????
     * @param content   ??????
     * @param cls       ????????????????????????Activity???
     * @param smallIcon ?????????
     * @param largeIcon ?????????
     */
    private void sendNotification(String channelId, int id, String title, String content,
                                  Class<?> cls, int smallIcon, Bitmap largeIcon)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.
                getActivity(this, 0,
                        new Intent(this, cls), PendingIntent.FLAG_IMMUTABLE);
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
     * ????????????
     *
     * @param channelId     ????????????
     * @param id            id
     * @param title         ??????
     * @param content       ??????
     * @param cls           cls
     * @param pendingIntent PendingIntent
     * @param smallIcon     ?????????
     * @param largeIcon     ?????????
     */
    private void sendNotification(String channelId, int id, String title, String content,
                                  Class<?> cls, PendingIntent pendingIntent, int smallIcon, Bitmap largeIcon)
    {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(smallIcon)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notificationManager.notify(id, notification);
    }
}
