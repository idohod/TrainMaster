package utilities

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val traineeName = MutableLiveData<String>()
    val fromTimer = MutableLiveData<Boolean>()

}