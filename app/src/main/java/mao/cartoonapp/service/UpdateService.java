package mao.cartoonapp.service;


import java.util.List;

import mao.cartoonapp.entity.VersionInfo;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.service
 * Interface(接口名): UpdateService
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/15
 * Time(创建时间)： 12:50
 * Version(版本): 1.0
 * Description(描述)： 无
 */

public interface UpdateService
{
    String getVersion();

    /**
     * 获取版本更新信息
     *
     * @param version 版本,比如v1.2、v1.3
     * @return {@link List}<{@link String}>
     */
    List<String> getVersionUpdateInfo(String version);

    /**
     * 获取版本信息
     *
     * @return {@link VersionInfo}
     */
    VersionInfo getVersionInfo();
}
