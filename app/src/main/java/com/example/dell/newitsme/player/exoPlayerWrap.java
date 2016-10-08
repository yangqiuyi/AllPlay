package com.example.dell.newitsme.player;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.Surface;
import com.example.util.log.LogUtil;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;


public class ExoPlayerWrap implements Player {

    public static final String TAG = "ExoPlayerWrap";
    private DemoPlayer _player;
    private Context _context;
    private Surface _surface;
    private int _contentType;

    public ExoPlayerWrap(int contentType, Context context) {
        _context = context;
        _contentType = contentType;
        VerboseLogUtil.setEnableAllTags(true);
    }

    @Override
    public long getCurrentPos()
    {
        if (_player != null )
        {
            return _player.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration()
    {
        if (_player != null )
        {
            return _player.getDuration();
        }
        return 0;
    }

    @Override
    public void seekTo(long positionMs) {

        if (_player != null )
        {
             _player.seekTo(positionMs);
        }
    }

    @Override
    public void setSurface(Surface surface) {
        if (_player != null) {
            LogUtil.d(TAG, "setSurface," + (surface == null ? "null" : surface.toString()));
            _player.setSurface(surface);
        } else {
            _surface = surface;
        }
    }


    private Uri _contentUri;

    @Override
    public Boolean play(Uri uri) {
        LogUtil.d(TAG, "play:" + uri.toString());

        _contentUri = uri;

        DemoPlayer player = new DemoPlayer(getRendererBuilder());

        EventLogger eventLogger = new EventLogger();
        eventLogger.startSession();

        player.addListener(eventLogger);

        player.setInfoListener(eventLogger);
        player.setInternalErrorListener(eventLogger);

        player.prepare();

        if (_surface != null) {
            LogUtil.d(TAG, "player.setSurface");
            player.setSurface(_surface);
            _surface = null;
        }

        player.setPlayWhenReady(true);

        _player = player;

        return true;
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(_context, "ExoPlayerDemo");

        switch (_contentType) {
            case Util.TYPE_HLS:
                return new HlsRendererBuilder(_context, userAgent, _contentUri.toString());
            case Util.TYPE_OTHER:
                return new ExtractorRendererBuilder(_context, userAgent, _contentUri);
            default:
                throw new IllegalStateException("Unsupported type: " + _contentType);
        }
    }

    @Override
    public void release() {
        LogUtil.d(TAG, "release");

        if (_player != null) {
            _player.release();
        }

        _audioRenderer = null;
        _videoRenderer = null;
    }



    private void runOnUiThread(Runnable runnable) {
        _handler.post(runnable);
    }

    private Handler _handler = new Handler();

    private MediaCodecVideoTrackRenderer _videoRenderer;
    private MediaCodecAudioTrackRenderer _audioRenderer;
}
