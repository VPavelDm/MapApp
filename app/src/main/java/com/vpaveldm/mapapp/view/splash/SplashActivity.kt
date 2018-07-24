package com.vpaveldm.mapapp.view.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import com.vpaveldm.mapapp.R
import com.vpaveldm.mapapp.view.map.MapActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(Runnable {
            val intent = Intent(this, MapActivity::class.java)
            finish()
            startActivity(intent)
        }, 2*1000)
    }
}