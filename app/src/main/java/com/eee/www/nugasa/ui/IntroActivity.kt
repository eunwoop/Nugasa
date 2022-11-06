package com.eee.www.nugasa.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.eee.www.nugasa.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity  : AppCompatActivity() {
    private val layouts: IntArray = intArrayOf(
        R.layout.intro_slide1,
        R.layout.intro_slide2,
        R.layout.intro_slide3
    )
    private val context = this as Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        addBottomDots(0)

        // making notification bar transparent
        changeStatusBarColor()

        initViewPager()
        btnSkip.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts.size) {
                viewPager.currentItem = current
            } else {
                finish()
            }
        }
    }

    private fun initViewPager() {
        viewPager.adapter = IntroViewPagerAdapter()
        viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)
                if (position == layouts.size - 1) {
                    btnNext.text = getString(R.string.start)
                    btnSkip.visibility = View.GONE
                } else {
                    btnNext.text = getString(R.string.next)
                    btnSkip.visibility = View.VISIBLE
                }
            }
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }

    private fun addBottomDots(currentPage: Int) {
        val dots: Array<TextView?> = arrayOfNulls(layouts.size)
        val colorsActive = resources.getColor(R.color.dot_active)
        val colorsInactive = resources.getColor(R.color.dot_inactive)
        dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive)
            dotsLayout.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive)
    }

    private fun getItem(i: Int): Int {
        return viewPager.currentItem + i
    }

    private fun changeStatusBarColor() {
        this.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    inner class IntroViewPagerAdapter : PagerAdapter() {
        private var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = layoutInflater.inflate(layouts[position], container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view: View = `object` as View
            container.removeView(view)
        }
    }
}