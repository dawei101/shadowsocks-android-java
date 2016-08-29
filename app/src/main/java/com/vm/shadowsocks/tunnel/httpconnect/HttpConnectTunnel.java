package com.vm.shadowsocks.tunnel.httpconnect;

import com.vm.shadowsocks.core.ProxyConfig;
import com.vm.shadowsocks.tunnel.Tunnel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;

public class HttpConnectTunnel extends Tunnel {

    private boolean m_TunnelEstablished;
    private HttpConnectConfig m_Config;

    public HttpConnectTunnel(HttpConnectConfig config, Selector selector) throws IOException {
        super(config.ServerAddress, selector);
        m_Config = config;
    }

    @Override
    protected void onConnected(ByteBuffer buffer) throws Exception {
        String request = String.format("CONNECT %s:%d HTTP/1.0\r\nProxy-Connection: keep-alive\r\nUser-Agent: %s\r\nX-App-Install-ID: %s\r\n\r\n",
                m_DestAddress.getHostName(),
                m_DestAddress.getPort(),
                ProxyConfig.Instance.getUserAgent(),
                ProxyConfig.AppInstallID);

        buffer.clear();
        buffer.put(request.getBytes());
        buffer.flip();
        if (this.write(buffer, true)) {//发送连接请求到代理服务器
            this.beginReceive();//开始接收代理服务器响应数据
        }
    }

    void trySendPartOfHeader(ByteBuffer buffer) throws Exception {
        int bytesSent = 0;
        if (buffer.remaining() > 10) {
            int pos = buffer.position() + buffer.arrayOffset();
            String firString = new String(buffer.array(), pos, 10).toUpperCase();
            if (firString.startsWith("GET /") || firString.startsWith("POST /")) {
                int limit = buffer.limit();
                buffer.limit(buffer.position() + 10);
                super.write(buffer, false);
                bytesSent = 10 - buffer.remaining();
                buffer.limit(limit);
                if (ProxyConfig.IS_DEBUG)
                    System.out.printf("Send %d bytes(%s) to %s\n", bytesSent, firString, m_DestAddress);
            }
        }
    }


    @Override
    protected void beforeSend(ByteBuffer buffer) throws Exception {
        if (ProxyConfig.Instance.isIsolateHttpHostHeader()) {
            trySendPartOfHeader(buffer);//尝试发送请求头的一部分，让请求头的host在第二个包里面发送，从而绕过机房的白名单机制。
        }
    }

    @Override
    protected void afterReceived(ByteBuffer buffer) throws Exception {
        if (!m_TunnelEstablished) {
            //收到代理服务器响应数据
            //分析响应并判断是否连接成功
            String response = new String(buffer.array(), buffer.position(), 12);
            if (response.matches("^HTTP/1.[01] 200$")) {
                buffer.limit(buffer.position());
            } else {
                throw new Exception(String.format("Proxy server responsed an error: %s", response));
            }

            m_TunnelEstablished = true;
            super.onTunnelEstablished();
        }
    }

    @Override
    protected boolean isTunnelEstablished() {
        return m_TunnelEstablished;
    }

    @Override
    protected void onDispose() {
        m_Config = null;
    }


}
