package readyfiji.app;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Generate SHA-1 Key
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA-1");
                md.update(signature.toByteArray());

                // Convert the byte array to a formatted hex string (SHA-1 fingerprint format)
                StringBuilder sha1String = new StringBuilder();
                byte[] digest = md.digest();
                for (int i = 0; i < digest.length; i++) {
                    if (i != 0) {
                        sha1String.append(":");
                    }
                    String hex = Integer.toHexString(0xff & digest[i]);
                    if (hex.length() == 1) sha1String.append('0');
                    sha1String.append(hex);
                }

                Log.d("SHA-1 Key", sha1String.toString());
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
