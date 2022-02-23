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
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	/** 日付フォーマット */
	private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss.SSS");
	
	/** テキスト出力用バッファ */
	private StringBuilder str = new StringBuilder();

	/**
	 * コンストラクタ
	 * 
	 * @param socket クライアントソケット
	 * @param seqNo 処理通番
	 */
	public ServerInputOutput(Socket socket, int seqNo) {
		
		str.append(log("起動時刻", SDF.format(new Date())));
		this.socket = socket;
		str.append(log("処理通番", seqNo));
		str.append(log("スレッド名", Thread.currentThread().getName()));

		System.out.println(str.toString());

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
			File outputFile = new File("C:\\Users\\Administrator\\Desktop\\client_sever\\server_recv" + seqNo + ".txt");
			
			System.out.println("C:\\Users\\Administrator\\Desktop\\client_sever\\server_recv" + seqNo + ".txt");
			
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

//		String time = null;
		str.append(log("処理開始時刻", SDF.format(new Date())));

		System.out.println("FileOutputStream=" + bufferedWriter_textfile);
        String separator =System.lineSeparator();
//        time = DateTimeFormatter.ISO_LOCAL_TIME.format(LocalDateTime.now());

		try {
//			// textfileに書込み
//			bufferedWriter_textfile.write(time + separator);
//
//			// client_Socketに返すstreamにTIMEstampをのせる
//			bufferedWriter_socket.write(time + separator);

			// ------------------------------------------------------
			// クライアントの応答時間をランダムにSleepする
			// ------------------------------------------------------
			try {
				// スレッドのsleepを1〜10秒でランダムに決める。
				int sleeptime = (int) (Math.random() * 9 + 1);
//				bufferedWriter_textfile.write("sleeptime =" +sleeptime + separator);
//				
//				bufferedWriter_socket.write("sleeptime =" + sleeptime + separator);
//				
//				System.out.println("sleeptime =" + sleeptime);
//				

				str.append(log("sleep time", sleeptime));
				Thread.sleep(sleeptime * 1000); // ランダムに処理を止める

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			// ------------------------------------------------------
			// クライアントからの要求を読み込む
			// ------------------------------------------------------
			String line = null;
			
			str.append(log("受信メッセージ", "▼▼▼▼▼▼▼▼▼▼"));
			while (!(line = bufferedReader.readLine()).equals(EOF)) {

				// 受信したデータを終了まで読み込む
				String txt = line + separator;
				str.append(txt);
				
			}
			str.append(log("受信メッセージ", "▲▲▲▲▲▲▲▲▲▲"));
			//stream終端を示す$end$をストリームにのせる
			str.append(log("応答送信時刻", SDF.format(new Date())));
			str.append(EOF);
			
			System.out.println(str.toString());
			
			bufferedWriter_socket.write(str.toString());
			bufferedWriter_socket.flush();
			bufferedWriter_textfile.write(str.toString());
			bufferedWriter_textfile.flush();
			
//			System.out.println("stringBuilder=" + stringBuilder.toString());
			

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
        return String.format("【Server】%-" + (length - byteDiff) + "s = %s", key, value) + System.lineSeparator();
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
