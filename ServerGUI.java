/*
 * 实现服务器端的图形界面
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI {
    public static int serverPort = 9999;
    public static infoPanel infop = new infoPanel();
    public static ipPanel ipp = new ipPanel();
    public static portPanel pop = new portPanel();

    public static void main(String[] args) {
        JFrame jf = new JFrame("服务器端");
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(400,300);
        jf.setResizable(false); // 不可缩放大小

        Box vBox = Box.createVerticalBox(); // 创建一个垂直盒子容器
        jf.setContentPane(vBox);
        vBox.add(infop);
        vBox.add(ipp);
        vBox.add(pop);
        vBox.add(new btPanel());

        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
    }
}

class infoPanel extends JPanel {
    private JLabel info;
    public infoPanel() {
        info = new JLabel("服务端信息 : 未启动服务！可以修改服务端口后启动！");
        add(info);
    }
    public void setInfo(String s) {
        info.setText(s);
    }
}
class ipPanel extends JPanel {
    private JLabel ipLabel;
    private JTextField ipText;
    public ipPanel() {
        ipText = new JTextField(25);
        ipText.setEditable(false);
        ipLabel = new JLabel("IP: ");
        add(ipLabel);
        add(ipText);
    }
    public void setIpText(String s) {
        ipText.setText(s);
    }
}
class portPanel extends JPanel {
    private JLabel portLabel;
    private JTextField portText;
    public portPanel() {
        portLabel = new JLabel("端口: ");
        portText = new JTextField(5);
        portText.setText(Integer.toString(ServerGUI.serverPort)); // int to string
        add(portLabel);
        add(portText);
    }
    public String getPortText() {
        return portText.getText();
    }
    public void setPortTextEdit(boolean b) {
        portText.setEditable(b);
    }
}
class btPanel extends JPanel {
    private JButton serverStar, serverStop;
    public btPanel() {
        serverStar = new JButton("启动服务");
        serverStop = new JButton("关闭服务");
        serverStar.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框
        serverStop.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框

        serverStar.addActionListener(new btListener());
        serverStop.addActionListener(new btListener());

        add(serverStar);
        add(serverStop);
    }

    private class btListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == serverStar) {
                try {
                    ServerGUI.serverPort = Integer.parseInt(ServerGUI.pop.getPortText()); //String to int

//                    new UDPServer();
                    ServerGUI.ipp.setIpText(" 127.0.0.1  |  localhost  | " + new UDPServer().getServerIP()); // 获得本机IP且开启线程
                    ServerGUI.infop.setInfo("服务端信息 : 成功启动服务！服务端口不可修改！");
                    serverStar.setEnabled(false); // 服务开启后，按钮不可用
                    ServerGUI.pop.setPortTextEdit(false); // 服务开启后，不可编辑

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            else if (e.getSource() == serverStop) {
                System.exit(0); // 关闭
            }
        }
    }
}