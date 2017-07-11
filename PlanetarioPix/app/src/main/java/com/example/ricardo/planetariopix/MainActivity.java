package com.example.ricardo.planetariopix;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //variavel da intent da foto
    static final int GALERIA_IMAGEM = 2; //variavel da intent da Galeria
    static final int ACTIVITY_2 = 3; //Retorno da segunda activity

    private ImageView imViewFoto;
    private Button btTirarFoto;
    private Button btGaleria;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--começo codigo
        imViewFoto = (ImageView) findViewById(R.id.imViewFoto);
        btTirarFoto = (Button) findViewById(R.id.btTirarFoto);
        btGaleria = (Button) findViewById(R.id.btGaleria);

        btTirarFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btGaleria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //chamar galeria
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALERIA_IMAGEM);

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
        } else if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGEM) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
            //usar aqui a função de processsamento de imagem

            //imViewFoto.setImageBitmap(thumbnail);

            thumbnail = colorToAlpha(thumbnail);
            //imViewFoto.setImageBitmap(thumbnail);
            Bitmap fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img2);

            //Intent it = new Intent(this, GaleriaActivity.class);
            //startActivityForResult(it, ACTIVITY_2);

            Bitmap pronto = overlay(fundo, thumbnail);
            imViewFoto.setImageBitmap(pronto);

        } else if(resultCode == RESULT_OK && requestCode == ACTIVITY_2){
            String imagem = data.getStringExtra("IMAGEM");
            if(imagem == "1"){
                Bitmap fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img1);
                imViewFoto.setImageBitmap(fundo);
            }
            if(imagem == "2"){
                Bitmap fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img2);
                imViewFoto.setImageBitmap(fundo);
            }
            if(imagem == "3"){
                Bitmap fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img3);
                imViewFoto.setImageBitmap(fundo);
            }
        }
    }



    public static Bitmap removeFundoVerde(Bitmap srcBitmap){
        Bitmap result = srcBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int nWidth = result.getWidth();
        int nHeight = result.getHeight();

        for (int y = 0; y < nHeight; ++y) {
            for (int x = 0; x < nWidth; ++x) {
                int nPixelColor = result.getPixel(x, y);
                float hsv[] = new float[3];

                Color.colorToHSV(nPixelColor, hsv);
                if(hsv[0] >= 60 && hsv[0] <= 130 && hsv[1] >= 0.4 && hsv[2] >= 0.3){
                    result.setPixel(x, y, 0);
                    //result.eraseColor(Color.TRANSPARENT);
                }

                //=====================================
                //FUNÇÂO QUE ARRUMA AS BORDAS DA IMAGEM
                //=====================================
                //if(hsv[0] >= 60 && hsv[0] <= 130 && hsv[1] >= 0.15 && hsv[2] > 0.15) {
                  //  if ((r * b) != 0 && (g * g) / (r * b) >= 1.5) {
                   //     result.setIntColor(x, y, 255, (int) (r * 1.4), (int) g, (int) (b * 1.4));
                    //} else {
                     //   image.setIntColor(x, y, 255, (int) (r * 1.2), g, (int) (b * 1.2));
                    //}
            }
        }

        Bitmap paramBitmap = Bitmap.createBitmap(result.getWidth(), result.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas localObject = new Canvas(paramBitmap);
        localObject.drawARGB(0, 0, 0, 0);
        Paint localPaint = new Paint();
        localPaint.setAlpha(255);
        localObject.drawBitmap(result, 0.0F, 0.0F, localPaint);
        //result.eraseColor(Color.TRANSPARENT);
        return result;

        //return imageWithBG;
    }



    public static Bitmap overlay(Bitmap paramBitmap1, Bitmap paramBitmap2)
    {
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap1.getWidth(), paramBitmap1.getHeight(), paramBitmap1.getConfig());
        Canvas localCanvas = new Canvas(localBitmap);
        localCanvas.drawBitmap(paramBitmap1, 0.0F, 0.0F, null);
        localCanvas.drawBitmap(paramBitmap2, 10.0F, 10.0F, null);
        return localBitmap;
    }

    public static Bitmap colorToAlpha(Bitmap paramBitmap)
    {
        Bitmap localBitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int k = localBitmap.getWidth();
        int m = localBitmap.getHeight();
        int i = 0;
        while (i < m)
        {
            int j = 0;
            while (j < k)
            {
                int n = paramBitmap.getPixel(j, i);
                float localObject[] = new float[3];
                Color.colorToHSV(n, (float[])localObject);
                if ((localObject[0] >= 60.0F) && (localObject[0] <= 130.0F) && (localObject[1] >= 0.15D) && (localObject[2] >= 0.15D)) {
                    localBitmap.setPixel(j, i, 0);
                }
                j += 1;
            }
            i += 1;
        }
        paramBitmap = Bitmap.createBitmap(localBitmap.getWidth(), localBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Object localObject = new Canvas(paramBitmap);
        ((Canvas)localObject).drawARGB(0, 0, 0, 0);
        Paint localPaint = new Paint();
        localPaint.setAlpha(255);
        ((Canvas)localObject).drawBitmap(localBitmap, 0.0F, 0.0F, localPaint);
        return paramBitmap;
    }







}
