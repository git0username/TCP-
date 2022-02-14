package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
//import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * TCP通信クラス<BR>
 * クライアントとソケット通信を行う。<BR>
 * ランダムなsleep実行（マルチスレッドプログラムが並行動作している確認のため）
 * →クライアントからの受信内容をタイムスタンプをつけて送信
 * →クライアントに送信する内容をローカルファイルに保存
 * 
 * @author Administrator
 *
 */
public class ServerInputOutput implements Runnable {

	/** TCP通信終端文字 */
	private static final String EOF = "$end$";

	/** クライアントソケット */
	private Socket socket = null;

	/** TCP通信　受信用Reader */
	private BufferedReader bufferedReader = null;

	/** TCP通信　送信用Writer */
	private BufferedWriter bufferedWriter_socket = null;

	/** ファイル　書込用Writer */
	private BufferedWriter bufferedWriter_textfile = null;

	/**
	 * コンストラクタ
	 * 
	 * @param socket クライアントソケット
	 * @param seqNo 処理通番
	 */
	public ServerInputOutput(Socket socket, int seqNo) {
		
		this.socket = socket;

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
			File outputFile = new File("C:\\Users\\itsys\\Desktop\\client_sever\\server_recv" + seqNo + ".txt");
			
			System.out.println("C:\\Users\\itsys\\Desktop\\client_sever\\server_recv" + seqNo + ".txt");
			
			// textfile用 bufferedWriterを準備する
			bufferedWriter_textfile = new BufferedWriter(new FileWriter(outputFile));

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
		}

	}

	@Override
	public void run() {

		String time = null;
		
		System.out.println("FileOutputStream=" + bufferedWriter_textfile);
        String separator =System.lineSeparator();
        time = DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now());

		try {
			// textfileに書込み
			bufferedWriter_textfile.write(time + separator);

			// client_Socketに返すstreamにTIMEstampをのせる
			bufferedWriter_socket.write(time + separator);

			// ------------------------------------------------------
			// クライアントの応答時間をランダムにSleepする
			// ------------------------------------------------------
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

			// ------------------------------------------------------
			// クライアントからの要求を読み込む
			// ------------------------------------------------------
			String line = null;
			StringBuilder stringBuilder = new StringBuilder();
			
			while (!(line = bufferedReader.readLine()).equals(EOF)) {
				String txt = null;
				try {
					txt = line + separator;
					System.out.println("sever=" + line);
		
				} catch (Exception e) {
					System.out.println("occured Exception.");
					e.printStackTrace();
				} finally { // 1行ごとに読んで、flushしている。中途半端に読込んだ時のために、ここではflushしないほうがいいのでは？
					
					// 各処理にてExceptionが発生した場合、以降の処理が実行されない
					// 個別のcatch処理が必要
					bufferedWriter_textfile.write(txt);
					bufferedWriter_socket.write(txt);

					bufferedWriter_textfile.flush();
					bufferedWriter_socket.flush();
					System.out.println("flush");
				}

			}
			//stream終端を示す$end$をストリームにのせる
			bufferedWriter_socket.write(EOF);
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

				// 各処理にてExceptionが発生した場合、以降の処理が実行されない
				// 個別のcatch処理が必要
				bufferedReader.close();
				bufferedWriter_socket.close();
				bufferedWriter_textfile.close();
				socket.close();

			} catch (IOException e) {
				System.out.println("occured IOException.");
			}

			System.out.println("end.");
		}
	}
}
