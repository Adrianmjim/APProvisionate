package pad.ucm.approvisionate;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;

public class ActivityAdd extends AppCompatActivity {
    private Button foto;
    private TextView texto;
    private Uri mImageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        foto = (Button) findViewById(R.id.buttonCamara);
        texto = (TextView) findViewById(R.id.nombreLocal);
        Spinner pruaba;

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
                    texto.setText(tempDir.getAbsolutePath());
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
    }
}
