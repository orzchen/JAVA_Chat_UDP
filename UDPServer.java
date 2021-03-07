/*
 * 实现服务器端
 */
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.SplittableRandom;
/*
* 一个重要的知识，在UDPClient类和UDPServer类中都使用了多线程编程，每个类都多只创建了一个线程
* */

public class UDPServer implements Runnable {
    private InetAddress serverIP = InetAddress.getLocalHost();//返回本地地址
    private DatagramSocket ds = null; // UDP协议的Socket
    private int port = ServerGUI.serverPort;
    private ArrayList<SocketAddress> clients = new ArrayList<>();  //保存好客户端的地址和端口
    private ArrayList<String> clientsName = new ArrayList<>();  //保存好客户端的name

    public UDPServer() throws Exception { //异常处理全抛
        try {
            ds = new DatagramSocket(port); // 将ds对象绑定到本机默认IP地址、指定端口
            Thread t = new Thread(this); // 创建并启动一个线程
            t.start();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() { // 当通过start方法启动一个线程之后，便进入run方法体去执行具体的任务。
        try {
            while(true) { // 多线程
                byte[] data = new byte[10240]; // 接受的数据
                DatagramPacket dp = new DatagramPacket(data,data.length); // 数据报 数据内容
                ds.receive(dp); //接受消息，所有的消息都在data字节数组中
                //维护地址集合
                SocketAddress cAddress = dp.getSocketAddress(); // 返回该数据报的发送主机的SocketAddress
                if(!clients.contains(cAddress)) // 对clients集合执行遍历，判断是否在集合中
                    clients.add(cAddress);  // 添加到集合中
//                System.out.println(clients); // [/127.0.0.1:53337]
                this.sendToAll(dp);  // /127.0.0.1:52416
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendToAll(DatagramPacket dp) throws Exception { // 使用此方法来对集合中的socket发送数据报
        for(SocketAddress sa:clients) {
            DatagramPacket datagramPacket = new DatagramPacket(dp.getData(),dp.getLength(),sa);
            ds.send(datagramPacket);
        }
    }

    public String getServerIP() {
        return serverIP.getHostAddress(); // 获取此IP地址字符串
    }
}
