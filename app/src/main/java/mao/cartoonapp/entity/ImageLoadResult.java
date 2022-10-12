package mao.cartoonapp.entity;

import android.graphics.Bitmap;

/**
 * Project name(项目名称)：CartoonApp
 * Package(包名): mao.cartoonapp.entity
 * Class(类名): ImageLoadResult
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/12
 * Time(创建时间)： 18:51
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public class ImageLoadResult
{
    /**
     * 状态
     */
    private boolean status;
    /**
     * 位图
     */
    private Bitmap bitmap;

    public ImageLoadResult(boolean status, Bitmap bitmap)
    {
        this.status = status;
        this.bitmap = bitmap;
    }

    public boolean isStatus()
    {
        return status;
    }

    public ImageLoadResult setStatus(boolean status)
    {
        this.status = status;
        return this;
    }

    public Bitmap getBitmap()
    {
        return bitmap;
    }

    public ImageLoadResult setBitmap(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        return this;
    }
}
