package readyfiji.app;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Disable night mode (dark theme) across the entire app
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
    }
}
