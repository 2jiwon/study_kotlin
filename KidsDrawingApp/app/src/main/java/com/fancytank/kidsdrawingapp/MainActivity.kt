package com.fancytank.kidsdrawingapp

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton

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

        // 만든 이미지 버튼 활용
        val ibBrush: ImageButton = findViewById(R.id.ib_brush)
        ibBrush.setOnClickListener{
            showBrushSizeSelectorDialog()
        }
    }

    // 브러시 사이즈 선택할 수 있게 하는 메서드
    /*
      강좌에서 설명하길, 나름 네이밍의 규칙은
        show: 화면에 표시하기 때문
        dialog: 다이얼로그(=화면에 뜨는 선택 가능한 팝업창)이기 때문
     */
    private fun showBrushSizeSelectorDialog() {
        // 객체 생성
        val brushDialog = Dialog(this)
        /*
          객체에 특성을 추가해주기
         */
        // 화면에 표시하도록 하기
        brushDialog.setContentView(R.layout.dialog_brush_size)
        // 다이얼로그 제목 (여기에서는 표시는 되지 않음)
        brushDialog.setTitle("Brush size: ")
        // small 버튼
        val smallBtn: ImageButton = brushDialog.findViewById(R.id.ib_small_brush)
        smallBtn.setOnClickListener {
            drawingView?.setSizeForBrush(10.toFloat())
            brushDialog.dismiss()
        }
        // medium 버튼
        val mediumBtn: ImageButton = brushDialog.findViewById(R.id.ib_medium_brush)
        mediumBtn.setOnClickListener {
            drawingView?.setSizeForBrush(20.toFloat())
            brushDialog.dismiss()
        }
        // large 버튼
        val largeBtn: ImageButton = brushDialog.findViewById(R.id.ib_large_brush)
        largeBtn.setOnClickListener {
            drawingView?.setSizeForBrush(30.toFloat())
            brushDialog.dismiss()
        }
        /* 여기에서 사용자가 좌우로 드래그해서 크기를 변경시킬 수 있는 방식으로 업그레이드해보기 */
        brushDialog.show()
    }
}