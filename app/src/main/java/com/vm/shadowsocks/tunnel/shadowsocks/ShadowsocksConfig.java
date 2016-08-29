package com.vm.shadowsocks.tunnel.shadowsocks;

import android.net.Uri;
import android.util.Base64;

import com.vm.shadowsocks.tunnel.Config;

import java.net.InetSocketAddress;

public class ShadowsocksConfig extends Config {
    public String EncryptMethod;
    public String Password;

    public static ShadowsocksConfig parse(String proxyInfo) throws Exception {
        ShadowsocksConfig config = new ShadowsocksConfig();
        Uri uri = Uri.parse(proxyInfo);
        if (uri.getPort() == -1) {
            String base64String = uri.getHost();
            proxyInfo = "ss://" + new String(Base64.decode(base64String.getBytes("ASCII"), Base64.DEFAULT));
            uri = Uri.parse(proxyInfo);
        }

        String userInfoString = uri.getUserInfo();
        if (userInfoString != null) {
            String[] userStrings = userInfoString.split(":");
            config.EncryptMethod = userStrings[0];
            if (userStrings.length >= 2) {
                config.Password = userStrings[1];
            }
        }
        if (!CryptFactory.isCipherExisted(config.EncryptMethod)) {
            throw new Exception(String.format("Method: %s does not support", config.EncryptMethod));
        }
        config.ServerAddress = new InetSocketAddress(uri.getHost(), uri.getPort());
        return config;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return this.toString().equals(o.toString());
    }

    @Override
    public String toString() {
        return String.format("ss://%s:%s@%s", EncryptMethod, Password, ServerAddress);
    }
}
