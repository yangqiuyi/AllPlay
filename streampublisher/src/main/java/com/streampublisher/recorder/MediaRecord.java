package com.streampublisher.recorder;

import android.media.AudioFormat;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import com.streampublisher.StreamPublisher;
import com.streampublisher.StreamStatitics;
import sdk.miraeye.codec.FlvDataSink;
import sdk.miraeye.codec.MediaEncoder;
import sdk.miraeye.flv.AudioDataHeader;
import sdk.miraeye.flv.Header;
import sdk.miraeye.flv.TagHeader;
import sdk.miraeye.flv.VideoDataHeader;

public class MediaRecord  {

    private static final String TAG = "MediaRecord";

    private AudioRecorder audioRecorder_ = null;
    private VideoRecorder videoRecorder_ = null;
    private FlvOutputDelegate _flvOutputdelegate;
    private int width_;
    private int height_;
    private int mActivity_rotation;
    public static void init()
    {
        Log.d(TAG,"MediaRecord init");
        MediaEncoder.initialize();
    }
    public MediaRecord(int activity_rotation,int width, int height) {
        width_ = width;
        height_ = height;
        mActivity_rotation = activity_rotation;
    }

    public boolean prepare(int cameraId, SurfaceHolder surfaceHolder)
    {
        _flvOutputdelegate = new FlvOutputDelegate();
        FlvDataSink dataSink = new FlvDataSink(_flvOutputdelegate);

        MediaFormat audioFmt = initAudioFormat();
        audioRecorder_ = new AudioRecorder(audioFmt, dataSink);
        audioRecorder_.start();

        MediaFormat videoFmt = initVideoFormat(width_, height_);
        videoRecorder_ = new VideoRecorder(videoFmt, dataSink, StreamPublisher.VIDEO_HD_ENCODER,mActivity_rotation);
        if(videoRecorder_.startPreview(cameraId, surfaceHolder)){
            videoRecorder_.startCapture();
            return true;
        }
        return false;
    }

    public Boolean switchCameraId(int cameraId) {
        if (videoRecorder_ != null) {
            return videoRecorder_.switchCameraId(cameraId);
        }
        return false;
    }

    public Boolean openLamp(Boolean open) {
        if (videoRecorder_ != null) {
            return videoRecorder_.openLamp(open);
        }
        return false;
    }


    public boolean startPublish(String url, String streamName, String session, PublishListener callback)
    {

        PublishConnector connector = new PublishConnector(callback);
        connector.connect(url,streamName,session);
        _flvOutputdelegate.setConnector(connector);
        return true;
    }


    public boolean stopPublish()
    {
        if (_flvOutputdelegate != null) {
            _flvOutputdelegate.discard();
            _flvOutputdelegate = null;
        }

        if (audioRecorder_ != null) {
            audioRecorder_.discard();
            audioRecorder_ = null;
        }
        if (videoRecorder_ != null){
            videoRecorder_.discard();
            videoRecorder_ = null;
        }
        return true;
    }

    private static final long VIDEO_LIFETIME = 5;
    private static final long AUDIO_LIFETIME = VIDEO_LIFETIME;


    private MediaFormat initAudioFormat()
    {
        int channelConfig = AudioFormat.CHANNEL_IN_STEREO;
        int sampleRate = 44100;
        int channelCount = 2;
        int bitRate = 32000;
        MediaFormat audioFormat = MediaFormat.createAudioFormat("audio/mp4a-latm", sampleRate, channelCount);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, channelConfig);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);


       // Log.d(TAG, "initAudioFormat,channelConfig:%d,channelCount:%d,bitRate:%d,sampeRate:%d", channelConfig, channelCount, bitRate,sampleRate);

        return audioFormat;
    }

    private MediaFormat initVideoFormat(int width, int height)
    {
        int bitRate = 512000;//比特率（Bit rate，变量Rbit）是单位时间内传输或处理的比特的数量。
        // 模拟信号转换为数字信号后，单位时间内的二进制数据量。

        int frameRate = 18;//就是在1秒钟时间里传输的图片的帧数

        int iFrame = 1;
        MediaFormat videoFormat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, width, height);
        videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, iFrame);//the frequency of I frames expressed in secs
        videoFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);

        //Log.i(TAG, "initVideoFormat,bitRate:%d,frameRate:%d,iFrame:%d:", bitRate, frameRate, bitRate, iFrame);
        return videoFormat;
    }

    class FlvOutputDelegate implements FlvDataSink.Output {


        PublishConnector _connector;
        public FlvOutputDelegate() {

        }

        public void setConnector(PublishConnector connector){
            _connector = connector;
        }
        public void discard() {

            if (_connector != null) {
                _connector.discard();
                _connector = null;
            }
        }

        @Override
        public void onHeader(Header header) {

        }

        private long _audioTagCount = 0;
        @Override
        public void onAudioTag(TagHeader tagHeader, AudioDataHeader audioDataHeader, byte[] tagData)
        {

            long lifetime = VIDEO_LIFETIME;
            if (audioDataHeader.getAacPacketType() == AudioDataHeader.AacPacketType.AAC_SEQUENCE_HEADER) {
                lifetime = 0;
            }
            PublishConnector connector = _connector;
            if (connector != null){
                //LogUtil.d(TAG,"onAudioTag");
                connector.sendAudio(tagHeader.getTimestamp(), tagData, lifetime);

                StreamStatitics.shareInstance().sendAudio(tagData.length);

                _audioTagCount++;
                if (_audioTagCount < 10){
                    Log.d(TAG,"onAudioTag:" + _audioTagCount + "," + tagData.length);
                }
            }else{
                Log.w(TAG, "onAudioTag,connector null");
            }

        }

        private long _videoTagCount = 0;
        @Override
        public void onVideoTag(TagHeader tagHeader, VideoDataHeader videoDataHeader, byte[] tagData)
        {

            long lifetime = AUDIO_LIFETIME;
            if (videoDataHeader.getAvcPacketType() == VideoDataHeader.AvcPacketType.AVC_SEQUENCE_HEADER) {
                lifetime = 0;
            }

            PublishConnector connector = _connector;
            if (connector != null){
                //LogUtil.d(TAG,"onVideoTag");
                connector.sendVideo(tagHeader.getTimestamp(), tagData, lifetime);

                StreamStatitics.shareInstance().sendVideo(tagData.length);

                _videoTagCount++;
                if (_videoTagCount < 10){
                    Log.d(TAG,"onVideoTag:" + _videoTagCount + "," + tagData.length);
                }
            }else{
                Log.w(TAG, "onVideoTag,connector null");
            }

        }

        @Override
        public void onComplete() {

        }
    }
}
