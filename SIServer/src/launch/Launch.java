package launch;
import server.*;

public class Launch { //klasa g³ówna, uruchamia serwer

	public static void main(String[] args) {
		Server server= new Server();
		server.addServerInterface(); //wyœwietla okno serwera
	}

}
