package se.umu.bide0023.pomodoroapp
import android.content.Context
import android.media.MediaPlayer
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.navigation.NavController
import java.util.Locale
import java.time.LocalTime


/**
 *
 * @author Biruk Debebe Kebede
 *
 * This helper class is the class that actually manages the Pomodoro timer functionality in terms of
 * starting, pausing, and updating the timer. It handles also handles the triggering of notifications and alarms
 * when the timer finishes.
 *
 * @param studyTime Duration for study time.
 * @param breakTime Duration for break time.
 * @param rounds Number of Pomodoro rounds
 */
class TimerManager() {

    var time: String =""
    private lateinit var countDownTimer : CountDownTimer
    var STUDYTIME: Long = 0
    var BREAKTIME: Long = 0
    var ROUND: Long = 4
    val rightNow = LocalTime.now()
    var timeList: ArrayList<Int> = arrayListOf(rightNow.hour, rightNow.minute, rightNow.second, rightNow.nano/ (1000000))



    constructor(studyTime: Long, breakTime: Long, rounds:Long) : this() {
        this.STUDYTIME = studyTime
        this.BREAKTIME = breakTime
        this.ROUND = rounds
    }



    /**
     *
     * The function starts CountDownTimer that counts down from the given time duration argument.
     * It updates the remaining time in the TIMElEFT variable every second
     * and displays the current time. When the timer finishes, it triggers notification handling,
     * playing an alarm sound, and navigating to another fragment.
     *
     *
     * @param context The context in which the timer operates.
     * @param requiredView The view containing the TextView to update with the remaining time.
     * @param navController navigation controller used to navigate between fragments.
     * @param timeDuration The initial time duration for the countdown.
     * @param appStateViewModel The view model used to store and observe data.
     * @param alarmSoundPlayer The media player for the alarm sound.
     * @param requestPermissionLaunch launcher for requesting permission to post notifications.
     *
     **/
    fun startStopTimer(context: Context,
                       requiredView: View,
                       navController: NavController,
                       timeDuration:Long,
                       appStateViewModel: AppStateViewModel,
                       alarmSoundPlayer: MediaPlayer,
                       currentText: TextView) {

        if(appStateViewModel.TIMELEFT == 0L)  appStateViewModel.TIMELEFT = timeDuration
        val timeTextView = requiredView.findViewById<TextView>(R.id.timeTextView)
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        if(appStateViewModel.hasTimerStarted.value == true){
        countDownTimer =
            object : CountDownTimer(appStateViewModel.TIMELEFT, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    appStateViewModel.TIMELEFT = millisUntilFinished
                    setTimeText(millisUntilFinished, BREAKTIME, appStateViewModel, currentText, alarmSoundPlayer)
                    timeTextView.text = time
//                    Log.d("ticking is: ", time)
                    if(appStateViewModel.isHomeButtonPressed.value != null && appStateViewModel.isHomeButtonPressed.value == true) countDownTimer.cancel()
                }

                override fun onFinish() {
                    appStateViewModel.hasBreakAlarmBeenSetOn = false
                    appStateViewModel.isBtnPressed = false
                    if (time.equals("00:00:00")){
                        val sharedPref = context.getSharedPreferences("pomodoro_sharedPref", Context.MODE_PRIVATE)?:return
                        val notifiationState = sharedPref.getBoolean("notificationSetting", true)
                        val notifiationState2 = sharedPref.getBoolean("notificationSetting2", false)
                        val hasAlarmBeenSetOnFlag = sharedPref.getBoolean("hasAlarmBeenSetOn", false)

                        if (navController.currentDestination?.id == R.id.sessionFragment) {
                            if(hasAlarmBeenSetOnFlag == false){

                                if(notifiationState && notifiationState2) NotificationHandler(context)
                                alarmSoundPlayer.seekTo(0)
                                alarmSoundPlayer.start()
                                sharedPref.edit().putBoolean("hasAlarmBeenSetOn", true).apply()
                            }
                            navController.navigate(R.id.navigateToRecapFromSession)

                        }
                    }
                    appStateViewModel.setValue("hasTimerStarted", false)
                }
            }.start()}
    }


    /**
     * Pauses the ongoing countdown timer by canceling it and updating the timer state.
     */
    fun pausTimer(){
        if (::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
    }


    /**
     * Computes the time difference between the current time and a saved time in milliseconds.
     *
     * The function calculates the time difference based on hours, minutes, and seconds. It
     * converts both the saved time and the current time to milliseconds and then returns
     * the difference.
     *
     * @param timeSaved An ArrayList of integers representing the saved time.
     * @param timeNow An ArrayList of integers representing the current time.
     * @return The time difference in milliseconds.
     */
    fun computeTimeDifference(timeSaved: ArrayList<Int>, timeNow: ArrayList<Int>): Long{
        val savedInMs = (timeSaved[0] * 3600000) + (timeSaved[1] * 60000) + (timeSaved[2] * 1000)
        val nowInMs = (timeNow[0] * 3600000) + (timeNow[1] * 60000) + (timeNow[2] * 1000)
        var res = 0
        if(nowInMs> savedInMs)  res = nowInMs - savedInMs;
        return res.toLong()
    }



    /**
     *
     * The function calculates the remaining time for both the study and break periods,
     * formats the time as a string  and updates time variable. It also checks
     * if the timer has entered.
     *
     * @param millisUntilFinished The remaining time in milliseconds until timer finishes.
     * @param breakTime The time duration for the break period.
     * @param appStateViewModel The view model used to store and observe objects.
     *
     */
    fun setTimeText(millisUntilFinished: Long, breakTime: Long, appStateViewModel: AppStateViewModel,
                    currentText: TextView, alarmSoundPlayer: MediaPlayer){
        if(millisUntilFinished > breakTime){
            var tempMillisUntilFinished =  millisUntilFinished - breakTime
            if(tempMillisUntilFinished>= (STUDYTIME + BREAKTIME)-2){
                tempMillisUntilFinished -= 100L
            }
            val secondsRemaining = (tempMillisUntilFinished / 1000) % 60
            val minutesRemaining = (tempMillisUntilFinished / 1000 / 60) % 60
            val hoursRemaining = (tempMillisUntilFinished / 1000 / 3600) % 24
            time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hoursRemaining, minutesRemaining, secondsRemaining)
            appStateViewModel.setValue("currentText", "Study")
            currentText.text = appStateViewModel.currentText.value
        }
        else{
            if(millisUntilFinished > breakTime - 1000 && millisUntilFinished < breakTime+1000) {
                if(!appStateViewModel.hasBreakAlarmBeenSetOn){
                    alarmSoundPlayer.seekTo(0)
                    alarmSoundPlayer.start()}
            }
            appStateViewModel.hasBreakAlarmBeenSetOn = true
            appStateViewModel.setValue("currentText", "Break")
            currentText.text = appStateViewModel.currentText.value
            val secondsRemaining = (millisUntilFinished / 1000) % 60
            val minutesRemaining = (millisUntilFinished / 1000 / 60) % 60
            val hoursRemaining = (millisUntilFinished / 1000 / 3600) % 24
            time = String.format(Locale.getDefault(), "%02d:%02d:%02d", hoursRemaining, minutesRemaining, secondsRemaining)
            appStateViewModel.starBtns = true
            appStateViewModel.startBtnState = false
        }
    }


    /**
     * This method updates the displayed time on the view based on the current remaining time.
     * It also calculates the remaining time then formats it into a time string and
     * updates the TextView.
     *
     * @param requiredView The view containing the TextView
     * @param appStateViewModel The ViewModel containing the app's state such as TIMELEFT and break time
     */
    fun showTime(requiredView: View, appStateViewModel: AppStateViewModel){
        var tempTimeLeft = appStateViewModel.TIMELEFT
        if(appStateViewModel.TIMELEFT > (appStateViewModel.breakTime.value?:0)) {
            tempTimeLeft = appStateViewModel.TIMELEFT - (appStateViewModel.breakTime.value?:0)}
        val secondsRemaining = (tempTimeLeft / 1000) % 60
        val minutesRemaining = (tempTimeLeft / 1000 / 60) % 60
        val hoursRemaining = (tempTimeLeft/ 1000 / 3600) % 24
        val tempTime = String.format(Locale.getDefault(), "%02d:%02d:%02d", hoursRemaining, minutesRemaining, secondsRemaining)
        val timeTextView = requiredView.findViewById<TextView>(R.id.timeTextView)
        timeTextView.text = tempTime
    }

}

