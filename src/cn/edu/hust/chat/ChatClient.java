package cn.edu.hust.chat;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClient extends Frame {
	
	private static final long serialVersionUID = -7821891232338381374L;
	private TextArea textArea = new TextArea(); // 显示已发送内容
	private TextField textField = new TextField(); // 发送消息框
	private Socket socket ;
	private DataOutputStream dos;
	private DataInputStream dis; //输入流   从服务器端接收
	private boolean isConnected = false;

	public void launchFrame() {
		this.setTitle("chat client");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage("qq.jpg"));
		this.setBounds(400, 200, 500, 400); // x, y, width, height
		this.add(textArea, BorderLayout.NORTH);
		this.add(textField, BorderLayout.SOUTH);
		this.pack();
		this.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				disconnect();
				System.exit(0);
			}
		});
		textField.addActionListener(new TFListener());
		this.setVisible(true);
	}
	
	public void connect() {
System.out.println("Try to connect the server.......");
		try {
			socket = new Socket("127.0.0.1", 8888);
			isConnected = true;
			dos = new DataOutputStream(socket.getOutputStream());
			dis = new DataInputStream(socket.getInputStream());
			new Thread(new RecvThread()).start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() { //关闭客户端之前 要关掉dos 和 socket
		try {
			dos.close();
			dis.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		ChatClient cc = new ChatClient();
		cc.connect(); // 连接服务器
		cc.launchFrame(); // 启动客户端界面
	}

	private class TFListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String mes = textField.getText();
//			textArea.setText(mes);
			textField.setText("");
			try {
//				DataOutputStream os = new DataOutputStream(socket.getOutputStream());
				dos.writeUTF(mes);
				dos.flush();
//				dos.close();  
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	private class RecvThread implements Runnable {

		@Override
		public void run() {
			try {
					while(isConnected) {
						String str;
							str = dis.readUTF();
		//					System.out.println(str);
							textArea.setText(textArea.getText()+str+"\n");
					}
				} catch (SocketException e) {
					System.out.println("我下线了。。");
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		
	}
}


