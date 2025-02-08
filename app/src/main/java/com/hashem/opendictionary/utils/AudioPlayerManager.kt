package com.hashem.opendictionary.utils

import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun rememberAudioPlayerManager(): AudioPlayerManager {
    return remember { AudioPlayerManager() }
}

class AudioPlayerManager {
    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingWord: String? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    var isPlaying by mutableStateOf(false)
        private set

    fun play(url: String) {
        if (currentPlayingWord == url && isPlaying) {
            stopPlayback()
            return
        }

        stopPlayback()

        coroutineScope.launch {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    setDataSource(url)
                    setOnCompletionListener {
                        stopPlayback()
                    }

                    prepare()
                    start()
                }
                currentPlayingWord = url
                isPlaying = true
            } catch (e: Exception) {
                stopPlayback()
            }
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentPlayingWord = null
        isPlaying = false
    }

    fun release() {
        stopPlayback()
    }
}