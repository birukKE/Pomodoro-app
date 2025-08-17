package se.umu.bide0023.pomodoroapp
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

/**
 *  * @author Biruk Debebe Kebede
 *
 * HomeFragment is a fragment that displays the home screen of the application.
 * It contains buttons for navigation to other screens, such as the recap and input screens.
 * It also handles the notification state changes by users.
 *
 */

class HomeFragment : Fragment() {


    lateinit var popUpMenu: PopupMenu
    lateinit var sharedPref: SharedPreferences
    val appStateViewModel: AppStateViewModel by activityViewModels()

    /**
     * Overidden method that inflates the fragment's layout and returns the root view.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }

    /**
     *
     * Overriden method to navigates to the recap screen when the recap button is pressed.
     * It also navigates to the input screen when the settings button is clicked.
     */

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val button = view.findViewById<Button>(R.id.button1)
        val setting = view.findViewById<ImageButton>(R.id.settingBtn)
        sharedPref = activity?.getSharedPreferences("pomodoro_sharedPref", Context.MODE_PRIVATE)?:return

        appStateViewModel.setValue("isHomeButtonPressed", false)
        popUpMenu = PopupMenu(requireContext(), setting)
        popUpMenu.inflate(R.menu.popup)

        upDateTitle()


        button.setOnClickListener{
            findNavController().navigate(R.id.navigateToRecapFromHome)
        }
        setting.setOnClickListener{settingButtonActions()
        }


        appStateViewModel.isHomeButtonPressed.observe(viewLifecycleOwner){ flag ->
            if(flag != appStateViewModel.isHomeButtonPressed.value) appStateViewModel.isHomeButtonPressed.value = flag
        }

    }


    /**
     * This method handles the actions for the setting button whcih diplays a popup menu with options.
     * When a menu item is selected then user will either navigates to another fragment or updates the different states.
     *
     */
    fun settingButtonActions(){
        popUpMenu.setOnMenuItemClickListener { item->
            when(item.itemId){
                R.id.firstItem -> {
                    findNavController().navigate(R.id.navigateToInputFromHome)
                    true
                }
                R.id.secondItem -> {
                    setNotificationState()
                    true
                }
                R.id.thirdItem->{
                    appStateViewModel.setValue("studyTime", 7000L)
                    appStateViewModel.setValue("breakTime", 4000L)
                    appStateViewModel.setValue("rounds", 2L)
                    findNavController().navigate(R.id.navigateToRecapFromHome)
                    true
                }
                else -> {
                    false
                }
            }

        }
        popUpMenu.show()

    }

    /**
     * It updates the notification title in shared preferences,
     * and it also updates the title of the notification item based on the new setting.
     */
    fun setNotificationState(){
        val notificState = sharedPref.getBoolean("notificationSetting", true)
        with(sharedPref.edit()){
            if(notificState){
                putBoolean("notificationSetting", false)
                putString("notificationTitle", "Turn on Notification")
                apply()
            }
            else {
                putBoolean("notificationSetting", true)
                putString("notificationTitle", "Turn off Notification")
                apply()
            }
            upDateTitle()

        }

    }

    /**
     * Updates the title of the second item in the popup menu based on the current notification title
     * stored in shared preferences.
     */
    fun upDateTitle(){
        val notificationTitle = sharedPref.getString("notificationTitle", "Turn off notification")
        popUpMenu.menu.findItem(R.id.secondItem).title = notificationTitle
    }


}
