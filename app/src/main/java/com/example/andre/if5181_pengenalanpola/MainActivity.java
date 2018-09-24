package com.example.andre.if5181_pengenalanpola;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button buttonTugas1;
    private Button buttonTugas2;
    private Button buttonTugas3;
    private Button buttonTugas4;
    private Button buttonTugas5;
    private Button buttonTugas6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.buttonTugas1 = this.findViewById(R.id.buttonTugas1);
        this.buttonTugas2 = this.findViewById(R.id.buttonTugas2);
        this.buttonTugas3 = this.findViewById(R.id.buttonTugas3);
        this.buttonTugas4 = this.findViewById(R.id.buttonTugas4);
        this.buttonTugas5 = this.findViewById(R.id.buttonTugas5);
        this.buttonTugas6 = this.findViewById(R.id.buttonTugas6);

        this.buttonTugas1.setOnClickListener(this);
        this.buttonTugas2.setOnClickListener(this);
        this.buttonTugas3.setOnClickListener(this);
        this.buttonTugas4.setOnClickListener(this);
        this.buttonTugas5.setOnClickListener(this);
        this.buttonTugas6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==buttonTugas1){
            this.tugas1();
        }
        if (v==buttonTugas2){
            this.tugas2();
        }
        if (v==buttonTugas3){
            this.tugas3();
        }
        if (v==buttonTugas4){
            this.tugas4();
        }
        if (v==buttonTugas5){
            this.tugas5();
        }
        if (v==buttonTugas6){
            this.tugas6();
        }
    }

    private void tugas1() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas1.class);
        startActivity(intent);
    }

    private void tugas2() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas2.class);
        startActivity(intent);
    }

    private void tugas3() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas3.class);
        startActivity(intent);
    }

    private void tugas4() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas4.class);
        startActivity(intent);
    }

    private void tugas5() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas5.class);
        startActivity(intent);
    }

    private void tugas6() {
        Intent intent = new Intent(MainActivity.this, Act_Tugas6.class);
        startActivity(intent);
    }
}