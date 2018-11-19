package com.example.andre.if5181_pengenalanpola;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.OutputStreamWriter;

public class Act_Tugas9 extends AppCompatActivity {

    private ImageView imageView, imageViewSobel, imageViewRobinson1, imageViewRobinson2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas9);

		imageView = findViewById(R.id.imageView);
        imageViewSobel = findViewById(R.id.imageViewSobel);
		imageViewRobinson1 = findViewById(R.id.imageViewRobinson1);
		imageViewRobinson2 = findViewById(R.id.imageViewRobinson2);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK && data != null) {
                if (requestCode == 1 && data.getData() != null) {
                    Cursor cursor = getContentResolver().query(data.getData(), new String[]{MediaStore.Images.Media.DATA}, null, null, null);

                    if (cursor == null)
                        return;

                    cursor.moveToFirst();
                    String imageString = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    cursor.close();

                    Bitmap image = BitmapFactory.decodeFile(imageString);

                    imageView.setImageBitmap(image);
                } else if (requestCode == 2 && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(image);
                } else if (requestCode == 3) {
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput("configmenori.txt", MODE_PRIVATE));
                    outputStreamWriter.write("test");
                    outputStreamWriter.close();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, String.format("Error : %s", e.getMessage()), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void loadImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    public void openCamera(View view) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 2);
    }

    public void process(View view) {
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap bmp = this.doSobelOperator(bitmap);
        imageViewSobel.setImageBitmap(bmp);
		
		Bitmap[] bitmapResults = this.doRobinsonCompass(bitmap);
        imageViewRobinson1.setImageBitmap(bitmapResults[6]);
        imageViewRobinson2.setImageBitmap(bitmapResults[2]);
		
	}
	
	public Bitmap doSobelOperator(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        int[][] p = new int[8][4];
        int[][] redGradient = new int[width][height];
        int[][] greenGradient = new int[width][height];
        int[][] blueGradient = new int[width][height];
        int gRed, gGreen, gBlue, count = 0;
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                for(int k = 0; k <points.length; k++){
                    p[k] = getPixelColor(bitmap, i + points[k][0], j + points[k][1]);
                }
                gRed = Math.abs((p[7][0] + (2*p[0][0]) + p[1][0]) - (p[5][0] + (2*p[4][0]) + p[3][0]))
                        + Math.abs((p[1][0] + (2*p[2][0]) + p[3][0]) - (p[7][0] + (2*p[6][0]) + p[5][0]));
                gGreen = Math.abs((p[7][1] + (2*p[0][1]) + p[1][1]) - (p[5][1] + (2*p[4][1]) + p[3][1]))
                        + Math.abs((p[1][1] + (2*p[2][1]) + p[3][1]) - (p[7][1] + (2*p[6][1]) + p[5][1]));
                gBlue = Math.abs((p[7][2] + (2*p[0][2]) + p[1][2]) - (p[5][2] + (2*p[4][2]) + p[3][2]))
                        + Math.abs((p[1][2] + (2*p[2][2]) + p[3][2]) - (p[7][2] + (2*p[6][2]) + p[5][2]));
                redGradient[i][j] = gRed;
                greenGradient[i][j] = gGreen;
                blueGradient[i][j] = gBlue;
            }
        }
        return getSobelBitmap(redGradient, greenGradient, blueGradient);
    }

    public Bitmap getSobelBitmap(int[][] red, int[][] green, int[][] blue){
        Bitmap bitmap = Bitmap.createBitmap(red.length, red[0].length, Bitmap.Config.ARGB_8888);
        for(int i = 0 ; i < red.length ; i++){
            for(int j = 0 ; j < red[0].length ; j++){
                bitmap.setPixel(i, j, Color.rgb(red[i][j], green[i][j], blue[i][j]));
            }
        }
        return bitmap;
    }

    public Bitmap[] doRobinsonCompass(Bitmap bitmap){
        Bitmap[] bitmapResults= new Bitmap[8];
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};
        int[][] p = new int[8][4];
        int[][][] redGradient = new int[8][width][height];
        int[][][] greenGradient = new int[8][width][height];
        int[][][] blueGradient = new int[8][width][height];
        int gRed, gGreen, gBlue, count = 0;
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {
                for(int k = 0; k <points.length; k++){
                    p[k] = getPixelColor(bitmap, i + points[k][0], j + points[k][1]);
                }
                for(int a = 0; a < 8; a++){
                    gRed = Math.abs((p[(7+a)%8][0] + (2*p[(0+a)%8][0]) + p[(1+a)%8][0]) - (p[(5+a)%8][0] + (2*p[(4+a)%8][0]) + p[(3+a)%8][0]))
                            + Math.abs((p[(1+a)%8][0] + (2*p[(2+a)%8][0]) + p[(3+a)%8][0]) - (p[(7+a)%8][0] + (2*p[(6+a)%8][0]) + p[(5+a)%8][0]));
                    gGreen = Math.abs((p[(7+a)%8][1] + (2*p[(0+a)%8][1]) + p[(1+a)%8][1]) - (p[(5+a)%8][1] + (2*p[(4+a)%8][1]) + p[(3+a)%8][1]))
                            + Math.abs((p[(1+a)%8][1] + (2*p[(2+a)%8][1]) + p[(3+a)%8][1]) - (p[(7+a)%8][1] + (2*p[(6+a)%8][1]) + p[(5+a)%8][1]));
                    gBlue = Math.abs((p[(7+a)%8][2] + (2*p[(0+a)%8][2]) + p[(1+a)%8][2]) - (p[(5+a)%8][2] + (2*p[(4+a)%8][2]) + p[(3+a)%8][2]))
                            + Math.abs((p[(1+a)%8][2] + (2*p[(2+a)%8][2]) + p[(3+a)%8][2]) - (p[(7+a)%8][2] + (2*p[(6+a)%8][2]) + p[(5+a)%8][2]));
                    redGradient[a][i][j] = gRed;
                    greenGradient[a][i][j] = gGreen;
                    blueGradient[a][i][j] = gBlue;
                }

            }
        }
        for(int i = 0; i < 8; i++){
            bitmapResults[i] = getSobelBitmap(redGradient[i], greenGradient[i], blueGradient[i]);
        }
        return bitmapResults;
    }

    private int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }
}
