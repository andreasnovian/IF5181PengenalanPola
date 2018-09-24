package com.example.andre.if5181_pengenalanpola;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class Act_Tugas1 extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    protected Bitmap bitmap, grayScaleBitmap, numberBitmap;
    private int w,h;
    private int[] redCount, greenCount, blueCount, grayCount, grayKumulatif;
    private ImageView iv, numberIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas1);
        this.dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            this.bitmap = imageBitmap;

            this.iv = this.findViewById(R.id.view_image_ori);
            this.iv.setImageBitmap(imageBitmap);

            this.w = this.bitmap.getWidth();
            this.h = this.bitmap.getHeight();

            countRGB();
            makeHistogram();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void makeHistogram() {
        GraphView graphRed = findViewById(R.id.graphRed);
        GraphView graphGreen = findViewById(R.id.graphGreen);
        GraphView graphBlue = findViewById(R.id.graphBlue);
        GraphView graphGray = findViewById(R.id.graphGray);

        DataPoint[] redDP = new DataPoint[256];
        DataPoint[] greenDP = new DataPoint[256];
        DataPoint[] blueDP = new DataPoint[256];
        DataPoint[] grayDP = new DataPoint[256];

        int temp = 0;
        this.grayKumulatif = new int[256];

        for (int i=0;i<256;i++){
            redDP[i] = new DataPoint(i,this.redCount[i]);
            greenDP[i] = new DataPoint(i,this.greenCount[i]);
            blueDP[i] = new DataPoint(i,this.blueCount[i]);
            grayDP[i] = new DataPoint(i,this.grayCount[i]);
            temp+=this.redCount[i]+this.greenCount[i]+this.blueCount[i];
            grayKumulatif[i] = temp;
        }

        LineGraphSeries<DataPoint> seriesRed = new LineGraphSeries<>(redDP);
        LineGraphSeries<DataPoint> seriesGreen = new LineGraphSeries<>(greenDP);
        LineGraphSeries<DataPoint> seriesBlue = new LineGraphSeries<>(blueDP);
        LineGraphSeries<DataPoint> seriesGray = new LineGraphSeries<>(grayDP);

        seriesRed.setColor(Color.RED);
        seriesGreen.setColor(Color.GREEN);
        seriesBlue.setColor(Color.BLUE);

        double highestRed = seriesRed.getHighestValueY();
        double highestGreen = seriesGreen.getHighestValueY();
        double highestBlue = seriesBlue.getHighestValueY();
        double highestGray = seriesGray.getHighestValueY();

        graphRed.getViewport().setMaxX(256);
        graphRed.getViewport().setMaxY(highestRed);
        graphRed.getViewport().setXAxisBoundsManual(true);
        graphRed.getViewport().setYAxisBoundsManual(true);

        graphGreen.getViewport().setMaxX(256);
        graphGreen.getViewport().setMaxY(highestGreen);
        graphGreen.getViewport().setXAxisBoundsManual(true);
        graphGreen.getViewport().setYAxisBoundsManual(true);

        graphBlue.getViewport().setMaxX(256);
        graphBlue.getViewport().setMaxY(highestBlue);
        graphBlue.getViewport().setXAxisBoundsManual(true);
        graphBlue.getViewport().setYAxisBoundsManual(true);

        graphGray.getViewport().setMaxX(256);
        graphGray.getViewport().setMaxY(highestGray);
        graphGray.getViewport().setXAxisBoundsManual(true);
        graphGray.getViewport().setYAxisBoundsManual(true);

        graphRed.addSeries(seriesRed);
        graphGreen.addSeries(seriesGreen);
        graphBlue.addSeries(seriesBlue);
        graphGray.addSeries(seriesGray);
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

}
