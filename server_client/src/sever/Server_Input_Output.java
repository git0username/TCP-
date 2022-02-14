package sever;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Server_Input_Output implements Runnable {

	Socket socket = null;
//	DataInputStream input = null;
//	DataOutputStream output = null;
//	ServerSocket server = null;
//	FileOutputStream fos = null;
//	FileInputStream fis = null;
    BufferedReader bufferedReader = null;
    BufferedWriter bufferedWriter_socket = null;
    BufferedWriter bufferedWriter_textfile = null;
	int ch;
	PrintWriter pw = null;
	String time = null;
	
	
	int No;

	// コンストラクタでストリームを準備する
	public Server_Input_Output(Socket socket, int No) {

		try {
			// ソケット用 入出力ストリームを準備する
//			input = new DataInputStream(socket.getInputStream());
			bufferedReader
		    = new BufferedReader(
		    new InputStreamReader(socket.getInputStream()));

			// ソケット用 bufferedWriterを準備する
			bufferedWriter_socket = new BufferedWriter(
				    new OutputStreamWriter(socket.getOutputStream()));
			
			// textfile用 入出力ストリームを準備する			
			File outputFile = new File("C:\\Users\\itsys\\Desktop\\client_sever\\server_recv" + No + ".txt");
			
			System.out.println("C:\\Users\\itsys\\Desktop\\client_sever\\server_recv" + No + ".txt");
			
			// textfile用 bufferedWriterを準備する
			bufferedWriter_textfile = new BufferedWriter(new FileWriter(outputFile));

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			this.socket = socket;
			this.No = No;
		}

	}

	@Override
	public void run() {
		
		System.out.println("FileOutputStream=" + bufferedWriter_textfile);
        String separator =System.lineSeparator();
        time = DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now());

		try {
			// textfileに書込み
			bufferedWriter_textfile.write(time + separator);

			// client_Socketに返すstreamにTIMEstampをのせる
			bufferedWriter_socket.write(time + separator);

			try {
				// スレッドのsleepを1〜10秒でランダムに決める。
				int sleeptime = (int) (Math.random() * 9 + 1);
				bufferedWriter_textfile.write("sleeptime =" +sleeptime + separator);
				
				bufferedWriter_socket.write("sleeptime =" + sleeptime + separator);
				
				System.out.println("sleeptime =" + sleeptime);
				

				Thread.sleep(sleeptime); // ランダムに処理を止める

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// clientからきたデータ(byte)をfile用、Socket用streamに乗せる
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			
			while (!(line = bufferedReader.readLine()).equals( "$end$")) {
				String txt = null;
				try {
					txt = line + separator;
					System.out.println("sever=" + line);
		
				} catch (Exception e) {
					System.out.println("occured Exception.");
					e.printStackTrace();
				} finally { // 1行ごとに読んで、flushしている。中途半端に読込んだ時のために、ここではflushしないほうがいいのでは？
					
					bufferedWriter_textfile.write(txt);
					bufferedWriter_socket.write(txt);

					bufferedWriter_textfile.flush();
					bufferedWriter_socket.flush();
					System.out.println("flush");
				}

			}
			//stream終端を示す$end$をストリームにのせる
			bufferedWriter_socket.write("$end$");
			bufferedWriter_socket.flush();
			
			System.out.println("stringBuilder=" + stringBuilder.toString());
			

		} catch (SocketException e) {
			System.out.println("Server_Input_Output_occured SocketException.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("occured IOException.");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("occured Exception.");
			e.printStackTrace();
		} finally {
			try {
//				pw.close();
//
//				fos.close();
//				fis.close();

				bufferedReader.close();
				bufferedWriter_socket.close();
				bufferedWriter_textfile.close();
				socket.close();
//				server.close();

			} catch (IOException e) {
				System.out.println("occured IOException.");
			}

			System.out.println("end.");
		}
	}
}
