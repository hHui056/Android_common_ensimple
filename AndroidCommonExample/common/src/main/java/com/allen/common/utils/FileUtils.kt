package com.allen.common.utils

import okio.Okio
import java.io.File
import java.io.InputStream

/**
 * Created by hHui on 2017/8/1.
 */
object FileUtils {

    /**
     * 保存一个输入流到文件中。
     * @param input 输入字节流
     * @param target 目标文件，如果文件不存在，自动创建；文件存在，覆盖。
     * @return true 操作成功
     */
    @JvmStatic fun saveFile(input: InputStream, target: File, onError: (Exception) -> Unit = { throw it }): Boolean {
        try {
            if (!target.exists()) {
                target.createNewFile()
            }

            val sink = Okio.buffer(Okio.sink(target))
            val source = Okio.buffer(Okio.source(input))
            sink.writeAll(source)
            sink.close()
            source.close()
            return true
        } catch (e: Exception) {
            onError(e)
            return false
        }
    }

    /**
     * 删除文件，或目录及目录下的所有文件和子目录。
     * 如果中途发生异常，已删除的文件不可恢复。
     *
     * @param filePath 文件或目录的绝对路径
     * @param keepRoot 如果是目录，保留根目录,默认false
     * @param onError 异常处理
     * @return true 文件或目录删除成功
     */
    @JvmStatic @JvmOverloads fun deleteFiles(filePath: String, keepRoot: Boolean = false,
                                             onError: (Exception) -> Unit = { throw  it }): Boolean = with(File(filePath)) {
        try {
            val needMkDir = isDirectory && keepRoot
            return@with exists() && deleteRecursively() && (if (needMkDir) mkdir() else true)
        } catch (e: Exception) {
            onError(e)
            return@with false
        }
    }

    /**
     * 获取文件大小，或目录下所有文件的总大小
     * @param filePath 文件或目录的绝对路径
     * @param onError 异常处理
     * @return 文件或目录的总字节数，如果文件不存在返回0
     */
    @JvmStatic @JvmOverloads fun getLength(filePath: String, onError: (Exception) -> Unit = { throw it }): Long {
        var totalSize = 0L
        try {
            with(File(filePath)) {
                when {
                    !exists() -> 0L
                    isFile -> length()
                    isDirectory -> this.walk().onFail { _, ioException -> throw ioException }.forEach {
                        if (it.isFile) totalSize += it.length()
                    }
                    else -> 0L
                }
            }
        } catch (e: Exception) {
            onError(e)
        } finally {
            return totalSize
        }
    }

    /**
     * 拷贝整个目录到目的目录。
     *
     * @param srcDirectory 源目录
     * @param destDirectory 目的目录
     * @param containSrcRootPath true,包含源目录的根目录，默认true
     * @param ignoreList 不拷贝的子目录和子文件，相对路径，不支持多层子目录。
     *
     *  允许的文件和目录: readme.txt  config
     *
     *  不允许的文件和目录: config/readme.txt config/subConfigs
     *  @param onError 异常处理
     * @return true 操作成功
     */
    @JvmStatic @JvmOverloads fun copyDirectory(srcDirectory: String, destDirectory: String, containSrcRootPath: Boolean = true,
                                               ignoreList: List<String> = emptyList<String>(),
                                               onError: (Exception) -> Unit = { throw it }): Boolean {
        with(File(srcDirectory)) {
            if (!isDirectory) return false

            try {
                val self = this

                val targetDir = if (containSrcRootPath) "$destDirectory${File.separator}$name" else destDirectory

                this.listFiles().filter { !ignoreList.contains(it.toRelativeString(self)) }.forEach {
                    if (it.isFile) {
                        it.copyTo(File(targetDir, it.toRelativeString(self)), true)
                    } else if (it.isDirectory) {
                        it.copyRecursively(File(targetDir, it.toRelativeString(self)), true, { _, e -> throw e })
                    }
                }
                return true
            } catch (e: Exception) {
                onError(e)
                return false
            }
        }
    }

    /**
     * 剪切整个目录到目的目录
     * @param srcDirectory 源目录
     * @param destDirectory 目的目录
     * @param containSrcRootPath true,包含源目录的根目录，默认true
     * @param ignoreList 不剪切的子目录和子文件，相对路径，不支持多层子目录。
     *
     *  允许的文件和目录: readme.txt  config
     *
     *  不允许的文件和目录: config/readme.txt config/subConfigs
     *  @param onError 异常处理
     * @return true 操作成功
     */
    @JvmStatic @JvmOverloads fun moveDirectory(srcDirectory: String, destDirectory: String, containSrcRootPath: Boolean = true,
                                               ignoreList: List<String> = emptyList<String>(),
                                               onError: (Exception) -> Unit = { throw it }): Boolean {
        try {
            return if (copyDirectory(srcDirectory, destDirectory, containSrcRootPath, ignoreList)) deleteFiles(srcDirectory) else false
        } catch (e: Exception) {
            onError(e)
            return false
        }
    }

}

