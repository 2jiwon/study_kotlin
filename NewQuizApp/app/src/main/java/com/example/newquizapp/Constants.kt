package com.example.newquizapp

import android.content.Context
import android.util.Log
import com.opencsv.CSVReaderHeaderAware
import java.io.BufferedReader
import java.io.InputStreamReader


object Constants {

    //  사용자 이름
    const val USER_NAME : String = "user_name"
    // 문제가 총 몇개인지
    const val TOTAL_QUESTIONS: String = "total_questions"
    // 정답을 몇 개 맞췄는지
    const val CORRECT_ANSWERS: String = "correct_answers"

    // 질문 목록 가져오기
    fun getQuestions(context: Context) : ArrayList<Question> {

        val questionsList = ArrayList<Question>()

        /** csv 파일 읽어오기 시작 **/
        val inputStream = context.assets.open("questionlist.csv")
        val csvfile = BufferedReader(InputStreamReader(inputStream, "euc-kr"))
        val reader = CSVReaderHeaderAware(csvfile)

        for ((index, nextRecord) in reader.withIndex()) {

//            Log.i("index :::::", index.toString())

//            if (reader.readNext() != null) {  // 이렇게 했더니 데이터를 못읽고 넘어가버리는 현상이 발생함...
                val quiz = Question(
                    nextRecord[0].toInt(),
                    nextRecord[1],
                    context.resources.getIdentifier(nextRecord[2].replace("\n", ""), "drawable", context.packageName),
                    nextRecord[3],
                    nextRecord[4],
                    nextRecord[5],
                    nextRecord[6],
                    nextRecord[7].toInt()
                )
                questionsList.add(quiz)
//            }
        }

        Log.i("question ::::", questionsList.toString())

        return questionsList
    }

}