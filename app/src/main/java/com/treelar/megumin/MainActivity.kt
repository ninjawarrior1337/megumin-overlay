package com.treelar.megumin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.SeekBar


class MainActivity : AppCompatActivity() {

    lateinit var svc: Intent

    lateinit var sharedPref: SharedPreferences
    lateinit var scalePercentage: SeekBar

    var meguminActive = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        testPermission()

        scalePercentage = findViewById<SeekBar>(R.id.scaleBar)
        sharedPref = this?.getSharedPreferences("megumin", Context.MODE_PRIVATE)
        svc = Intent(this, OverlayShowingService::class.java)
        scalePercentage.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                with(sharedPref.edit()){
                    putInt("scale", progress)
                    apply()
                }
                if (meguminActive)
                    restartMegumin()
            }
        })

        scalePercentage.progress = sharedPref.getInt("scale", 1)

        val enable = findViewById<Button>(R.id.enable)
        val disable = findViewById<Button>(R.id.disable)
        val restart = findViewById<Button>(R.id.restart)

        restart.setOnClickListener{
            restartMegumin()
        }

        enable.setOnClickListener{
            activateMegumin()
            meguminActive = true
        }

        disable.setOnClickListener {
            deactivateMegumin()
            meguminActive = false
        }

        if (intent.extras != null)
        {
            if (intent.extras.getString("action") == "stop")
            {
                deactivateMegumin()
            }
        }
    }

    fun restartMegumin()
    {
        deactivateMegumin()
        activateMegumin()
    }

    fun activateMegumin()
    {
        startService(svc)
    }

    fun deactivateMegumin()
    {
        stopService(svc)
    }

    fun testPermission() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName"))
            startActivityForResult(intent, 5469)
        }
    }


}
