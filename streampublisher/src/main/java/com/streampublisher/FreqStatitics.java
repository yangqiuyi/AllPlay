package com.streampublisher;


public class FreqStatitics
{

    public FreqStatitics()
    {
        _tickLast = System.currentTimeMillis();
        _totalSize = 0;
    }

    public int triger(){
        _totalSize++;
        return calcFreq();
    }

    public int triger(int d)
    {
        _totalSize += d;
        return calcFreq();
    }

    public int getFreq(){
        int slaps = (int)(System.currentTimeMillis() - _tickLast);
        if (slaps >= 1000){
            _freq = _totalSize * 1000 / slaps;
            _totalSize = 0;
            _tickLast = System.currentTimeMillis();
        }
        return _freq;
    }

    private int calcFreq()
    {
        return _freq;
    }

    private int _freq;
    private int  _totalSize;
    private long _tickLast;
}
