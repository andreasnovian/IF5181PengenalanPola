package com.example.andre.if5181_pengenalanpola;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStreamWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Act_Tugas7 extends AppCompatActivity {

    private ImageView imageView;
    private TextView textViewFeature, textViewResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas7);

        imageView = findViewById(R.id.imageView);
        textViewFeature = findViewById(R.id.textViewFeature);
        textViewResult = findViewById(R.id.textViewResult);

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

                    imageView.setImageBitmap(getBinaryImage(image, 128));
                } else if (requestCode == 2 && data.getExtras().get("data") != null) {
                    Bitmap image = (Bitmap) data.getExtras().get("data");

                    imageView.setImageBitmap(getBinaryImage(image, 128));
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
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        textViewFeature.setText("Feature : " + getSkeletonFeature(image));
    }

    private static Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = width * height;
        int[] pixels = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        for (int i = 0; i < size; i++) {
            int pixel = pixels[i];
            int grayscale = (((pixel & 0x00ff0000) >> 16) + ((pixel & 0x0000ff00) >> 8) + (pixel & 0x000000ff)) / 3;

            if (grayscale < threshold) {
                pixels[i] = pixel & 0xff000000;
            } else {
                pixels[i] = pixel | 0x00ffffff;
            }
        }

        return Bitmap.createBitmap(pixels, bitmap.getWidth(), bitmap.getHeight(), bitmap.getConfig());
    }

    private String getSkeletonFeature(Bitmap bitmap) {
        int count;
        int[] border, border2;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        String feature = "";

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = floodFill(pixels, i, j, width);

                    do {
                        count = zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    border2 = getNewBorder(pixelsa, border[0], border[1], border[2], border[3], width);
                    HandSkeletonFeature sf = extractFeature(pixelsa, border2[0], border2[1], border2[2], border2[3], width);

                    feature += sf.endpoints.size() + "," + sf.endpointspos.toString() + "," +
                            sf.hTop + "," + sf.hMid + "," + sf.hBottom + "," +
                            sf.vLeft + "," + sf.vMid + "," + sf.vRight + "," +
                            sf.lTop + "," + sf.lMid + "," + sf.lBottom + "\r\n";
                }
            }
        }
        feature = feature.trim();

        String result = "";
        String[] features = feature.split("\r\n");
        for (int i = 0;i<features.length;i++){
            Log.d("feature",features[i]);
            result += HandSkeletonRecognizer.recognize(features[i]) + "\r\n";
        }

        result = result.trim();
        textViewResult.setText("Character : " + result);
        return feature;
    }

    private int zhangSuenStep(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
        int count = 0;

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x000000ff) == 0) {
                    int[] neighbours = {
                            pixels[i + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + j * width] & 0x000000ff,
                            pixels[(i + 1) + (j + 1) * width] & 0x000000ff,
                            pixels[i + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + j * width] & 0x000000ff,
                            pixels[(i - 1) + (j - 1) * width] & 0x000000ff
                    };
                    int[] function = zhangSuenAB(neighbours);

                    if (function[1] < 2 || 6 < function[1]) continue;
                    if (function[0] != 1) continue;
                    if (neighbours[0] != 255 && neighbours[2] != 255 && neighbours[4] != 255)
                        continue;
                    if (neighbours[2] != 255 && neighbours[4] != 255 && neighbours[6] != 255)
                        continue;

                    pixels[i + j * width] = pixels[i + j * width] | 0x0000ff00;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[i + j * width] = pixels[i + j * width] | 0x00ffffff;
                    count++;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x000000ff) == 0) {
                    int[] neighbours = {
                            pixels[i + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + (j - 1) * width] & 0x000000ff,
                            pixels[(i + 1) + j * width] & 0x000000ff,
                            pixels[(i + 1) + (j + 1) * width] & 0x000000ff,
                            pixels[i + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + (j + 1) * width] & 0x000000ff,
                            pixels[(i - 1) + j * width] & 0x000000ff,
                            pixels[(i - 1) + (j - 1) * width] & 0x000000ff
                    };
                    int[] function = zhangSuenAB(neighbours);

                    if (function[1] < 2 || 6 < function[1]) continue;
                    if (function[0] != 1) continue;
                    if (neighbours[0] != 255 && neighbours[2] != 255 && neighbours[6] != 255)
                        continue;
                    if (neighbours[0] != 255 && neighbours[4] != 255 && neighbours[6] != 255)
                        continue;

                    pixels[i + j * width] = pixels[i + j * width] | 0x0000ff00;
                }
            }
        }

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                if ((pixels[i + j * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[i + j * width] = pixels[i + j * width] | 0x00ffffff;
                    count++;
                }
            }
        }

        return count;
    }

    private int[] zhangSuenAB(int[] neighbours) {
        int countA = 0;
        int countB = 0;

        for (int i = 0; i < 8; i++) {
            if (neighbours[i] == 255 && neighbours[(i + 1) % 8] == 0) {
                countA++;
            }
            if (neighbours[i] == 0) {
                countB++;
            }
        }

        return new int[]{countA, countB};
    }

    private static int[] floodFill(int[] pixels, int x, int y, int width) {

        int xmax = x;
        int xmin = x;
        int ymax = y;
        int ymin = y;
        Queue<Integer> queueX = new LinkedList<>();
        Queue<Integer> queueY = new LinkedList<>();

        queueX.offer(x);
        queueY.offer(y);

        while (!queueX.isEmpty()) {
            x = queueX.poll();
            y = queueY.poll();

            int pixel = pixels[x + y * width] & 0x000000ff;

            if (pixel != 255) {
                pixels[x + y * width] = pixels[x + y * width] | 0x00ffffff;

                if (x < xmin) xmin = x;
                if (x > xmax) xmax = x;
                if (y < ymin) ymin = y;
                if (y > ymax) ymax = y;

                queueX.offer(x);
                queueY.offer(y + 1);
                queueX.offer(x);
                queueY.offer(y - 1);
                queueX.offer(x + 1);
                queueY.offer(y);
                queueX.offer(x - 1);
                queueY.offer(y);
            }
        }

        return new int[]{xmin, ymin, xmax, ymax};
    }

    public static int[] getNewBorder(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {

        int pxmin = (xmax + xmin) / 2;
        int pxmax = (xmax + xmin) / 2;
        int pymin = (ymax + ymin) / 2;
        int pymax = (ymax + ymin) / 2;

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) == 0x00000000) {
                    if (p % width < pxmin) pxmin = p % width;
                    if (p % width > pxmax) pxmax = p % width;
                    if (p / width < pymin) pymin = p / width;
                    if (p / width > pymax) pymax = p / width;
                }
            }
        }

        return new int[]{pxmin, pymin, pxmax, pymax};
    }

    public HandSkeletonFeature extractFeature(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {

        HandSkeletonFeature sf = new HandSkeletonFeature();

        // titik ujung
        List<Integer> endpoints = new ArrayList<>();
        int[] endpointspos = new int[9];

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) != 0x00ffffff) {
                    int[] neighbour = {
                            p - width,
                            p - width + 1,
                            p + 1,
                            p + width + 1,
                            p + width,
                            p + width - 1,
                            p - 1,
                            p - width - 1
                    };
                    int black = 0;
                    int index = -1;

                    for (int k = 0; k < neighbour.length; k++) {
                        if ((pixels[neighbour[k]] & 0x00ffffff) != 0x00ffffff) {
                            black++;
                            index = k;
                        }
                    }

                    if (black == 1) { //ketemu ujung
                        endpoints.add((index + 4) % 8);

                        //cari posisi
                        if (i<=xmax/3) {
                            if (j<=ymax/3) {
                                endpointspos[0]++;
                            } else if (j<=2*(ymax/3)){
                                endpointspos[3]++;
                            } else {
                                endpointspos[6]++;
                            }
                        } else if (i<=2*(xmax/3)){
                            if (j<=ymax/3) {
                                endpointspos[1]++;
                            } else if (j<=2*(ymax/3)){
                                endpointspos[4]++;
                            } else {
                                endpointspos[7]++;
                            }
                        } else {
                            if (j<=ymax/3) {
                                endpointspos[2]++;
                            } else if (j<=2*(ymax/3)){
                                endpointspos[5]++;
                            } else {
                                endpointspos[8]++;
                            }
                        }
                    }
                }
            }
        }

        String pos = "";
        for (int i=0;i<endpointspos.length;i++){
            pos += endpointspos[i];
        }

        sf.endpointspos = pos;
        sf.endpoints = endpoints;

        // garis tegak
        int[] h = new int[ymax - ymin + 1];
        int[] v = new int[xmax - xmin + 1];

        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) != 0x00ffffff) {
                    h[j - ymin]++;
                    v[i - xmin]++;
                }
            }
        }

        int[] hsum = new int[3];
        for (int i = 0; i < h.length; i++) {
            if (h[i] > (xmax - xmin + 1) / 2 && h[i] > 1) {
                if (i < (ymax - ymin) * 4 / 10) {
                    hsum[0]++;
                } else if (i < (ymax - ymin) * 6 / 10) {
                    hsum[1]++;
                } else {
                    hsum[2]++;
                }
            }
        }

        int[] vsum = new int[3];
        for (int i = 0; i < v.length; i++) {
            if (v[i] > (ymax - ymin + 1) / 2 && v[i] > 0) {
                if (i < (xmax - xmin) * 4 / 10) {
                    vsum[0]++;
                } else if (i < (xmax - xmin) * 6 / 10) {
                    vsum[1]++;
                } else {
                    vsum[2]++;
                }
            }
        }

        sf.hTop = hsum[0] > 0;
        sf.hMid = hsum[1] > 0;
        sf.hBottom = hsum[2] > 0;
        sf.vLeft = vsum[0] > 0;
        sf.vMid = vsum[1] > 0;
        sf.vRight = vsum[2] > 0;

        // lubang
        int[] hole = new int[3];
        for (int j = ymin; j <= ymax; j++) {
            for (int i = xmin; i <= xmax; i++) {
                int p = i + j * width;

                if ((pixels[p] & 0x00ffffff) == 0x00ffffff) {
                    int midpoint = holeFloodFill(pixels, xmin - 1, ymin - 1, xmax + 1, ymax + 1, width, p);

                    if (midpoint / width < (ymax - ymin + 2) * 4 / 10 + (ymin - 1)) {
                        hole[0]++;
                    } else if (midpoint / width < (ymax - ymin + 2) * 6 / 10 + (ymin - 1)) {
                        hole[1]++;
                    } else {
                        hole[2]++;
                    }
                }
            }
        }

        sf.lTop = hole[0] > 0;
        sf.lMid = hole[1] - 1 > 0;
        sf.lBottom = hole[2] > 0;

        return sf;
    }

    private int holeFloodFill(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width, int p) {

        int pxmin = p % width;
        int pxmax = p % width;
        int pymin = p / width;
        int pymax = p / width;
        Queue<Integer> queue = new ArrayDeque<>();

        queue.offer(p);

        while (!queue.isEmpty()) {
            int pt = queue.poll();

            if ((pixels[pt] & 0x00ffffff) == 0x00ffffff
                    && xmin <= (pt % width)
                    && (pt % width) <= xmax
                    && ymin <= (pt / width)
                    && (pt / width) <= ymax) {
                pixels[pt] = (pixels[pt] & 0xff000000);

                if (pt % width < pxmin) pxmin = pt % width;
                if (pt % width > pxmax) pxmax = pt % width;
                if (pt / width < pymin) pymin = pt / width;
                if (pt / width > pymax) pymax = pt / width;

                queue.offer(pt - width);
                queue.offer(pt + 1);
                queue.offer(pt + width);
                queue.offer(pt - 1);
            }
        }

        return (pxmax + pxmin) / 2 + (pymax + pymin) / 2 * width;
    }
}
