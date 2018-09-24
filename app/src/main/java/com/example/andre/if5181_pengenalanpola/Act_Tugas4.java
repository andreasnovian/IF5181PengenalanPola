package com.example.andre.if5181_pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class Act_Tugas4 extends AppCompatActivity {
    private ImageView numberIV;
    protected Bitmap numberBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas4);

        this.numberIV = this.findViewById(R.id.view_image_number_detect);
        this.numberIV.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) this.numberIV.getDrawable();
        this.numberBitmap = drawable.getBitmap();
        detectEdge(this.numberIV, this.numberBitmap);
    }

    private void detectEdge(ImageView a, Bitmap b) {
        int width = b.getWidth();
        int height = b.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int i=1;i<width-1;i++){
            for (int j=1;j<height-1;j++){
                if (Math.abs(b.getPixel(i,j)-b.getPixel(i+1,j))>=2000000){
                    result.setPixel(i,j, Color.RED);
                } else if (Math.abs(b.getPixel(i,j)-b.getPixel(i+1,j+1))>=2000000){
                    result.setPixel(i,j, Color.RED);
                } else if (Math.abs(b.getPixel(i,j)-b.getPixel(i,j+1))>=2000000){
                    result.setPixel(i,j, Color.RED);
                }
            }
        }

        a.setImageBitmap(result);
    }
}
