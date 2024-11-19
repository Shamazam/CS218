package LoginRegister;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class DeveloperModeCheck {

    // Method to check if Developer Mode (USB Debugging) is enabled
    public static boolean isDeveloperModeEnabled(Context context) {
        boolean isDeveloperMode = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // For Android versions JELLY_BEAN_MR1 and above
            isDeveloperMode = Settings.Global.getInt(
                    context.getContentResolver(),
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
        } else {
            // For Android versions lower than JELLY_BEAN_MR1
            isDeveloperMode = Settings.Secure.getInt(
                    context.getContentResolver(),
                    Settings.Secure.DEVELOPMENT_SETTINGS_ENABLED, 0) != 0;
        }

        return isDeveloperMode;
    }

    // Method to stop the app if Developer Mode is enabled
    public static void preventAppIfDeveloperModeEnabled(Context context) {
        if (isDeveloperModeEnabled(context)) {
            // Show a message and exit the app
            Toast.makeText(context, "Developer Mode must be turned off to run the app.", Toast.LENGTH_LONG).show();

            // Delay for a second before closing the app
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Close the app
                    System.exit(0);  // This will terminate the app
                }
            }, 2000);  // 2 second delay for the message
        }
    }
}

