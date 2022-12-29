package com.weipu.dx45gdata.common.util;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;

/**
 * sftp连接工具类
 */
@Slf4j
public class SftpUtils {

    private static ChannelSftp sftp = null;
    private static Session session = null;

    // 登录
    public static ChannelSftp login(String username,String password,String host,int port) throws JSchException, SftpException {
        JSch jSch = new JSch();
        // 设置用户名和主机，端口号一般都是22
        session = jSch.getSession(username, host, port);
        // 设置密码
        session.setPassword(password);
        Properties config = new Properties();
        //严格主机密钥检查
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        //开启sshSession链接

        session.connect();
        log.info("session会话是否连接成功 session.isConnected() :{}",session.isConnected());
        //获取sftp通道
        Channel channel = session.openChannel("sftp");
        channel.connect();
        sftp = (ChannelSftp) channel;
        return sftp;
    }

    // 退出登录
    public static void logout() {
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }



}
