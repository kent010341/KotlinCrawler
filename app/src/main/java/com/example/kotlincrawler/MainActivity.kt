package com.example.kotlincrawler

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private val scope = MainScope()
    private var bitmap: Bitmap? = null
    private var text: String? = null
    private var iv: ImageView? = null
    private var tv: TextView? = null
    private var url: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        etURL.requestFocus()
        iv = ImageView(this)
        tv = TextView(this)

        btnSubmit.setOnClickListener(View.OnClickListener {
            ll_container.removeAllViews()
            url = etURL.text.toString()//"https://i.imgur.com/2Q8bCiD.jpg"

            scope.launch(Dispatchers.Main) {
                val task = async(Dispatchers.IO){
                    if(swStatus.isChecked){
                        bitmap = getBitmapFromUrl(url!!)
                    }else{
                        text = getStringFromUrl(url!!)
                    }
                }
                task.await()
                if(swStatus.isChecked){
                    iv!!.setImageBitmap(bitmap)
                    ll_container.addView(iv)
                }else{
                    tv!!.text = text
                    ll_container.addView(tv)
                }
            }

        })
    }

    private fun getInputStream(src: String): InputStream {
        val url = URL(src)
        val conn = url.openConnection() as HttpURLConnection
        conn.connect()
        //Log.e("notice", "connected")

        return conn.inputStream
    }

    private fun getStringFromUrl(src: String): String?{
        try {
            val reader = BufferedReader(getInputStream(src).reader())
            var content: String
            try{
                content = reader.readText()
            }finally {
                reader.close()
            }
            //Log.e("notice", "get content")
            return content

        }catch (e: java.io.IOException){
            e.printStackTrace()
        }

        return null
    }

    private fun getBitmapFromUrl(src: String): Bitmap? {
        try {
            return BitmapFactory.decodeStream(getInputStream(src))
        }catch (e: java.io.IOException){
            e.printStackTrace()
        }
        return null
    }
}
