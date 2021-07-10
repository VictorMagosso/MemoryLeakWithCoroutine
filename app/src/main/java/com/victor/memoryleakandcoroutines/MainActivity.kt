package com.victor.memoryleakandcoroutines

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

class MainActivity : AppCompatActivity() {
    private lateinit var btnLaunchCoroutines: Button
    private lateinit var btnNavigateActivity: Button
    private lateinit var txtCoroutineResult1: TextView
    private lateinit var txtCoroutineResult2: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtCoroutineResult1 = findViewById(R.id.txtCoroutineReturn1)
        txtCoroutineResult2 = findViewById(R.id.txtCoroutineReturn2)
        btnLaunchCoroutines = findViewById(R.id.btnLaunchCoroutines)
        btnNavigateActivity = findViewById(R.id.btnGoAct)

        btnLaunchCoroutines.setOnClickListener {
            CoroutineScope(IO).launch {
                val job1 = launch {
                    val time1 = measureTimeMillis {
                        val result = handleMainThreadResults1("Job 1 response from fake API")
                    }
                    Log.d("RES1: ", "$time1 ms")
                }

                val job2 = launch {
                    val time2 = measureTimeMillis {
                        val result = handleMainThreadResults2("Job 2 response from fake API")
                    }
                    Log.d("RES2: ", "$time2 ms")
                }
            }
        }

        btnNavigateActivity.setOnClickListener {
            CoroutineScope(IO).launch {
                finish()
                MemoryLeakClass(this@MainActivity).navigateToActivity()
            }
        }
    }

    private suspend fun handleMainThreadResults1(text: String): String {
        Thread.sleep(5000)
        withContext(Main) {
            txtCoroutineResult1.text = text
        }
        return "result 1 completed"
    }

    private suspend fun handleMainThreadResults2(text: String): String {
        withContext(Main) {
            txtCoroutineResult2.text = text
        }
        return "result 2 completed"
    }


    class MemoryLeakClass(ctx: Context?) {
        private val context = ctx
        suspend fun navigateToActivity() {
            withContext(Main) {
                Log.d("context: ", context!!.packageName)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("OnDestroy: ", "Activity destroyed")
    }
}