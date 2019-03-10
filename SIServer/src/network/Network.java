package network;
import java.io.IOException;
import java.net.*;

public class Network { //obs³uga gniazdek sieciowych
	private ServerSocket serverSocket; //gniazdo serwera
	private Socket socket; //gniazdo klienta
	
	public Socket start(){
		try{
				System.out.println("server started"); 
				socket= serverSocket.accept(); //oczekuje na klientów (blokuje w¹tek serwera)
				return socket; //kiedy nadejdzie klijent zwraca jego gniazdo
		}
		catch(IOException e) { //wyj¹tek rzucany przy zamykaniu gniazda serwera
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public void startSocket(String port) { //tworzy gniazdo serwera na wskazanym porcie
		try {
			serverSocket = new ServerSocket(Integer.parseInt(port));
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isClosed() { //sprawdza czy gniazdo serwera jest zamkniête
		return serverSocket.isClosed();
	}
	
	public void stop(){ //zamyka gniazdo serwera
		try {
			if(!serverSocket.isClosed()) {
				serverSocket.close();
				System.out.println("server stopped");
				}
			}
		catch(IOException e) {
			e.getMessage();
		}
	}
	
	public String getAdress() { //zwraca adres IP localhost
		try {
			return serverSocket.getInetAddress().getLocalHost().getLoopbackAddress().getHostAddress();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return "Brak po³¹czenia z sieci¹";
		
	}
	
	public String getExternalAdress() { //zwraca adres IP karty sieciowej
		try {
			return serverSocket.getInetAddress().getLocalHost().getHostAddress();
		}
		catch(UnknownHostException e) {
			e.printStackTrace();
		}
		return "Brak po³¹czenia z sieci¹";
	}
		
}
