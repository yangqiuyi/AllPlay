package com.streampublisher.recorder;

import android.media.*;
import android.util.Log;
import sdk.miraeye.codec.DataSink;
import sdk.miraeye.codec.MediaEncoder;
import java.nio.ByteBuffer;

public class AudioRecorder {
    private static final String TAG = "AudioRecorder";

    public static final int SAMPLES_PER_FRAME = 2048;    // AAC, bytes/frame/channel
    public static final int FRAMES_PER_BUFFER = 12;    // AAC, frame/buffer/sec

    private MediaEncoder encoder_;
    private MediaFormat mediaFormat_;
    private AudioThread audioThread_ = null;
   // private int channel_mask_;
   // private int bitRate_;
    private DataSinkWrap _dataSinkWrap;


    public AudioRecorder(MediaFormat mediaFormat, DataSink sink) {

        mediaFormat_ = mediaFormat;
        _dataSinkWrap = new DataSinkWrap(sink);
        prepare();
    }


    private void prepare() {

        encoder_ = MediaEncoder.create(MediaEncoder.HD_AUDIO_ENCODER, _dataSinkWrap);
        //channel_mask_ = mediaFormat_.getInteger(MediaFormat.KEY_CHANNEL_MASK);
        //bitRate_ = mediaFormat_.getInteger(MediaFormat.KEY_BIT_RATE);

        try {
            encoder_.prepare(mediaFormat_);
        }catch (Exception e){
            Log.i(TAG,"prepare exception",e);
        }
    }

    public boolean start() {

        // create and execute audio capturing thread using internal mic
        if (audioThread_ == null) {
            audioThread_ = new AudioThread(encoder_);
        }
        return true;
    }

    public boolean discard()
    {
        if (_dataSinkWrap != null){
            _dataSinkWrap.discard();
            _dataSinkWrap = null;
        }

        if (audioThread_ != null)
        {
            audioThread_.discard();
            audioThread_ = null;
        }
        return true;
    }


    /**
     * Thread to capture audio data from internal mic as uncompressed 16bit PCM data
     * and write them to the MediaCodec encoder
     */
    private class AudioThread implements Runnable {
        public static final int ACTION_INIT = 0;
        public static final int ACTION_START = 1;
        public static final int ACTION_STOP = 2;

        private int audioState_ = ACTION_INIT;
        private AudioRecord audioRecord_ = null;
        private Thread thread_ = null;

        private MediaEncoder encoder_;

        public AudioThread(MediaEncoder encoder) {
            encoder_ = encoder;
            thread_ = new Thread(this, "AudioRecordThread");
            thread_.start();
        }

        public void discard() {
            synchronized (this) {
                audioState_ = ACTION_STOP;
            }
        }

        private void initAudioRecord() {

            int channelConfig = mediaFormat_.getInteger(MediaFormat.KEY_CHANNEL_MASK);
            int sampleRate = mediaFormat_.getInteger(MediaFormat.KEY_SAMPLE_RATE);

            int min_buffer_size = AudioRecord.getMinBufferSize(sampleRate,
                    channelConfig, AudioFormat.ENCODING_PCM_16BIT);
            int buffer_size = SAMPLES_PER_FRAME * FRAMES_PER_BUFFER;
            if (buffer_size < min_buffer_size) {
                buffer_size = ((min_buffer_size / SAMPLES_PER_FRAME) + 1) * SAMPLES_PER_FRAME * 2;
            }

            audioRecord_ = AudioRecorderCreator.create(sampleRate, channelConfig, buffer_size);
        }

        private int frameRecvCount_ = 0;
        private void beginAudioRecord() {
            Log.d(TAG, "beginAudioRecord");

            if (audioRecord_ == null) {
                Log.e(TAG, "beginAudioRecord,audioRecord_ == null");
                return;
            }
            int state = -1;
            synchronized (this) {
                state = audioState_;
                if (state == ACTION_STOP) {
                    return;
                }
                audioState_ = ACTION_START;
            }

            audioRecord_.startRecording();

            final ByteBuffer buffer = ByteBuffer.allocateDirect(SAMPLES_PER_FRAME);
            int readBytes;

            while (true) {
                synchronized (this) {
                    state = audioState_;
                }
                if (state != ACTION_START) {
                    break;
                }

                if (audioRecord_.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    break;
                }

                // read audio data from internal mic
                buffer.clear();
                readBytes = audioRecord_.read(buffer, SAMPLES_PER_FRAME);
                if (readBytes > 0) {
                    // set audio data to encoder
                    buffer.position(readBytes);
                    buffer.flip();

                    byte bytes[] = new byte[readBytes];
                    buffer.get(bytes, 0, readBytes);

                    if (frameRecvCount_ < 10)
                    {
                        Log.d(TAG,"beginAudioRecord,recv frame:"+frameRecvCount_ + "," + readBytes);

                    }
                    frameRecvCount_++;
                    synchronized (this) {
                        state = audioState_;
                        if (state != ACTION_START) {
                            break;
                        }
                        encoder_.write(System.currentTimeMillis(), bytes);
                    }

                }
            }
        }

        private void stopAudioRecord()
        {
            Log.d(TAG,"stopAudioRecord");
            if (audioRecord_ != null) {
                audioRecord_.stop();
                audioRecord_ = null;
            }
            if (encoder_ != null) {
                encoder_.release();
                encoder_ = null;
            }
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
            try {
                Log.d(TAG, "AudioThread#run, in");
                initAudioRecord();
                beginAudioRecord();
                stopAudioRecord();

            } catch ( Exception e) {
                Log.i(TAG, "AudioThread#run", e);
            }
            Log.d(TAG, "AudioThread#run,out");
        }
    }

    class DataSinkWrap implements DataSink
    {
        DataSink _sink;

        public DataSinkWrap(DataSink sink){
            _sink = sink;
        }
        public void discard(){
            _sink = null;
        }
        @Override
        public void write(MediaFormat mediaFormat, int i, long l, byte[] bytes)
        {
            if (_sink != null){
                _sink.write(mediaFormat,i,l,bytes);
            }
        }

        @Override
        public void flush() {
            if (_sink != null){
                _sink.flush();
            }
        }
    }
}
