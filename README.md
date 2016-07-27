# shadowsocks-android-java

Last release: [Download](https://github.com/dawei101/shadowsocks-android-java/raw/v1.0/app/shadowsocks-vpnmore.apk)

sha1 hash: 8cf9fde49f2f5b875d4bfd0968dec04bb788d326

### 关于

本版本为shadowsocks android版的纯java版本

代码多整理自 [smartproxy](https://github.com/hedaode/SmartProxy) 和 [shadowsocks-java](https://github.com/blakey22/shadowsocks-java)

本shadowsocks-android的特点继承了SmartProxy的优点, 体积小,耗电低,设置保持最简单的方式

shadowsocks格式：

```
ss://method:password@host:port
ss://base64encode(method:password@host:port)
```

其中代码保留了SmartProxy对http(s)代理的支持, 使用时将配置链接填写标准http(s)代理格式即可.

http(s)代理格式
```
http(s)://(username:passsword)@host:port
```
支持的加密类型：
```
bf-cfb
seed-cfb
aes-128-cfb
aes-192-cfb
aes-256-cfb
aes-128-ofb
aes-192-ofb
aes-256-ofb
camellia-128-cfb
camellia-192-cfb
camellia-256-cfb
```

如有问题请及时反馈

### 兄弟版本

##### [Shadowsocks android(Scala版)](https://github.com/shadowsocks/shadowsocks-android)

Scala版对很多开发者有些门槛, 所以本版本是不错的选择.


#### LICENSE

[Apache License](./LICENSE)



