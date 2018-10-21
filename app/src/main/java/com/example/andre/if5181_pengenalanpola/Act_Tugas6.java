package com.example.andre.if5181_pengenalanpola;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.Queue;

public class Act_Tugas6 extends AppCompatActivity {

    private final int GRAYSCALE = 3;

    ImageView imageView, imageViewThinningZS, imageViewThinningC;
    Bitmap numberBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tugas6);

        imageView = findViewById(R.id.imageView);
        imageView.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        numberBitmap = drawable.getBitmap();
        numberBitmap = getBinaryImage(numberBitmap, 128);
        imageView.setImageBitmap(numberBitmap);

        Bitmap[] result = getSkeleton(numberBitmap);

        imageViewThinningZS = findViewById(R.id.imageViewThinningZS);
        imageViewThinningZS.setImageBitmap(result[0]);

        imageViewThinningC = findViewById(R.id.imageViewThinningC);
        imageViewThinningC.setImageBitmap(result[1]);
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

    private int[] getPixelColor(Bitmap bitmap, int x, int y) {
        int pixel, red, green, blue, grayscale;

        pixel = bitmap.getPixel(x, y);
        red = Color.red(pixel);
        green = Color.green(pixel);
        blue = Color.blue(pixel);
        grayscale = (red + green + blue) / 3;

        return new int[]{red, green, blue, grayscale};
    }

    private void setPixelColor(Bitmap bitmap, int x, int y, int red, int green, int blue) {
        bitmap.setPixel(x, y, Color.argb(255, red, green, blue));
    }

    private Bitmap[] getSkeleton(Bitmap bitmap) {
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

    private void customStep(int[] pixels, int xmin, int ymin, int xmax, int ymax, int x, int y, int width) {
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

    private int[] floodFill(int[] pixels, int x, int y, int width) {

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
}