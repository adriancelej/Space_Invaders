package server;
import display.*;
import network.*;
import java.awt.event.*;

public class Server implements Runnable{ //klasa g³ówna serwera
 private Display display= new Display("SI_Server1.0"); //tworzenie okna, grafiki
 private Thread thread= new Thread(this); //w¹tek obs³uguj¹cy serwer
 private Network network= new Network(); //obs³uga sieci
 private String port; //numer portu
 private boolean running=false; //czy serwer dzia³a
 private Server server=this; //referencja na this, wykorzystywana przy obs³udze zdarzeñ u¿ytkownika
 
 @Override
 public void run() {
	
	 while(running) {
		 try {
			 if(!network.isClosed())
			 new RequestHandler(network.start()); //jeœli nadszed³ klijent tworzy obiekt obs³uguj¹cy go
		 }
		 catch(Exception e) {
			 e.getStackTrace();
		 	}
		 }
	 }
 
 
public synchronized void start(){ //synchronized - zawsze tego uzywamy w metodach start i stop
	thread.start();
	//running=true;
	}

private void getPortNumber() { 
	//ustawia s³uchacza przycisku start, pobiera numer portu, tworzy gniazdo servera, aktualizuje komponenty graficzne, gdy nast¹pi zdarzenie
	display.setStartButtonListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
			 running=true;
			 port=display.getPortNumber();
			 System.out.println(port); 
			 network.startSocket(port);
			 display.setStartButtonVisible(false);
			 display.setStopButtonVisible(true);
			 display.setAdressLabelVisible(true);
			 display.setAdressVisible(true);
			 display.setExternalAdressLabelVisible(true);
			 display.setExternalAdressVisible(true); 
			 display.setAdress(network.getAdress());
			 display.setExternalAdress(network.getExternalAdress());
			 if(!thread.isAlive()) {
				 thread=new Thread(server);
				 server.start();
			 }
			 
		}
		});
}

private void closeSocket() {
	//ustawia s³uchacza przycisku stop, aktualizuje komponenty graficzne, zamyka gniazdo serwera
	display.setStopButtonListener(new ActionListener() {
		 public void actionPerformed(ActionEvent event) {
			 display.setStartButtonVisible(true);
			 display.setStopButtonVisible(false);
			 display.setAdressLabelVisible(false);
			 display.setAdressVisible(false);
			 display.setExternalAdressLabelVisible(false);
			 display.setExternalAdressVisible(false);
			 network.stop(); 
			 running=false;
		}
		});
}

public void addServerInterface() { //tworzy okno, ustawia s³uchaczy 
	display.createGui();
	getPortNumber();
	closeSocket();
}

 
}
