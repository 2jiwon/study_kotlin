package com.fancytank.kidsdrawingapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    // 드로잉뷰
    private var drawingView: DrawingView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 드로잉뷰 불러오기
        drawingView = findViewById(R.id.drawing_view)
        // brush size 지정하기
        drawingView?.setSizeForBrush(20.toFloat())
    }
}