package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

public class SocketSend implements Runnable {

	private Object object;
	int No;
	int lifetime;

	public SocketSend(int No) {
		this.No = No;
//     	this.lifetime = lifetime;

	}

	Socket socket = null;
	DataOutputStream output = null;
	DataInputStream input = null;
	File fos = null;
//        FileInputStream fis = null;
//        BufferedOutputStream buffer_output = null;

	StringBuilder str = new StringBuilder();

	String separator = System.lineSeparator();

	BufferedReader bufferedReader_txt_in = null;

	BufferedReader bufferedReader_server_in = null;

	String line = null;

	BufferedWriter bufferedWriter_textfile = null;

	public void run() {
		try {
			System.out.println("SocketSend run start.");

			// 出力ソケットを作成
			socket = new Socket("localhost", 9000);

			// 入出力ストリームを準備
			File inputFile = new File("C:\\Users\\itsys\\Desktop\\client_sever\\client_send" + No + ".txt");
			bufferedReader_txt_in = new BufferedReader(new FileReader(inputFile));

			fos = new File("C:\\Users\\itsys\\Desktop\\client_sever\\client_recv" + No + ".txt");

			// 入力ストリームの内容を全て送信
			int ch;

			output = new DataOutputStream(socket.getOutputStream());
//            buffer_output = new BufferedOutputStream(output);

			str.append("インスタンスNo＝" + No + separator + "スレッドNo＝" + Thread.currentThread().getName() + separator);

//            output.writeUTF("インスタンスNo＝" + No);

//            output.writeUTF("lifetime＝" + lifetime);
			String line = null;
			while ((line = bufferedReader_txt_in.readLine()) != null) {
				System.out.println(line);
				str.append(line + separator);

//                System.out.println("client_send=" + String.valueOf(ch));

//                System.out.println("client_send=" + new BufferedOutputStream(output).flush());   

			}

			str.append("$end$");

			output.writeUTF(str.toString());

			output.write(0);

			System.out.println("client_output=" + str.toString());
			
			

	// サーバからのレスポンスを受信し、ファイルに出力
			
			 
			//ソケットの読込み用
			bufferedReader_server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// textfile用 bufferedWriterを準備する
			bufferedWriter_textfile = new BufferedWriter(new FileWriter(fos));
		

//            input =  new DataInputStream(socket.getInputStream());             

			while (!(line = bufferedReader_server_in.readLine()).equals("$end$")) {
				bufferedWriter_textfile.write(line + separator);
				bufferedWriter_textfile.flush();

				System.out.println("client_recv=" + line);
			}
			

	

		} catch (SocketException e) {
			System.out.println("SocketSend_occured SocketException.");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("occured Exception.");
			e.printStackTrace();
		} finally {
			try {
				socket.close();
				bufferedWriter_textfile.close();
				bufferedReader_txt_in.close();
			} catch (IOException e) {
				System.out.println("occured IOException.");
				e.printStackTrace();
			}
		
	}
		System.out.println("SocketSend run end.");

}
}