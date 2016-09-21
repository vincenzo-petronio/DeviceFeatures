package it.localhost.app.mobile.devicefeatures;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.jaredrummler.android.device.DeviceName;

import android.content.Context;
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

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ListView lvItems;
    private List<String> items = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        // VIEW
        setContentView(R.layout.activity_main);
        lvItems = (ListView) findViewById(R.id.lvItems);

        // INIT
        mContext = this;

        // SETUP
        items.add(checkPlayServices());
        items.add(checkOpenGL());
        items.add(checkDpi());
        items.add(checkSize());
        items.add(checkAPI());
        items.add(checkDeviceName());

        Observable.create(itemsAction) // qui creo la sorgente di dati
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(itemsFunc)
                .subscribe(lvItemsSubscriber);
    }

    // Qui definisco il consumatore dei dati
    Subscriber<String> lvItemsSubscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {
            Log.v(TAG, "[Rx]onCompleted" + " ");
            lvItems.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, items));
        }

        @Override
        public void onError(Throwable e) {
            Log.e(TAG, "[Rx]onError" + " " + e);
        }

        @Override
        public void onNext(String strings) {
            Log.v(TAG, "[Rx]onNext" + " " + strings);
        }
    };

    /**
     * ACtion che viene chiamata quando un Subscriber si sottoscrive all'Observable
     */
    Observable.OnSubscribe<List<String>> itemsAction = subscriber -> {
        Log.v(TAG, "[Rx]call: itemsAction");

        subscriber.onNext(items);
        subscriber.onCompleted();
    };

    /**
     * Function che prende una List di Strings e le emette una ad una.
     */
    Func1<List<String>, Observable<String>> itemsFunc = Observable::from;

    // DATA
    private String checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(mContext);

        String playServices = String.format(getResources().getString(R.string.play_services),
                apiAvailability.getErrorString(resultCode));

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.e(TAG, "This device is not supported.");
            }
        }

        return playServices;
    }

    private String checkOpenGL() {
        FeatureInfo[] list = this.getPackageManager()
                .getSystemAvailableFeatures();

        String opengl = String.format(getResources().getString(R.string.opengl),
                list[list.length - 1].getGlEsVersion());

        return opengl;
    }

    private String checkDpi() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        String density = String.format(getResources().getString(R.string.density),
                Float.toString(displayMetrics.density),
                Integer.toString(displayMetrics.densityDpi));

        return density;
    }

    private String checkSize() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        String size = String.format(getResources().getString(R.string.size),
                Integer.toString(displayMetrics.heightPixels),
                Integer.toString(displayMetrics.widthPixels),
                Float.toString(displayMetrics.ydpi),
                Float.toString(displayMetrics.xdpi)
        );

        return size;
    }

    private String checkAPI() {
        String api = String.format(getResources().getString(R.string.api),
                Integer.toString(Build.VERSION.SDK_INT), Build.VERSION.RELEASE);

        return api;
    }

    private String checkDeviceName() {
        String deviceName = String.format(getResources().getString(R.string.device_name),
                DeviceName.getDeviceName());

        return deviceName;
    }
}
