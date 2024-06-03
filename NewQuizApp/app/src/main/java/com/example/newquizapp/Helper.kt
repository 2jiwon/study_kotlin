package com.example.newquizapp

import android.content.Context
import android.os.Build
import android.speech.tts.TextToSpeech
import android.widget.Toast
import java.util.*

class Helper {
    var toast: Toast? = null

    fun makeToast(context: Context, message: String, long: Boolean = true) {
        toast?.cancel()
        var toastLength = if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        toast = Toast.makeText(context, message, toastLength)
        toast!!.show()
    }

    private var tts: TextToSpeech? = null

    fun initTextToSpeech(context: Context): TextToSpeech? {
        // 롤리팝(API 21, Android 5.0)이상에서만 지원됨
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Toast.makeText(context, "지원하지 않는 SDK 버전입니다.", Toast.LENGTH_SHORT).show()
            return null
        }

        tts = TextToSpeech(context) {
            if (it == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(context, "지원하지 않는 언어입니다.", Toast.LENGTH_SHORT).show()
//                    return@TextToSpeech
                }
                Toast.makeText(context, "TTS 설정 성공", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "TTS 설정 실패", Toast.LENGTH_SHORT).show()
            }
        }

        return tts as TextToSpeech
    }

    fun ttsSpeak(str: String) {
        tts?.speak(str, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}