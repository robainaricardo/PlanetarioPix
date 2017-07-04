package com.example.ricardo.planetariopix;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //variavel da intent da foto

    private ImageView imViewFoto;
    private Button btTirarFoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--começo codigo
        imViewFoto = (ImageView) findViewById(R.id.imViewFoto);
        btTirarFoto = (Button) findViewById(R.id.btTirarFoto);

        btTirarFoto.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                dispatchTakePictureIntent();
            }
        });
    }

    //método que chama a camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //retorno da foto
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            //usar aqui a função de processsamento de imagem
            imViewFoto.setImageBitmap(imageBitmap);
        }
    }
}
