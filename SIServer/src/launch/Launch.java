package launch;
import server.*;

public class Launch { //klasa g��wna, uruchamia serwer

	public static void main(String[] args) {
		Server server= new Server();
		server.addServerInterface(); //wy�wietla okno serwera
	}

}
