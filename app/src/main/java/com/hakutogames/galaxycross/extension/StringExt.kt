package com.hakutogames.galaxycross.extension

import java.security.MessageDigest

fun String.toSHA256(): String {
    val bytes = this.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)
    return digest.joinToString("") { "%02x".format(it) }
}
