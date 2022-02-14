package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

//import org.apache.log4j.Logger;
//
/**
 * TCP通信サーバ<BR>
 * クライアントからの接続を待ち、送受信処理を並行で行うために
 * 別スレッドにて実施する。
 * 
 * @author Administrator
 *
 */
public class SocketReceive {

	/** TCP通信ポート番号 */
	private static final int PORT_NO = 9000;
	
	/** サーバ実行フラグ */
	private static boolean doFlag = true;

	/**
	 * メイン処理
	 * 
	 * @param args 未使用
	 */
	public static void main(String[] args) {

		// クライアントソケット
		Socket socket = null;
		
		// サーバソケット
		ServerSocket server = null;

		// 処理通番
		int seqNo = 0;

		System.out.println("start.");

		// ------------------------------------------------------
		// ソケットを作成して、クライアントからの接続を待ち受ける
		// ------------------------------------------------------
		try {
			// ソケット作成
			server = new ServerSocket(PORT_NO);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("server側Socketエラー");
		}

		while (doFlag) {
			// client側からの受信を常に待つ
			
			try {				
				
				socket = server.accept();

				Thread thread = new Thread(new ServerInputOutput(socket, seqNo));
				thread.start();
				seqNo++;
				
			} catch (SocketException e) {
				System.out.println("ソケットエラー");
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("SocketReceive_occured SocketException.");
				e.printStackTrace();

			}
		}

		try {

			if (server != null) {
				server.close();
			}
		} catch (IOException e) {
			// "socket.server.クローズのエラー
			System.out.println("socket.server.クローズ;occured IOException.");
		}
		System.out.println("end.");

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