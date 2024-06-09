package models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val homeToInfoUserName = MutableLiveData<String>()
    val homeToInfoUserEmail = MutableLiveData<String>()
    val homeToInfoUserPassword = MutableLiveData<String>()

    val infoToHomeUserName = MutableLiveData<String>()
    val infoToHomeUserEmail = MutableLiveData<String>()
    val infoToHomeUserPassword = MutableLiveData<String>()


}
