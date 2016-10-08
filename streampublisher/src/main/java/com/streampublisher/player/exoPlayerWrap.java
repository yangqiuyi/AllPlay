package com.streampublisher.player;


import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.util.Util;
import com.google.android.exoplayer.util.VerboseLogUtil;
import com.streampublisher.player.exoplayer.DemoPlayer;
import com.streampublisher.player.exoplayer.EventLogger;
import com.streampublisher.player.exoplayer.ExtractorRendererBuilder;
import com.streampublisher.player.exoplayer.HlsRendererBuilder;



public class ExoPlayerWrap implements Player {
    public static final String TAG = "ExoPlayerWrap";
    //private ExoPlayer _player;
    private DemoPlayer _player;

    private static final int BUFFER_SEGMENT_SIZE = 4 * 1024;
    private static final int BUFFER_SEGMENT_COUNT = 1024;
    private static final int RENDERER_COUNT = 2;

    private PlayerEvent _callbackListener;
    private ExoListenerDelegate _exoListener;
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
            Log.d(TAG, "setSurface," + (surface == null ? "null" : surface.toString()));
            //_player.sendMessage(_videoRenderer, MediaCodecVideoTrackRenderer.MSG_SET_SURFACE, surface);
            _player.setSurface(surface);
        } else {
            _surface = surface;
        }
    }

    @Override
    public void setListener(PlayerEvent listener) {
        _callbackListener = listener;
    }

    private Uri _contentUri;

    @Override
    public Boolean play(Uri uri) {
        Log.d(TAG, "play:" + uri.toString());

        _contentUri = uri;

        DemoPlayer player = new DemoPlayer(getRendererBuilder());

        EventLogger eventLogger = new EventLogger();
        eventLogger.startSession();
        player.addListener(eventLogger);

        player.setInfoListener(eventLogger);
        player.setInternalErrorListener(eventLogger);

        player.prepare();

        if (_surface != null) {
            Log.d(TAG, "player.setSurface");
            player.setSurface(_surface);
            //setSurface(_surface);
            _surface = null;
        }

        player.setPlayWhenReady(true);

        _player = player;
        _exoListener = new ExoListenerDelegate();
        _player.addListener(_exoListener);

        return true;
    }

    private DemoPlayer.RendererBuilder getRendererBuilder() {
        String userAgent = Util.getUserAgent(_context, "ExoPlayerDemo");
        //int contentType = Util.TYPE_HLS;
        switch (_contentType) {
//            case Util.TYPE_SS:
//                return new SmoothStreamingRendererBuilder(this, userAgent, contentUri.toString(),
//                        new SmoothStreamingTestMediaDrmCallback());
//            case Util.TYPE_DASH:
//                return new DashRendererBuilder(this, userAgent, contentUri.toString(),
//                        new WidevineTestMediaDrmCallback(contentId, provider));
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
        Log.d(TAG, "release");
        if (_exoListener != null) {
            _exoListener.discard();
            _exoListener = null;
        }
        if (_player != null) {
            //_player.stop();
            _player.release();
        }

        _audioRenderer = null;
        _videoRenderer = null;
    }

    private class ExoListenerDelegate implements DemoPlayer.Listener {
        protected Boolean _discard = false;

        public void discard() {
            _discard = true;
        }

        @Override
        public void onStateChanged(boolean playWhenReady, int playbackState) {
            if (_discard) {
                return;
            }
            String text = "playWhenReady=" + playWhenReady + ", playbackState=";
            switch (playbackState) {
                case ExoPlayer.STATE_BUFFERING:
                    text += "buffering";
                    setState(PlayerEvent.STATE_LOADING_BUFFER);
                    break;
                case ExoPlayer.STATE_ENDED:
                    text += "ended";
                    setState(PlayerEvent.STATE_CONNECT_ERR);
                    break;
                case ExoPlayer.STATE_IDLE:
                    text += "idle";
                    break;
                case ExoPlayer.STATE_PREPARING:
                    text += "preparing";
                    break;
                case ExoPlayer.STATE_READY:
                    text += "ready";
                    setState(PlayerEvent.STATE_READY);
                    break;
                default:
                    text += "unknown";
                    break;
            }

            Log.d(TAG, "onStateChanged:" + text);
        }

        @Override
        public void onError(Exception e) {
            if (_discard) {
                return;
            }
            Log.d(TAG, "播放出错！");
            setState(PlayerEvent.STATE_CONNECT_ERR);
        }

        @Override
        public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

        }
    }

    private void setState(int state) {
        if (_callbackListener != null) {
            _callbackListener.onPlayerEvent(state);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        _handler.post(runnable);
    }

    private Handler _handler = new Handler();

    private MediaCodecVideoTrackRenderer _videoRenderer;
    private MediaCodecAudioTrackRenderer _audioRenderer;
}
