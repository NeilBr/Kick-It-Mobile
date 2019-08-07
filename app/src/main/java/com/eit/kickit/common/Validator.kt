package com.eit.kickit.common

import com.google.android.material.textfield.TextInputLayout

public class Validator{

    companion object {

        public fun validateView(view: TextInputLayout, text: String, type: Int): Boolean{


            var result = false

            when(type){
                1 -> {
                    if(text.isNotBlank()){
                        view.helperText = ""
                        result = true
                    }
                    else{
                        view.helperText = "Required"
                        result = false
                    }
                }

                2 -> {
                    result = validateView(view, text, 1)
                    if (result){
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                            result = true
                            view.helperText = ""
                        }
                        else{
                            result = false
                            view.helperText = "Enter Valid Email"
                        }

                    }
                }

                3 -> {
                    result = validateView(view, text, 1)
                    if (result){
                        if(android.util.Patterns.PHONE.matcher(text).matches() && (text.length == 10)){
                            result = true
                            view.helperText = ""
                        }
                        else {
                            result = false
                            view.helperText = "Enter Valid Phone Number"
                        }
                    }
                }

                4 -> {
                    result = validateView(view, text, 1)
                    //Longer than 8 characters
                    //One number
                    val check = "a[a-z0-9]+d?".toRegex()

                    if(result){
                        if(check.containsMatchIn(text) && (text.length >= 8)){
                            result = true
                            view.helperText = ""
                        }
                        else {
                            result = false
                            view.helperText = "Must contain a number, a letter and be longer than 8 characters"
                        }
                    }
                }
            }

            return result
        }

    }

}