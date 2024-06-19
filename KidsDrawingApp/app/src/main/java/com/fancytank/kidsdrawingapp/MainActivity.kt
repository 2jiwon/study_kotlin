package com.fancytank.kidsdrawingapp

import android.app.Dialog
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.get

class MainActivity : AppCompatActivity() {

    // 드로잉뷰
    private var drawingView: DrawingView? = null
    // 색상 팔레트 버튼
    private var mImageButtonCurrentPaint: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 드로잉뷰 불러오기
        drawingView = findViewById(R.id.drawing_view)
        // brush size 지정하기
        drawingView?.setSizeForBrush(20.toFloat())

        // 색상 팔레트 버튼이 담긴 선형 레이아웃
        /**
         * 선형 레이아웃 안에 담긴 버튼 하나하나는 id를 가지고 있지 않다. 그 대신 이 선형 레이아웃 안에서 인덱스를 가지고 이 버튼들을 불러오기 위해 이렇게 사용한다.
         */
        val linearLayoutPaintColors = findViewById<LinearLayout>(R.id.ll_paint_colors)
        // 위의 선형 레이아웃의 1번 위치에 있는 버튼을 가져와서 할당
        mImageButtonCurrentPaint = linearLayoutPaintColors[1] as ImageButton
        mImageButtonCurrentPaint!!.setImageDrawable(
            ContextCompat.getDrawable(this, R.drawable.pallet_selected)
        )


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

    // 색상 버튼이 클릭되었을 때 동작할 메서드
    fun paintClicked(view: View) {
        // 테스트를 위해 토스트를 먼저 띄워본다
//        Toast.makeText(this, "clicked paint", Toast.LENGTH_LONG).show()

        // 다른 버튼을 눌렀을때 색상이 바뀌는지 체크하기
        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            // 이미지버튼의 tag속성을 가져와서 문자열로 만들고 그 값을 저장
            val colorTag = imageButton.tag.toString()
            // setColor 메서드를 사용해서 색상 태그 전달하기
            drawingView?.setColor(colorTag)
            // 선택한 이미지로 실행되도록
            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_selected))
            // 선택하지 않은 버튼들은 기본 이미지로 표시되도록
            mImageButtonCurrentPaint?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_normal))
            // 변경한 정보값을 저장하기 -> view로 재정의해서 현재 선택한 버튼을 다시 사용할 수 있게... (잘 이해가 안됨ㅠ)
            mImageButtonCurrentPaint = view
        }
    }
}