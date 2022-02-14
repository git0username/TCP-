package client;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Clients {

	public static void main(String[] args) {
		
		System.out.println("Client start.");
		
		int No;
	
		ExecutorService pool = Executors.newFixedThreadPool(3);
		

		ArrayList<Thread> client_list = new ArrayList<>();

		for (int i = 0; i < 6; i++) {
			 // インスタンスに番号を振る。
			No = i;			
			
	    
			Thread thread = new Thread(new SocketSend(No));
						
			client_list.add(thread);
			
			pool.submit(thread);
			
			
//			thread.start();

		}
		
		System.out.println("Client end.");
	}
}
