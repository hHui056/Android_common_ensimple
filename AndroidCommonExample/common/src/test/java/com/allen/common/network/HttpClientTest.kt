package com.allen.common.network

import android.os.Environment
import android.util.Log
import com.google.gson.annotations.SerializedName
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Test
import java.io.File

/**
 * Created by hHui on 2017/9/9.
 */
class HttpClientTest {
    @Test
    fun testGetString() {
        val url: String = "http://ota.xjxueche.com/download/ota/e515-app/update_newer.json"

        HttpClient().getString(url).subscribeOn(Schedulers.newThread()).subscribe {
            object : DisposableObserver<String>() {
                override fun onError(e: Throwable) {
                }

                override fun onNext(t: String) {
                    Log.v("test", t)

                }

                override fun onComplete() {
                }


            }
        }
    }

    data class Person(val name: String, val age: Int)

    //  @Test
    fun testGetJson() {
        val url: String = ""
        HttpClient().getJson(url, Person::class.java).subscribeOn(Schedulers.newThread()).subscribe {
            object : DisposableObserver<Person>() {
                override fun onComplete() {
                }

                override fun onNext(t: Person) {


                }

                override fun onError(e: Throwable) {

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

    // @Test
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
                            Assert.assertEquals("200", t.statusCode)
                            Log.v("test", t.toString())
                        }

                    }
                }
    }

}