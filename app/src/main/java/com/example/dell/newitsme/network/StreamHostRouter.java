package com.example.dell.newitsme.network;


import android.util.Log;
import com.example.SelfInfo;
import com.example.net.ClientApi;
import java.net.URLEncoder;
import java.util.ArrayList;

public class StreamHostRouter {

    public static final String TAG = "StreamHostRouter";

    private static StreamHostRouter g_inst = null;
    private ArrayList<Item> _ipList = new ArrayList<>();

    public static StreamHostRouter inst() {
        if (g_inst == null) {
            g_inst = new StreamHostRouter();
        }
        return g_inst;
    }

    private String getStreamQueryHost() {
        if (_ipList.isEmpty()) {
            return Consts.DEFAULT_STREAM_HOST;
        }
        return _ipList.get(0).ip;

    }

    public String formQueryUrl(boolean publish, String streamName, String sessionId) {
        StringBuilder b = new StringBuilder();
        String host = getStreamQueryHost();
        String urlHead = "http://" + host + ":" + Consts.STREAM_HOST_PORT + Consts.STREAM_HOST_URL_PATH;

        b.append(urlHead);
        if (publish) {
            b.append("?name=CreateStream");
            b.append("&publisher=" + SelfInfo.uid());
        } else {
            b.append("?name=PlayStream");
            b.append("&player=" + SelfInfo.uid());
        }

        b.append("&token=" + URLEncoder.encode(SelfInfo.token()));
        b.append("&streamname=" + streamName);

        int region = StreamServerRouter.inst().hostToRegion(host);
        if (region >= 0) {
            Log.d(TAG,"region:" + region);
            b.append("&region=" + region);
        }

        b.append("&sessionid=" + sessionId);
        b.append("&quality=0");

        String dev = ClientApi.getDeviceParam();
        b.append("&param=" + URLEncoder.encode(dev));

        b.append("&protocol=");
        String url = b.toString();
      //  Log.d(TAG, "formQueryUrl:%s", url);

        return url;
    }

    public void init() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String[] ip = DNS.resolve(Consts.DEFAULT_STREAM_HOST);

                ArrayList<Item> ipList = new ArrayList<>();
                for (int i = 0; i < ip.length; i++) {
                    ipList.add(new Item(ip[i]));
                }

                start(ipList);
            }
        }).start();

    }


    private int _index = 0;

    private void start(ArrayList<Item> ipList) {
        _index = 0;
        final int total = ipList.size();
        for (int i = 0; i < ipList.size(); i++) {
            final int idx = i;
            Item item = ipList.get(idx);
            Log.i(TAG, "start ping:" + item.ip);
        }
    }


    private static class Item {

        public Item(String ip) {
            this.ip = ip;
        }

        private String ip;
        private int latency = -1;

        @Override
        public String toString() {
            String msg = ip + " : " + latency + "ms";
            return msg;
        }

    }


}
