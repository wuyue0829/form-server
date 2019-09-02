package com.tssk.form.utils.question;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.Properties;


import com.sun.mail.util.MailSSLSocketFactory;
import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.tssk.form.consts.MailConfig;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtil {
    private static final String HOST = MailConfig.host;
    private static final Integer PORT = MailConfig.port;
    private static final String USERNAME = MailConfig.userName;
    private static final String PASSWORD = MailConfig.passWord;
    private static final String emailForm = MailConfig.emailForm;
    private static final String timeout = MailConfig.timeout;
    private static final String personal = MailConfig.personal;
    private static final String subject = MailConfig.subject;
    private static final String html = MailConfig.html;

    /**
     * 邮件发送器
     *
     * @return 配置好的工具
     *//*
    private static JavaMailSenderImpl createMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(HOST);
        sender.setPort(PORT);
        sender.setUsername(USERNAME);
        sender.setPassword(PASSWORD);
        sender.setDefaultEncoding("Utf-8");
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        Properties props = new Properties();
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.host",HOST);
        props.put("mail.smtp.username", USERNAME);
        props.put("mail.smtp.password", PASSWORD);
        Session session = Session.getDefaultInstance(props,  new Authenticator() {
            //身份认证
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });
        sender.setJavaMailProperties(props);
        return sender;
    }*/

    /**
     * 发送邮件
     *
     * @param to 接受人
     * @param html 发送内容
     * @throws MessagingException 异常
     * @throws UnsupportedEncodingException 异常
     */
    public static void sendMail(String to, String html, String subject, String title) throws javax.mail.MessagingException {
        // 2.创建一个Message，它相当于是邮件内容
        Message message = new MimeMessage(createMailSender());
        //设置发送者
        message.setFrom(new InternetAddress(USERNAME));
        //设置发送方式与接收者
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        //设置邮件主题
        message.setSubject(subject);
        //设置邮件内容
        message.setContent(html, "text/html;charset=utf-8");
        // 3.创建 Transport用于将邮件发送
        Transport.send(message);

    }


    private static Session  createMailSender() {
        // 1.创建一个程序与邮件服务器会话对象 Session
        Properties props = new Properties();
        //设置发送的协议
        props.setProperty("mail.transport.protocol", "SMTP");
        props.setProperty("mail.smtp.auth", "true");// 指定验证为true
        //设置发送邮件的服务器
        props.setProperty("mail.host", "smtp.qq.com");
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);
        // 创建验证器
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                //设置发送人的帐号和密码
                return new PasswordAuthentication("forever111wuyue@foxmail.com", "bklcaaffaabcbjig");
            }
        };
        Session session = Session.getInstance(props, auth);
        return  session;
    }
}