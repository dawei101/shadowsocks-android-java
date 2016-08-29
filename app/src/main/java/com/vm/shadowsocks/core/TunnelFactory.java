package com.vm.shadowsocks.core;

import com.vm.shadowsocks.tunnel.Config;
import com.vm.shadowsocks.tunnel.RawTunnel;
import com.vm.shadowsocks.tunnel.Tunnel;
import com.vm.shadowsocks.tunnel.httpconnect.HttpConnectConfig;
import com.vm.shadowsocks.tunnel.httpconnect.HttpConnectTunnel;
import com.vm.shadowsocks.tunnel.shadowsocks.ShadowsocksConfig;
import com.vm.shadowsocks.tunnel.shadowsocks.ShadowsocksTunnel;

import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TunnelFactory {

    public static Tunnel wrap(SocketChannel channel, Selector selector) {
        return new RawTunnel(channel, selector);
    }

    public static Tunnel createTunnelByConfig(InetSocketAddress destAddress, Selector selector) throws Exception {
        if (destAddress.isUnresolved()) {
            Config config = ProxyConfig.Instance.getDefaultTunnelConfig(destAddress);
            if (config instanceof HttpConnectConfig) {
                return new HttpConnectTunnel((HttpConnectConfig) config, selector);
            } else if (config instanceof ShadowsocksConfig) {
                return new ShadowsocksTunnel((ShadowsocksConfig) config, selector);
            }
            throw new Exception("The config is unknow.");
        } else {
            return new RawTunnel(destAddress, selector);
        }
    }

}
