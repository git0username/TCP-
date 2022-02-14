package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * TCP通信クラス<BR>
 * ソケット生成し、サーバとソケット通信を行う。<BR>
 * ソケット生成→ローカルファイルの内容をサーバに送信→サーバから受信した内容をローカルファイルに保存
 * 
 * @author Administrator
 *
 */
public class SocketSend implements Runnable {

	/** TCP通信ポート番号 */
	private static final int PORT_NO = 9000;

	/** TCP通信終端文字 */
	private static final String EOF = "$end$";

	/** スレッド番号 */
	private int threadNo = 0;

	/**
	 * コンストラクタ
	 * 
	 * @param no スレッド番号
	 */
	public SocketSend(int no) {

		this.threadNo = no;
	}

	@Override
	public void run() {

		// TCP通信用スレッド
		Socket socket = null;
		// TCP通信送信用ストリーム
		DataOutputStream output = null;
		// TCP通信受信ログファイル
		File fos = null;

		StringBuilder str = new StringBuilder();
		String separator = System.lineSeparator();

		// 送信データ読み込み用
		BufferedReader bufferedReader_txt_in = null;
		// 受信データ読み込み用
		BufferedReader bufferedReader_server_in = null;

		String line = null;

		// 受信データ書き込み用
		BufferedWriter bufferedWriter_textfile = null;


		try {

			System.out.println("SocketSend run start.");

			// ------------------------------------------------------
			// ローカルファイルの内容を読み込み、サーバに送信
			// ------------------------------------------------------
			
			// 出力ソケットを作成
			socket = new Socket("localhost", PORT_NO);

			// 入出力ストリームを準備
			File inputFile = new File("C:\\Users\\itsys\\Desktop\\client_sever\\client_send" + threadNo + ".txt");
			bufferedReader_txt_in = new BufferedReader(new FileReader(inputFile));

			fos = new File("C:\\Users\\itsys\\Desktop\\client_sever\\client_recv" + threadNo + ".txt");

			// 入力ストリームの内容を全て送信
			output = new DataOutputStream(socket.getOutputStream());

			str.append("スレッドNo＝" + threadNo + separator + "スレッドName＝" + Thread.currentThread().getName() + separator);

			while ((line = bufferedReader_txt_in.readLine()) != null) {

				// ローカルファイルを終了まで読み込む
				System.out.println(line);
				str.append(line + separator);
			}

			// 終端文字を付与
			str.append("$end$");

			output.writeUTF(str.toString());
			output.write(0);

			System.out.println("client_output=" + str.toString());
			
			// ------------------------------------------------------
			// サーバからのレスポンスを受信し、ファイルに出力
			// ------------------------------------------------------
			 
			//ソケットの読込み用
			bufferedReader_server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// textfile用 bufferedWriterを準備する
			bufferedWriter_textfile = new BufferedWriter(new FileWriter(fos));

			while (!(line = bufferedReader_server_in.readLine()).equals(EOF)) {

				// 受信したデータを終了まで読み込む
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