package com.eee.www.chewchew

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.eee.www.chewchew.viewmodels.PreferenceViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel: PreferenceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initCanvasView()
        initCountPicker(viewModel.pickList)
        initMenuSpinner()
    }

    private fun initCanvasView() {
        canvasView.fingerPressed.observe(
            this,
            Observer { fingerPressed ->
                menuLayout.apply {
                   (if (fingerPressed)
                        ObjectAnimator.ofFloat(this, "alpha", 1f, 0f).apply {
                            duration = 500;
                        }
                    else
                        ObjectAnimator.ofFloat(this, "alpha", 0f, 1f).apply {
                            duration = 500;
                        }).start()
                    this.isEnabled = !fingerPressed
                }
            })
        viewModel.fingerCount.observe(
            this,
            Observer { fingerCount -> canvasView.fingerCount = fingerCount })
    }

    private fun initMenuSpinner() {
        ArrayAdapter.createFromResource(this, R.array.menu_array, R.layout.spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.spinner_item)
            menuSpinner.adapter = adapter
        }

        menuSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(position) {
                    // pick
                    0-> {
                        countSpinner.isEnabled = true
                        countSpinner.visibility = View.VISIBLE
                        initCountPicker(viewModel.pickList)
                    }
                    // team
                    1-> {
                        countSpinner.isEnabled = true
                        countSpinner.visibility = View.VISIBLE
                        initCountPicker(viewModel.teamList)
                    }
                    // rank
                    2-> {
                        countSpinner.isEnabled = false
                        countSpinner.visibility = View.GONE
                    }
                }
                viewModel.setFingerSelectionCount(viewModel.pickList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun initCountPicker(countList: MutableList<Int>) {
        val arrayAdapter = ArrayAdapter<Int>(this, R.layout.spinner_item, countList)
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item)
        countSpinner.adapter = arrayAdapter

        // set initial value -> not sure needed.
//        if (viewModel.fingerCount.value != null) viewModel.fingerCount.value else 0.let {
//            countSpinner.setSelection(countList[it])
//        }

        countSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setFingerSelectionCount(countList[position])
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    override fun onDestroy() {
        canvasView.destroy()
        super.onDestroy()
    }
}