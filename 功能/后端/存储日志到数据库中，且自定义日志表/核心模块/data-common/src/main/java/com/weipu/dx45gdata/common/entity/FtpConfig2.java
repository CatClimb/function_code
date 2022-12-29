package com.weipu.dx45gdata.common.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ftp2")
@Data
public class FtpConfig2 {
    //sftp ip
    private String host;
    //sftp 端口
    private int port;
    //sftp 用户名
    private String username;
    //sftp 密码
    private String password;
    //sftp 下载文件保存位置
    private String savePath;
    //sftp 连接超时时间
    private String timeout;
    //sftp 文件目录
    private String dir;
}
