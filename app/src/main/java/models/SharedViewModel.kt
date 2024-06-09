package models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val homeToInfoUserName = MutableLiveData<String>()
    val infoToHomeUserName = MutableLiveData<String>()

    val userEmail = MutableLiveData<String>()
    val userPassword = MutableLiveData<String>()

}
