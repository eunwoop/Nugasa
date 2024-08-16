package com.eee.www.nugasa.ui

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.eee.www.nugasa.R
import java.io.IOException
import java.io.InputStream


class LicenseActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        val licenseTextView = findViewById<TextView>(R.id.license_textView)
        licenseTextView.text = readLicenseTxt()
    }

    private fun readLicenseTxt(): String {
        try {
            val inputStream: InputStream = assets.open("license.txt")
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            return String(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

}