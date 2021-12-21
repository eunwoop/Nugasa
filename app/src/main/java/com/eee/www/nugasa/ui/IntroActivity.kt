package com.eee.www.nugasa.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter

import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager.widget.ViewPager
import com.eee.www.nugasa.R


class IntroActivity  : AppCompatActivity() {
    private var viewPager: ViewPager? = null
    private var myViewPagerAdapter: MyViewPagerAdapter? = null
    private var dotsLayout: LinearLayout? = null
    private lateinit var dots: Array<TextView?>
    private lateinit var layouts: IntArray
    private var btnSkip: Button? = null
    private  var btnNext:Button? = null
    private var context = this as Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: use data binding
        setContentView(R.layout.activity_intro)

        //TODO: remove it
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        viewPager = findViewById<View>(R.id.view_pager) as ViewPager
        dotsLayout = findViewById<View>(R.id.layoutDots) as LinearLayout
        btnSkip = findViewById<View>(R.id.btn_skip) as Button
        btnNext = findViewById<View>(R.id.btn_next) as Button

        layouts = intArrayOf(
            R.layout.intro_slide1,
            R.layout.intro_slide2,
            R.layout.intro_slide3
        )

        addBottomDots(0)

        // making notification bar transparent
        changeStatusBarColor()
        myViewPagerAdapter = MyViewPagerAdapter()
        viewPager?.adapter = myViewPagerAdapter
        viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                addBottomDots(position)
                if (position == layouts.size - 1) {
                    btnNext?.text = getString(R.string.start)
                    btnSkip?.visibility = View.GONE
                } else {
                    btnNext?.text = getString(R.string.next)
                    btnSkip?.visibility = View.VISIBLE
                }
            }
            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        btnSkip?.setOnClickListener { finish() }
        btnNext?.setOnClickListener {
            val current = getItem(+1)
            if (current < layouts.size) {
                viewPager?.currentItem = current
            } else {
                finish()
            }
        }
    }

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(layouts.size)
        val colorsActive = resources.getColor(R.color.dot_active)
        val colorsInactive = resources.getColor(R.color.dot_inactive)
        dotsLayout?.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]?.text = Html.fromHtml("&#8226;")
            dots[i]?.textSize = 35f
            dots[i]?.setTextColor(colorsInactive)
            dotsLayout?.addView(dots[i])
        }
        if (dots.isNotEmpty()) dots[currentPage]?.setTextColor(colorsActive)
    }

    private fun getItem(i: Int): Int {
        return viewPager!!.currentItem + i
    }

    private fun changeStatusBarColor() {
        this.window.apply {
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            statusBarColor = Color.TRANSPARENT
        }
    }

    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View? = layoutInflater?.inflate(layouts.get(position), container, false)
            container.addView(view)
            return view as Any
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