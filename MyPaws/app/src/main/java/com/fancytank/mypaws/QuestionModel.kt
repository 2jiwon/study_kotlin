package com.fancytank.mypaws

import android.content.Context

data class Question(
    val text: String, // 질문 텍스트
    val options: List<Option> // 보기 목록
)

data class Option(
    val text: String, // 보기 텍스트
    val nextQuestion: Question? = null // 선택 시 다음 질문 (없으면 null)
)

object QuestionData { // 질문 데이터를 관리하는 객체
    // 초기화 전에 참조 가능하도록 lateinit 사용
    lateinit var petTypeQuestion: Question
    lateinit var dogBreedsQuestion: Question
    lateinit var catBreedsQuestion: Question

    lateinit var bodyColorQuestion: Question
    lateinit var eyesColorQuestion: Question


    fun initialize(context: Context) {
        // 눈 색상 질문
        eyesColorQuestion = Question(
            text = context.getString(R.string.question_eyes_color),
            options = listOf(
                Option(context.getString(R.string.eyes_color_white), null),
                Option(context.getString(R.string.eyes_color_black), null),
                Option(context.getString(R.string.eyes_color_brown), null),
                Option(context.getString(R.string.eyes_color_gray), null),
                Option(context.getString(R.string.eyes_color_light_brown), null),
                Option(context.getString(R.string.eyes_color_dark_brown), null),
                Option(context.getString(R.string.eyes_color_yellow), null),
                Option(context.getString(R.string.eyes_color_red), null),
                Option(context.getString(R.string.eyes_color_green), null),
                Option(context.getString(R.string.eyes_color_sky_blue), null),
            )
        )

        // 바디 색상 질문
        bodyColorQuestion = Question(
            text = context.getString(R.string.question_body_color),
            options = listOf(
                Option(context.getString(R.string.body_color_white), eyesColorQuestion),
                Option(context.getString(R.string.body_color_black), eyesColorQuestion),
                Option(context.getString(R.string.body_color_brown), eyesColorQuestion),
                Option(context.getString(R.string.body_color_gray), eyesColorQuestion),
                Option(context.getString(R.string.body_color_light_brown), eyesColorQuestion),
                Option(context.getString(R.string.body_color_dark_brown), eyesColorQuestion),
                Option(context.getString(R.string.body_color_black_white_mix), eyesColorQuestion),
                Option(context.getString(R.string.body_color_black_brown_mix), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_black_mix), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_brown_mix), eyesColorQuestion),
                Option(context.getString(R.string.body_color_black_white_spot), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_black_spot), eyesColorQuestion),
                Option(context.getString(R.string.body_color_tabby), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_black_eye_patches), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_brown_eye_patches), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_black_ears), eyesColorQuestion),
                Option(context.getString(R.string.body_color_white_brown_ears), eyesColorQuestion),
            )
        )

        // 강아지 종류 질문
        dogBreedsQuestion = Question(
            text = context.getString(R.string.question_dog_breed),
            options = listOf(
                Option(context.getString(R.string.dog_breed_beagle), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_boston_terrier), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_boxer), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_bulldog), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_cocker_spaniel), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_chihuahua), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_dachshund), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_doberman), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_golden_retriever), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_husky), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_labrador_retriever), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_maltiz), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_poodle), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_pug), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_pomspitz), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_pomeranian), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_rottweiler), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_schnauzer), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_shepherd), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_shih_tzu), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_spitz), bodyColorQuestion),
                Option(context.getString(R.string.dog_breed_welsh_corgi), bodyColorQuestion),
            )
        )

        // 고양이 종류 질문
        catBreedsQuestion = Question(
            text = context.getString(R.string.question_cat_breed),
            options = listOf(
                Option(context.getString(R.string.cat_breed_abyssinian), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_american_shorthair), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_bengal), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_birman), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_bombay), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_british_shorthair), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_devon_rex), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_korean_shorthair), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_maine_coon), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_munchkin), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_norwegian_forest), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_ocicat), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_persian), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_persian_chinchilla), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_ragdoll), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_russian_blue), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_scottish_fold), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_siamese), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_siberian), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_sphynx), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_tonkinese), bodyColorQuestion),
                Option(context.getString(R.string.cat_breed_turkish_angora), bodyColorQuestion),
            )
        )

        // 첫번째 질문: 반려동물 유형 선택
        petTypeQuestion = Question(
            text = context.getString(R.string.question_pet_type),
            options = listOf(
                Option(context.getString(R.string.pet_type_dog), nextQuestion = dogBreedsQuestion),
                Option(context.getString(R.string.pet_type_cat), nextQuestion = catBreedsQuestion),
                Option(context.getString(R.string.pet_type_hamster), null),
            )
        )
    }
}