package com.example.ricardo.planetariopix;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
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
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1; //variavel da intent da foto
    static final int GALERIA_IMAGEM = 2; //variavel da intent da Galeria
    static final int ACTIVITY_2 = 3; //Retorno da segunda activity

    private SeekBar brilho;
    private ImageView imViewFoto;
    private Button btTirarFoto;
    private Button btGaleria;
    private TextView brilhoView;
    private ProgressBar carregando;

    Uri URI;

    public double brilhoHsv;

    public Bitmap original;
    public Bitmap semFundo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //--começo codigo
        imViewFoto = (ImageView) findViewById(R.id.imViewFoto);
        btTirarFoto = (Button) findViewById(R.id.btTirarFoto);
        btGaleria = (Button) findViewById(R.id.btGaleria);
        brilho = (SeekBar) findViewById(R.id.seekBrilho);
        brilhoView = (TextView) findViewById(R.id.brilhoView);
        carregando = (ProgressBar) findViewById(R.id.carregando);

        brilhoHsv = 0.25D;

        btTirarFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btGaleria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chamaGaleriaFotos();
            }
        });

        brilho.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 5;

            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                //Toast.makeText(getApplicationContext(), "Changing seekbar's progress", Toast.LENGTH_SHORT).show();
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                //Toast.makeText(getApplicationContext(), "Started tracking seekbar", Toast.LENGTH_SHORT).show();
            }


            public void onStopTrackingTouch(SeekBar seekBar) {
                brilhoHsv = progress*0.05D;
                if (brilhoHsv < 0.05D)
                    brilhoHsv = 0.05D;
                brilhoView.setText(""+brilhoHsv);
                //Toast.makeText(getApplicationContext(), "Stopped tracking seekbar", Toast.LENGTH_SHORT).show();
            }

        });

    }

    //método que chama a camera
    private void dispatchTakePictureIntent() {
        //Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
        //    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        //}
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    public void chamaGaleriaFotos(){
        //chamar galeria
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALERIA_IMAGEM);
    }

    //retorno da foto
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            //Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //Get our saved file into a bitmap object:
            File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
            //Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1080, 760);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

            //----------
            //salvarImagem(bitmap);
            //-------

            //bitmap = Bitmap.createScaledBitmap(bitmap, 2070, 1165, false);
            //semFundo = colorToAlpha(bitmap);
            //Intent it = new Intent(this, Galeria.class);
            //startActivityForResult(it, ACTIVITY_2);
            clearImage(bitmap, brilhoHsv);

        } else if (resultCode == RESULT_OK && requestCode == GALERIA_IMAGEM) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
            //----------
            //salvarImagem(thumbnail);
            //-------

            //thumbnail = Bitmap.createScaledBitmap(thumbnail, 2070, 1165, false);
            //semFundo = colorToAlpha(thumbnail);
            //Intent it = new Intent(this, Galeria.class);
            //startActivityForResult(it, ACTIVITY_2);
            clearImage(bitmap, brilhoHsv);


        } else if (resultCode == RESULT_OK && requestCode == ACTIVITY_2) {//retorno da escolha do fundo
            String imagem = data.getStringExtra("IMAGEM");
            Bitmap fundo;
            if (imagem.equals("1")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img1);
                finalizeImage(fundo);
            }
            if (imagem.equals("2")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img2);
                finalizeImage(fundo);
            }
            if (imagem.equals("3")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img3);
                finalizeImage(fundo);
            }
            if (imagem.equals("4")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img4);
                finalizeImage(fundo);
            }
            if (imagem.equals("5")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img5);
                finalizeImage(fundo);
            }
            if (imagem.equals("6")) {
                fundo = new BitmapFactory().decodeResource(getResources(), R.drawable.img6);
                finalizeImage(fundo);
            }
        }
    }


    public static Bitmap overlay(Bitmap paramBitmap1, Bitmap paramBitmap2) {
        Bitmap localBitmap = Bitmap.createBitmap(paramBitmap1.getWidth(), paramBitmap1.getHeight(), paramBitmap1.getConfig());
        Canvas localCanvas = new Canvas(localBitmap);
        localCanvas.drawBitmap(paramBitmap1, 0.0F, 0.0F, null);
        localCanvas.drawBitmap(paramBitmap2, 0.0F, 0.0F, null);
        return localBitmap;
    }

    public static Bitmap colorToAlpha(Bitmap paramBitmap, double brilhoHsv) {
        Bitmap localBitmap = paramBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int k = localBitmap.getWidth();
        int m = localBitmap.getHeight();
        int r,g,b;
        int i = 0;
        while (i < m) {
            int j = 0;
            while (j < k) {
                int n = paramBitmap.getPixel(j, i);
                r = Color.red(n);
                g = Color.green(n);
                b = Color.blue(n);
                float localObject[] = new float[3];
                Color.colorToHSV(n, (float[]) localObject);//60n130n15n15
                if ((localObject[0] >= 60.0F) && (localObject[0] <= 130.0F) && (localObject[1] >= brilhoHsv) && (localObject[2] >= brilhoHsv)) {
                    localBitmap.setPixel(j, i, 0);
                }
                j += 1;
            }
            i += 1;
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

        paramBitmap = Bitmap.createBitmap(localBitmap.getWidth(), localBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Object localObject = new Canvas(paramBitmap);
        ((Canvas) localObject).drawARGB(0, 0, 0, 0);
        Paint localPaint = new Paint();
        localPaint.setAlpha(255);
        ((Canvas) localObject).drawBitmap(localBitmap, 0.0F, 0.0F, localPaint);
        return paramBitmap;
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    void salvarImagem(Bitmap pronto){
        String imgSaved = MediaStore.Images.Media.insertImage(getContentResolver(),pronto,"teste.png", "drawing");
        addImageToGallery(imgSaved, this);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight){
        // BEST QUALITY MATCH

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight)
        {
            inSampleSize = Math.round((float)height / (float)reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public void clearImage(Bitmap bitmap, double brilhoHsv) throws RuntimeException{
        carregando.setVisibility(View.VISIBLE);
        try {
            original = Bitmap.createScaledBitmap(bitmap, 2070, 1165, false);
            semFundo = colorToAlpha(original, brilhoHsv);
            carregando.setVisibility(View.INVISIBLE);
            Intent it = new Intent(this, Galeria.class);
            startActivityForResult(it, ACTIVITY_2);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"ERRO AO LIMPAR IMAGEM", Toast.LENGTH_SHORT).show();
            carregando.setVisibility(View.INVISIBLE);
            chamaGaleriaFotos();
        }
    }

    public void finalizeImage(Bitmap bitmap){
            Bitmap pronto = overlay(bitmap, semFundo);
            imViewFoto.setImageBitmap(pronto);
            salvarImagem(pronto);
    }




}

//Toast.makeText(getApplicationContext(),"ERRO AO FINALIZAR IMAGEM", Toast.LENGTH_SHORT).show();