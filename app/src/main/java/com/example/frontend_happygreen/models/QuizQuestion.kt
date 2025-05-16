package com.example.frontend_happygreen.models

class QuizQuestion {
    var id: Int = 0
    var quiz: Int = 0
    var question_text: String? = null
    var correct_answer: String? = null
    var wrong_answers: List<String>? = null
}