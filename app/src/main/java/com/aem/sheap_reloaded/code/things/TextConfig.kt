package com.aem.sheap_reloaded.code.things

import android.content.Context
import android.text.InputType
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.aem.sheap_reloaded.R
import com.google.android.material.textfield.TextInputLayout

class TextConfig (private val context: Context) {
    private fun getItString(editText: EditText, layout: TextInputLayout): String?{
        layout.error = null
        return if (editText.text.toString().isEmpty()) null
        else editText.text.toString().trim()
    }

    fun getInfo(editText: EditText, layout: TextInputLayout, op: Int): String?{
        return when(op){
            2 -> { //Get User
                val user = getItString(editText, layout)
                if (!isValid(user, op)) showError(layout, R.string.error_user, context)
                else user
            }
            3 -> { //Get User Name
                val name = getItString(editText, layout)
                if (!isValid(name, op)) showError(layout, R.string.error_name, context)
                else name
            }
            4 -> { //Get Mail
                val mail = getItString(editText, layout)
                if (!isValid(mail, op)) showError(layout, R.string.error_mail, context)
                else mail
            }
            5 -> { //Get Password
                val pass = getItString(editText, layout)
                if (!isValid(pass, op)) showErrorNClean(layout, editText, R.string.error_pass, context)
                else pass
            }
            6 -> { //Get Project Name
                val projectName = getItString(editText, layout)
                if (!isValid(projectName, op)) showError(layout, R.string.error_project_name, context)
                else projectName
            }
            7 -> { //Get Project Description
                val projectDesc = getItString(editText, layout)
                if (!isValid(projectDesc, op) && projectDesc != null) {
                    showError(layout, R.string.error_project_desc, context)
                    "-1"
                }
                else projectDesc
            }
            else -> {
                val config = Cipher()
                config.showMe(context, context.getString(R.string.error))
                null
            }
        }
    }

    fun isValid(string: String?, op: Int): Boolean{
        val validUser = Regex("[A-Za-z0-9\\S]{4,25}")
        val validName = Regex("[A-Za-z\\s]{1,50}")
        val validPass = Regex("^(?=(?:[^a-zA-Z]*[a-zA-Z]){3})" +
                "(?=(?:[^\\p{Punct}]*\\p{Punct}){3})(?=(?:[^0-9]*[0-9]){3}).{9,32}$")
        val validNameProject = Regex("[A-Za-z0-9\\s]{5,25}")
        val validDescProject = Regex("[A-Za-z0-9\\s]{1,50}")

        return if(string != null){
            when (op){
                2 -> validUser.matches(string)
                3 -> validName.matches(string)
                4 -> Patterns.EMAIL_ADDRESS.matcher(string).matches()
                5 -> validPass.matches(string)
                6 -> validNameProject.matches(string)
                7 -> validDescProject.matches(string)
                else -> false
            }
        } else false
    }

    fun showErrorNClean(layout: TextInputLayout, editText: EditText, error: Int, context: Context): String?{
        clearText(editText)
        return showError(layout, error, context)
    }

    fun showError(layout: TextInputLayout, error: Int, context: Context): String?{
        layout.error = context.getString(error)
        return null
    }

    fun clearText(editText: EditText){
        editText.setText("")
    }

    fun viewPass(button: ShowPassButton, editText: EditText, context: Context, view: View?) {
        button.setOnTouchListener { _, event ->
            with(button) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        editText.inputType =
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        setColorFilter(ContextCompat.getColor(context, R.color.red))
                        setImageResource(R.drawable.ic_toggle_on)
                        true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        editText.inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                        setColorFilter(ContextCompat.getColor(context, R.color.green))
                        setImageResource(R.drawable.ic_toggle_off)
                        view?.performClick()
                        true
                    }
                    else -> false
                }
            }
        }
    }
}