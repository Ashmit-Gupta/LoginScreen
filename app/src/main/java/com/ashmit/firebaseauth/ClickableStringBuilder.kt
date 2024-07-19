package com.ashmit.firebaseauth

import android.graphics.Color
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View

class ClickableStringBuilder{

    fun clickableText(fullText :String, startIndex :Int, onClickAction : () ->Unit) : SpannableString {
        //span is a feature that allows to style and manipulate text in textview such as bold , italics color change , clickable links or any custom behaviour
        // Create a SpannableStringBuilder to apply styling and click functionality
        //SpannableString: Immutable, used for static text with fixed spans.
        //SpannableStringBuilder: Mutable, used for dynamic text with changeable spans.

        val spannableString = SpannableString(fullText)

        val clickableSpan = object :ClickableSpan(){
            // Handle click action, e.g., navigate to another activity
            override fun onClick(widget: View) {
                onClickAction()
            }
        }
        //setting on click
        // Apply ClickableSpan to the entire spannable text
        /*setSpan() method attaches a span (in this case, ClickableSpan) to a specific portion of the text (signUpText).
        Parameters:
            clickableSpan: The span to attach.
            0: Start index of the span (beginning of signUpText).
            signUpText.length: End index of the span (end of signUpText).
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE: Flag indicating how the span should behave (exclusive to exclusive).
            values of textspan
            SPAN_INCLUSIVE_EXCLUSIVE: Includes the start character but excludes the end character.
            SPAN_EXCLUSIVE_INCLUSIVE: Excludes the start character but includes the end character.
            SPAN_INCLUSIVE_INCLUSIVE: Includes both the start and end characters.
                    */

        spannableString.setSpan(
            clickableSpan,
            startIndex ,
            spannableString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        //changing the color
        spannableString.setSpan(
            ForegroundColorSpan(Color.BLUE),
            startIndex,
            spannableString.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return spannableString
    }

}