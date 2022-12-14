package mao.cartoonapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class BackupAndRecoveryActivity extends AppCompatActivity
{
    /**
     * 备份按钮
     */
    private Button backupButton;

    /**
     * 恢复按钮
     */
    private Button recoveryButton;

    private int requestTotal = 0;

    private final String[] permissions =
            {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    //Manifest.permission.MANAGE_EXTERNAL_STORAGE
            };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_and_recovery);

        backupButton = findViewById(R.id.backupButton);
        recoveryButton = findViewById(R.id.recoveryButton);

        backupButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(BackupAndRecoveryActivity.this, BackupActivity.class));
            }
        });
        recoveryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new AlertDialog.Builder(BackupAndRecoveryActivity.this)
                        .setTitle("警告")
                        .setMessage("恢复数据将会覆盖当前原有的所有用户数据！是否继续？")
                        .setPositiveButton("继续", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                startActivity(new Intent(BackupAndRecoveryActivity.this, RecoveryActivity.class));
                            }
                        })
                        .setNeutralButton("取消", null)
                        .create()
                        .show();
            }
        });
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        backupAndRecovery(1);
    }

    /**
     * 备份和恢复
     *
     * @param requestCode 请求代码 ,可以是组件的id
     */
    public void backupAndRecovery(int requestCode)
    {
        if (checkPermission(BackupAndRecoveryActivity.this, permissions,
                requestCode % 65536))
        {
            //成功获取到权限
            backupButton.setEnabled(true);
            recoveryButton.setEnabled(true);
        }
        else
        {
            backupButton.setEnabled(false);
            recoveryButton.setEnabled(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && requestTotal < 1)
        {
            // android 11  且 不是已经被拒绝
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager())
            {
                requestTotal++;
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            }
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            if (!Environment.isExternalStorageManager())
            {
                backupButton.setEnabled(false);
                recoveryButton.setEnabled(false);
                toastShow("安卓12需要完全的文件访问权限，请允许");
            }
            else
            {
                if (checkPermission(BackupAndRecoveryActivity.this, permissions,
                        requestCode % 65536))
                {
                    //成功获取到权限
                    backupButton.setEnabled(true);
                    recoveryButton.setEnabled(true);
                }
            }

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // requestCode不能为负数，也不能大于2的16次方即65536
        if (requestCode == 1 % 65536)
        {
            if (checkGrant(grantResults))
            {
                //用户选择了同意授权
                backupButton.setEnabled(true);
                recoveryButton.setEnabled(true);
            }
            else
            {
                backupButton.setEnabled(false);
                recoveryButton.setEnabled(false);
                toastShow("没有存储权限! 请在设置中允许本软件存储权限的访问");
            }
        }
        if (requestCode == 1024 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
        {
            // 检查是否有权限
            if (Environment.isExternalStorageManager())
            {
                // 授权成功
            }
            else
            {
                backupButton.setEnabled(false);
                recoveryButton.setEnabled(false);
                // 授权失败
                toastShow("安卓12需要完全的文件访问权限，请允许");
            }
        }
    }


    /**
     * 检查权限结果数组，
     *
     * @param grantResults 授予相应权限的结果是PackageManager.PERMISSION_GRANTED
     *                     或PackageManager.PERMISSION_DENIED
     *                     从不为空
     * @return boolean 返回true表示都已经获得授权 返回false表示至少有一个未获得授权
     */
    public static boolean checkGrant(int[] grantResults)
    {
        boolean result = true;
        if (grantResults != null)
        {
            for (int grant : grantResults)
            {
                //遍历权限结果数组中的每条选择结果
                if (grant != PackageManager.PERMISSION_GRANTED)
                {
                    //未获得授权，返回false
                    result = false;
                    break;
                }
            }
        }
        else
        {
            result = false;
        }
        return result;
    }


    /**
     * 检查某个权限
     *
     * @param act         Activity对象
     * @param permission  许可
     * @param requestCode 请求代码
     * @return boolean 返回true表示已启用该权限，返回false表示未启用该权限
     */
    public static boolean checkPermission(Activity act, String permission, int requestCode)
    {
        return checkPermission(act, new String[]{permission}, requestCode);
    }


    /**
     * 检查多个权限
     *
     * @param act         Activity对象
     * @param permissions 权限
     * @param requestCode 请求代码
     * @return boolean 返回true表示已完全启用权限，返回false表示未完全启用权限
     */
    @SuppressWarnings("all")
    public static boolean checkPermission(Activity act, String[] permissions, int requestCode)
    {
        boolean result = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            int check = PackageManager.PERMISSION_GRANTED;
            //通过权限数组检查是否都开启了这些权限
            for (String permission : permissions)
            {
                check = ContextCompat.checkSelfPermission(act, permission);
                if (check != PackageManager.PERMISSION_GRANTED)
                {
                    //有个权限没有开启，就跳出循环
                    break;
                }
            }
            if (check != PackageManager.PERMISSION_GRANTED)
            {
                //未开启该权限，则请求系统弹窗，好让用户选择是否立即开启权限
                ActivityCompat.requestPermissions(act, permissions, requestCode);
                result = false;
            }
        }
        return result;
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
}