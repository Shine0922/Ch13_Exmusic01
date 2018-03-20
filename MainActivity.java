package com.example.win7.exmusic01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;


public class MainActivity extends AppCompatActivity
{
    private ImageView imgFront,imgStop, imgPlay, imgPause, imgNext, imgEnd;
    private ListView listMusic;
    private TextView txtMusic;
    private MediaPlayer mediaplayer;

    //  宣告 songpath 常數儲存SD卡路徑
    private final String SONGPATH= Environment.getExternalStorageDirectory().getPath() + "/";

    //歌曲名稱
    String[] songname=new String[] {"greensleeves", "mario", "songbird", "summersong", "tradewinds"};
    //歌曲檔案
    String[] songfile=new String[] {"greensleeves.mp3", "mario.mp3", "songbird.mp3", "summersong.mp3", "tradewinds.mp3"};
    //目前播放歌曲
    private int cListItem = 0;
    //暫停旗標
    private Boolean falgPause = false;

    private ArrayAdapter<String> adaSong;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgFront=(ImageView)findViewById(R.id.imgFront);
        imgStop=(ImageView)findViewById(R.id.imgStop);
        imgPlay=(ImageView)findViewById(R.id.imgPlay);
        imgPause=(ImageView)findViewById(R.id.imgPause);
        imgNext=(ImageView)findViewById(R.id.imgNext);
        imgEnd=(ImageView)findViewById(R.id.imgEnd);

        listMusic=(ListView)findViewById(R.id.listMusic);
        txtMusic=(TextView)findViewById(R.id.txtMusic);

        imgFront.setOnClickListener(listener);
        imgStop.setOnClickListener(listener);
        imgPlay.setOnClickListener(listener);
        imgPause.setOnClickListener(listener);
        imgNext.setOnClickListener(listener);
        imgEnd.setOnClickListener(listener);
        listMusic.setOnItemClickListener(listListener);

        mediaplayer=new MediaPlayer();
        //  建立　ArrayAdapter 物件 並設定資料來源為 songname陣列
        adaSong = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, songname);
        //  檢查是否取得執行時授權
        requestStoragePermission();
    }
    //檢查驗證
    private void requestStoragePermission()
    {
     if(Build.VERSION.SDK_INT >=23)
        {
        //  判斷是否取得驗證
            int hasPermission = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            if(hasPermission != PackageManager.PERMISSION_GRANTED)
            {   //  未取得驗證
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                return;
            }
        }
        //  已取得驗證 (使用setAdapter( ) 方法 將adaSong 設定為 list 的顯示內容
      listMusic.setAdapter(adaSong);
    }

    //  requestPermissions 觸發的事件
    @Override
    public void onRequestPermissionsResult(int requestCode,String[]permissions,int[]grantResults)
    {
        if(requestCode == 1)
        {   //  按允許鈕
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                listMusic.setAdapter(adaSong);
            }
            else
            {
                Toast.makeText(this,"未取得權限",Toast.LENGTH_LONG).show();
                finish();   //  結束應用程式
            }
        }
        else
        {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    //  ImageView的監聽事件
    private ImageView.OnClickListener listener = new ImageView.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch(v.getId())
            {
                case R.id.imgFront:     //  上一首
                frontSong();
                break;

                case R.id.imgStop:  //  停止
                if(mediaplayer.isPlaying()) // 是否正在播放
                {
                    mediaplayer.reset();    //  重置MediaPlayer
                }
                break;

                case R.id.imgPlay:  // 播放
                    if(falgPause)   //  如果是暫停狀態就繼續播放
                    {
                        mediaplayer.start();
                        falgPause = false;
                    }
                    else    //  非暫停狀態則重頭撥放
                    {
                     playSong(SONGPATH + songfile[cListItem]);
                    }
                    break;

                case R.id.imgPause: //  暫停
                    mediaplayer.pause();
                    falgPause = true;
                    break;

                case R.id.imgNext:  // 下一首
                    nextSong();
                    break;

                case R.id.imgEnd:   //結束
                    mediaplayer.release();
                    finish();
                    break;
            }
        }
    };
        //  ListView 監聽事件
    private ListView.OnItemClickListener listListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            cListItem = position;   // 取得點選位置
            playSong(SONGPATH + songfile[cListItem]);   //  播放
        }
    };
    //  playSong 方法
    private void playSong(String path)
    {
        try
        {
            mediaplayer.reset();
            mediaplayer.setDataSource(path);    //  播放歌曲路徑
            mediaplayer.prepare();
            mediaplayer.start();    //  開始播放
            txtMusic.setText(" 歌名 " + songname[cListItem]); // 更新顯示歌名
            mediaplayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp)
                {
                    nextSong(); //  播放完後下一首
                }
            });
        }
        catch(IOException e)
        {

        }
    }

    //  nextSong (下一首) 方法
    private void nextSong()
    {
        cListItem++;
        if(cListItem >= listMusic.getCount())   //若到最後就移到第一首
            cListItem = 0;
            playSong(SONGPATH + songfile[cListItem]);
    }

    //  上一首 方法
    private void frontSong()
    {
        cListItem--;
        if(cListItem<0)
          cListItem = listMusic.getCount()-1; // 若到第一首就移到最後
            playSong(SONGPATH + songfile[cListItem]);

    }
}
