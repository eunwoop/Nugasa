package com.eee.www.nugasa.ui

interface Mediator {

    fun initOnDataChanged()

    fun setMode(mode: Int)

    fun setPickCount(count: Int)

    fun setTeamCount(count: Int)

    fun setPressed(pressed: Boolean)

    fun showPartyConfetti()
}