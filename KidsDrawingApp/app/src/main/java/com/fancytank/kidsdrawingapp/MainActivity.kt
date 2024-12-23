package com.fancytank.kidsdrawingapp

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.github.dhaval2404.colorpicker.ColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.util.ColorUtil.parseColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    // 드로잉뷰
    private var drawingView: DrawingView? = null
    // 색상 팔레트 버튼
    private var mImageButtonCurrentPaint: ImageButton? = null
    // 사용자에게 표시할 진행 다이얼로그
    var customProgressDialog: Dialog? = null

    val openGalleryLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                // 가져온 데이터를 백그라운드 이미지뷰에 대입
                val imageBackground: ImageView = findViewById(R.id.iv_background)
                imageBackground.setImageURI(result.data?.data)
            }
    }

    // 여러개의 권한을 요청할 수 있게 하기 위한 부분
    val requestPermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        permissions -> permissions.entries.forEach {
            val permissionName = it.key
            val isGranted = it.value

            // 승인 되었을 때
            if (isGranted) {
                Toast.makeText(this@MainActivity, "Permission granted. Now you can read the storage files.", Toast.LENGTH_LONG).show()

                // intent 를 사용해서 다른 activity 뿐 아니라 다른 앱으로도 갈 수 있다.
                val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                openGalleryLauncher.launch(pickIntent)
            } else {
                // 여러개의 권한 요청 중 외부 저장소 이미지 읽기 요청이 맞는지 확인 후 메시지
                if (permissionName == Manifest.permission.READ_MEDIA_IMAGES) {
                    Toast.makeText(this@MainActivity, "You denied the permission.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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
        // 갤러리 버튼에 권한 요청 연결
        val ibGallery: ImageButton = findViewById(R.id.ib_gallery)
        ibGallery.setOnClickListener{
            requestStoragePermission()
        }
        // undo 버튼에 취소 기능 연결
        val ibUndo: ImageButton = findViewById(R.id.ib_undo)
        ibUndo.setOnClickListener{
            drawingView?.onClickUndo()
        }
        // redo 버튼에 재실행 기능 연결
        val ibRedo: ImageButton = findViewById(R.id.ib_redo)
        ibRedo.setOnClickListener{
            drawingView?.onClickRedo()
        }
        // save 버튼
        val ibSave: ImageButton = findViewById(R.id.ib_save)
        ibSave.setOnClickListener{
            // 저장소 읽기 권한이 있는지 먼저 체크하고
            if (isReadStorageAllowed()) {
                // coroutine 실행 전에 사용자에게 다이얼로그 띄우기
                showProgressDialog()
                // coroutine 실행
                lifecycleScope.launch {
                    // 드로잉뷰와 배경이미지가 있는 FrameLayout을 가져와서 샌드위치처럼 합치는 작업
                    val flDrawingView: FrameLayout = findViewById(R.id.fl_drawing_view_container)
                    saveBitmapFile(getBitmapFromView(flDrawingView))
                }
            }
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

    // 컬러피커 추가한 부분
    fun colorPickerClicked(view: View) {
//        Toast.makeText(this, "picker clicked", Toast.LENGTH_SHORT).show()

        ColorPickerDialog
            .Builder(this)        				// Pass Activity Instance
            .setTitle("Pick Theme")           	// Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)   // Default ColorShape.CIRCLE
            .setDefaultColor(parseColor("#ffffff"))     // Pass Default Color
            .setColorListener { color, colorHex ->
                // Handle Color Selection
//                Toast.makeText(this, "color picked", Toast.LENGTH_SHORT).show()
                val imageButton = view as ImageButton
                drawingView?.setColor(colorHex) // 그림그리는 색상 변경
                if (view !== mImageButtonCurrentPaint) {
                    imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_selected))
                    imageButton.setBackgroundColor(color)

                    mImageButtonCurrentPaint?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_normal))
                    mImageButtonCurrentPaint = view
                }
            }
            .show()
    }

    // 저장소 읽기 권한을 주었는지 확인하기 위한 메서드
    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)

        return result == PackageManager.PERMISSION_GRANTED
    }

    // 저장소 권한을 요청하기 위한 메서드
    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
            showRationaleDialog("Kids Drawing App", "Kids Drawing App " + "needs to access your External Media Images.")
        } else {
            // 권한을 요청하지 못한 경우
            requestPermission.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }
    }

    // 이유(=rationale)를 설명하기 위한 다이얼로그
    private fun showRationaleDialog(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") {
                dialog, _ -> dialog.dismiss()
            }
        builder.create().show()
    }

    // view를 bitmap으로 변환해서 저장하기 위한 메서드
    private fun getBitmapFromView(view: View): Bitmap {
        // 마지막에 반환될 bitmap
        val returnedBitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)

        // canvas를 view와 바인딩함
        val canvas = Canvas(returnedBitmap)
        // 백그라운드 이미지 가져오기
        val bgDrawable = view.background
        // 만약 백그라운드에 뭔가 있다면 캔버스에 그림을 그려주고 비어있으면 흰색 배경에 그린다.
        if (bgDrawable != null) {
            bgDrawable.draw(canvas)
        } else {
            canvas.drawColor(Color.WHITE)
        }

        // canvas를 view위에 그리기
        view.draw(canvas)

        // bitmap 반환
        return returnedBitmap
    }

    // bitmap을 기기에 저장하기 위한 부분 - coroutine 사용
    private suspend fun saveBitmapFile(mBitmap: Bitmap?): String {
        var result = ""

        // dispatcher로 입출력 조절하는 것
        withContext(Dispatchers.IO) {
            // 주어진 비트맵이 null이 아닌지 먼저 확인해야함
            if (mBitmap != null) {
                try {
                    // 바이트 배열 출력스트림을 생성하는 이미지를 출력
                    val bytes = ByteArrayOutputStream()
                    // 비트맵 압축 처리
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 90, bytes)
                    // 저장할 파일 생성
                    /**
                     * 여기에서부터 참고한 URL : https://kimyunseok.tistory.com/137
                     */
                    val filename = "KidsDrawingApp_" + System.currentTimeMillis()/1000 + ".png"

//                    Log.d("package name :: ", packageName)

                    val contentValues = ContentValues()
                    contentValues.apply {
                        put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + packageName) // 경로설정(DCIM 아래에 내 패키지명)
                        put(MediaStore.Images.Media.DISPLAY_NAME, filename) // 파일이름 put
                        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                        put(MediaStore.Images.Media.IS_PENDING, 1) // 해당 저장소 사용중으로 만들기
                    }

                    // 이미지 저장할 uri 미리 설정
                    val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    try {
                        if (uri != null) {
                            // write 모드로 파일 open
                            val image = contentResolver.openFileDescriptor(uri, "w", null)
                            if (image != null) {
                                val fos = FileOutputStream(image.fileDescriptor)
                                fos.write(bytes.toByteArray())
                                fos.close()

                                contentValues.clear()
                                contentValues.put(MediaStore.Images.Media.IS_PENDING, 0) // 저장소 사용중 상태 해제
                                contentResolver.update(uri, contentValues, null, null)
                            }
                        }
                    } catch(e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    // 실제 결과 받기
                    result = uri.toString()

                    // UI thread에 결과 표시 : 사용자가 파일을 어디에 저장했는지 알려주기
                    runOnUiThread{
                        // 진행 상태 다이얼로그 감추기
                        cancelProgressDialog()

                        if (result.isNotEmpty()) {
                            Toast.makeText(this@MainActivity, "File saved successfully at DCIM/" + packageName, Toast.LENGTH_SHORT).show()

                            // result가 비어있지 않은 경우에만 이미지 공유 기능을 사용할 수 있어야 함
                            if (uri != null) {
                                shareImage(uri)
                            }

                        } else {
                            Toast.makeText(this@MainActivity, "Something went wrong while saving the file.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    result = ""
                    e.printStackTrace()
                }
            }
        }

        return result
    }

    // 사용자들에게 무슨 일이 일어나고 있는지 알려주기 위한 커스텀 다이얼로그 띄우는 메서드
    private fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)
        // 레이아웃과 연결
        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)
        // 다이얼로그를 화면에 띄움
        customProgressDialog?.show()
    }

    // 커스텀 다이얼로그를 정지하기 위한 메서드 (이 부분이 없으면 다이얼로그가 계속 화면에 떠 있게 됨)
    private fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    // 이미지 공유 기능
    private fun shareImage(uri: Uri) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
            shareIntent.type = "image/png"
            startActivity(Intent.createChooser(shareIntent, "Share"))
    }
}