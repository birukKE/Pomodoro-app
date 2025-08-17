package se.umu.bide0023.pomodoroapp
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import java.time.LocalTime
import android.Manifest
import android.content.Context
import android.util.Log

/**
 * @author Biruk Debebe Kebede
 *
 *  This Fragment manages the Pomodoro session and it handles the starting, pausing the timer.
 *  It observes changes to the ViewModel and updates things.
 *  The fragment also requests notification permissions and handles the Home button action, which
 *  resets the session and navigates to the main activity.
 * */
class SessionFragment : Fragment() {
    lateinit var navController: NavController
    lateinit var alarmSoundPlayer: MediaPlayer
    lateinit var currentText: TextView
    var notificationState2 = false
    val appStateViewModel: AppStateViewModel by activityViewModels()
    var timeManager = TimerManager(25*60000, 5*60000, 4)

    /**
     * This method inflates layout and returns view
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_session , container, false)
    }

    /**
     * Overridden method that handles  creation of the view.
     * It initializes shared preference, sets up permission request for notifications and it also
     * initializes buttons, and observes LiveData objects. It also makes sure that users cannot leave by pressing back button
     * and they must use home button.
     * */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("pomodoro_sharedPref", Context.MODE_PRIVATE)?:return

        val requestPermissionLaunch:  ActivityResultLauncher<String> = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                isCaptured ->
            if(isCaptured){ notificationState2 = true
                sharedPref.edit().putBoolean("notificationSetting2", notificationState2).apply()
            } else{ notificationState2 = false
                sharedPref.edit().putBoolean("notificationSetting2", notificationState2).apply()
            }
        }
        requestPermissionLaunch.launch(Manifest.permission.POST_NOTIFICATIONS)

        observeLiveObjects()
        val startBtn = view.findViewById<Button>(R.id.startBtn)
        val pauseBtn = view.findViewById<Button>(R.id.pauseBtn)
        val homebtn = view.findViewById<ImageButton>(R.id.homeButtonSession)
        currentText = view.findViewById(R.id.currentText)

        timeManager = TimerManager(appStateViewModel.studyTime.value?:0L, appStateViewModel.breakTime.value?:0L, appStateViewModel.breakTime.value?:0L)
        navController = findNavController()
        alarmSoundPlayer = MediaPlayer.create(requireContext(), R.raw.alarm_sound)

        startBtn.setOnClickListener{startButtonAction()}
        pauseBtn.setOnClickListener{pauseButtonAction()}
        homebtn.setOnClickListener{homeButtonAction()}

        val callback = object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    /**
     * This method handles the Home button action by starting the MainActivity and
     * and then finishes the current activity.
     */
    fun homeButtonAction(){
        val intent = Intent(requireActivity(), MainActivity::class.java)
        appStateViewModel.resetEveryhting()
        alarmSoundPlayer.stop()
        alarmSoundPlayer.release()
        appStateViewModel.setValue("isHomeButtonPressed", true)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * Handles the Start button action by initiating the timer if it has not started.
     * It then updtes the ViewModel state and starts the timer with the chosen break/ study time.
     */
    fun startButtonAction(){
        if(appStateViewModel.hasTimerStarted.value == false){//timeManager.hasTimerStarted) {
            appStateViewModel.setValue("hasTimerStarted", true)
            appStateViewModel.isBtnPressed = true
            val requiredView: View  = requireView()
            appStateViewModel.setValue("hasStartStopTimerStarted", true)
            timeManager.startStopTimer(requireContext(), requiredView, navController,
                timeManager.STUDYTIME + timeManager.BREAKTIME, appStateViewModel,
                alarmSoundPlayer, currentText)
            appStateViewModel.setValue("hasPickedStar", false)

        }
    }


    /**
     * Handles the pause button action by pausing the timer
     * if it is running and then it updates the timer state.
     */
    fun pauseButtonAction(){
            appStateViewModel.setValue("hasTimerStarted", false)
            timeManager.pausTimer()
    }

    /**
     * Observes changes to LiveData objects in the ViewModel and updates
     * the values.
     */
    fun observeLiveObjects(){
        appStateViewModel.studyTime.observe(viewLifecycleOwner)
        { studyTimeTemp ->
            if(studyTimeTemp != appStateViewModel.studyTime.value) appStateViewModel.studyTime.value = studyTimeTemp
        }
        appStateViewModel.breakTime.observe(viewLifecycleOwner)
        { breakTimeTemp ->
            if(breakTimeTemp != appStateViewModel.breakTime.value) appStateViewModel.breakTime.value = breakTimeTemp
        }
        appStateViewModel.isHomeButtonPressed.observe(viewLifecycleOwner)
        { flag ->
            if(flag != appStateViewModel.isHomeButtonPressed.value) appStateViewModel.isHomeButtonPressed.value = flag
        }
        appStateViewModel.hasAlreadyStarted.observe(viewLifecycleOwner)
        { flag ->
            if(flag != appStateViewModel.hasAlreadyStarted.value) appStateViewModel.hasAlreadyStarted.value = flag
        }

        appStateViewModel.currentText.observe(viewLifecycleOwner) { currentTextTemp ->
            if(currentTextTemp != appStateViewModel.currentText.value) appStateViewModel.currentText.value = currentTextTemp
        }
        appStateViewModel.hasAlarmBeenSetOn.observe(viewLifecycleOwner) { hasAlarmBeenSetOnTemp ->
            if(hasAlarmBeenSetOnTemp != appStateViewModel.hasAlarmBeenSetOn.value) appStateViewModel.setValue("hasAlarmBeenSetOn", hasAlarmBeenSetOnTemp)
        }
        appStateViewModel.hasTimerStarted.observe(viewLifecycleOwner) { hasTimerStartedTemp ->
            if(hasTimerStartedTemp != appStateViewModel.hasTimerStarted.value) appStateViewModel.setValue("hasTimerStarted", hasTimerStartedTemp)

            appStateViewModel.hasStartStopTimerStarted.observe(viewLifecycleOwner) { hasStartStopTimerStartedTemp ->
                if(hasStartStopTimerStartedTemp != appStateViewModel.hasStartStopTimerStarted.value) appStateViewModel.setValue("hasStartStopTimerStarted", hasStartStopTimerStartedTemp)
            }

        }

    }

    /**
     * Overridden method to save the variable state used in this fragment.
     * Specifically it is used to save the remaining time and the current time hour/ minute/ second/ nanosecond)
     *  before the fragment is destroyed.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong("TIME_LEFT", appStateViewModel.TIMELEFT)
        timeManager.timeList.clear()
        val rightNow = LocalTime.now()
        timeManager.timeList = arrayListOf(rightNow.hour, rightNow.minute, rightNow.second, rightNow.nano/ (1000000))
        outState.putSerializable("TimeList", timeManager.timeList)
        outState.putBoolean("hasBreakAlarmBeenSetOn", appStateViewModel.hasBreakAlarmBeenSetOn)

    }


    /**
     * Overridden method to restore the state of the fragment. It retrieves the saved timeList
     * and remaining time and computes the time difference to update the remaining time more accurately.
     */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        appStateViewModel.TIMELEFT = savedInstanceState?.getLong("TIME_LEFT")?:0

        if(appStateViewModel.hasTimerStarted.value == true) {
            val savedTimeList = savedInstanceState?.getSerializable("TimeList")
            if(savedTimeList!=null) timeManager.timeList = savedTimeList as ArrayList<Int>
            val rightNow = LocalTime.now()
            val timeListNow: ArrayList<Int> = arrayListOf(
                rightNow.hour,
                rightNow.minute,
                rightNow.second,
                rightNow.nano / (1000000)
            )
            val timeToSubtract =
                timeManager.computeTimeDifference(timeManager.timeList, timeListNow)
            if (appStateViewModel.TIMELEFT > timeToSubtract) {
                appStateViewModel.TIMELEFT = appStateViewModel.TIMELEFT - timeToSubtract
            } else if (appStateViewModel.TIMELEFT < timeToSubtract) appStateViewModel.TIMELEFT =
                50L
        }
//        timeManager.startStopTimer(requireContext(), requireView(), navController, appStateViewModel.TIMELEFT, appStateViewModel,alarmSoundPlayer)
//        appStateViewModel.hasAlarmBeenSetOn = savedInstanceState?.getBoolean("hasAlarmBeenSetOn")?:false
        appStateViewModel.hasBreakAlarmBeenSetOn = savedInstanceState?.getBoolean("hasBreakAlarmBeenSetOn")?:false
    }

    /**
     * Overridden method to resume the timer when the fragment is resumed.
     */
    override fun onResume() {
        super.onResume()

        if(appStateViewModel.hasTimerStarted.value == true) {
            appStateViewModel.setValue("hasStartStopTimerStarted", true)
            timeManager.startStopTimer(
                requireContext(),
                requireView(),
                navController,
                appStateViewModel.TIMELEFT,
                appStateViewModel,
                alarmSoundPlayer,
                currentText
            )
        }else timeManager.showTime(requireView(), appStateViewModel)
    }

   /**
    * Overriden method to clean up and cancel timer when the fragment's view is destroyed.
   * */
    override fun onDestroyView() {
        super.onDestroyView()
            timeManager.pausTimer()
    }


}
