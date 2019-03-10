package server;
import java.net.*;
import java.io.*;
import loader.*;

public class RequestHandler implements Runnable { //obs³uga jednego klienta 
	private Socket socket; //gniazdko obs³ugiwanego klienta
	private StringBuilder builder;
	private BufferedReader input;
	private InputStream inputStream;
	private InputStreamReader inputStreamReader;
	private OutputStream outputStream;
	private OutputStreamWriter outputStreamWriter;
	private String request;
	private Thread thread;
	private WorldLoader loader=new WorldLoader();
	private ResultsLoader resultsLoader;

	public RequestHandler(Socket clientSocket) {
		this.socket= clientSocket;
		try {
			resultsLoader= new ResultsLoader();
			inputStream= socket.getInputStream();
			inputStreamReader= new InputStreamReader(inputStream);
			input= new BufferedReader(inputStreamReader);
			
			outputStream= socket.getOutputStream();
			outputStreamWriter= new OutputStreamWriter(outputStream);
			start();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	@Override
	public void run() {
		System.out.println("request_accepted");
		try {
			while(!socket.isClosed()) {
			request=input.readLine();
			System.out.println(request);
			SEND_ALIEN_SPEED(request);
			SEND_SHOOTING_FREQ(request);
			SEND_MAP(request);
			ADD_RESULT(request);
			SEND_RESULTS(request);
			END_COMUNNICATION(request);
			thread.sleep(500);
			}
			
		}catch(Exception e) {
			e.getStackTrace();
			}
	}
	
	public void start() {
		if(socket.isBound()) {
			thread=new Thread(this);
			thread.start();
		}
	}
	
	private void SEND_ALIEN_SPEED(String req) throws IOException { //wysy³a prêdkoœæ stworków dla odpowiedniego levelu
		if(req.matches("GET_ALIEN_SPEED(.*)")) {
			int level= Integer.parseInt(req.split("\\s")[1]);
			loader.loadWorld(level);
			builder=new StringBuilder("ALIEN_SPEED ");
			builder.append(loader.getAlienSpeed());
			builder.append('\n');
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
		}
	}
	
	private void SEND_SHOOTING_FREQ(String req) throws IOException { //wysy³a parametr mówi¹cy o czêstoœci oddawania strza³u przez stworka
		if(req.matches("GET_SHOOTING_FREQ(.*)")) {
			int level= Integer.parseInt(req.split("\\s")[1]);
			loader.loadWorld(level);
			builder= new StringBuilder("SHOOTING_FREQ ");
			builder.append(loader.getShotingFreq());
			builder.append('\n');
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
		}
	}
	
	private void SEND_MAP(String req) throws IOException {
		if(req.matches("GET_MAP(.*)")) {
			int level= Integer.parseInt(req.split("\\s")[1]);
			loader.loadWorld(level);
			builder=new StringBuilder("MAP ");
			builder.append(loader.getMap());
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
		}
	}
	
	private void ADD_RESULT(String req) throws IOException {
		if(req.matches("ADD_RESULT(.*)")) {
			String player= req.split("\\s")[1];
			int result= Integer.parseInt(req.split("\\s")[2]);
			resultsLoader.addResult(player, result);
			builder=new StringBuilder("OK");
			//builder.append(resultsLoader.getResults());
			builder.append('\n');
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
		}
	}
	
	private void SEND_RESULTS(String req) throws IOException {
		if(req.matches("GET_RESULT(.*)")) {
			builder=new StringBuilder("RESULTS ");
			builder.append(resultsLoader.getLoadedResults());
			builder.append('\n');
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
		}
	}
	
	private void END_COMUNNICATION(String req) throws IOException {
		if(req.matches("END_COMUNNICATION(.*)")){
			builder= new StringBuilder("END_COMUNNICATION\n");
			outputStreamWriter.write(builder.toString());
			outputStreamWriter.flush();
			System.out.println(builder.toString());
			socket.close();
		}
	}
}
