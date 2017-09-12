package com.allen.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.graphics.drawable.Drawable
import android.util.Log
import java.io.File


/**
 * Created by hHui on 2017/9/11.
 *
 * App相关工具类
 *
 */
object AppUtils {


    /**
     * 判断App是否安装

     * @param packageName 包名
     * *
     * @return `true`: 已安装<br></br>`false`: 未安装
     */
    @JvmStatic fun isInstallApp(packageName: String): Boolean {
        return !isSpace(packageName) && IntentUtils.getLaunchAppIntent(packageName) != null
    }

    /**
     * 安装App(支持7.0)

     * @param filePath  文件路径
     * *
     * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
     * *                  <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     */
    @JvmStatic fun installAppByFilePath(filePath: String, authority: String) {
        installApp(FileUtils.getFileByPath(filePath)!!, authority)
    }

    /**
     * 安装App（支持7.0）

     * @param file      文件
     * *
     * @param authority 7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
     * *                  <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     */
    @JvmStatic fun installApp(file: File, authority: String) {
        if (!FileUtils.isFileExists(file)) return
        Utils.getApp().startActivity(IntentUtils.getInstallAppIntent(file, authority))
    }

    /**
     * 安装App（支持6.0）

     * @param activity    activity
     * *
     * @param filePath    文件路径
     * *
     * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
     * *                    <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     * *
     * @param requestCode 请求值
     */
    @JvmStatic fun installAppByFilePath(activity: Activity, filePath: String, authority: String, requestCode: Int) {
        installApp(activity, FileUtils.getFileByPath(filePath)!!, authority, requestCode)
    }

    /**
     * 安装App(支持6.0)

     * @param activity    activity
     * *
     * @param file        文件
     * *
     * @param authority   7.0及以上安装需要传入清单文件中的`<provider>`的authorities属性
     * *                    <br></br>参看https://developer.android.com/reference/android/support/v4/content/FileProvider.html
     * *
     * @param requestCode 请求值
     */
    @JvmStatic fun installApp(activity: Activity, file: File, authority: String, requestCode: Int) {
        if (!FileUtils.isFileExists(file)) return
        activity.startActivityForResult(IntentUtils.getInstallAppIntent(file, authority), requestCode)
    }

    /**
     * 静默安装App
     *
     * 非root需添加权限 `<uses-permission android:name="android.permission.INSTALL_PACKAGES" />`

     * @param filePath 文件路径
     * *
     * @return `true`: 安装成功<br></br>`false`: 安装失败
     */
    @JvmStatic fun installAppSilent(filePath: String): Boolean {
        val file = FileUtils.getFileByPath(filePath)
        if (!FileUtils.isFileExists(file)) return false
        val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install " + filePath
        val commandResult = ShellUtils.execCmd(command, !isSystemApp(), true)
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success")
    }

    /**
     * 卸载App

     * @param packageName 包名
     */
    @JvmStatic fun uninstallApp(packageName: String) {
        if (isSpace(packageName)) return
        Utils.getApp().startActivity(IntentUtils.getUninstallAppIntent(packageName))
    }

    /**
     * 卸载App

     * @param activity    activity
     * *
     * @param packageName 包名
     * *
     * @param requestCode 请求值
     */
    @JvmStatic fun uninstallApp(activity: Activity, packageName: String, requestCode: Int) {
        if (isSpace(packageName)) return
        activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode)
    }

    /**
     * 静默卸载App
     *
     * 非root需添加权限 `<uses-permission android:name="android.permission.DELETE_PACKAGES" />`

     * @param packageName 包名
     * *
     * @param isKeepData  是否保留数据
     * *
     * @return `true`: 卸载成功<br></br>`false`: 卸载失败
     */
    @JvmStatic fun uninstallAppSilent(packageName: String, isKeepData: Boolean): Boolean {
        if (isSpace(packageName)) return false
        val command = "LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall " + (if (isKeepData) "-k " else "") + packageName
        val commandResult = ShellUtils.execCmd(command, !isSystemApp(), true)
        return commandResult.successMsg != null && commandResult.successMsg.toLowerCase().contains("success")
    }


    /**
     * 判断App是否有root权限

     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isAppRoot(): Boolean {
        val result = ShellUtils.execCmd("echo root", true)
        if (result.result === 0) {
            return true
        }
        if (result.errorMsg != null) {
            Log.d("AppUtils", "isAppRoot() called" + result.errorMsg)
        }
        return false
    }

    /**
     * 打开App

     * @param packageName 包名
     */
    @JvmStatic fun launchApp(packageName: String) {
        if (isSpace(packageName)) return
        Utils.getApp().startActivity(IntentUtils.getLaunchAppIntent(packageName))
    }

    /**
     * 打开App

     * @param activity    activity
     * *
     * @param packageName 包名
     * *
     * @param requestCode 请求值
     */
    @JvmStatic fun launchApp(activity: Activity, packageName: String, requestCode: Int) {
        if (isSpace(packageName)) return
        activity.startActivityForResult(IntentUtils.getLaunchAppIntent(packageName), requestCode)
    }

    /**
     * 关闭App
     */
    @JvmStatic fun exitApp() {
        val activityList = Utils.sActivityList
        for (i in activityList.indices.reversed()) {
            activityList.get(i).finish()
            activityList.removeAt(i)
        }
        System.exit(0)
    }

    /**
     * 获取App包名

     * @return App包名
     */
    @JvmStatic fun getAppPackageName(): String {
        return Utils.getApp().getPackageName()
    }

    /**
     * 获取App具体设置
     */
    @JvmStatic fun getAppDetailsSettings() {
        getAppDetailsSettings(Utils.getApp().getPackageName())
    }

    /**
     * 获取App具体设置

     * @param packageName 包名
     */
    @JvmStatic fun getAppDetailsSettings(packageName: String) {
        if (isSpace(packageName)) return
        Utils.getApp().startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName))
    }

    /**
     * 获取App名称

     * @return App名称
     */
    @JvmStatic fun getAppName(): String {
        return getAppName(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App名称

     * @param packageName 包名
     * *
     * @return App名称
     */
    @JvmStatic fun getAppName(packageName: String): String? {
        if (isSpace(packageName)) return null
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return if (pi == null) null else pi!!.applicationInfo.loadLabel(pm).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App图标

     * @return App图标
     */
    @JvmStatic fun getAppIcon(): Drawable {
        return getAppIcon(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App图标

     * @param packageName 包名
     * *
     * @return App图标
     */
    @JvmStatic fun getAppIcon(packageName: String): Drawable? {
        if (isSpace(packageName)) return null
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return if (pi == null) null else pi!!.applicationInfo.loadIcon(pm)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App路径

     * @return App路径
     */
    @JvmStatic fun getAppPath(): String {
        return getAppPath(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App路径

     * @param packageName 包名
     * *
     * @return App路径
     */
    fun getAppPath(packageName: String): String? {
        if (isSpace(packageName)) return null
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return if (pi == null) null else pi!!.applicationInfo.sourceDir
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App版本号

     * @return App版本号
     */
    @JvmStatic fun getAppVersionName(): String {
        return getAppVersionName(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App版本号

     * @param packageName 包名
     * *
     * @return App版本号
     */
    @JvmStatic fun getAppVersionName(packageName: String): String? {
        if (isSpace(packageName)) return null
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return if (pi == null) null else pi!!.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取App版本码

     * @return App版本码
     */
    @JvmStatic fun getAppVersionCode(): Int {
        return getAppVersionCode(Utils.getApp().getPackageName())
    }

    /**
     * 获取App版本码

     * @param packageName 包名
     * *
     * @return App版本码
     */
    @JvmStatic fun getAppVersionCode(packageName: String): Int {
        if (isSpace(packageName)) return -1
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return if (pi == null) -1 else pi!!.versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return -1
        }

    }

    /**
     * 判断App是否是系统应用

     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isSystemApp(): Boolean {
        return isSystemApp(Utils.getApp().getPackageName())
    }

    /**
     * 判断App是否是系统应用

     * @param packageName 包名
     * *
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isSystemApp(packageName: String): Boolean {
        if (isSpace(packageName)) return false
        try {
            val pm = Utils.getApp().getPackageManager()
            val ai = pm.getApplicationInfo(packageName, 0)
            return ai != null && ai!!.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 判断App是否是Debug版本

     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isAppDebug(): Boolean {
        return isAppDebug(Utils.getApp().getPackageName())
    }

    /**
     * 判断App是否是Debug版本

     * @param packageName 包名
     * *
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isAppDebug(packageName: String): Boolean {
        if (isSpace(packageName)) return false
        try {
            val pm = Utils.getApp().getPackageManager()
            val ai = pm.getApplicationInfo(packageName, 0)
            return ai != null && ai!!.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return false
        }

    }

    /**
     * 获取App签名

     * @return App签名
     */
    @JvmStatic fun getAppSignature(): Array<Signature> {
        return getAppSignature(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App签名

     * @param packageName 包名
     * *
     * @return App签名
     */
    @JvmStatic fun getAppSignature(packageName: String): Array<Signature>? {
        if (isSpace(packageName)) return null
        try {
            val pm = Utils.getApp().getPackageManager()
            @SuppressLint("PackageManagerGetSignatures")
            val pi = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            return if (pi == null) null else pi!!.signatures
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 获取应用签名的的SHA1值
     *
     * 可据此判断高德，百度地图key是否正确

     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    @JvmStatic fun getAppSignatureSHA1(): String {
        return getAppSignatureSHA1(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取应用签名的的SHA1值
     *
     * 可据此判断高德，百度地图key是否正确

     * @param packageName 包名
     * *
     * @return 应用签名的SHA1字符串, 比如：53:FD:54:DC:19:0F:11:AC:B5:22:9E:F1:1A:68:88:1B:8B:E8:54:42
     */
    @JvmStatic fun getAppSignatureSHA1(packageName: String): String? {
        val signature = getAppSignature(packageName) ?: return null
        return EncryptUtils.encryptSHA1ToString(signature[0].toByteArray())!!.replace("(?<=[0-9A-F]{2})[0-9A-F]{2}".toRegex(), ":$0")
    }

    /**
     * 判断App是否处于前台

     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isAppForeground(): Boolean {
        val manager = Utils.getApp().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val info = manager.runningAppProcesses
        if (info == null || info.size == 0) return false
        for (aInfo in info) {
            if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return aInfo.processName == Utils.getApp().getPackageName()
            }
        }
        return false
    }

    /**
     * 判断App是否处于前台
     *
     * 当不是查看当前App，且SDK大于21时，
     * 需添加权限 `<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS"/>`

     * @param packageName 包名
     * *
     * @return `true`: 是<br></br>`false`: 否
     */
    @JvmStatic fun isAppForeground(packageName: String): Boolean {
        return !isSpace(packageName) && packageName == ProcessUtils.getForegroundProcessName()
    }

    /**
     * 封装App信息的Bean类
     */
    class AppInfo
    /**
     * @param name        名称
     * *
     * @param icon        图标
     * *
     * @param packageName 包名
     * *
     * @param packagePath 包路径
     * *
     * @param versionName 版本号
     * *
     * @param versionCode 版本码
     * *
     * @param isSystem    是否系统应用
     */
    (packageName: String, name: String, icon: Drawable, packagePath: String,
     versionName: String, versionCode: Int, isSystem: Boolean) {

        var name: String? = null
        var icon: Drawable? = null
        var packageName: String? = null
        var packagePath: String? = null
        var versionName: String? = null
        var versionCode: Int = 0
        var isSystem: Boolean = false

        init {
            this.name = name
            this.icon = icon
            this.packageName = packageName
            this.packagePath = packagePath
            this.versionName = versionName
            this.versionCode = versionCode
            this.isSystem = isSystem
        }

        override fun toString(): String {
            return "pkg name: " + packageName +
                    "\napp name: " + name +
                    "\napp path: " + packagePath +
                    "\napp v name: " + versionName +
                    "\napp v code: " + versionCode +
                    "\nis system: " + isSystem
        }
    }

    /**
     * 获取App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）

     * @return 当前应用的AppInfo
     */
    @JvmStatic fun getAppInfo(): AppInfo {
        return getAppInfo(Utils.getApp().getPackageName())!!
    }

    /**
     * 获取App信息
     *
     * AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）

     * @param packageName 包名
     * *
     * @return 当前应用的AppInfo
     */
    @JvmStatic fun getAppInfo(packageName: String): AppInfo? {
        try {
            val pm = Utils.getApp().getPackageManager()
            val pi = pm.getPackageInfo(packageName, 0)
            return getBean(pm, pi)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    /**
     * 得到AppInfo的Bean

     * @param pm 包的管理
     * *
     * @param pi 包的信息
     * *
     * @return AppInfo类
     */
    private fun getBean(pm: PackageManager?, pi: PackageInfo?): AppInfo? {
        if (pm == null || pi == null) return null
        val ai = pi.applicationInfo
        val packageName = pi.packageName
        val name = ai.loadLabel(pm).toString()
        val icon = ai.loadIcon(pm)
        val packagePath = ai.sourceDir
        val versionName = pi.versionName
        val versionCode = pi.versionCode
        val isSystem = ApplicationInfo.FLAG_SYSTEM and ai.flags != 0
        return AppInfo(packageName, name, icon, packagePath, versionName, versionCode, isSystem)
    }

    /**
     * 获取所有已安装App信息
     *
     * [.getBean]（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）
     *
     * 依赖上面的getBean方法

     * @return 所有已安装的AppInfo列表
     */
    @JvmStatic fun getAppsInfo(): List<AppInfo> {
        val list = ArrayList<AppInfo>()
        val pm = Utils.getApp().getPackageManager()
        // 获取系统中安装的所有软件信息
        val installedPackages = pm.getInstalledPackages(0)
        for (pi in installedPackages) {
            val ai = getBean(pm, pi) ?: continue
            list.add(ai)
        }
        return list
    }

    /**
     * 清除App所有数据

     * @param dirPaths 目录路径
     * *
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmStatic fun cleanAppData(vararg dirPaths: String): Boolean {
        val dirs = arrayOfNulls<File>(dirPaths.size)
        var i = 0
        for (dirPath in dirPaths) {
            dirs[i++] = File(dirPath)
        }
        return cleanAppData(*dirs)
    }

    /**
     * 清除App所有数据

     * @param dirs 目录
     * *
     * @return `true`: 成功<br></br>`false`: 失败
     */
    @JvmStatic fun cleanAppData(vararg dirs: File?): Boolean {
        var isSuccess = CleanUtils.cleanInternalCache()
        isSuccess = isSuccess and CleanUtils.cleanInternalDbs()
        isSuccess = isSuccess and CleanUtils.cleanInternalSP()
        isSuccess = isSuccess and CleanUtils.cleanInternalFiles()
        isSuccess = isSuccess and CleanUtils.cleanExternalCache()
        for (dir in dirs) {
            isSuccess = isSuccess and CleanUtils.cleanCustomCache(dir)
        }
        return isSuccess
    }

    private fun isSpace(s: String?): Boolean {
        if (s == null) return true
        var i = 0
        val len = s.length
        while (i < len) {
            if (!Character.isWhitespace(s[i])) {
                return false
            }
            ++i
        }
        return true
    }

}
