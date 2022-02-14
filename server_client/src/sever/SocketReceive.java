package sever;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

//import org.apache.log4j.Logger;
//

public class SocketReceive {

	public static void main(String[] args) {

		Socket socket = null;
		ServerSocket server = null;
		int No =0;

		System.out.println("start.");

		// 入力ソケットを作成して、送信を待ち受ける
		try {
			// ソケット作成
			server = new ServerSocket(9000);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("server側Socketエラー");
		}

		// ここループ スレッドプール
		// client側からの受信を常に待つ
		while (true) {
			
			try {				
				
				socket = server.accept();

				Thread thread = new Thread(new Server_Input_Output(socket,No));
				thread.start();
				No++;
				
			} catch (SocketException e) {
				e.printStackTrace();
				System.out.println("ソケットエラー");
			} catch (IOException e) {
				System.out.println("SocketReceive_occured SocketException.");
				e.printStackTrace();

			}
		
//				finally {
//				try {
//
//					server.close();
//
//				} catch (IOException e) {
//					// "socket.server.クローズのエラー
//					System.out.println("socket.server.クローズ;occured IOException.");
//				}
//
//			}

//		System.out.println("end.");

		
		}
	}

}

//Logger logger = Logger.getLogger("Logger");
////
//// INFOを吐きます
//logger.info("INFOです");
//
////WARNを吐きます
//logger.warn("WARNです");
//
//// ERRORを吐きます
//logger.error("ERRORです。");
//