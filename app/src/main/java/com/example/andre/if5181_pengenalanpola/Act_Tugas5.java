package com.example.andre.if5181_pengenalanpola;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Act_Tugas5 extends AppCompatActivity {

    private final int GRAYSCALE = 3;

    private ImageView imageView;
    private TextView textViewResult, textViewChainCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas5);

        imageView = findViewById(R.id.imageView);
        textViewResult = findViewById(R.id.textViewResult);
        textViewChainCode = findViewById(R.id.textViewChainCode);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
        Bitmap ori = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap image = ori.copy(ori.getConfig(), true);

        int[] color;
        String chain;

        StringBuilder result = new StringBuilder();
        StringBuilder chaincode = new StringBuilder();

        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                color = getPixelColor(image, i, j);

                if (color[GRAYSCALE] == 0) {
                    chain = getChainCode(image, i, j);
                    chaincode.append(String.format("%s\n\n", chain));
                    result.append(String.format("%d", translate(chain)));

                    floodFill(image, i, j);
                }
            }
        }

        textViewResult.setText("Hasil deteksi : "+ result);
        textViewChainCode.setText("Chaincode : "+ chaincode);
    }

    private Bitmap getBinaryImage(Bitmap bitmap, int threshold) {
        Bitmap result = bitmap.copy(bitmap.getConfig(), true);
        int[] color;

        for (int i = 0; i < bitmap.getWidth(); i++) {
            for (int j = 0; j < bitmap.getHeight(); j++) {
                color = getPixelColor(bitmap, i, j);

                if (color[GRAYSCALE] < threshold) {
                    setPixelColor(result, i, j, 0, 0, 0);
                } else {
                    setPixelColor(result, i, j, 255, 255, 255);
                }
            }
        }

        return result;
    }

    private void floodFill(Bitmap bitmap, int x, int y) {
        int[] color = getPixelColor(bitmap, x, y);

        if (color[GRAYSCALE] != 255) {
            setPixelColor(bitmap, x, y, 255, 255, 255);
            floodFill(bitmap, x - 1, y);
            floodFill(bitmap, x + 1, y);
            floodFill(bitmap, x, y - 1);
            floodFill(bitmap, x, y + 1);
        }
    }

    private void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }

    private String getChainCode(Bitmap bitmap, int x, int y) {
        int a = x;
        int b = y;
        int[] next;
        int source = 6;
        String chain = "";

        do {
            next = getNextPixel(bitmap, a, b, source);
            a = next[0];
            b = next[1];
            source = (next[2] + 4) % 8;
            chain = chain + next[2];
        }
        while (!(a == x && b == y));

        return chain;
    }

    private int[] getNextPixel(Bitmap bitmap, int x, int y, int source) {
        int a, b, target = source;
        int[][] points = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

        do {
            target = (target + 1) % 8;
            a = x + points[target][0];
            b = y + points[target][1];
        }
        while (getPixelColor(bitmap, a, b)[GRAYSCALE] == 255);

        return new int[]{a, b, target};
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

    private static int translate(String chain) {

        ArrayList<Integer> count;
        String simpleChain = getSimplifiedChain(chain);

        if (simpleChain.equals("2460")) {
            count = getCountChain(chain);

            if((double)count.get(0) / count.get(1) <= 0.2){
                return 1;
            }
            else{
                return 0;
            }
        } else if (simpleChain.equals("246424602060")) {
            count = getCountChain(chain);

            if (count.get(1) > count.get(count.size() - 3)) {
                return 2;
            } else {
                return 5;
            }
        } else if (simpleChain.equals("246020602060")) {
            return 3;
        } else if (simpleChain.equals("2420246060")) {
            return 4;
        } else if (simpleChain.equals("24642460")) {
            return 6;
        } else if (simpleChain.equals("246060")) {
            return 7;
        } else if (simpleChain.equals("24602060")) {
            return 9;
        } else {
            return -1;
        }
    }

    private static String getSimplifiedChain(String chain) {

        if (chain.length() < 2) {
            return chain;
        }

        char current;
        char last = chain.charAt(0);
        StringBuilder result = new StringBuilder();

        result.append(last);

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (current != last && Character.getNumericValue(current) % 2 == 0) {
                last = chain.charAt(i);
                result.append(last);
            }
        }

        return result.toString();
    }

    private static ArrayList<Integer> getCountChain(String chain) {

        if (chain.length() < 2) {
            return new ArrayList<>();
        }

        ArrayList<Integer> list = new ArrayList<>();
        char current;
        char last = chain.charAt(0);
        int counter = 1;

        for (int i = 1; i < chain.length(); i++) {
            current = chain.charAt(i);
            if (Character.getNumericValue(current) % 2 == 0) {
                if (current != last) {
                    list.add(counter);
                    last = chain.charAt(i);
                    counter = 1;
                } else {
                    counter++;
                }
            }
        }

        list.add(counter);

        return list;
    }
}
