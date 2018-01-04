package com.company;
import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Logger;

public class ChatServer  {
	private ServerSocket ss=null;
	private Socket s = null;

	Vector<String> v = new Vector<String>();
	ArrayList<ChatTread> chatTreads = new ArrayList<ChatTread>(); //클라이언트마다 정보 저장하기위해 리스트로 저장
	Logger logger;
	//서버 시작하는 함수
	void serverstart() {
		logger = Logger.getLogger(this.getClass().getName());
		try {
			ss = new ServerSocket(5555);
			logger.info("Server Start");

			while (true) {      //클라이언트 소켓을 받을때까지 무한루프돔
				s = ss.accept();
				ChatTread chat = new ChatTread(); //클라이언트 마다 새로운 스레드들로 생성
				chatTreads.add(chat); //리스트에 add
				chat.start(); //ChatTread에 run메소드로 감
			}
		} catch (Exception e) {
			logger.info("SeverError");
		}
	}

	public void msgSendAll(String msg) {    //서버에 접속된 클라이언트 모두에게 동일한 메세지를 뿌려준다
		for(ChatTread ct : chatTreads) {
			ct.outMsg.println(msg);
		}
	}
	//실질적인 네트워크 입출력이 발생하는 부분
	class ChatTread extends Thread {
		String msg;
		Message m = new Message();
		Gson gson = new Gson();
		boolean status = true;

		private BufferedReader inMsg = null;
		private PrintWriter outMsg = null;

		public ChatTread () throws IOException { //생성자에 입출력 스트림 객체 생성
			inMsg = new BufferedReader(new InputStreamReader(s.getInputStream()));
			outMsg = new PrintWriter(s.getOutputStream(),true);
		}
		@Override
		public void run() {
			while (status) { //로그인 로그아웃 상태에 따라 채팅계속 실행, 종료
				try {
					msg = inMsg.readLine(); //입력스트림으로 입력된 텍스트 한줄 씩 읽어옴
					m = gson.fromJson(msg,Message.class); //읽어온 텍스트를 Message.class형식으로 파싱
					if(m.getType().equals("logout")) { //로그아웃할 때 아래문장 출력
						chatTreads.remove(this);
						for(int i=0;i<v.size();i++) { //로그아웃시 벡터에 해당 유저 아이디 찾아서 제거
							if(v.get(i).equals(m.getId())) v.remove(i);
						}
						System.out.println(userAll());
						msgSendAll(gson.toJson(new Message(m.getId(),"","님이 종료했습니다.","server")));
						msgSendAll(gson.toJson(new Message(m.getId(),"",userAll(),"exit")));
						status = false;
					}
					else if (m.getType().equals("login")) { //로그인할 때 아래문장 출력
						v.add(m.getId());
						msgSendAll(gson.toJson(new Message(m.getId(),"","님이 로그인했습니다.","server")));
						msgSendAll(gson.toJson(new Message(m.getId(),"",userAll(),"id")));
					}
					else { //로그인 후 상태
						msgSendAll(msg);
					}
				} catch (Exception e) { System.out.println("thread Error");}
			}
			this.interrupt();
			logger.info(this.getName()+" 종료");
		}

	}

}
public class Main {
    public static void main(String[] args) {
    	MyActionListener my = new MyActionListener();
    	my.LoginListenerSet();
    	ChatServer server = new ChatServer();
		server.serverstart();
    	AppManager.getInstance().getLoginView();
    }
}

