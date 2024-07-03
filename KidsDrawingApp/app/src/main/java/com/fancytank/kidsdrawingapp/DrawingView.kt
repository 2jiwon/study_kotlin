package com.fancytank.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    // 그림을 그리기 위해 필요한 요소들을 정의하기 위한 변수들

    private var mDrawPath: CustomPath? = null
    private var mCanvasBitmap: Bitmap? = null
    private var mDrawPaint: Paint? = null
    private var mCanvasPaint: Paint? = null

    // brush 두께
    private var mBrushSize: Float = 0.toFloat()
    // 색상 : 검정색
    private var color = Color.BLACK
    // 그림을 그릴 배경이 되는 캔버스
    private var canvas: Canvas? = null

    // path를 유지하기 위한 부분(-> 그림을 활동주기 동안 유지하게 하기 위한 부분)
    /* val로 ArrayList를 만들면, ArrayList 안의 요소는 변경가능하지만 새로운 ArrayList를 만들 수는 없다. */
    private val mPaths = ArrayList<CustomPath>()

    // path를 취소하기 위한 부분
    private val mUndoPaths = ArrayList<CustomPath>()

    // 변수들의 초기화를 위한 부분
    init {
        // 메서드에서 처리
        setUpDrawing()
    }

    // 그림을 취소시킬 메서드
    // mPaths의 입력값을 1개 지우고 그걸 mUndoPaths에 저장
    fun onClickUndo() {
        // mPaths의 값이 있는 경우에만 삭제
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            // onDraw를 한번 더 호출하는데, 직접 호출이 아니라 invalidate로 무효화시킴
            invalidate()

        }
    }

    private fun setUpDrawing() {
        // paint 객체 생성
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        // mDrawPaint의 color 속성 지정
        // 여기에 느낌표 2개는 non-null assetion 지정
        mDrawPaint!!.color = color
        // style 속성 지정, 일단 stroke만 지정
        mDrawPaint!!.style = Paint.Style.STROKE
        // stroke의 시작과 끝을 둥글게 할지 말지 지정 : 여기에서는 일단 둥글게 지정
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        // 선 끝의 위치를 정하는 Cap 지정
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND

        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        // brush size 지정
        //mBrushSize = 20.toFloat() <-- 임시로 지정한 값이라서 삭제하면 된다.
    }

    // 이 안의 내용은 화면 크기가 바뀔때마다 불러오게 된다.
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // canvas bitmap 설정
        // 여기에서 사용된 ARGB_8888은 각 픽셀이 각각 4Byte에 저장되고, 각 채널(RGB + alpha)은 8bit의 정밀도로 저장된다
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)

        // 지정한 비트맵을 canvas에 전달
        canvas = Canvas(mCanvasBitmap!!)
    }

    // 그림을 그릴 때 실행될 부분 => 즉, 화면을 터치할 때 => mDrawPath(= 그려질 path)를 변경하는 것
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // 터치한 곳의 좌표
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action) {
            // 모션이벤트에서 제일 중요한 액션 3가지 : 1. 화면에 손가락을 댔을때, 2. 드래그했을때, 3. 손가락을 화면에서 뗐을때
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
//              mDrawPath!!.moveTo(touchX!!, touchY!!) <- 이렇게 해도 되지만 100%확실하게 하려면 null 검사 형식으로
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!) // path를 저장(?)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }

        // 전체 view를 무효화
        invalidate()

        return true
    }

    // 만약에 오류가 나면 Canvas를 Canvas?로 변경할 것(-> 강좌에서 주석으로 달으라고 한 부분임)
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // drawBitmap을 불러와서 전달, mCanvasBitmap을 사용하고 싶다고 하는 것. 시작할 위치는 왼쪽 위가 됨.
        canvas.drawBitmap(mCanvasBitmap!!, 0f, 0f, mCanvasPaint)

        // 그림을 그리는 부분
        for (path in mPaths) {
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        // path를 그리는 부분
        // mDrawPath가 비어있을 때(즉, 아무것도 그리지 않았을 때) 뭔가를 그리게 한다.
        if (!mDrawPath!!.isEmpty) {
            // brush 두께 설정
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            // CustomPath 색상 설정
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    // brush 크기를 설정하기
    fun setSizeForBrush(newSize: Float) {
        // brush size를 아무 float 값이나 줄 수 없다. 그래서 아래의 메서드를 이용하는 것임.
        // 결국 여기서 하는 것은, 화면 측정 단위에 따라서 브러시 크기가 조정되도록 하는 것임.
        // (예를 들어, 작은 스크린에서 손가락 만한 굵기로 그렸다면 큰 스크린에서도 같은 비율로 굵게 보여야 하는 것)
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics) // unit: 사용할 단위, value: 정보값, metrics: 측정 기준

        mDrawPaint!!.strokeWidth = mBrushSize
    }

    // 선택한 색상이 적용되도록 하기
    fun setColor(newColor: String) {
        // 이 파일의 윗 부분에서 설정한 color를 override함
        color = Color.parseColor(newColor)
        mDrawPaint!!.color = color
    }



    // 중첩된 class면서 internal inner class로 생성
    // internal inner class는 이 class 안에서만 사용하고, 변수를 가져오거나 내보낼 수 있다.

    // Path를 include할때 android.graphics로 가져오기
    internal inner class CustomPath(var color: Int, var brushThickness: Float) : Path() {

    }
}