package com.example.ricardo.planetariopix;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

public class GaleriaActivity extends AppCompatActivity {

    private ImageButton imb1;
    private ImageButton imb2;
    private ImageButton imb3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        ImageButton imb1 = (ImageButton) findViewById(R.id.imb1);
        ImageButton imb2 = (ImageButton) findViewById(R.id.imb2);
        ImageButton imb3 = (ImageButton) findViewById(R.id.imb3);

        imb1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                retorno(1);
            }
        });

        imb2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //chamar galeria
                Intent returnIntent = new Intent();
                returnIntent.putExtra("IMAGEM",2);
                setResult(RESULT_OK,returnIntent);
            }
        });

        imb3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //chamar galeria
                Intent returnIntent = new Intent();
                returnIntent.putExtra("IMAGEM",3);
                setResult(RESULT_OK,returnIntent);
            }
        });

    }

    void retorno(int imagem){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("IMAGEM",imagem);
        setResult(RESULT_OK,returnIntent);
    }
}
