package com.allen.common.network

import android.os.Environment
import com.google.gson.annotations.SerializedName
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * Created by hHui on 2017/9/9.
 */
class HttpClientTest {

    // @Test
    fun testGetString() {
        val url: String = ""

        HttpClient().getString(url).subscribe {
            object : Observer<String> {
                override fun onComplete() {

                }

                override fun onError(e: Throwable) {

                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: String) {

                }

            }
        }
    }

    data class Person(val name: String, val age: Int)

    // @Test
    fun testGetJson() {
        val url: String = ""
        HttpClient().getJson(url, Person::class.java).subscribe {
            object : Observer<Person> {
                override fun onNext(t: Person) {

                }

                override fun onError(e: Throwable) {

                }

                override fun onComplete() {

                }

                override fun onSubscribe(d: Disposable) {

                }

            }
        }
    }

    // @Test
    fun testGetFile() {
        val fileUrl = "http://ota.xjxueche.com/download/ota/e515-app/DrivingStudyAssistor.apk"
        val downloadPath = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Download"

        HttpClient().getFile(fileUrl, File(downloadPath, "test.apk")).subscribe {
            object : DisposableObserver<File>() {
                override fun onNext(t: File) {
                }

                override fun onError(e: Throwable) {
                }

                override fun onComplete() {
                }

            }
        }
    }

    // @Test
    fun testPostJsonString() {
        val url = ""
        val jsonString = ""

        HttpClient().postJsonString(url, jsonString, Person::class.java).subscribe {
            object : DisposableObserver<Person>() {
                override fun onComplete() {
                }

                override fun onError(e: Throwable) {
                }

                override fun onNext(t: Person) {
                }

            }
        }

    }

    data class SignRequestBody(
            /**E515 Uid*/
            @SerializedName("appId") val appId: String,
            /**签到码*/
            @SerializedName("signCode") val signCode: String)

    data class SignResponseBody(val statusCode: String, val result: SignResult, val message: String, val flag: Boolean,
                                val stuInfoList: List<StudentInfo>,
                                @SerializedName("additional_parameter") val additionalParameter: String)

    data class StudentInfo(val name: String, val signCode: String, val phone: String)

    data class SignResult(val studentId: String, val bookedNo: String, val status: String, val studentName: String,
                          val bookDateBeg: String, val bookDateEnd: String)

    @Test
    fun testPostJson() {
        val url = "http://jx.xjxueche.com/ecs-boot/mobile/autentication/signIn"

        HttpClient().postJson(url, SignRequestBody("26808", "4321"), SignResponseBody::class.java).subscribeOn(Schedulers.newThread()).
                subscribe {
                    object : DisposableObserver<SignResponseBody>() {
                        override fun onComplete() {

                        }

                        override fun onError(e: Throwable) {

                        }

                        override fun onNext(t: SignResponseBody) {
                            Assert.assertNotNull(t)
                            Assert.assertEquals("0", t.statusCode)
                        }

                    }
                }
    }

}