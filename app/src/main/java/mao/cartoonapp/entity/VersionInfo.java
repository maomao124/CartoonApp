package mao.cartoonapp.entity;

import java.util.List;

/**
 * Project name(项目名称)：解析漫画网站
 * Package(包名): mao.entity
 * Class(类名): VersionInfo
 * Author(作者）: mao
 * Author QQ：1296193245
 * GitHub：https://github.com/maomao124/
 * Date(创建日期)： 2022/10/15
 * Time(创建时间)： 13:19
 * Version(版本): 1.0
 * Description(描述)： 无
 */


public class VersionInfo
{
    /**
     * 版本
     */
    private String version;
    /**
     * 版本更新信息
     */
    private List<String> versionUpdateInfo;

    /**
     * Instantiates a new Version info.
     */
    public VersionInfo()
    {

    }

    /**
     * Instantiates a new Version info.
     *
     * @param version           the version
     * @param versionUpdateInfo the version update info
     */
    public VersionInfo(String version, List<String> versionUpdateInfo)
    {
        this.version = version;
        this.versionUpdateInfo = versionUpdateInfo;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    public String getVersion()
    {
        return version;
    }

    /**
     * Sets version.
     *
     * @param version the version
     * @return the version
     */
    public VersionInfo setVersion(String version)
    {
        this.version = version;
        return this;
    }

    /**
     * Gets version update info.
     *
     * @return the version update info
     */
    public List<String> getVersionUpdateInfo()
    {
        return versionUpdateInfo;
    }

    /**
     * Sets version update info.
     *
     * @param versionUpdateInfo the version update info
     * @return the version update info
     */
    public VersionInfo setVersionUpdateInfo(List<String> versionUpdateInfo)
    {
        this.versionUpdateInfo = versionUpdateInfo;
        return this;
    }

    @Override
    @SuppressWarnings("all")
    public String toString()
    {
        final StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append("version：").append(version).append('\n');
        stringbuilder.append("versionUpdateInfo：").append(versionUpdateInfo).append('\n');
        return stringbuilder.toString();
    }
}
