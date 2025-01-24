package com.ainsln.rhythmicabc.sound.utils

import javax.inject.Inject

interface TimeProvider {
     fun getCurrentTimeNanos(): Long
}

class SystemTimeProvider @Inject constructor() : TimeProvider {
    override fun getCurrentTimeNanos() = System.nanoTime()
}
