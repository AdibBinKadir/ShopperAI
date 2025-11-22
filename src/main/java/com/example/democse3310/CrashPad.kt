package com.example.helloworld

// CrashPad.kt
data class User(val id: Int, val name: String?, val points: Int = 0)

// val = immutable, var = mutable
val greetingPrefix: String = "Hi"
var mutableCounter: Int = 0

// Extension: adds new function to String
fun String.title(): String = replaceFirstChar { it.uppercase() }

// when = switch
fun describePoints(p: Int): String = when {
    p < 0 -> "error"
    p == 0 -> "newbie"
    p in 1..99 -> "rising"
    else -> "pro"
}

fun main() {
    val u = User(1, null)
    mutableCounter++

    val name = u.name?.title() ?: "Anonymous"
    println("$greetingPrefix, $name! (${describePoints(u.points)})")

    val u2 = u.copy(name = "Robin")
    println(u2)      // auto toString
    println("Runs so far: $mutableCounter")
}
