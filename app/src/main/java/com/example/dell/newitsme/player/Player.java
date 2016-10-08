package com.example.dell.newitsme.player;


import android.net.Uri;
import android.view.Surface;

public interface Player {

    //void setListener(PlayerEvent listener);
    void setSurface(Surface surface);
    Boolean play(Uri uri);
    long getCurrentPos();
    long getDuration();
    void seekTo(long positionMs);
    void release();
}