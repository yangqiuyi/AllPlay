package com.streampublisher.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;


public class AudioRecorderCreator {

    public static final String TAG = "AudioRecorderCreator";

    private static final int[] AUDIO_SOURCES = new int[]{
            MediaRecorder.AudioSource.MIC,
            MediaRecorder.AudioSource.DEFAULT,
            MediaRecorder.AudioSource.CAMCORDER,
            MediaRecorder.AudioSource.VOICE_COMMUNICATION,
            MediaRecorder.AudioSource.VOICE_RECOGNITION,
    };

    public static AudioRecord create(int bitRate,int channelConfig,int buffer_size)
    {
        AudioRecord audioRecord = null;
        for (final int source : AUDIO_SOURCES) {
            try {
                audioRecord = new AudioRecord(
                        source, bitRate,
                        channelConfig, AudioFormat.ENCODING_PCM_16BIT, buffer_size);
                if (audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
                    audioRecord = null;
                    Log.e(TAG, "audio record is not initialized, source:" + source);
                }
            } catch (final Exception e) {
                audioRecord = null;
            }
            if (audioRecord != null) break;
        }
        return audioRecord;
    }
}
