package com.example.mycalculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    // 계산기에 숫자를 보여주는 화면 view
    private var textViewInput: TextView? = null

    // 마지막에 숫자를 입력했는지 여부
    var lastNumeric: Boolean = false

    // 마지막에 .을 입력했는지 여부
    var lastDot: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewInput = findViewById(R.id.textView_input)
    }

    // 숫자나 계산버튼을 눌렀을 때 동작하는 함수
    fun onDigit(view: View) {
//        Toast.makeText(this, "Button clicked", Toast.LENGTH_LONG).show()
        textViewInput?.append((view as Button).text)
        // 숫자를 입력했는지 여부를 true로 바꿔줘야 한다(-> 추후 상세한 수정 필요
        lastNumeric = true
    }

    // CLR 버튼을 눌렀을 때 동작하는 함수
    fun onClear(view: View) {
        textViewInput?.text = ""
    }

    // . 버튼을 눌렀을 때 동작하는 함수
    fun onDecimalPoint(view: View) {
        // 마지막 입력 값이 숫자이고 . 이 아닐 때만 동작
        if (lastNumeric && !lastDot) {
            textViewInput?.append(".")
            // 위에서 . 이 입력되었으니 lastNumeric은 false가 되어야 함
            lastNumeric = false
            // 같은 논리로 lastDot이 true가 되어야 함
            lastDot = true
        }
    }

    // 연산자를 사용할 때 동작하는 함수
    fun onOperator(view: View) {
        // textViewInput이 nullable이기 때문에 let 연산 안에서 아래처럼 사용해야 한다.
        textViewInput?.text?.let {
            // 마지막 입력값이 숫자이면서 연산자가 추가된게 아닌 경우 실행
            if (lastNumeric && !isOperatorAdded(it.toString())) {    // textViewInput을 it 으로 받아서 사용한다.

            }
        }
    }



    // 연산자가 추가되었는지를 체크하는 함수
    private fun isOperatorAdded(value: String) : Boolean {
        return if (value.startsWith("-")) { // 음수 계산을 위해 맨 앞에 -가 붙는 경우는 false 처리
                    false
                } else {
                        value.contains("/") ||
                        value.contains("*") ||
                        value.contains("+") ||
                        value.contains("-")
                }
    }
}