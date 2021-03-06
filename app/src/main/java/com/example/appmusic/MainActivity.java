package com.example.appmusic;

import android.Manifest;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String[] itemAll;
    private ListView mSongsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSongsList = findViewById(R.id.songsList);


        appExternalStorageStoragePermission();
    }


    public void  appExternalStorageStoragePermission(){
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                        displayAudioSongsName();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.cancelPermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> readOnlyAudioSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();

        File[] allFiles = file.listFiles();

        for(File individuaFile : allFiles){
            if(individuaFile.isDirectory() && !individuaFile.isHidden()){
                arrayList.addAll(readOnlyAudioSongs(individuaFile));
            }else {
                if(individuaFile.getName().endsWith(".mp3") || individuaFile.getName().endsWith(".flac")) {
                    arrayList.add(individuaFile);
                }
            }
        }
        return  arrayList;
    }

    private  void displayAudioSongsName(){
        final ArrayList<File> audioSongs = readOnlyAudioSongs(Environment.getExternalStorageDirectory());

        itemAll = new String[audioSongs.size()];

        for(int songCounter = 0;songCounter<audioSongs.size();songCounter++){
            itemAll[songCounter] = audioSongs.get(songCounter).getName().replace(".mp3","").replace(".flac","");
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,itemAll);
        mSongsList.setAdapter(arrayAdapter);

        mSongsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                startActivity(new Intent(getApplicationContext(),Player.class).putExtra("pos",position).putExtra("songlist",audioSongs));
            }
        });
    }
}
