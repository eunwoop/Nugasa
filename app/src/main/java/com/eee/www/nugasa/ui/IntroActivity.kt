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
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.eee.www.nugasa.R
import com.eee.www.nugasa.databinding.ActivityIntroBinding

class IntroActivity : AppCompatActivity() {
    private val layouts: IntArray = intArrayOf(
        R.layout.intro_slide1,
        R.layout.intro_slide2,
        R.layout.intro_slide3
    )
    private val context = this as Context
    private lateinit var binding: ActivityIntroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro)
        binding.activity = this

        addBottomDots(0)

        // making notification bar transparent
        changeStatusBarColor()
        initViewPager()
    }

    private fun addBottomDots(currentPage: Int) {
        val dots: Array<TextView?> = arrayOfNulls(layouts.size)
        val colorsActive = resources.getColor(R.color.dot_active)
        val colorsInactive = resources.getColor(R.color.dot_inactive)

        binding.dotsLayout.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive)
            binding.dotsLayout.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive)
    }

    private fun changeStatusBarColor() {
        this.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    private fun initViewPager() {
        binding.viewPager.adapter = IntroViewPagerAdapter()
        binding.viewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)
                if (position == layouts.size - 1) {
                    binding.btnNext.text = getString(R.string.start)
                    binding.btnSkip.visibility = View.GONE
                } else {
                    binding.btnNext.text = getString(R.string.next)
                    binding.btnSkip.visibility = View.VISIBLE
                }
            }
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
    }

    fun onNextButtonClicked() {
        val current = getItem(+1)
        if (current < layouts.size) {
            binding.viewPager.currentItem = current
        } else {
            finish()
        }
    }

    private fun getItem(i: Int): Int {
        return binding.viewPager.currentItem + i
    }

    fun onSkipButtonClicked() {
        finish()
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