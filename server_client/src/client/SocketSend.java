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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	/** 日付フォーマット */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");

	/** スレッド番号 */
	private int threadNo = 0;
	
	/** テキスト出力用バッファ */
	private StringBuilder str = new StringBuilder();

	/**
	 * コンストラクタ
	 * 
	 * @param no スレッド番号
	 */
	public SocketSend(int no) {

		str.append(log("起動時刻", SDF.format(new Date())));
		this.threadNo = no;
		str.append(log("スレッド番号", threadNo));
		str.append(log("スレッド名", Thread.currentThread().getName()));
	}

	@Override
	public void run() {

		// TCP通信用スレッド
		Socket socket = null;
		// TCP通信送信用ストリーム
		DataOutputStream output = null;
		// TCP通信受信ログファイル
		File fos = null;

		str.append(log("処理開始時刻", SDF.format(new Date())));

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
			File inputFile = new File("C:\\Users\\Administrator\\Desktop\\client_sever\\client_send" + threadNo + ".txt");
			bufferedReader_txt_in = new BufferedReader(new FileReader(inputFile));

			fos = new File("C:\\Users\\Administrator\\Desktop\\client_sever\\client_recv" + threadNo + ".txt");

			// 入力ストリームの内容を全て送信
			output = new DataOutputStream(socket.getOutputStream());

			String separator = System.lineSeparator();

			str.append(log("送信メッセージ", ""));
			while ((line = bufferedReader_txt_in.readLine()) != null) {

				// ローカルファイルを終了まで読み込む
				str.append(line);
				str.append(separator);
			}

			// 終端文字を付与
			str.append(log("要求送信時刻", SDF.format(new Date())));
			str.append(EOF);
			str.append(separator);

			output.write(str.toString().getBytes());
			output.write(0);

			System.out.println(str.toString());
			
			// ------------------------------------------------------
			// サーバからのレスポンスを受信し、ファイルに出力
			// ------------------------------------------------------
			 
			//ソケットの読込み用
			bufferedReader_server_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// textfile用 bufferedWriterを準備する
			bufferedWriter_textfile = new BufferedWriter(new FileWriter(fos));
			
//			str.delete(0, str.length());
			str.append(log("応答受信時刻", SDF.format(new Date())));
			str.append(log("受信メッセージ", "▽▽▽▽▽▽▽▽▽▽"));

			while (!(line = bufferedReader_server_in.readLine()).equals(EOF)) {

				// 受信したデータを終了まで読み込む
				str.append(line);
				str.append(separator);

//				System.out.println("client_recv=" + line);
			}
			str.append(log("受信メッセージ", "△△△△△△△△△△"));

			bufferedWriter_textfile.write(str.toString());
			bufferedWriter_textfile.flush();
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
	
	/**
	 * ログ整形文字作成（key=valueの形式にする）
	 * 
	 * @param key ログ出力文字左辺
	 * @param value ログ出力文字左辺
	 */
	private String log(String key, int value) {
		
		return log(key, String.valueOf(value));
	}
	
	/**
	 * ログ整形文字作成（key=valueの形式にする）
	 * 
	 * @param key ログ出力文字左辺
	 * @param value ログ出力文字左辺
	 */
	private String log(String key, String value) {
		
		return format(key, value);
	}
	
	/**
	 * 固定長文字を作成
	 * 
	 * @param key ログ出力文字左辺
	 * @param value  ログ出力文字左辺
	 * @return
	 */
    private String format(String key, String value){
    	
    	int length = 16;
        int byteDiff = (getByteLength(key, Charset.forName("UTF-8")) - key.length())/2;
        return String.format("【Client】%-" + (length - byteDiff) + "s = %s", key, value) + System.lineSeparator();
    }

    /**
     * 指定した文字コードの文字列長を取得する
     * @param string 文字列
     * @param charset 文字コード
     * @return
     */
    private int getByteLength(String string, Charset charset) {
        return string.getBytes(charset).length;
    }
		
}