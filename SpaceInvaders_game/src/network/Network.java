package network;
import java.net.*;

import loader.TextLoader;

import java.io.*;

public class Network implements Runnable{
	private Socket socket;
	private Thread thread;
	private InetSocketAddress adress;
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private BufferedReader input;
	private StringBuilder builder;
	private String response;
	private int alienSpeed, shootingFreq;
	private int width, height;
	private int map[][];
	private String[] playerNames, playerResults;
	private int actuallLevel;
	private boolean loaded;
	private String player;
	private int result=0;

	public Network(int level) {
		socket= new Socket();
		actuallLevel=level;
	}
	
	public Network() {
		socket= new Socket();
	}
	
	@Override
	public synchronized void run() {
			try {
			socket.connect(adress);
			System.out.println("Po³¹czono");
			
			outputStream=socket.getOutputStream();
			outputStreamWriter= new OutputStreamWriter(outputStream);
			
			inputStream=socket.getInputStream();
			inputStreamReader= new InputStreamReader(inputStream);
			input = new BufferedReader(inputStreamReader);
			
			sendRequest(actuallLevel);
			thread.sleep(1000);
			
			while((response=input.readLine())!=null) {
				reciveResponse(response);
			}
			loaded=true;
			System.out.println(loaded);
			socket.close();
			notifyAll();
		}
		catch(IOException e) {
			e.getStackTrace();
		}
		catch(Exception e) {
			e.getStackTrace();
		}
	}
	
	public synchronized void bind(String ip, String port) {
		adress=new InetSocketAddress(ip,Integer.parseInt(port));
	}
	
	public synchronized void start() {
		thread= new Thread(this);
		thread.start();
	}
	
	private void sendRequest(int level) throws IOException {
		ADD_RESULT(player, result);
		GET_ALIEN_SPEED(level);
		GET_SHOOTING_FREQ(level);
		GET_MAP(level);
		GET_RESULTS();
		END_COMUNNICATION();
	}
	
	private void reciveResponse(String resp) {
		ALIEN_SPEED_RESPONSE(resp);
		SHOOTING_FREQ_RESPONSE(resp);
		MAP_RESPONSE(resp);
		RESULTS_RESPONSE(resp);
	}
	
	private void GET_ALIEN_SPEED(int level) throws IOException {
		builder=new StringBuilder("GET_ALIEN_SPEED ");
		builder.append(level);
		builder.append('\n');
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
	}
	
	private void GET_SHOOTING_FREQ(int level) throws IOException {
		builder = new StringBuilder("GET_SHOOTING_FREQ ");
		builder.append(level);
		builder.append('\n');
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
	}
	
	private void GET_MAP(int level) throws IOException {
		builder= new StringBuilder("GET_MAP ");
		builder.append(level);
		builder.append('\n');
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
	}
	
	private void ADD_RESULT(String player,int result) throws IOException {
		if(player!=null && result!=0) {
			builder= new StringBuilder("ADD_RESULT ");
			builder.append(player);
			builder.append(' ');
			builder.append(result);
			builder.append('\n');
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
		}
	}
	
	private void GET_RESULTS() throws IOException {
		builder= new StringBuilder("GET_RESULT\n");
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
	}
	
	private void END_COMUNNICATION() throws IOException {
		builder= new StringBuilder("END_COMUNNICATION\n");
		outputStreamWriter.write(builder.toString());
		outputStreamWriter.flush();
	}
	
	private void ALIEN_SPEED_RESPONSE(String resp) {
		if(resp.matches("ALIEN_SPEED(.*)")) {
			int speed = Integer.parseInt(resp.split("\\s")[1]);
			alienSpeed=speed;
			System.out.println(resp);
		}
	}
	
	private void SHOOTING_FREQ_RESPONSE(String resp) {
		if(resp.matches("SHOOTING_FREQ(.*)")) {
			int freq = Integer.parseInt(resp.split("\\s")[1]);
			shootingFreq=freq;
			System.out.println(resp);
		}
	}
	
	private void MAP_RESPONSE(String resp) {
		if(resp.matches("MAP(.*)")) {
			String response[] = resp.split("\\s");
			width=Integer.parseInt(response[1]);
			height=Integer.parseInt(response[2]);
			map= new int[width][height];
			for(int y = 0;y < height;++y){
				for(int x = 0;x < width;++x){
					map[x][y] = Integer.parseInt(response[(x + y*width) + 3]);	
				}
			}
			System.out.println(resp);
		}
	}
	
	private void RESULTS_RESPONSE(String resp) {
		if(resp.matches("RESULTS(.*)")) {
			String response[] = resp.split("\\s");
			playerNames= new String[10];
			playerResults= new String[10]; 
			for(int i=0;i<playerNames.length;++i) {
				playerNames[i]=response[2*i+1];
				playerResults[i]=response[2*i+2];
			}
			System.out.println(resp);
		}
	}
	
	public synchronized int[][] getMap() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return map;
	}
	
	public synchronized int getWidth() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return width;
	}
	
	public synchronized int getHeight() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return height;
	}
	
	public synchronized int getAlienSpeed() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return alienSpeed;
	}
	
	public synchronized int getShootingFreq() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return shootingFreq;
	}
	
	public synchronized String[] getPlayerNames() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return playerNames;
	}
	
	public synchronized String[] getPlayerResults() {
		while(!loaded) {
			try {
				wait(20);
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return playerResults;
	}
	
	public synchronized void setPlayerResult(String player, int result) {
		this.player=player;
		this.result=result;
	}
		
}
