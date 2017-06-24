package pad.ucm.approvisionate;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import pad.ucm.approvisionate.modelo.Local;

public class ActivityAdd extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 120;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 121;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 2;
    private Button foto;
    private EditText texto;
    private Button add;
    private Spinner apertura;
    private Spinner cierre;
    private Button ubi;
    private ToggleButton confirmacion;
    private Double latitude;
    private Double longitude;
    public static final int ACTIVITY_SELECT_FROM_CAMERA = 120;
    private ProgressDialog progressDialog;
    private String mCurrentPhotoPath;
    private Bitmap imageBitmap;
    private File imagen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        progressDialog = new ProgressDialog(this);
        foto = (Button) findViewById(R.id.buttonCamara);
        texto = (EditText) findViewById(R.id.nombreLocaluse);
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
            }
        });
        foto.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                doPhoto();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!texto.getText().toString().equalsIgnoreCase("")) {
                    if (confirmacion.isChecked()) {
                        if (imagen != null) {
                            FirebaseAuth mAuth = FirebaseAuth.getInstance();

                            FirebaseDatabase db = FirebaseDatabase.getInstance();
                            DatabaseReference postsRef = db.getReference("Locales");

                            DatabaseReference newPostRef = postsRef.push();
                            String key = newPostRef.getKey();
                            Local aux = new Local(texto.getText().toString(), key, apertura.getSelectedItem().toString(), cierre.getSelectedItem().toString(), latitude,longitude, mAuth.getCurrentUser().getEmail());
                            newPostRef.setValue(aux);
                            String ruta = "images/"+key+".jpg";
                            FirebaseStorage serv = FirebaseStorage.getInstance();
                            StorageReference ref = serv.getReferenceFromUrl("gs://approvisionate.appspot.com/");
                            ref = ref.child(ruta);
                            UploadTask upload = ref.putFile(Uri.fromFile(imagen));

                            progressDialog.setTitle("Añadiendo locaclización...");
                            progressDialog.show();
                            upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    progressDialog.cancel();
                                    finish();
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "Realice una fotografía del lugar para poder añadir la ubicación", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Obtenga la ubicación del lugar para poder añadirlo", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Introduzca el nombre del sitio para poder añadirlo", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }
    private void permisosAlmacenamiento() {
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    private void obtenerUbi() {
        LocationManager location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (checkLocation(location)) {
                location.requestLocationUpdates(location.GPS_PROVIDER, 500, 10, locationListenerBest);
                progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Obteniendo ubicación...");
                progressDialog.show();
            }


        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            obtenerUbi();
        }

    }

    private boolean checkLocation(LocationManager location) {
        if (!isLocationEnabled(location))
            showAlert();
        return isLocationEnabled(location);
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Su ubicación esta desactivada.\npor favor active su ubicación " +
                        "usa esta app")
                .setPositiveButton("Configuración de ubicación", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });
        dialog.show();
    }

    private boolean isLocationEnabled(LocationManager location) {
        return location.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                location.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private final LocationListener locationListenerBest = new LocationListener() {
        public void onLocationChanged(Location location) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    confirmacion.setChecked(true);
                    progressDialog.cancel();
                }
            });
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private void doPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            try {
                imagen = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        FileOutputStream out = new FileOutputStream(image);
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


}
