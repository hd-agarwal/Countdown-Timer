package com.example.basictimer

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var running=false
    var min:Int=0
    var sec:Int=0
    var setTime:Int=0
    lateinit var sw:StopWatch
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnPauseResume.visibility=View.INVISIBLE
        btnPauseResume.isEnabled=false
        btnReset.visibility=View.INVISIBLE
        btnReset.isEnabled=false
        tvTimeUp.visibility=View.INVISIBLE
        llRoot.setOnClickListener {
            it.hideKeyboard()
        }
        btnStart.setOnClickListener {
            if(etMin.text.toString()==""&&etSec.text.toString()=="")
            {
                Toast.makeText(applicationContext,"Please enter time",Toast.LENGTH_SHORT).show()
            }
            else if(Integer.valueOf(etSec.text.toString())>59)
            {
                Toast.makeText(applicationContext,"Seconds cannot be greater than 59",Toast.LENGTH_SHORT).show()
            }
            else
            {
                if(etMin.text.toString()=="")
                    etMin.setText("0")
                if(etSec.text.toString()=="")
                    etSec.setText("0")
                it.hideKeyboard()
                if(running)
                {
                    running=false
                    btnPauseResume.isEnabled=false
                    btnPauseResume.visibility=View.INVISIBLE
                    btnReset.isEnabled=false
                    btnReset.visibility=View.INVISIBLE
                    min = Integer.valueOf(etMin.text.toString())
                    sec = Integer.valueOf(etSec.text.toString())
                    tvTimeUp.visibility = View.INVISIBLE
                    setTime = min * 60 + sec
                    display()
                    Toast.makeText(applicationContext,"Timer reset\nClick 'START' button to start the timer",Toast.LENGTH_SHORT).show()
                }
                else
                {
                    min = Integer.valueOf(etMin.text.toString())
                    sec = Integer.valueOf(etSec.text.toString())
                    sw = StopWatch()
                    running = true
                    sw.execute(min, sec)

                    btnPauseResume.visibility = View.VISIBLE
                    btnPauseResume.isEnabled = true
                    btnPauseResume.text = "PAUSE"
                    btnReset.visibility = View.VISIBLE
                    btnReset.isEnabled = true
                    tvTimeUp.visibility = View.INVISIBLE
                    setTime = min * 60 + sec
                }
            }
        }
        btnPauseResume.setOnClickListener {
            it.hideKeyboard()
            if(running)
            {
                running=false
                btnPauseResume.text="RESUME"
                tvTimeUp.visibility=View.INVISIBLE
            }
            else
            {
                running=true
                btnPauseResume.text="PAUSE"
                sw=StopWatch()
                sw.execute(min,sec)
            }
        }
        btnReset.setOnClickListener {
            it.hideKeyboard()
            running=false
            sw.cancel(true)
            min=setTime/60
            sec=setTime%60
            btnPauseResume.visibility=View.INVISIBLE
            btnPauseResume.isEnabled=false
            btnReset.visibility=View.INVISIBLE
            btnReset.isEnabled=false
            tvTimeUp.visibility=View.INVISIBLE
            display()
            Toast.makeText(applicationContext,"Timer reset\nClick 'START' button to start the timer",Toast.LENGTH_SHORT).show()
        }
    }
    inner class StopWatch: AsyncTask<Int, Int, Int>() {
        override fun doInBackground(vararg params: Int?): Int {
            var minutes=params[0]
            var seconds=params[1]
            var time:Int= (minutes?.times(60) ?: 0) + seconds!!
            while(time>=0)
            {
                if(!running)
                    break
                publishProgress(time/60,time%60)
                if(time==0)
                    break
                wait1sec()
                time--
            }
            return time
        }
        override fun onPreExecute() {
            super.onPreExecute()
            running=true
            display()
        }
        override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            min= values[0]!!
            sec= values[1]!!
            display()
            if(values[0]==0&&values[1]==0) {
                tvTimeUp.visibility = View.VISIBLE
                btnPauseResume.isEnabled = false
                btnPauseResume.visibility = View.INVISIBLE
            }
        }
        override fun onPostExecute(result: Int?) {
            super.onPostExecute(result)
            running=false
            display()
        }
        override fun onCancelled(result: Int?) {
            super.onCancelled(result)
            running=false
        }
    }
    private fun wait1sec()
    {
        var t=System.currentTimeMillis()
        while(System.currentTimeMillis()<t+1000)
        {
            if(!running)
                break
        }
    }
    fun display(){
        tvMin.text=if(min<10) "0$min" else min.toString()
        tvSec.text=if(sec<10) "0$sec" else sec.toString()
    }
    private fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
        etMin.clearFocus()
        etSec.clearFocus()
    }
}