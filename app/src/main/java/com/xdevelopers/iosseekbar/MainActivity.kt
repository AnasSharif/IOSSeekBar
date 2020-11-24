package com.xdevelopers.iosseekbar

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private lateinit var colorSlider: IOSColorSeekBar
    lateinit var colorView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        colorView = findViewById(R.id.colorView)

        colorSlider = findViewById(R.id.iosColorSlider)
        colorSlider.setColorBrightness(0.8f)
        colorView.setBackgroundColor(colorSlider.getCurrentColor())
        colorSlider.setOnColorChangeListener(object : IOSColorSeekBar.OnColorChangeListener {
            override fun onColorChangeListener(color: Int) {
                colorView.setBackgroundColor(color)
            }

            override fun onThumbClickedListener(color: Int) {
                colorView.setBackgroundColor(color)
                Toast.makeText(this@MainActivity, "Thumb clicked", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun colorViewClicked(view: View) {
        val color = (view.background as ColorDrawable).color
        colorSlider.seekTo(color)
        colorView.setBackgroundColor(color)
    }
}