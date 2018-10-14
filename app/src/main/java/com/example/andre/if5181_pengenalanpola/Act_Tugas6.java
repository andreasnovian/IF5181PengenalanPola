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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.Queue;

public class Act_Tugas6 extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas6);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

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
        Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Bitmap result = getSkeletonFeature(image, textView);

        imageView.setImageBitmap(result);
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

    private static Bitmap[] getSkeleton(Bitmap bitmap) {
        int count;
        int[] border;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        int[] pixelsb = new int[size];

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsa, 0, width, 0, 0, width, height);
        bitmap.getPixels(pixelsb, 0, width, 0, 0, width, height);

        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                if ((pixels[i + j * width] & 0x000000ff) != 255) {
                    border = floodFill(pixels, i, j, width);

                    do {
                        count = zhangSuenStep(pixelsa, border[0], border[1], border[2], border[3], width);
                    }
                    while (count != 0);

                    customStep(pixelsb, border[0], border[1], border[2], border[3], i, j, width);
                }
            }
        }

        return new Bitmap[]{
                Bitmap.createBitmap(pixelsa, width, height, bitmap.getConfig()),
                Bitmap.createBitmap(pixelsb, width, height, bitmap.getConfig())
        };
    }

    private static Bitmap getSkeletonFeature(Bitmap bitmap, TextView textView) {
        int count;
        int[] border;

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int size = height * width;
        int[] pixels = new int[size];
        int[] pixelsa = new int[size];
        StringBuffer stringBuffer = new StringBuffer();

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

                    stringBuffer.append(extractFeature(pixelsa, border[0], border[1], border[2], border[3], width));
                }
            }
        }

        textView.setText(stringBuffer);

        return Bitmap.createBitmap(pixelsa, width, height, bitmap.getConfig());
    }

    private static int zhangSuenStep(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
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

    private static int[] zhangSuenAB(int[] neighbours) {
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

    private static void customStep(int[] pixels, int xmin, int ymin, int xmax, int ymax, int x, int y, int width) {
        int counterDirection, length, c, d, averageLength;

        int a = x;
        int b = y;
        int direction = 2;
        int totalLength = 0;
        int chainCount = 0;
        int[][] neighbours = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}};

        do {
            int i = 0;
            for (; i < 8; i++) {
                int pixel = pixels[(a + neighbours[(direction + i + 5) % 8][0]) + (b + neighbours[(direction + i + 5) % 8][1]) * width] & 0x000000ff;
                if (pixel == 0) break;
            }

            if (i == 9) {
                return;
            } else {
                direction = (direction + i + 5) % 8;
                counterDirection = (direction + 2) % 8;
                c = a + neighbours[counterDirection][0];
                d = b + neighbours[counterDirection][1];
                length = 0;

                while ((pixels[c + d * width] & 0x000000ff) == 0) {
                    c = c + neighbours[counterDirection][0];
                    d = d + neighbours[counterDirection][1];
                    length++;
                }

                if (length > 1) {
                    totalLength += length;
                    chainCount++;
                }

                a = a + neighbours[direction][0];
                b = b + neighbours[direction][1];
            }
        }
        while (!(a == x && b == y));

        averageLength = totalLength / chainCount;
        a = x;
        b = y;
        direction = 2;

        do {
            int i = 0;
            for (; i < 8; i++) {
                int pixel = pixels[(a + neighbours[(direction + i + 5) % 8][0]) + (b + neighbours[(direction + i + 5) % 8][1]) * width] & 0x000000ff;
                if (pixel == 0) break;
            }

            if (i == 9) {
                return;
            } else {
                direction = (direction + i + 5) % 8;
                counterDirection = (direction + 2) % 8;
                c = a + neighbours[counterDirection][0];
                d = b + neighbours[counterDirection][1];
                length = 0;

                while ((pixels[c + d * width] & 0x000000ff) == 0) {
                    c = c + neighbours[counterDirection][0];
                    d = d + neighbours[counterDirection][1];
                    length++;
                }

                if (length > 1 && length <= averageLength) {
                    c = (c - 1 + a) / 2;
                    d = (d - 1 + b) / 2;
                    pixels[c + d * width] = pixels[c + d * width] | 0x0000ff00;
                }

                a = a + neighbours[direction][0];
                b = b + neighbours[direction][1];
            }
        }
        while (!(a == x && b == y));

        for (b = ymin; b <= ymax; b++) {
            for (a = xmin; a <= xmax; a++) {
                if ((pixels[a + b * width] & 0x00ffffff) == 0x0000ff00) {
                    pixels[a + b * width] = pixels[a + b * width] & 0xff000000;
                } else if ((pixels[a + b * width] & 0x000000ff) == 0) {
                    pixels[a + b * width] = pixels[a + b * width] | 0x00ffffff;
                }
            }
        }
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

    private static StringBuffer extractFeature(int[] pixels, int xmin, int ymin, int xmax, int ymax, int width) {
        int next, i, j, neighbourCount;

        int p = 0;
        int endCount = 0;
        int[][] moves = new int[8][8];
        boolean end = false;
        Queue<Integer> queue = new LinkedList<>();

        j = ymin;
        while (p == 0 && j <= ymax) {
            i = xmin;
            while (p == 0 && i <= xmax) {
                if ((pixels[i + j * width] & 0x000000ff) == 0)
                    p = i + j * width;

                i++;
            }
            j++;
        }

        if (p != 0) {
            next = p;
            int before = 2;
            int temp = 0;
            while (!end) {
                int[] neighbours = {
                        p - width,
                        p - width + 1,
                        p + 1,
                        p + width + 1,
                        p + width,
                        p + width - 1,
                        p - 1,
                        p - width - 1
                };

                //Log.i("pixel", "" + p);

                pixels[p] = pixels[p] | 0x0000ff00;
                neighbourCount = 0;

                for (i = 0; i < 8; i++) {
                    if ((pixels[neighbours[i]] & 0x000000ff) == 0) {
                        neighbourCount++;

                        if ((pixels[neighbours[i]] & 0x0000ff00) >> 8 == 0) {
                            moves[before][i]++;
                            if (next == p) {
                                next = neighbours[i];
                                temp = i;
                            } else {
                                queue.offer(neighbours[i]);
                            }
                        }
                    }
                }

                if (neighbourCount == 1) endCount++;

                if (next != p) {
                    p = next;
                    before = temp;
                } else {
                    while (!queue.isEmpty() && (pixels[queue.peek()] & 0x0000ffff) != 0) {
                        queue.poll();
                    }

                    if (queue.isEmpty()) {
                        end = true;
                    } else {
                        p = queue.poll();
                        next = p;
                    }
                }
            }
        }

        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(String.format("End Count : %d\r\n", endCount));
        stringBuffer.append("Moves :\r\n");
        for (int a = 0; a < 8; a++) {
            for (int b = 0; b < 8; b++) {
                stringBuffer.append(String.format("%d %d | %d\r\n", a, b, moves[a][b]));
            }
        }

        return stringBuffer;
    }
}