/*
 * 实现客户端的图形界面
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.DatagramPacket;

public class ClientGUI {
    public static UDPClient udpClient;
    public static LinkPanel linkPanel = new LinkPanel();
    public static MsgPanel msgPanel = new MsgPanel();
    public static InPanel inPanel = new InPanel();
    public static SendPanel sendPanel = new SendPanel();

    public static void main(String[] args) {
        JFrame jf = new JFrame("聊天客户端");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setSize(750,500);
        jf.setResizable(false);  // 设置是否可缩放窗口大小

        Box vBox = Box.createVerticalBox(); // 创建一个垂直盒子容器

        vBox.add(linkPanel);
        vBox.add(msgPanel);
        vBox.add(inPanel);
        vBox.add(sendPanel);

        jf.setContentPane(vBox);// 把垂直箱容器作为内容面板设置到窗口
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);

    }

}
class LinkPanel extends JPanel { // 连接服务端的面板
    private JLabel name, ip, port, tips;
    private JTextField nameTF, ipTF, portTF;
    private JButton link;

    public LinkPanel() {
        name = new JLabel("Name: ");
        ip = new JLabel("IP: ");
        port = new JLabel("端口: ");
        tips = new JLabel("填入信息再登录！");
        nameTF = new JTextField("",10);
        ipTF = new JTextField("127.0.0.1",10);
        portTF = new JTextField("9999",10);
        link = new JButton("连接");
        link.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框
        link.addActionListener(new linkListener());

        add(name);
        add(nameTF);
        add(ip);
        add(ipTF);
        add(port);
        add(portTF);
        add(link);
        add(tips);

    }
    public String getName() {
        return nameTF.getText();
    }
    public String getIP() {
        return ipTF.getText();
    }
    public int getPort() {
        return Integer.parseInt(portTF.getText());
    }
    class linkListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == link) {
//                new UDPClient();
                if (!nameTF.getText().equals("") && !ipTF.getText().equals("") && !portTF.getText().equals("")) {
                    nameTF.setEditable(false);
                    ipTF.setEditable(false);
                    portTF.setEditable(false);
                    link.setEnabled(false);
                    ClientGUI.udpClient = new UDPClient(); // 创建客户端UDP

                    ClientGUI.sendPanel.setSendBt(true);
                    ClientGUI.sendPanel.setClearBt(true);

                    tips.setText("已成功连接服务！");
                }
                else
                    tips.setText("信息填入不完整！");
            }
        }
    }

}
class MsgPanel extends JPanel { // 信息显示的滚动面板
    private JLabel tips = new JLabel(" 信息显示区 "), tips2 = new JLabel(" 当前在线列表");
    private static JTextArea msgArea = new JTextArea(15,50); // 文本输入区域
    private JScrollPane sp;
    private JPanel panelMsg, panelList, panelList2;
    private String[] columnNames = new String[]{"Name", "IP", "端口"};
    private String[][] rowData ;
    private Box hBox = Box.createHorizontalBox(); // 创建一个横向的盒子容器
    private JTable table;
    public MsgPanel() {
        tips.setSize(1,1);

        msgArea.setLineWrap(true);  // 自动换行
        msgArea.setBackground(new Color(245,246,247));
        msgArea.setEditable(false); // 不允许编辑信息区域
        sp = new JScrollPane(msgArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        panelMsg = new JPanel(new BorderLayout());
        panelMsg.add(tips,BorderLayout.NORTH);
        panelMsg.add(sp,BorderLayout.SOUTH);

        panelList = new JPanel(new BorderLayout());
        panelList2 = new JPanel(new BorderLayout());
        panelList2.add(tips2, BorderLayout.NORTH);

        rowData = new String[][]{};
        table = new JTable(rowData, columnNames); // 在线列表
        table.setEnabled(false); // 是否能操作表格
        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        panelList.add(table.getTableHeader(), BorderLayout.NORTH);
        // 把 表格内容 添加到容器中心
        panelList.add(table, BorderLayout.CENTER);
        panelList2.add(panelList, BorderLayout.CENTER);

        hBox.add(panelMsg);
        hBox.add(panelList2);
        add(hBox);
    }
    public void setMsgArea(String s) {
        msgArea.setText(s);
    }
}
class InPanel extends JPanel { // 输入区域的滚动面板
    private JLabel tips = new JLabel("信息输入区");
    private JScrollPane sp;
    private static JTextArea inArea = new JTextArea(5,70); // static 引用
    public InPanel() {
        inArea.setLineWrap(true);
        inArea.setBackground(new Color(245,246,247));
        inArea.addKeyListener(new KeyAdapter() {  //文本框回车监听事件
            @Override
            public void keyTyped(KeyEvent e) {
                if((char)e.getKeyChar()==KeyEvent.VK_ENTER) {
                    try {
                        String msg = "【" + ClientGUI.linkPanel.getName() + "】 : " + ClientGUI.inPanel.getInArea();
                        byte[] data = msg.getBytes();
                        DatagramPacket dp = new DatagramPacket(data,data.length);
                        ClientGUI.udpClient.getDS().send(dp);
                        InPanel.setInArea(""); // 发送消息后清除输入框
                    }
                    catch (Exception ex) {
                    }
                }
            }
        });
        sp = new JScrollPane(inArea,ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(tips);
        add(sp);
    }
    public static void setInArea(String s) {
        inArea.setText(s);
    }
    public String getInArea() {
        return inArea.getText();
    }
}
class SendPanel extends JPanel { // Send 和 退出 的面板
    private JButton sendBt, clearBt, exitBt;
    public SendPanel() {
        sendBt = new JButton("Send");
        clearBt = new JButton("清屏");
        exitBt = new JButton("EXIT");
        sendBt.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框
        clearBt.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框
        exitBt.setFocusPainted(false); //不绘制图片或文字周围的焦点虚框
        sendBt.addActionListener(new btListener());
        clearBt.addActionListener(new btListener());
        exitBt.addActionListener(new btListener());

        sendBt.setEnabled(false);
        clearBt.setEnabled(false);
        add(sendBt);
        add(clearBt);
        add(exitBt);
    }

    class btListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) { // 动作监听，向服务端发送信息
            if (e.getSource() == sendBt) {
                try {
                    String msg = "【" + ClientGUI.linkPanel.getName() + "】 : " + ClientGUI.inPanel.getInArea();
                    byte[] data = msg.getBytes();
                    DatagramPacket dp = new DatagramPacket(data,data.length);
                    ClientGUI.udpClient.getDS().send(dp);
                    InPanel.setInArea(""); // 发送消息后清除输入框
                }
                catch (Exception ex) {
                }
            }
            else if (e.getSource() == clearBt) {
                InPanel.setInArea(""); // 清除输入
            }
            else if (e.getSource() == exitBt)
                System.exit(0);
        }
    }
    public void setSendBt(boolean b) {
        sendBt.setEnabled(b);
    }
    public void setClearBt(boolean b) {
        clearBt.setEnabled(b);
    }

}
