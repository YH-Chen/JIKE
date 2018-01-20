package com.example.great.project.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.example.great.project.R;

public class MusicService extends Service {
    public static MediaPlayer mp = new MediaPlayer();
    public static MediaPlayer finish_mp = new MediaPlayer();
    public MyBinder mBinder = new MyBinder();
    //private String musicPath = Environment.getExternalStorageDirectory()+"/data/melt.mp3";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        mp = MediaPlayer.create(this, R.raw.lowsing);
        finish_mp = MediaPlayer.create(this, R.raw.finish);
        mp.setLooping(true);
        return mBinder;
    }

    public class MyBinder extends Binder {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code){
                case 101://Play music
                    if(mp.isPlaying()){
                        mp.pause();
                    }
                    else{
                        mp.start();
                    }
                    break;
                case 102:
                    finish_mp.start();
                    break;
                case 104://界面刷新
                    reply.writeInt(mp.getCurrentPosition());
                    reply.writeInt(mp.getDuration());
                    if(mp.isPlaying())
                        reply.writeInt(1);
                    else
                        reply.writeInt(0);
                    break;
                case 105://拖动进度条，服务处理函数
                    int music_time = data.readInt();
                    mp.seekTo(music_time);
                    break;
                case 106:
                    try {
                        //mp.setDataSource(musicPath);
                        //mp.prepare();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
            }
            return super.onTransact(code, data, reply, flags);

        }
    }

}
