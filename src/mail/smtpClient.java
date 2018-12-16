package mail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.Date;

public class smtpClient {

    private String userAdress;
    private String userPassword;
    private String userNickname;

    private String smtpServer;
    private String Pop3Server;

    public smtpClient(String userAdress, String userPassword, String userNickname) {
        this.userAdress = userAdress;
        this.userPassword = userPassword;
        this.userNickname = userNickname;
    }

    public static void main(String[] args) throws UnknownHostException, IOException {
        smtpClient client = new smtpClient("", "", "");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("这是一个简单的smtp客户端,支持QQ邮箱和163邮箱\n请首先验证您的邮箱和密码：");
            System.out.println("请输入邮箱地址，并以回车键结束：");
            client.setUserAdress(bufferedReader.readLine());
            if (!client.parseServer()) {
                System.out.println("邮箱输入错误或邮箱不受支持" + client.Pop3Server);
                client.setUserAdress(bufferedReader.readLine());
                continue;
            }
            System.out.println("请输入密码，并以回车键结束：");
            client.setUserPassword(bufferedReader.readLine());
            client.serve();
        }
    }

    private void serve() {
        int choice = 0;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.println("请选择您要进行的操作（输入序号并回车）：");
            System.out.println("1.发送邮件");
            System.out.println("2.接收邮件");
            System.out.println("3.退出账号");
            try {
                choice = Integer.parseInt(bufferedReader.readLine());
            } catch (IOException e) {
                e.printStackTrace();
            }
            switch (choice) {
                case 1:
                    this.sendMail();
                    break;
                case 2:
                    this.recieveMail();
                    break;
                case 3:
                    return;
            }
            continue;
        }
    }

    private void sendMail() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String in="";
        MailSender mail = new MailSender();
        mail.setSmtpServer(this.smtpServer);
        mail.setFromMail(this.userAdress);
        try {
            System.out.println("请输入接收方的邮件地址\n每个地址用回车隔开\n以输入q/Q结束：");
            while(true){
                in = bufferedReader.readLine();
                if(!(in.equals("q")||in.equals("Q")))
                    mail.addToMail(in);
                else
                    break;
            }
            System.out.println("请输入您要使用的用户名：");
            mail.setUserName(bufferedReader.readLine());
            System.out.println("请输入邮件主题：");
            mail.setMailSubject(bufferedReader.readLine());
            System.out.println("请输入邮件内容：");
            mail.setMailContent(bufferedReader.readLine()+"\n\nsent by caoyu's desktop smtp client at:" + new Date().toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mail.setPassword(this.userPassword);
        mail.setDebug(false);
        mail.send();
        System.out.println("发送结束\n\n");
    }

    private void recieveMail() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        MailReciever mailreciever = null;
        try {
            mailreciever = new MailReciever(this.Pop3Server, 110);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("为您拉取的邮件列表：\n");
        mailreciever.listMail(this.userAdress, this.userPassword);
        System.out.println("请输入要查看的邮件序号并回车：");

        try {
            int mailNum = Integer.parseInt(bufferedReader.readLine());
            mailreciever.retrieveMail(mailNum,this.userAdress,this.userPassword);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("查看结束\n\n");
    }

    public boolean parseServer() {
        String mailAdress = this.userAdress;
        String mailAdressSufix = mailAdress.substring(mailAdress.indexOf("@") + 1);
        if (mailAdressSufix.equals("qq.com") || mailAdressSufix.equals("163.com")) {
            this.Pop3Server = "pop3." + mailAdressSufix;
            this.smtpServer = "smtp." + mailAdressSufix;
            return true;
        }
        return false;
    }

    public String getUserAdress() {
        return userAdress;
    }

    public void setUserAdress(String userAdress) {
        this.userAdress = userAdress;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
