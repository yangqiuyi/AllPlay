package com.example.dell.newitsme.network;


import java.net.InetAddress;

public class DNS {

    public static final String TAG = "DNS";

    public static String[] resolve(String hostName) {
        try {
            InetAddress[] ips = InetAddress.getAllByName(hostName);
            String[] res = new String[ips.length];
            for (int i=0;i<ips.length;i++)
            {
                res[i] = ips[i].getHostAddress();
            }
            return res;
        }catch (Exception e) {
            e.printStackTrace();
        }

        String[] tmp = new String[1];
        tmp[0] = hostName;
        return tmp;
    }

}
