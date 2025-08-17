package se.umu.bide0023.pomodoroapp
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat

/**
 * This is a help class for SessionFragment that handles setting up and displaying a notification.
 * */
class NotificationHandler(myContext: Context) {
    val CHANNELID = "NOTIFICATION_ID"

    init {
        makeNotification(myContext)
    }

    /**
     * This funciton creates and displays a notification when a Pomodoro session ends.
     * It first cheks that a notification channel exists, then builds a notification
     * and if permission is not granted the function exits without displaying the notification.
     *
     * @param myContext The context used to create and send the notification.
     */
    fun makeNotification(myContext: Context){
        createChannelForNotification(myContext, "Pomodoro Session Alerts", "Notification about pomodoro", )
        val builder = Notification.Builder(myContext, CHANNELID)
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Pomodoro App complete")
            .setContentText("Your session has ended. Please start the next session or start a new one.")

        with(NotificationManagerCompat.from(myContext)){
            if (ActivityCompat.checkSelfPermission(
                    myContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                return@with
            }
            notify(1, builder.build())
           }
        }

    /**
     * Creates a notification channel which is required for sending notifications on newer
     * Android versions so it checks compatibilty.
     *
     * @param myContext  context used to access the system notification service.
     * @param channelName  Name of the notification channel.
     * @param descriptionText Description of the channel's purpose.
     */
    fun createChannelForNotification(myContext: Context, channelName:String, descriptionText:String){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNELID, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = descriptionText

            val notificationManager: NotificationManager = myContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}