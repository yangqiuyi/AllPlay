package com.streampublisher.player;



public interface PlayerEvent
{
    public static final int STATE_IDLE              = -1;
    public static final int STATE_READY             = 0x1;
    public static final int STATE_CONNECTING        = 0x2;
    public static final int STATE_CONNECT_ERR       = 0x4;
    public static final int STATE_LOADING_BUFFER    = 0x8;
    public static final int STATE_RE_CONNECTING     = 0x10;
    public static final int STATE_RE_CONNECTED      = 0x20;
    public static final int STATE_REPLAY      = 0x30;//看回放
    public static final int STATE_MASK_ALL          = -1;

    void onPlayerEvent(int event);
}