package com.example.appmusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.FileObserver;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class Player extends AppCompatActivity {

    static MediaPlayer mediaPlayer;
    ArrayList<File> mySongs;
    TextView txtTitle, txtTimeStart, txtTimeEnd;
    SeekBar skbarSong;
    ImageView imgdisc;
    ImageButton imgbtnPrev, imgbtnPlay, imgbtnStop, imgbtnNext;

    //ArrayList<Song> mySongs;

    int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if(mediaPlayer != null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        Intent i =getIntent();

        Bundle b = i.getExtras();

        mySongs = (ArrayList)b.getParcelableArrayList("songlist");

        position = b.getInt("pos",0);

        AnhXa();

        KhoiTao();

        CapNhatTimeSong();


        mediaPlayer.start();



        imgbtnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()==true){
                    //nếu đang phát -> pause -> đổi hình play
                    mediaPlayer.pause();
                    imgbtnPlay.setImageResource(R.drawable.play64);
                }else {
                    //đang ngừng -> phát -> đổi hình pause
                    mediaPlayer.start();
                    imgbtnPlay.setImageResource(R.drawable.pause64);
                }
                SetTimeEnd();
            }
        });

        imgbtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mediaPlayer.stop();

                mediaPlayer.release();

                imgbtnPlay.setImageResource(R.drawable.play64);

                KhoiTao();
            }
        });

        imgbtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position++;
                if(position > mySongs.size() -1){
                    position = 0;
                }
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                }

                KhoiTao();

                mediaPlayer.start();

                imgbtnPlay.setImageResource(R.drawable.pause64);
            }
        });

        imgbtnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                position--;
                if (position < 0) {
                    position = mySongs.size() - 1;
                }
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }

                KhoiTao();

                mediaPlayer.start();
                imgbtnPlay.setImageResource(R.drawable.pause64);
            }
        });

        skbarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(skbarSong.getProgress());
            }
        });

    }

    private  void  AnhXa(){
        txtTitle        =   (TextView)  findViewById(R.id.txtTitle);
        txtTimeStart    =   (TextView)  findViewById(R.id.txtTimeStart);
        txtTimeEnd      =   (TextView)  findViewById(R.id.txtTimeEnd);

        skbarSong       =   (SeekBar)   findViewById(R.id.skbarSong);

        //imgdisc         =   (ImageView) findViewById(R.id.imageViewDisc);

        imgbtnPrev      =   (ImageButton)findViewById(R.id.imgbtnPrev);
        imgbtnNext      =   (ImageButton)findViewById(R.id.imgbtnNext);
        imgbtnPlay      =   (ImageButton)findViewById(R.id.imgbtnPlay);
        imgbtnStop      =   (ImageButton)findViewById(R.id.imgbtnStop);
    }

    private void KhoiTao(){

        Uri u =Uri.parse(mySongs.get(position).toString());

        mediaPlayer =MediaPlayer.create(getApplicationContext(),u);

        SetTimeEnd();
    }

    private  void SetTimeEnd(){
        SimpleDateFormat dinhdanggio = new SimpleDateFormat("mm:ss");
        txtTimeEnd.setText(dinhdanggio.format(mediaPlayer.getDuration()));
        //gán max của skbarSong = mediaPlayer.getDuratiom()
        skbarSong.setMax(mediaPlayer.getDuration());
    }

    private  void CapNhatTimeSong() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat dinhdanggio = new SimpleDateFormat("mm:ss");
                txtTimeStart.setText(dinhdanggio.format(mediaPlayer.getCurrentPosition()));
                //cập nhật progress skbarSong
                skbarSong.setProgress(mediaPlayer.getCurrentPosition());
                //kiểm tra thời gian bài hát -> nếu kết thúc -> next
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        position++;
                        if (position > mySongs.size() - 1) {
                            position = 0;
                        }
                        if (mediaPlayer.isPlaying()) {
                            mediaPlayer.stop();
                        }
                        KhoiTao();
                        mediaPlayer.start();
                        imgbtnPlay.setImageResource(R.drawable.pause64);
                        SetTimeEnd();
                        CapNhatTimeSong();
                    }
                });
                handler.postDelayed(this, 500);
            }
        }, 100);
    }
}
