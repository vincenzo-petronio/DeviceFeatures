package it.localhost.app.mobile.devicefeatures;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import android.content.pm.FeatureInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ListView lvItems;
    private List<String> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // VIEW
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);

        // SETUP
        checkPlayServices();
        checkOpenGL();
        checkDpi();
        checkSize();
        checkAPI();

        // INIT
        lvItems.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items));
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        String playServices = String.format(getResources().getString(R.string.play_services),
                apiAvailability.getErrorString(resultCode));
        items.add(playServices);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    private void checkOpenGL() {
        FeatureInfo[] list = this.getPackageManager()
                .getSystemAvailableFeatures();

        String opengl = String.format(getResources().getString(R.string.opengl),
                list[list.length - 1].getGlEsVersion());

        items.add(opengl);
    }

    private void checkDpi() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        String density = String.format(getResources().getString(R.string.density),
                Float.toString(displayMetrics.density),
                Integer.toString(displayMetrics.densityDpi));

        items.add(density);
    }

    private void checkSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        String size = String.format(getResources().getString(R.string.size),
                Integer.toString(displayMetrics.heightPixels),
                Integer.toString(displayMetrics.widthPixels),
                Float.toString(displayMetrics.ydpi),
                Float.toString(displayMetrics.xdpi)
        );

        items.add(size);
    }

    private void checkAPI() {
        String api = String.format(getResources().getString(R.string.api),
                Integer.toString(Build.VERSION.SDK_INT), Build.VERSION.RELEASE);

        items.add(api);
    }
}
