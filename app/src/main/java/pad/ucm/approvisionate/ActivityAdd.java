package pad.ucm.approvisionate;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.File;
import java.security.Provider;

public class ActivityAdd extends AppCompatActivity {
    private Button foto;
    private TextView texto;
    private Uri mImageUri;
    private Button add;
    private Spinner apertura;
    private Spinner cierre;
    private Button ubi;
    private ToggleButton confirmacion;
    private Double latitude;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        foto = (Button) findViewById(R.id.buttonCamara);
        texto = (TextView) findViewById(R.id.nombreLocal);
        apertura = (Spinner) findViewById(R.id.spinnerApertura);
        cierre = (Spinner) findViewById(R.id.spinner2);
        add = (Button) findViewById(R.id.buttonAñadir);
        ubi = (Button) findViewById(R.id.botonUbicacion);
        confirmacion = (ToggleButton) findViewById(R.id.toggleButton);
        confirmacion.setClickable(false);
        ubi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                obtenerUbi();
                if (latitude != null && longitude != null) confirmacion.setChecked(true);
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {
            public static final int ACTIVITY_SELECT_FROM_CAMERA = 120;

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        "android.media.action.IMAGE_CAPTURE");
                File photo = null;
                try {
                    // place where to store camera taken picture
                    File tempDir = getApplicationContext().getExternalCacheDir();
                    tempDir = new File(tempDir.getAbsolutePath() + "/temp/");
                    if (!tempDir.exists()) {
                        tempDir.mkdir();
                    }
                    photo = File.createTempFile("temporal", ".jpg", tempDir);
                    photo.delete();
                } catch (Exception e) {
                    Log.v(getClass().getSimpleName(),
                            "Can't create file to take picture!");
                }
                mImageUri = Uri.fromFile(photo);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                startActivityForResult(intent, ACTIVITY_SELECT_FROM_CAMERA);
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    private void obtenerUbi() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = location.getBestProvider(criteria, true);
        latitude = location.getLastKnownLocation(provider).getLatitude();
        longitude = location.getLastKnownLocation(provider).getLongitude();
    }
}
