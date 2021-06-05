package com.eee.www.chewchew.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import kotlin.properties.Delegates

class SoundEffector(context: Context) {

    private val ASSET_SELECT_TRIGGER = "select_trigger_effect.mp3"
    private val ASSET_SELECT = "select_effect.mp3"

    private val audioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    private val soundPool = SoundPool.Builder().setAudioAttributes(audioAttributes).build()

    private var selectTriggerEffect by Delegates.notNull<Int>()
    private var selectEffect by Delegates.notNull<Int>()

    init {
        loadSoundEffects(context)
    }

    private fun loadSoundEffects(context: Context) {
        val selectTriggerAsset = context.assets.openFd(ASSET_SELECT_TRIGGER)
        selectTriggerEffect = soundPool.load(selectTriggerAsset, 1)
        val selectAsset = context.assets.openFd(ASSET_SELECT)
        selectEffect = soundPool.load(selectAsset, 1)
    }

    fun playSelectTrigger() {
        play(selectTriggerEffect)
    }

    fun playSelect() {
        play(selectEffect)
    }

    fun pause() {
        soundPool.autoPause()
    }

    private fun play(soundId: Int) {
        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
    }
}