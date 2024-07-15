package com.ashmit.firebaseauth

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannedString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.ashmit.firebaseauth.databinding.ActivityWelcomeScreenBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class WelcomeScreen : AppCompatActivity() {
    private val delayMillis = 1000 // 2 seconds delay
    private val coroutineScope = CoroutineScope(Dispatchers.Main)//Dispatchers.Main specifically indicates that the coroutines launched within the coroutineScope will run on the main thread of the application. This is essential for updating the UI since only the main thread can make changes to the user interface.
    private val binding : ActivityWelcomeScreenBinding by lazy {
            ActivityWelcomeScreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val welcomeTxt = binding.tvWelcome.text.toString()
        val spannableString = SpannableString(welcomeTxt)

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this,R.color.red )),
            0,
            3,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableString.setSpan(
            ForegroundColorSpan(ContextCompat.getColor(this,R.color.brown )),
            4,
            welcomeTxt.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvWelcome.text = spannableString

        /*we can create a post handler using handler  { val handler = Handler(Looper.getMainLooper())

        val delayMillis = 1000 // 1 second delay
        val runnable = Runnable {
            // Code to be executed after delay
            textView.text = "Delayed text update"
        }

        handler.postDelayed(runnable, delayMillis.toLong())} */


        /*that method has been depreciated now we use Coroutines :Coroutines in Kotlin are a powerful tool for asynchronous programming that help simplify code that needs to perform long-running tasks, such as network requests or database operations, without blocking the main thread. They are a language feature that allow you to write asynchronous code sequentially, making asynchronous programming more readable and maintainable compared to traditional callback-based approaches or using threads directly.
        * 1. implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2'
           2. import kotlinx.coroutines.*
           * //creating a coroutine scope
           * 3.val coroutineScope = CoroutineScope(Dispatchers.Main)

*/

        coroutineScope.launch {
            delay(delayMillis.toLong())//toLong(): This is a function that converts the delayMillis value to a Long type.
            startActivity(Intent(this@WelcomeScreen , LoginScreen::class.java))
            finish()// finish splash act to prevent it from going back to it
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel() // Cancel coroutine scope to avoid memory leaks
    }
}