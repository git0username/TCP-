package client;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCP通信クライアント<BR>
 * スレッドプールを使用し、マルチスレッドプログラムの確認を行う。
 * 
 * @author Administrator
 *
 */
public class Clients {

	/**
	 * メイン処理
	 * 
	 * @param args 未使用
	 */
	public static void main(String[] args) {
		
		// スレッドプール数
		final int MAX_POOL = 3;
		
		// 起動スレッド数
		final int MAX_THREAD = 6;
		
		System.out.println("Client start.");
	
		ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL);
		
		ArrayList<Thread> client_list = new ArrayList<>();

		for (int no = 0; no < MAX_THREAD; no++) {

			// プール数＜スレッド数とすることで、スレッドがプール数のみ
			// 動作していることを確認する。
			// インスタンスに番号を振る。
			Thread thread = new Thread(new SocketSend(no));
			client_list.add(thread);
			pool.submit(thread);
		}
		
		System.out.println("Client end.");
	}
}
