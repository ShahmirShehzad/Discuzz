package com.assignment1.calculatorapp.discuzz_smdproject

data class Post(
    val id: String = "",
    val title: String = "",
    val details: String = "",
    val userId: String = "",
    val timestamp: Long = 0L,
    var likes: Int = 0,
    var dislikes: Int = 0,
    var likedBy: MutableMap<String, Boolean> = mutableMapOf(),
    var dislikedBy: MutableMap<String, Boolean> = mutableMapOf()
)
