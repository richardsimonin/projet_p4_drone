package com.example.drone;

import androidx.appcompat.app.AppCompatActivity;
import java.nio.ByteBuffer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import android.os.Handler;
import java.net.*;
import android.os.Message;
import java.io.DataInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;



public class MainActivity extends AppCompatActivity {

    //final private Handler handler;

    ImageButton buttonJoy1=null;
    ImageButton buttonJoy2=null;
    ImageView joy1=null;
    ImageView joy2=null;
    ImageView imJoy1=null;
    ImageView imJoy2=null;
    int throttle; int variation_throttle=0; int throttle_prec=0; int consigne_throttle;
    int yaw; int variation_yaw=0; int yaw_prec=0; int consigne_yaw;
    int pitch; int variation_pitch=0; int pitch_prec=0; int consigne_pitch;
    int roll; int variation_roll=0; int roll_prec=0; int consigne_roll=0;
    TextView th = null;
    TextView ya = null;
    TextView ro = null;
    TextView pi = null;
    Animation animation = null;
    Animation largeButon = null;
    Animation animationRetour = null;
    Animation animationRetourLargeButon=null;
    float yj1,xj1,stock_yj1,stock_xj1;
    float yj2,xj2,stock_yj2,stock_xj2;
    final static int port = 4000;
    private Handler handler;

    ClientTCP clientTCP=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(clientTCP == null)
        {
            clientTCP = new ClientTCP("192.168.1.9", 4242, handler);
            clientTCP.demarrer();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonJoy1=findViewById(R.id.boutonJoy1);
        buttonJoy2=findViewById(R.id.boutonJoy2);
        joy1=findViewById(R.id.joy1);
        joy2=findViewById(R.id.joy2);
        imJoy1=findViewById(R.id.imJoy1);
        imJoy2=findViewById(R.id.imJoy2);
        th=findViewById(R.id.throttle);
        ya=findViewById(R.id.yaw);
        ro=findViewById(R.id.roll);
        pi=findViewById(R.id.pitch);
        animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animation);
        animation.setFillAfter(true);
        animation.setFillEnabled(true);
        largeButon=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animlargebuton);
        animationRetour=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animationretour);
        animationRetour.setFillAfter(true);
        animationRetour.setFillBefore(true);
        animationRetourLargeButon=AnimationUtils.loadAnimation(getApplicationContext(),R.anim.animationretourlargebuton);



        buttonJoy1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonJoy1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    imJoy1.startAnimation(largeButon);
                    joy1.startAnimation(animation);
                    throttle=1500;
                    yaw=1500;
                    throttle_prec=(int)event.getY(0);
                    yaw_prec=(int)event.getX(0);
                    xj1 = buttonJoy1.getX()-15;   //reglage offset affichage quand on lache
                    yj1 = buttonJoy1.getY()-15;
                    stock_xj1=xj1;
                    stock_yj1=yj1;

                }

                else if (action == MotionEvent.ACTION_MOVE){
                    variation_throttle= (int)(event.getY(0)*1.5)-throttle_prec;
                    throttle_prec=(int)(event.getY(0)*1.5);
                    throttle=throttle-variation_throttle;
                    consigne_throttle=throttle;
                    yj1=yj1+variation_throttle/5;
                    if(throttle<1000) consigne_throttle=1000;
                    else if(throttle>2000) consigne_throttle=2000;
                    else joy1.setY(yj1-5);      //reglage offset pour le curseur autour du doigt



                    variation_yaw=(int)(event.getX(0)*1.5)-yaw_prec;
                    yaw_prec=(int)(event.getX(0)*1.5);
                    yaw=yaw+variation_yaw;
                    consigne_yaw=yaw;
                    xj1=xj1+variation_yaw/5;
                    if(yaw<1000) consigne_yaw=1000;
                    else if(yaw>2000) consigne_yaw=2000;
                    else joy1.setX(xj1-5);


                    th.setText("throttle : " + String.valueOf(consigne_throttle));
                    ya.setText("yaw : " + String.valueOf(consigne_yaw));

                    if(clientTCP != null)
                    {
                        clientTCP.envoyer("throttle:"+ String.valueOf(consigne_throttle) + ",pitch:" + String.valueOf(consigne_pitch) + ",roll:" +String.valueOf(consigne_roll) +",yaw:"+ String.valueOf(consigne_yaw)+".");
                    }


                }
                else {
                    joy1.startAnimation(animationRetour);
                    imJoy1.startAnimation(animationRetourLargeButon);
                    joy1.setX(stock_xj1);
                    joy1.setY(stock_yj1);
                }



                return false;
            }
        });

        buttonJoy2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonJoy2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    imJoy2.startAnimation(largeButon);
                    joy2.startAnimation(animation);
                    pitch=1500;
                    roll=1500;
                    pitch_prec=(int)event.getY(0);
                    roll_prec=(int)event.getX(0);
                    xj2 = buttonJoy2.getX()-15;
                    yj2 = buttonJoy2.getY()-15;
                    stock_xj2=xj2;
                    stock_yj2=yj2;

                }
                else if (action == MotionEvent.ACTION_MOVE){
                    variation_pitch= (int)(event.getY(0)*1.5)-pitch_prec;
                    pitch_prec=(int)(event.getY(0)*1.5);
                    pitch=pitch-variation_pitch;
                    consigne_pitch=pitch;
                    yj2=yj2+variation_pitch/5;
                    if(pitch<1000) consigne_pitch=1000;
                    else if(pitch>2000) consigne_pitch=2000;
                    else joy2.setY(yj2-5);


                    variation_roll=(int)(event.getX(0)*1.5)-roll_prec;
                    roll_prec=(int)(event.getX(0)*1.5);
                    roll=roll+variation_roll;
                    consigne_roll=roll;
                    xj2=xj2+variation_roll/5;
                    if(roll<1000) consigne_roll=1000;
                    else if(roll>2000) consigne_roll=2000;
                    else joy2.setX(xj2-5);


                    pi.setText("pitch : " + String.valueOf(consigne_pitch));
                    ro.setText("roll : " + String.valueOf(consigne_roll));
                }
                else {
                    joy2.startAnimation(animationRetour);
                    imJoy2.startAnimation(animationRetourLargeButon);
                    joy2.setX(stock_xj2);
                    joy2.setY(stock_yj2);
                }
                return false;
            }
        });



    }
}
