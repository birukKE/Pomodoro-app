package se.umu.bide0023.pomodoroapp
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.jvm.Throws
import android.Manifest
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 *  * @author Biruk Debebe Kebede
 *
 *
 * The SessionRecap fragment handles UI and logic for displaying the session recap.
 * It handles photo capturing, focus rating, and navigation based on session state.
 * It also includes permission handling for the camera and observes LiveData changes.
 */
class SessionRecap : Fragment() {

    val appStateViewModel: AppStateViewModel by activityViewModels()
    lateinit var photoImgView1: ImageView
    lateinit var photoImgView2: ImageView
    lateinit var sessionBtn: Button
    lateinit var star1: ImageButton
    lateinit var star2: ImageButton
    lateinit var star3: ImageButton
    lateinit var photoBtn: Button
    lateinit var homebtn: ImageButton
    lateinit var recapTextView: TextView
    lateinit var sessionInfo: TextView
    var sessionInfoText: String = "   After each session, \nyou need to rate the session 1-3"
    var pointText = ""


/**
 * It handles the request for camera permission and launches the camera.
 *
 * */
    val requestPermissionLaunch = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        isCaptured ->
            if(isCaptured){
                if(appStateViewModel.isSecondPicture.value == false) pictureLaunch.launch(appStateViewModel.imgUri1.value)
                if(appStateViewModel.isSecondPicture.value == true) pictureLaunch.launch(appStateViewModel.imgUri2.value)
            } else{ }
    }

    /**
     * This handles taking the picture and updates  photoImgView(1/2) if successful
     */
    val pictureLaunch = registerForActivityResult(ActivityResultContracts.TakePicture()){
            isCaptured ->
        if(isCaptured){
            println("Success")
            if(appStateViewModel.isSecondPicture.value == false) photoImgView1.setImageURI(appStateViewModel.imgUri1.value)
            if(appStateViewModel.isSecondPicture.value ==  true)  photoImgView2.setImageURI(appStateViewModel.imgUri2.value)

            if(appStateViewModel.isSecondPicture.value == false){
                appStateViewModel.startBtnState = true
                sessionBtn.isEnabled = appStateViewModel.startBtnState
            }
            appStateViewModel.setValue("isSecondPicture",  true)

            appStateViewModel.pictureBtnState = false
            photoBtn.isEnabled = appStateViewModel.pictureBtnState
        }
        else{
            println("No success")
        }
    }

    /**
     * This method creates an image file.
     *
     * @return an object representing the created image file.
     * */
    @Throws(IOException::class)
    fun createImageFile(): File {
        val makeTimeName = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val directionOfStorage: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${makeTimeName}_", ".jpg", directionOfStorage) //.apply { myCurrPhoto = absolutePath }
    }



    /**
     *  This method inflates layout and returns view. It also makes sure nothing happend when the default
     *  back navigation is clicked
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view1 = inflater.inflate(R.layout.fragment_session_recap, container, false)

        val callback = object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view1
    }

    /**
     * overriden method that checks when buttons ar clicked and then calls function that handle them.
     * It also updates flag and variable.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recapTextView = view.findViewById<TextView>(R.id.averageTextView)
        sessionInfo = view.findViewById<TextView>(R.id.sessionInfo)

        initialize(view)

        recapTextView.text = pointText
        homebtn.setOnClickListener{homeButtonAction()}
        observeViewModel()
        sessionInfo.text = sessionInfoText
        if(appStateViewModel.MAXROUND.value?:0 >= appStateViewModel.rounds.value?:4) photoImgView1.visibility = View.VISIBLE
        else photoImgView1.visibility = View.INVISIBLE

        star1.setOnClickListener {starBtnsAction(1F)}
        star2.setOnClickListener {starBtnsAction(2F)}
        star3.setOnClickListener {starBtnsAction(3F)}


        appStateViewModel.setValue("hasPickedStar", true)

        if(appStateViewModel.MAXROUND.value?:0  >= appStateViewModel.rounds.value?:4){
            appStateViewModel.pictureBtnState = true
            photoBtn.isEnabled = appStateViewModel.pictureBtnState
        }
        if (appStateViewModel.isSecondPicture.value == false) {
            sessionInfoText = "You must take a picture to start the session \n      and track your motivation.\n You will see the pictures at the end!"
        }
        sessionInfo.text = sessionInfoText
        sessionBtn.isEnabled = appStateViewModel.startBtnState
        sessionBtn.setOnClickListener{
            sessionButtonActions()

        }


        photoBtn.setOnClickListener {photoButtonActions()}


    }

    /**
     * This method initializes UI components.
     * *
     * @param view The root view of the fragments layout.
     */
    fun initialize(view: View) {
        photoImgView1 = view.findViewById(R.id.photoView1)
        photoImgView2 = view.findViewById(R.id.photoView2)
        photoBtn = view.findViewById<Button>(R.id.photoButton)
        sessionBtn = view.findViewById<Button>(R.id.sessionBtn)
        star1 = view.findViewById<ImageButton>(R.id.star1)
        star2 = view.findViewById<ImageButton>(R.id.star2)
        star3 = view.findViewById<ImageButton>(R.id.star3)
        homebtn =  view.findViewById<ImageButton>(R.id.homeButtonRecap)
    }

    /*
     ** Clicking home button clears current activity and takes user back to home page.
     */
    fun homeButtonAction(){
        appStateViewModel.resetEveryhting()
        photoImgView1.setImageDrawable(null)
        photoImgView1.setImageDrawable(null)
        pointText = ""
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    /**
     * It controlls the visibility of the star buttons based on flag that describes the state of the star button.
     * If the flag is false, the buttons are hidden if not then they are made visible.
     *
     * @param star1 The first star button.
     * @param star2 The second star button.
     * @param star3 The third star button.
     */
    fun controlStarBtns(star1: ImageButton, star2: ImageButton, star3: ImageButton){
        if(appStateViewModel.starBtns == false){
            star1.visibility = View.INVISIBLE
            star2.visibility = View.INVISIBLE
            star3.visibility = View.INVISIBLE
        } else{
            star1.visibility = View.VISIBLE
            star2.visibility = View.VISIBLE
            star3.visibility = View.VISIBLE
        }
    }

    /**
     * This method handles the session button actions which is incrementing MAXROUND and
     * setting flags on conditions and displays values and as well as disable the button afterwards.
     * It also navigates to the session screen if MAXROUND is less than 3.
     */
    fun sessionButtonActions(){
        val sharedPref = requireContext().getSharedPreferences("pomodoro_sharedPref", Context.MODE_PRIVATE)?:return
        sharedPref.edit().putBoolean("hasAlarmBeenSetOn", false).apply()
        val temp0 = appStateViewModel.MAXROUND.value
        if(appStateViewModel.isStartBtnPressed.value == false){
            if (temp0 != null) appStateViewModel.setValue("MAXROUND", temp0 + 1)
            appStateViewModel.setValue("isStartBtnPressed", true)

        }
        val MAX = appStateViewModel.MAXROUND.value

        if (MAX != null && MAX < appStateViewModel.rounds.value?:4){
            findNavController().navigate(R.id.navigateToSessionFromRecap)
        }else if(MAX != null && appStateViewModel.rounds.value==1L) {
            appStateViewModel.setValue("rounds", (appStateViewModel.rounds.value?:4)-1)
            findNavController().navigate(R.id.navigateToSessionFromRecap)
        }
        appStateViewModel.startBtnState = false
    }

    /**
     * Handles the photo button click actions which is checking if it is the first or second picture to capture
     * and then set the URI and grants the permissions. It also launches the camera permission request.
     */
    fun photoButtonActions(){
        if (appStateViewModel.isSecondPicture.value == false) {
            appStateViewModel.imgUri1.value = FileProvider.getUriForFile(requireContext(),
                "se.umu.bide0023.pomodoroapp.fileprovider", createImageFile())
            requireContext().grantUriPermission("se.umu.bide0023.pomodoroapp.fileprovider",
                appStateViewModel.imgUri1.value, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)

            sessionInfoText = " After each session, \n" + "you need to rate the session 1-3"
            sessionInfo.text = sessionInfoText
        }
        if (appStateViewModel.isSecondPicture.value == true) {
            appStateViewModel.imgUri2.value = FileProvider.getUriForFile(requireContext(),
                "se.umu.bide0023.pomodoroapp.fileprovider", createImageFile())
            requireContext().grantUriPermission("se.umu.bide0023.pomodoroapp.fileprovider",
                appStateViewModel.imgUri2.value, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        requestPermissionLaunch.launch(Manifest.permission.CAMERA)

    }

    /**
     * Observes the LiveData from the ImageViewModel that listens for changes
     * and updates them as needed.
     */
    fun observeViewModel(){
        appStateViewModel.imgUri1?.let { uri ->
            photoImgView1.setImageURI(uri.value)
        }

        appStateViewModel.imgUri2?.let { uri ->
            photoImgView2.setImageURI(uri.value)
        }

        appStateViewModel.imgUri1.observe(requireActivity()) { uri ->
            photoImgView1.setImageURI(uri)
        }

        appStateViewModel.imgUri2.observe(requireActivity()) { uri ->
            photoImgView2.setImageURI(uri)
        }
        appStateViewModel.averagePoint.observe(viewLifecycleOwner){
                value -> if(value != appStateViewModel.averagePoint.value)appStateViewModel
                    .setValue("averagePoint", value)
        }

        appStateViewModel.hasPickedStar.observe(viewLifecycleOwner){
                flag -> if(flag != appStateViewModel.hasPickedStar.value) appStateViewModel.setValue("hasPickedStar", flag)
        }

        appStateViewModel.isStartBtnPressed.observe(viewLifecycleOwner){
                flag -> if(flag != appStateViewModel.isStartBtnPressed.value) appStateViewModel.setValue("isStartBtnPressed", flag)
        }
        appStateViewModel.isSecondPicture.observe(viewLifecycleOwner){
                flag -> if(flag != appStateViewModel.isSecondPicture.value) appStateViewModel.setValue("isSecondPicture", flag) }


        appStateViewModel.studyTime.observe(viewLifecycleOwner) { studyTimeTemp ->
            if(studyTimeTemp != appStateViewModel.studyTime.value) appStateViewModel.setValue("studyTime", studyTimeTemp)
        }
        appStateViewModel.breakTime.observe(viewLifecycleOwner) { breakTimeTemp ->
            if(breakTimeTemp != appStateViewModel.breakTime.value) appStateViewModel.setValue("breakTime", breakTimeTemp)
        }
        appStateViewModel.rounds.observe(viewLifecycleOwner) { roundsTime ->
            if(roundsTime != appStateViewModel.rounds.value) appStateViewModel.setValue("rounds", roundsTime)
        }


    }


    /**
     * Handles the action when a star button is clicked. It updates the averagePoint and MAXROUND in the ViewModel class,
     * then it navigates to the session screen if the current round is less than 3,and it also updates
     * the displayed average focus point.
     *
     * @param starValue The value represents the rating value given by the star button clicked,
     *                  each star has a different value.
     */
    fun starBtnsAction(starValue: Float){

        if(appStateViewModel.MAXROUND.value?:0 <= appStateViewModel.rounds.value?:4) {
            val sharedPref = requireContext().getSharedPreferences("pomodoro_sharedPref", Context.MODE_PRIVATE)?:return
            sharedPref.edit().putBoolean("hasAlarmBeenSetOn", false).apply()

            if(appStateViewModel.MAXROUND.value?:0 < appStateViewModel.rounds.value?:4) {
                findNavController().navigate(R.id.navigateToSessionFromRecap)
            }

            appStateViewModel.starBtns = false
            val s1 = appStateViewModel.averagePoint.value
            if (s1 != null) appStateViewModel.setValue("averagePoint", s1 + starValue)
            val temp3 = appStateViewModel.MAXROUND.value
            if (temp3 != null) appStateViewModel.setValue("MAXROUND", temp3 + 1)

            if(appStateViewModel.MAXROUND.value?:0 > appStateViewModel.rounds.value?:4) appStateViewModel.setValue("averagePoint",
                (appStateViewModel.averagePoint.value ?: 0f)/(appStateViewModel.rounds.value?:4))


            appStateViewModel.averagePoint.observe(viewLifecycleOwner) { newAveragePoint ->
                pointText = "Average focus point:\n $newAveragePoint"
                recapTextView.text =  pointText
            }
            sessionInfoText = ""
            sessionInfo.text = sessionInfoText

            controlStarBtns(star1, star2, star3)

        }
    }

    /**
     * Overriden method to save important information when activity is destoryed.
     * */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("startBtnState", appStateViewModel.startBtnState)
        outState.putBoolean("starBtns", appStateViewModel.starBtns)
        outState.putBoolean("pictureBtnState", appStateViewModel.pictureBtnState)
        outState.putBoolean("startBtnState", appStateViewModel.startBtnState)
        outState.putString("pointText", pointText)
        outState.putString("sessionInfoText", sessionInfoText)

    }

    /**
     * Overriden method to restore important informations that had earlier been saved
     * before activity was destoryed.
     * */
    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        val tempStartBtnState:Boolean? = savedInstanceState?.getBoolean("startBtnState")
        if(tempStartBtnState!=null) appStateViewModel.startBtnState = tempStartBtnState

        val tempPictureBtnState:Boolean? = savedInstanceState?.getBoolean("pictureBtnState")
        if(tempPictureBtnState!=null) appStateViewModel.pictureBtnState = tempPictureBtnState
        photoBtn.isEnabled = appStateViewModel.pictureBtnState

        val tempSessionBtn:Boolean? = savedInstanceState?.getBoolean("startBtnState")
        if(tempSessionBtn!=null) appStateViewModel.startBtnState = tempSessionBtn
        sessionBtn.isEnabled = appStateViewModel.startBtnState

        val tempStarBtns:Boolean? = savedInstanceState?.getBoolean("starBtns")
        if(tempStarBtns!=null) appStateViewModel.starBtns = tempStarBtns
        controlStarBtns(star1, star2, star3)

        pointText = savedInstanceState?.getString("pointText")?:""
        recapTextView.text =  pointText

        val sessionInfoTextTemp = savedInstanceState?.getString("sessionInfoText")
        if (sessionInfoTextTemp!=null) sessionInfoText = sessionInfoTextTemp
        sessionInfo.text = sessionInfoText
    }



}


