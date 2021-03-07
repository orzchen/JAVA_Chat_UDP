
import java.net.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UDPClient implements Runnable {
    private InetAddress clientIP ;//返回本地地址
    private InetAddress clientPort ;//返回本地地址
    private DatagramSocket ds = null;
    private String linkName = ClientGUI.linkPanel.getName();
    private String ip = ClientGUI.linkPanel.getIP();
    private int port = ClientGUI.linkPanel.getPort();
    private String tfMsg = ClientGUI.inPanel.getInArea();
    private StringBuffer taMsg = new StringBuffer();  // 接受服务端的信息内容

    public UDPClient() {
        try {
            ds = new DatagramSocket();
            InetAddress add = InetAddress.getByName(ip);  // 连接服务端的IP
            clientIP = InetAddress.getLocalHost();
            ds.connect(add,port); // 连接服务端
            //特意给服务器发送一个包，告诉客户端其地址
            String msg = "@" + linkName + " 登录！\n";
            byte[] data = msg.getBytes();
            DatagramPacket dp = new DatagramPacket(data,data.length);
            ds.send(dp);

            new Thread(this).start(); // 创建一个线程
        }
        catch (Exception ex) {
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                byte[] data = new byte[10240];
                DatagramPacket dp = new DatagramPacket(data,data.length);
                ds.receive(dp); // 接收服务端信息
                String msg = new String(dp.getData(),0,dp.getLength());
                taMsg.append("<|>" + msg + "  (" + getTime() + ")\n"); //添加内容
                ClientGUI.msgPanel.setMsgArea(String.valueOf(taMsg));
            }
        }
        catch (Exception ex) {
        }
    }

    public DatagramSocket getDS() {
        return ds;
    }

    public String getTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
        Date date = new Date();// 获取当前时间
        return sdf.format(date);
    }
}