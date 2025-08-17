package se.umu.bide0023.pomodoroapp
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
/**
 *  * @author Biruk Debebe Kebede
 *
 *
 *
 * This class extends ViewModel and is responsible for managing and storing app state data.
 * It handles variables for managing timer settings, images, and UI states.
 * The class uses a SavedStateHandle to store and restore states.
 *
 * @param savedStateHandle to save and get state data.
 */
class AppStateViewModel(private val savedStateHandle: SavedStateHandle): ViewModel() {
    /** Variabel intialiseringar */
    val imgUri1: MutableLiveData<Uri?> = savedStateHandle.getLiveData("imgUri1", null)
    val imgUri2: MutableLiveData<Uri?> = savedStateHandle.getLiveData("imgUri2", null)
    val averagePoint: MutableLiveData<Float> = savedStateHandle.getLiveData<Float>("averagePoint", 0f)
    val MAXROUND:  MutableLiveData<Int> = savedStateHandle.getLiveData<Int>("MAXROUND", 0)
    val studyTime: MutableLiveData<Long>  = savedStateHandle.getLiveData("studyTime", 25*60000)
    val breakTime: MutableLiveData<Long>  = savedStateHandle.getLiveData("breakTime", 5*60000)
    val rounds: MutableLiveData<Long>  = savedStateHandle.getLiveData("rounds", 4)
    val isHomeButtonPressed: MutableLiveData<Boolean>  = savedStateHandle.getLiveData("isHomeButtonPressed", false)
    val hasPickedStar: MutableLiveData<Boolean> = savedStateHandle.getLiveData("hasPickedStar",false)
    val isStartBtnPressed: MutableLiveData<Boolean> = savedStateHandle.getLiveData("isStartBtnPressed", false)
    val isSecondPicture: MutableLiveData<Boolean> = savedStateHandle.getLiveData("isSecondPicture",false)
    val hasAlreadyStarted: MutableLiveData<Boolean> = savedStateHandle.getLiveData("hasAlreadyStarted",false)
    val currentText:  MutableLiveData<String> = savedStateHandle.getLiveData<String>("currentText", "")
    val hasAlarmBeenSetOn: MutableLiveData<Boolean> = savedStateHandle.getLiveData("hasAlarmBeenSetOn",false)
    val hasTimerStarted: MutableLiveData<Boolean> = savedStateHandle.getLiveData("hasTimerStarted",false)


    // this variable helps us make sure we don't start multiple threads of the startStopTimer fucntion from TimeMangaer.
    val hasStartStopTimerStarted: MutableLiveData<Boolean> = savedStateHandle.getLiveData("hasStartStopTimerStarted",false)


    var startBtnState = false
    var starBtns = false
    var isBtnPressed = false
    var hasBreakAlarmBeenSetOn = false
    var TIMELEFT: Long = 0L
    var pictureBtnState = true





    /**
     * Sets a value in the saved state handle.
     *
     * @param name The key where the value will be stored in.
     * @param valToSet The integer value to set.
     */
    fun setValue(name: String, valToSet: Int){
        savedStateHandle[name] = valToSet
    }
    /**
     * Sets a value in the saved state handle.
     *
     * @param name The key where the value will be stored in.
     * @param valToSet The boolean value to set.
     */
    fun setValue(name: String, valToSet: Boolean){
        savedStateHandle[name] = valToSet
    }

    /**
     * Sets a value in the saved state handle.
     *
     * @param name The key where the value will be stored in.
     * @param valToSet The long value to set.
     */
    fun setValue(name: String, valToSet: Long){
        savedStateHandle[name] = valToSet
    }
    /**
     * Sets a value in the saved state handle.
     *
     * @param name The key where the value will be stored in.
     * @param valToSet The float value to set.
     */
    fun setValue(name: String, valToSet: Float){
        savedStateHandle[name] = valToSet
    }

    /**
     * Sets a value in the saved state handle.
     *
     * @param name The key where the value will be stored in.
     * @param valToSet The string value to set.
     */
    fun setValue(name: String, valToSet: String){
        savedStateHandle[name] = valToSet
    }


    /**
     * This function resets everything stored here
     * */
    fun resetEveryhting(){
        savedStateHandle["imgUri1"] = null
        savedStateHandle["imgUri2"] = null
        savedStateHandle["averagePoint"] = 0f
        savedStateHandle["MAXROUND"] =  0
        savedStateHandle["studyTime"] = 25 * 60000L
        savedStateHandle["breakTime"] = 5 * 60000L
        savedStateHandle["rounds"] = 4L
        savedStateHandle["isHomeButtonPressed"] = false
        savedStateHandle["hasPickedStar"] = false
        savedStateHandle["isStartBtnPressed"] = false
        savedStateHandle["isSecondPicture"] =  false
        savedStateHandle["hasAlarmBeenSetOn"] = false
        startBtnState = false
        starBtns = false
        isBtnPressed = false
        hasBreakAlarmBeenSetOn = false
        TIMELEFT = 0L
        pictureBtnState = true
        savedStateHandle["currentText"] = ""
    }
}

