package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import sun.misc.BASE64Encoder;

public class MailSender {
    public static void main(String[] args) throws IOException {
        MailSender mail = new MailSender();
        mail.setSmtpServer("smtp.qq.com");
        mail.setFromMail("763433257@qq.com");
        mail.addToMail("caoyupersonal@163.com");
        mail.setUserName("CaoYu");
        mail.setPassword("mmzxvefsoqvvbdfh");
        mail.setMailSubject("测试邮件");
        mail.setMailContent("sent by caoyu's desktop smtp client at:" + new Date().toString());
        mail.setDebug(true);
        mail.send();
        System.out.println("程序结束");
    }

    //邮件主题
    private String mailSubject;
    //从此地址发出
    private String senderMailAdress;
    //用户名
    private String senderUserName;
    //登录密码...授权码
    private String senderLoginPassword;
    //SMTP 服务器地址
    private String smtpServer;
    //SMTP 服务器端口
    private int smtpServerPort = 25;
    //发送到 recieverMailList 中的所有地址
    private List<String> recieverMailList;
    //邮件内容
    private String mailContent;
    //是否显示日志
    private boolean debug;

    //添加该邮件需要发送的邮件地址
    public void addToMail(String mail) {
        if (recieverMailList == null)
            recieverMailList = new ArrayList<String>();
        recieverMailList.add(mail);
    }

    //发送邮件
    public void send() {
        if (smtpServer == null) {
            throw new RuntimeException("smtpServer 不能为空");
        }
        if (senderUserName == null) {
            throw new RuntimeException("senderUserName 不能为空");
        }
        if (senderLoginPassword == null) {
            throw new RuntimeException("senderLoginPassword 不能为空");
        }
        if (senderMailAdress == null) {
            throw new RuntimeException("senderMailAdress 不能为空");
        }
        if (recieverMailList == null || recieverMailList.isEmpty()) {
            throw new RuntimeException("recieverMailList 不能为空");
        }
        if (mailContent == null || recieverMailList.isEmpty()) {
            throw new RuntimeException("mailContent 不能为空");
        }

        //建立socket套接字
        Socket socket = null;
        InputStream in = null;
        OutputStream out = null;
        try {
            socket = new Socket(smtpServer, smtpServerPort);
            socket.setSoTimeout(3000);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException("连接到 " + smtpServer + ":" + smtpServerPort + " 失败", e);
        }
        //获取读写流
        BufferedReaderProxy reader = new BufferedReaderProxy(new InputStreamReader(in), debug);
        PrintWriterProxy writer = new PrintWriterProxy(out, debug);

        //与邮件服务器进行交互，登录、发送数据
        reader.showResponse();
        writer.println("HELO " + smtpServer);
        reader.showResponse();
        writer.println("AUTH LOGIN");
        reader.showResponse();
        writer.println(new String(new BASE64Encoder().encode(senderMailAdress.getBytes())));
        reader.showResponse();
        writer.println(new String(new BASE64Encoder().encode(senderLoginPassword.getBytes())));
        reader.showResponse();
        writer.println("MAIL FROM:<" + senderMailAdress + ">");
        reader.showResponse();
        System.out.println(recieverMailList);
        for (String mail : recieverMailList) {
            writer.println("RCPT TO:<" + mail + ">");
            reader.showResponse();
        }

        writer.println("DATA");
        writer.println("Content-Type:text/html");
        if (mailSubject != null) {
            writer.println("Subject:" + mailSubject);
        }
        writer.println("From:" + senderMailAdress);
        writer.print("To:");
        for (String mail : recieverMailList) {
            writer.print(mail + "; ");
        }
        writer.println();
        writer.println();
        writer.println(mailContent);
        writer.println(".");
        reader.showResponse();
        writer.println("QUIT");
        reader.showResponse();
        try {
            socket.close();
        } catch (IOException e) {
            System.err.println("发送邮件完成，关闭 Socket 出错：" + e.getMessage());
        }
    }

    public String getMailSubject() {
        return mailSubject;
    }

    public void setMailSubject(String mailSubject) {
        this.mailSubject = mailSubject;
    }

    public String getFromMail() {
        return senderMailAdress;
    }

    public void setFromMail(String senderMailAdress) {
        this.senderMailAdress = senderMailAdress;
    }

    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public int getSmtpServerPort() {
        return smtpServerPort;
    }

    public void setSmtpServerPort(int smtpServerPort) {
        this.smtpServerPort = smtpServerPort;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent;
    }

    public List<String> getToMail() {
        return recieverMailList;
    }

    public void setToMail(List<String> recieverMailList) {
        this.recieverMailList = recieverMailList;
    }

    public String getUserName() {
        return senderUserName;
    }

    public void setUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getPassword() {
        return senderLoginPassword;
    }

    public void setPassword(String senderLoginPassword) {
        this.senderLoginPassword = senderLoginPassword;
    }

    public boolean getDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    static class PrintWriterProxy extends PrintWriter {
        private boolean showRequest;

        public PrintWriterProxy(OutputStream out, boolean showRequest) {
            super(out, true);
            this.showRequest = showRequest;
        }

        @Override
        public void println() {
            if (showRequest)
                System.out.println();
            super.println();
        }

        public void print(String s) {
            if (showRequest)
                System.out.print(s);
            super.print(s);
        }
    }

    static class BufferedReaderProxy extends BufferedReader {
        private boolean showResponse = true;

        public BufferedReaderProxy(Reader in, boolean showResponse) {
            super(in);
            this.showResponse = showResponse;
        }

        public void showResponse() {
            try {
                String line = readLine();
                String number = line.substring(0, 3);
                int num = -1;
                try {
                    num = Integer.parseInt(number);
                } catch (Exception e) {
                }
                if (num == -1) {
                    throw new RuntimeException("响应信息错误 : " + line);
                } else if (num >= 400) {
                    throw new RuntimeException("发送邮件失败 : " + new String(line.getBytes("UTF-8")));
                }
                if (showResponse) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("获取响应失败");
            }
        }

    }
}