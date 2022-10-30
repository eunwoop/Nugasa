package com.eee.www.nugasa.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.eee.www.nugasa.utils.SoundEffector.Constants.ASSET_SELECT
import com.eee.www.nugasa.utils.SoundEffector.Constants.ASSET_SELECT_TRIGGER
import com.eee.www.nugasa.utils.SoundEffector.Constants.MESSAGE_PLAY
import kotlin.properties.Delegates

class SoundEffector(context: Context) : Handler.Callback {
    private object Constants {
        const val ASSET_SELECT_TRIGGER = "select_trigger_effect.mp3"
        const val ASSET_SELECT = "select_effect.mp3"

        const val MESSAGE_PLAY = 0
    }

    private val appContext = context.applicationContext
    private val eventHandler = Handler(Looper.getMainLooper(), this)
    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()

    private var selectTriggerEffect by Delegates.notNull<Int>()
    private var selectEffect by Delegates.notNull<Int>()

    init {
        setOnlyControlMediaVolume(context)
        loadSoundEffects(context)
    }

    private fun setOnlyControlMediaVolume(context: Context) {
        if (context is Activity) {
            context.volumeControlStream = AudioManager.STREAM_MUSIC
        }
    }

    private fun loadSoundEffects(context: Context) {
        val selectTriggerAsset = context.assets.openFd(ASSET_SELECT_TRIGGER)
        selectTriggerEffect = soundPool.load(selectTriggerAsset, 1)
        val selectAsset = context.assets.openFd(ASSET_SELECT)
        selectEffect = soundPool.load(selectAsset, 1)
    }

    fun playTriggerInMillis(millis: Long) {
        eventHandler.sendEmptyMessageDelayed(MESSAGE_PLAY, millis)
    }

    fun playSelect() {
        play(selectEffect)
    }

    fun stop() {
        if (eventHandler.hasMessages(MESSAGE_PLAY)) {
            eventHandler.removeMessages(MESSAGE_PLAY)
        }
        soundPool.autoPause()
    }

    override fun handleMessage(msg: Message): Boolean {
        if (msg.what == MESSAGE_PLAY) {
            play(selectTriggerEffect)
            return true
        }
        return false
    }

    private fun play(soundId: Int) {
        val mediaVolume = getMediaVolume()
        soundPool.play(soundId, mediaVolume, mediaVolume, 1, 0, 1f)
    }

    private fun getMediaVolume(): Float {
        val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return currentVolume.toFloat() / maxVolume.toFloat()
    }

    fun release() {
        soundPool.release()
    }
}