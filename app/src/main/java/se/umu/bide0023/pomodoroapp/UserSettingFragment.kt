package se.umu.bide0023.pomodoroapp
//import android.app.Fragment
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder


/**
 *
 * @author Biruk Debebe Kebede
 *
 * This fragment handles user input for customizing a Pomodoro session: study time, break time,
 * and the number of rounds. It validates the input, updates the session settings in the ViewModel,
 * and navigates to the session recap. If the input is invalid, an alert dialog is displayed.
 *
 */
class UserSettingFragment : Fragment() {

    /**Global variables*/
    val appStateViewModel: AppStateViewModel by activityViewModels()

    /**
     * Overriden function that inflates the layout for this t
     * */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_input_handler, container, false)
    }
    /**
     * Sets up the confirm button click listener and observes changes.
     * If the entered times are valid numbers then user will be navigated to the recap screen,
     * otherwise an alert dialog is shown.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val confirmBtn = view.findViewById<Button>(R.id.button2)
        val studyText = view.findViewById<EditText>(R.id.studyText)
        val breakText = view.findViewById<EditText>(R.id.breakText)
        val rounds = view.findViewById<EditText>(R.id.rounds)
        confirmBtn.setOnClickListener{confirmButtonAction(studyText, breakText, rounds)}

        //observe changes
        appStateViewModel.studyTime.observe(viewLifecycleOwner) { studyTimeTemp ->
            if(studyTimeTemp != appStateViewModel.studyTime.value) appStateViewModel.studyTime.value = studyTimeTemp
        }
        appStateViewModel.breakTime.observe(viewLifecycleOwner) { breakTimeTemp ->
            if(breakTimeTemp != appStateViewModel.breakTime.value) appStateViewModel.breakTime.value = breakTimeTemp
        }
        appStateViewModel.rounds.observe(viewLifecycleOwner) { roundsTimeTemp ->
            if(roundsTimeTemp != appStateViewModel.rounds.value) appStateViewModel.rounds.value = roundsTimeTemp
        }
    }

    /**
     * This method will take care of the action when the confirm button is clicked.
     * It checks/ verifies the user input for study and break time is in correct format,
     * then saves the values if valid, and navigates to the recap screen.
     * If the input is invalid, an error dialog will be shown.
     *
     *  @param studyText The EditText variable from user inputs for study time.
     *  @param breakText The EditText variable from user inputs for break time.
     *  @param rounds The EditText variable from user inputs that gives the number of rounds.
     */
    fun confirmButtonAction(studyText: EditText, breakText: EditText, rounds: EditText){
        val isStudyTextNumber = studyText.text.matches("\\d+".toRegex())
        val isBreakTextNumber = breakText.text.matches("\\d+".toRegex())
        val isroundsTextNumber = rounds.text.matches("\\d+".toRegex())
        if(isStudyTextNumber && isBreakTextNumber && isroundsTextNumber){
            appStateViewModel.setValue("studyTime", studyText.text.toString().toLong() * 60000)
            appStateViewModel.setValue("breakTime", breakText.text.toString().toLong() * 60000)
            appStateViewModel.setValue("rounds", rounds.text.toString().toLong())
            findNavController().navigate(R.id.navigateToRecapFromInput)
        }
        else{
            MaterialAlertDialogBuilder(requireActivity()).
            setTitle("Alert").
            setMessage("You can only enter digits!\n No text is allowed just number!").show()
        }
    }

}