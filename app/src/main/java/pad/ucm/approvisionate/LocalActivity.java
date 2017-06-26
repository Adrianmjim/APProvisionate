package pad.ucm.approvisionate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import pad.ucm.approvisionate.modelo.Local;

public class LocalActivity extends AppCompatActivity {
    private ImageView image_scrolling_top;
    private byte[] bmp;
    private TextView texto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        image_scrolling_top = (ImageView) findViewById(R.id.image_scrolling_top);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Local aux = (Local) getIntent().getExtras().getSerializable("local");
        getSupportActionBar().setTitle(aux.getNombre());
        String ruta = "images/"+aux.getFoto()+".jpg";
        FirebaseStorage serv = FirebaseStorage.getInstance();
        StorageReference ref = serv.getReferenceFromUrl("gs://approvisionate.appspot.com/");
        ref = ref.child(ruta);
        ref.getBytes(1920*1080).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                bmp = bytes;
                cambiarImagen();
            }
        });
        texto = (TextView) findViewById(R.id.textoLocal);
        texto.setText("HOLA");
    }
    private void cambiarImagen() {
        Glide.with(this).load(bmp).fitCenter().into(image_scrolling_top);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

}
