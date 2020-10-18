package com.example.testapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testapp.io.AdaptesSingletonRetro;
import com.example.testapp.io.POSTService;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.widget.Toast.*;



public class MainActivity extends AppCompatActivity {
    private final int REQUEST_CODE_ASK_PERMISSIONS = 123;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String currentPhotoPath;
    static Uri capturedImageUri=null;
    String fileName = "profilePic";
    String path = Environment.getExternalStorageDirectory()+ "/example";
    private POSTService mAPIService;
    String TAGcamera="cameratag";
    TextView textView;
    Button front_btn,senddata;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //para retrofit
        mAPIService = AdaptesSingletonRetro.getApiService();
        textView=(TextView) findViewById(R.id.textochido);
        front_btn=(Button)findViewById(R.id.button_chido);
        senddata=(Button)findViewById(R.id.senddata);

        mImageView = findViewById(R.id.imageView);

        //checar si tiene camara
        if( checkCameraHardware(this))
        {
            check_camera_permission();
            Toast.makeText(this,"tienes camara", LENGTH_SHORT).show();
        }else
            Toast.makeText(this,"No tiene camara", LENGTH_SHORT).show();

        front_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              makePhoto();
            }
        });

        senddata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              postpregunta();
            }
        });


        textView.setText(path);

    }



    private void makePhoto() {
        try {
            File f = createImageFile();
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          //  capturedImageUri = Uri.fromFile(f);
            capturedImageUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.file.provider", f);
           // FileProvider.getUriForFile(getApplicationContext(), getPackageName()+".fileprovider", f);
            //We have to check if it's nougat or not.
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            {
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(),"com.example.file.provider", f);
                Toast.makeText(getApplicationContext(),""+path, LENGTH_SHORT).show();
                i.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            }
            else
            {
                i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

            }
            i.putExtra(MediaStore.EXTRA_OUTPUT, capturedImageUri);
            startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
        } catch (IOException e) {
            Log.e(TAGcamera, "IO error", e);
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "foto" ;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode != RESULT_CANCELED){
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap myBitmap = BitmapFactory.decodeFile(currentPhotoPath);
              textView.setText(currentPhotoPath);
                mImageView.setImageBitmap(myBitmap);
                postimagenes();
            }
        }
    }

    //nojalo
    public void passdatatoapp()
    {
        Intent intent = new Intent("com.example.mcoder.datosbanco");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("data","data string");
        startActivity(intent);
    }


    public void check_camera_permission()
    {

        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1) {// Marshmallow+
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No se necesita dar una explicación al usuario, sólo pedimos el permiso.
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS );
                    // MY_PERMISSIONS_REQUEST_CAMARA es una constante definida en la app. El método callback obtiene el resultado de la petición.
                }
            }else{ //have permissions
                Toast.makeText(this,"tienes permiso", LENGTH_SHORT).show();
            }
        }else{ // Pre-Marshmallow
            Toast.makeText(this,"tienes tinees permiso ", LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS : {
                // Si la petición es cancelada, el array resultante estará vacío.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // El permiso ha sido concedido.
                   // abrirCamara ();
                } else {
                    // Permiso denegado, deshabilita la funcionalidad que depende de este permiso.
                }
                return;
            }
            // otros bloques de 'case' para controlar otros permisos de la aplicación
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }



    String respuesta="09";

    //post_imagenes
    private void postimagenes() {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        File images= new File(currentPhotoPath);

            RequestBody fbody = RequestBody.create(MediaType.parse("image/*"),
                    images);


        RequestBody name = RequestBody.create(MediaType.parse("text/plain"),
                "Connie");


        AdaptesSingletonRetro singletonRetro = new AdaptesSingletonRetro();

        Call<String> call = mAPIService.imagen_chida(fbody,name);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.d(TAGcamera, "" + response.code());
                if (response.isSuccessful()) {
                    String urJson = response.body();
                 textView.setText(urJson);
                 respuesta=urJson;

                } else {
                    Toast.makeText(MainActivity.this, "No hay conexion a server", Toast.LENGTH_SHORT);

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                //error de jasson //manejarlo con toast de no conexion con ser
                Toast.makeText(MainActivity.this, "No hay conexion a server", Toast.LENGTH_SHORT);

            }
        });


    }

    //post_imagenes
    private void postpregunta() {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);


        RequestBody auth = RequestBody.create(MediaType.parse("text/plain"),respuesta
                );

        RequestBody name = RequestBody.create(MediaType.parse("text/plain"),
                "Connie");


        AdaptesSingletonRetro singletonRetro = new AdaptesSingletonRetro();

        Call<String> call = mAPIService.ch2(auth,name);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.d(TAGcamera, "" + response.code());
                if (response.isSuccessful()) {
                    String urJson = response.body();
                    textView.setText(urJson);

                } else {
                    Toast.makeText(MainActivity.this, "No hay conexion a server", Toast.LENGTH_SHORT);

                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                //error de jasson //manejarlo con toast de no conexion con ser
                Toast.makeText(MainActivity.this, "No hay conexion a server", Toast.LENGTH_SHORT);

            }
        });


    }
}