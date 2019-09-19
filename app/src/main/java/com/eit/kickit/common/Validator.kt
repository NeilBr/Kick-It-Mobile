package com.eit.kickit.common

import com.google.android.material.textfield.TextInputLayout

public class Validator{

    companion object {

        public fun validateView(view: TextInputLayout, text: String, type: Int): Boolean{


            var result = false

            when(type){
                1 -> {
                    if(text.isNotBlank()){
                        view.error = ""
                        result = true
                    }
                    else{
                        view.error = "Required"
                        result = false
                    }
                }

                2 -> {
                    result = validateView(view, text, 1)
                    if (result){
                        if (android.util.Patterns.EMAIL_ADDRESS.matcher(text).matches()){
                            result = true
                            view.error = ""
                        }
                        else{
                            result = false
                            view.error = "Enter Valid Email"
                        }

                    }
                }

                3 -> {
                    result = validateView(view, text, 1)
                    if (result){
                        if(android.util.Patterns.PHONE.matcher(text).matches() && (text.length == 10)){
                            result = true
                            view.error = ""
                        }
                        else {
                            result = false
                            view.error = "Enter Valid Phone Number"
                        }
                    }
                }

                4 -> {
                    result = validateView(view, text, 1)
                    //Longer than 8 characters
                    //One number
                    val check = "a[a-zA-Z0-9]+d?".toRegex()

                    val letterCheck = ".*[a-zA-Z].*".toRegex()
                    val numCheck = ".*[0-9].*".toRegex()

                    if(result){
                        if(text.length >= 8){
                            if(letterCheck.containsMatchIn(text) && numCheck.containsMatchIn(text)){
                                result = true
                                view.helperText = ""
                            }
                            else{
                                result = false
                                view.error = "Must contain letters and numbers"
                            }
                        }
                        else {
                            result = false
                            view.error = "Must be more than 8 or more characters."
                        }
                    }
                }
            }

            return result
        }

    }

}