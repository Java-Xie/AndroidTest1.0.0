package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener { showToast("点击了按钮") }
    }

    private fun showToast(message: String) {
        var namep = (findViewById<EditText>(R.id.editText)).text.toString()
        var password = (findViewById<EditText>(R.id.editTextPwd)).text.toString()
        var ip = (findViewById<EditText>(R.id.editTextIp)).text.toString()
        val myThread = MyThread(ip, namep, password)
        try {
            myThread.start()
            myThread.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        Toast.makeText(this,  myThread.result.toString(), Toast.LENGTH_SHORT).show()
    }
}

internal class MyThread(
    private val path: String,
    private val namep: String,
    private val password: String
) :
    Thread() {
    var result = false
        private set

    override fun run() {
        try {
            val url = URL(path)
            val httpURLConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            httpURLConnection.setConnectTimeout(8000) //设置连接超时时间
            httpURLConnection.setReadTimeout(8000) //设置读取超时时间
            httpURLConnection.setRequestMethod("POST") //设置请求方法,post
            val data = "name=" + URLEncoder.encode(
                namep,
                "utf-8"
            ).toString() + "&pwd=" + URLEncoder.encode(password, "utf-8") //设置数据
            httpURLConnection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded"
            ) //设置响应类型
            httpURLConnection.setRequestProperty(
                "Content-Length",
                data.length.toString() + ""
            ) //设置内容长度
            httpURLConnection.setDoOutput(true) //允许输出
            val outputStream: OutputStream = httpURLConnection.getOutputStream()
            outputStream.write(data.toByteArray(charset("utf-8"))) //写入数据
            result = httpURLConnection.getResponseCode() === 200
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
