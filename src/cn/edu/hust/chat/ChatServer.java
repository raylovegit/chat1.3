package cn.edu.hust.chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
	boolean started = false; //服务端是否启动
	ServerSocket ss = null;
	
	List<Client> clients = new ArrayList<Client>(); // 保存客户端的 socket
	
	public static void main(String[] args) {
		new ChatServer().start();
	}
	
	public void start() {
		
		try {
			ss = new ServerSocket(8888); // 监听8888端口
		}catch (BindException e) {
			System.out.println("端口已经被占用！");
			System.exit(0);
		}catch (IOException e){
			e.printStackTrace();
		}
		try{
			started = true;
			while(started) { //服务端是否启动
				Socket s = ss.accept();
				System.out.println("一个用户上线了 !");	
				Client c = new Client(s);
				clients.add(c);
				new Thread(new Client(s)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				  ss.close();
			} catch (IOException e) {
				System.out.println("关闭失败！");
				e.printStackTrace();
			}
		}
	}

	class Client implements Runnable {
		private Socket s;
		private DataInputStream dis;
		private DataOutputStream dos;
		boolean isConnected = false;//客户端是否连接
		
		public Client(Socket s) {
			this.s = s;
			try {
				this.dis = new DataInputStream(s.getInputStream());
				this.dos = new DataOutputStream(s.getOutputStream());
				isConnected = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void sendMes(String str) {
			try {
				dos.writeUTF(str);
			} catch (IOException e) {
				clients.remove(this);
				System.out.println("一个用户退出了！服务器从list 里面去除了它的socket");
			}
		}
		
		@Override
		public void run() {
			try {
					while(isConnected) {
						String receivedMes = dis.readUTF();
System.out.println(receivedMes);
						for(Client c:clients) 
							c.sendMes(receivedMes); //从客户端接收消息 然后转发给每个客户端
					}
				} catch (EOFException e) {
					System.out.println("一个客户端"+Thread.currentThread().getName()+"已经关闭！");
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						if(dis!=null) dis.close();
						if(dos!=null) dos.close();
						if(s!=null) s.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
		}
		
	}
}
