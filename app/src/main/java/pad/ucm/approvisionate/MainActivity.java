package pad.ucm.approvisionate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pad.ucm.approvisionate.modelo.Local;
import pad.ucm.approvisionate.modelo.Taska;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int RC_SIGN_IN = 123;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 120;
    private static final int RC_ADD = 4;
    private TextView texto;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private GoogleMap mapa;
    private NavigationView navigationView;
    private FirebaseDatabase bd;
    private DatabaseReference refGithub;
    private String github;
    private DatabaseReference refPlay;
    private String play;
    private LocationManager locationManager;
    private FirebaseAuth.AuthStateListener listener;
    private DatabaseReference refLocales;
    private HashMap<String, Local> locales;
    private boolean todos;
    private List<Marker> marcadores;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        locales = new HashMap<String, Local>();
        todos = true;
        marcadores = new ArrayList<Marker>();
        signIn();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            lanzarSignIn();
        } else {
            iniciarVistaUsuario();
            iniciarBD();
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out
                    lanzarSignIn();
                }
                // ...
            }
        };
    }

    private void signIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.server_client_id))
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .requestScopes(new Scope(Scopes.PLUS_ME))
                .requestProfile()
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                // .addApi(Plus.API, null)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                // .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

        lanzarSignIn();


    }
    private void lanzarSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void iniciarVistaUsuario() {
        View header = navigationView.getHeaderView(0);
        TextView nombre = (TextView) header.findViewById(R.id.nombreUsuario);
        TextView email = (TextView) header.findViewById(R.id.emailUsuario);
        nombre.setText(mAuth.getCurrentUser().getDisplayName());
        email.setText(mAuth.getCurrentUser().getEmail());
        ImageView imagen = (ImageView) header.findViewById(R.id.imagenUsuario);
        Uri aux = mAuth.getCurrentUser().getPhotoUrl();
        if (aux != null) {

            Bitmap bitmap = null;
            try {
                bitmap = new Taska().execute(aux.toString()).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            imagen.setImageBitmap(bitmap);
        }
    }

    private void iniciarBD() {
        bd = FirebaseDatabase.getInstance();
        refGithub = bd.getReference("GitHub");
        refGithub.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                github = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
        refPlay = bd.getReference("PlayStore");
        refPlay.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                play = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        refLocales = bd.getReference("Locales");
        refLocales.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                locales.put(dataSnapshot.getKey(), dataSnapshot.getValue(Local.class));
                Local aux = dataSnapshot.getValue(Local.class);
                Marker marcador = mapa.addMarker(new MarkerOptions()
                        .position(new LatLng(aux.getLatitud(), aux.getLongitud()))
                        .title(aux.getNombre())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).snippet(aux.getHoraApertura()+"-"+aux.getHoraCierre()));
                marcador.setTag(dataSnapshot.getKey());
                marcadores.add(marcador);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                locales.remove(dataSnapshot.getKey());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add) {
            Intent i = new Intent(this, ActivityAdd.class);
            startActivityForResult(i, RC_ADD);
        } else if (id == R.id.locate) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                checkLocation();
                mapa.setMyLocationEnabled(true);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            todos = true;
            mostrarLocales();
        } else if (id == R.id.nav_gallery2) {
            todos = false;
            mostrarLocales();
        } else if (id == R.id.nav_share) {
            Intent i = new Intent(this, WebViewActivity.class);
            i.putExtra("github", github);
            i.putExtra("play", play);
            startActivity(i);
        } else if (id == R.id.nav_send) {
            Intent i = new Intent(this, LocalActivity.class);
            Local aux = locales.get(marcadores.get(0).getTag());
            i.putExtra("local", (Serializable) aux);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);


            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.

                firebaseAuthWithGoogle(result.getSignInAccount());

            } else {
                // Signed out, show unauthenticated UI.

                View header = navigationView.getHeaderView(0);
                TextView nombre = (TextView) header.findViewById(R.id.nombreUsuario);
                nombre.setText(result.getStatus().toString());
            }


        } else if (requestCode == RC_ADD) Toast.makeText(getApplicationContext(), "Muchas gracias por añadir tu localización. Esta aplicación funciona gracias a ti :)", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (permissions.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                checkLocation();
                mapa.setMyLocationEnabled(true);
            } else {

            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {


        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            iniciarVistaUsuario();
                            iniciarBD();

                        } else {
                            // If sign in fails, display a message to the user.


                        }

                        // ...
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapa = googleMap;




    }

    private boolean checkLocation() {
        if (!isLocationEnabled())
            showAlert();
        return isLocationEnabled();
    }

    private void showAlert() {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Ubicación desactivada")
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

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(listener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (listener != null) {
            mAuth.removeAuthStateListener(listener);
        }
    }
    private void mostrarLocales() {

        if (todos) {
            for (int i = 0; i < marcadores.size(); i++) {
                marcadores.get(i).setVisible(true);
            }
        } else {
            Calendar c = Calendar.getInstance();
            Date fecha = c.getTime();
            Date apertura = null, cierre = null;
            for (int i = 0; i < marcadores.size(); i++) {
                Local aux = locales.get(marcadores.get(i).getTag());
                c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(aux.getHoraApertura().split(":")[0]));
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                apertura = c.getTime();
                int j = Integer.parseInt(aux.getHoraCierre().split(":")[0]);
                if (j < Integer.parseInt(aux.getHoraApertura().split(":")[0])) {
                    c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH)+1);
                }
                c.set(Calendar.HOUR_OF_DAY, j);
                c.set(Calendar.MINUTE, 0);
                c.set(Calendar.SECOND, 0);
                cierre = c.getTime();
                if (j < Integer.parseInt(aux.getHoraApertura().split(":")[0])) {
                    c.set(Calendar.DAY_OF_MONTH,c.get(Calendar.DAY_OF_MONTH)-1);
                }

                if (fecha.after(apertura) && fecha.before(cierre)) marcadores.get(i).setVisible(true);
                else marcadores.get(i).setVisible(false);
            }
        }
    }
}
