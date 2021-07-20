package com.myco

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.locuslabs.sdk.llpublic.*

class HeadlessModeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.headless_mode)

        startHeadlessMode()
    }

    private fun startHeadlessMode() {

        // The SDK is now ready to perform all headless operations including getting venue data, search results, directions, etc. e.g   get details for a POI
        // The 2 lines below imply illustrate how to instantiate the necessary databases for the different headless operations
        val llpoiDatabase = LLPOIDatabase()

        llpoiDatabase.getPOIDetails("lax", "870", object : LLOnGetPOIDetailsCallback {
            override fun successCallback(poi: LLPOI) {
                val message = """Name: ${poi.name}
             ID: ${poi.id}
             Desc: ${poi.description}
             """.trimIndent()
                val dialog = AlertDialog.Builder(this@HeadlessModeActivity)
                dialog.setMessage(message)
                dialog.setTitle("Got POI details in headless mode")
                dialog.setPositiveButton("OK", null)
                dialog.create().show()
            }

            override fun failureCallback(throwable: Throwable) {
                Log.d("log", "xxx" +throwable.localizedMessage)
            }
        })
    }
}