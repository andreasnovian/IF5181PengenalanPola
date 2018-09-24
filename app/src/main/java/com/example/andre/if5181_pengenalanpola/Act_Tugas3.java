package com.example.andre.if5181_pengenalanpola;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

public class Act_Tugas3 extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected Bitmap bitmap, grayScaleBitmap;
    private ImageView iv;
    private int w,h;
    private int[] redCount, greenCount, blueCount, grayCount;
    private SeekBar sbGrayscale, sbRGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas3);
        this.sbGrayscale = this.findViewById(R.id.seekbarGrayscale);
        this.sbGrayscale.setOnSeekBarChangeListener(this);
        this.sbRGB = this.findViewById(R.id.seekbarRGB);
        this.sbRGB.setOnSeekBarChangeListener(this);
        this.dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            this.bitmap = imageBitmap;

            this.w = this.bitmap.getWidth();
            this.h = this.bitmap.getHeight();

            countRGB();
            makeGrayscaleImageOri();
            makeGrayscaleImageEq(0.5);
            makeRGBImageOri();
            makeRGBImageEq(0.5);
            makeImageSmoothing();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void makeImageSmoothing() {
        this.iv = this.findViewById(R.id.view_image_smoothing_ori);
        this.iv.setImageBitmap(this.bitmap);

        Bitmap tempBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
        int red=0, green=0, blue=0, a;

        for (int i=1;i<this.w-1;i++){
            for (int j=1;j<this.h-1;j++){
                a = this.bitmap.getPixel(i,j);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i-1,j-1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i,j-1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i-1,j);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i+1,j+1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i+1,j);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i,j+1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i+1,j-1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                a = this.bitmap.getPixel(i-1,j+1);
                red += Color.red(a);
                green += Color.green(a);
                blue += Color.blue(a);

                red /= 9;
                green /= 9;
                blue /= 9;
                int rgb = Color.rgb(red, green, blue);
                tempBitmap.setPixel(i, j, rgb);
                red = 0; green = 0; blue = 0;
            }
        }

        this.iv = this.findViewById(R.id.view_image_smoothing_transform);
        this.iv.setImageBitmap(tempBitmap);
    }

    private void makeRGBImageOri() {
        this.iv = this.findViewById(R.id.view_image_RGB_ori);
        this.iv.setImageBitmap(this.bitmap);
    }

    private void makeRGBImageEq(double w) {
        Bitmap tempBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
        int[] tempRed = this.redCount.clone();
        int[] tempGreen = this.greenCount.clone();
        int[] tempBlue = this.blueCount.clone();
        int mass = this.w*this.h;
        int r,g,b;
        long sumRed = 0, sumGreen = 0, sumBlue = 0;

        float scale = (float) 255.0/mass;
        for (int i = 0; i < tempRed.length; i++) {
            sumRed += tempRed[i];
            sumGreen += tempGreen[i];
            sumBlue += tempBlue[i];
            int valueRed = (int) (scale * (sumRed*(w)));
            int valueGreen = (int) (scale * (sumGreen*(w)));
            int valueBlue = (int) (scale * (sumBlue*(w)));
            if (valueRed > 255) {
                valueRed = 255;
            }
            if (valueGreen > 255) {
                valueGreen = 255;
            }
            if (valueBlue > 255) {
                valueBlue = 255;
            }
            tempRed[i] = valueRed;
            tempGreen[i] = valueGreen;
            tempBlue[i] = valueBlue;
        }

        for (int i=0;i<this.w;i++){
            for (int j=0;j<this.h;j++){
                int pixel = this.bitmap.getPixel(i, j);
                r = tempRed[Color.red(pixel)];
                g = tempGreen[Color.green(pixel)];
                b = tempBlue[Color.blue(pixel)];
                int rgb = Color.rgb(r, g, b);
                tempBitmap.setPixel(i, j, rgb);
            }
        }

        this.iv = this.findViewById(R.id.view_image_RGB_transform);
        this.iv.setImageBitmap(tempBitmap);
    }

    private void makeGrayscaleImageOri() {
        int pixel, red, green, blue, gray;
        Bitmap tempBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        for (int i=0;i<w;i++){
            for (int j=0;j<h;j++){
                pixel = this.bitmap.getPixel(i,j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                gray = (red+green+blue)/3;
                tempBitmap.setPixel(i,j,Color.rgb(gray,gray,gray));
            }
        }

        this.grayScaleBitmap = tempBitmap;

        this.iv = this.findViewById(R.id.view_image_grayscale_ori);
        this.iv.setImageBitmap(this.grayScaleBitmap);
    }

    private void makeGrayscaleImageEq(double w) {
        Bitmap tempBitmap = Bitmap.createBitmap(this.w, this.h, Bitmap.Config.ARGB_8888);
        int[] tempInt = this.grayCount.clone();
        int mass = this.w*this.h;
        int k;
        long sum = 0;

        float scale = (float) 255.0/mass;
        for (int i = 0; i < tempInt.length; i++) {
            sum += tempInt[i];
            int value = (int) (scale * (sum*(w)));
            if (value > 255) {
                value = 255;
            }
            tempInt[i] = value;
        }

        for (int i=0;i<this.w;i++){
            for (int j=0;j<this.h;j++){
                int pixel = this.grayScaleBitmap.getPixel(i, j);
                k = tempInt[Color.red(pixel)];
                int rgb = Color.rgb(k, k, k);
                tempBitmap.setPixel(i, j, rgb);
            }
        }

        this.iv = this.findViewById(R.id.view_image_grayscale_transform);
        this.iv.setImageBitmap(tempBitmap);
    }

    protected void countRGB(){
        this.redCount = new int[256];
        this.greenCount = new int[256];
        this.blueCount = new int[256];
        this.grayCount = new int[256];
        int pixel, red, green, blue, gray;

        for (int i=0;i<w;i++){
            for (int j=0;j<h;j++){
                pixel = bitmap.getPixel(i,j);
                red = Color.red(pixel);
                green = Color.green(pixel);
                blue = Color.blue(pixel);
                gray = (red+green+blue)/3;

                redCount[red]++;
                greenCount[green]++;
                blueCount[blue]++;
                grayCount[gray]++;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbGrayscale){
            this.makeGrayscaleImageEq((double)seekBar.getProgress()/100);
        }
        if (seekBar == sbRGB){
            this.makeRGBImageEq((double)seekBar.getProgress()/100);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
