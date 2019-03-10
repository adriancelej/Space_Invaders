package charakter;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import game.Game;
import java.util.Iterator;

public class Shot extends Charakter{ //klasa strza�u gracza
private BufferedImage shot; //grafika strza�u
private boolean running; //aktywno�� w�tku strza�u
private int width,height; //szeroko��, wysoko��
private Thread thread; //w�tek strza�u
private Game game; //gra do kt�rej nale�y
private Player player;  //gracz do kt�rego nale�y

	
public Shot(int x, int y, int speed, BufferedImage shot, Game game, Player player){
		super(x,y,speed);
		this.shot=shot;
		this.game=game;
		width=game.getWIDTH()/15;
		height=game.getHEIGHT()/5;
		this.player= player;
		start();
	}

public void run() {
	while (running) {
    	try {
    		this.move(); //ruch strza�u
    		this.checkColision(); //sprawd� kolizj� ze stworkami
    		Thread.sleep(50); //od�wie�aj co 50 ms
    	}
    	catch(InterruptedException e) { //wynika z uspiania
    		e.printStackTrace();
    	}
    }
}

public void move() { //ruch pocisku
	y_position-= move_speed;
	if(y_position<(-height)) { //je�li pocisk jest poza obaszarem gry
		player.getShots().remove(this); //usu� strza� z listy gracza
		this.stop(); //zako�cz w�tek strza�u
	}	
}

private void checkColision() { // sprawdza kolizj� ze stworkami
	Iterator<Alien> iter = game.getAliens().iterator(); 
	while(iter.hasNext()) {
		if(iter.next().colision(x_position, y_position)) {
		//je�li kolizja nast�pi�a
			player.getShots().remove(this); //usu� strza� z listy gracza
			this.stop(); //zako�cz w�tek strza�u
		}
	}
	Iterator<Alien2> iter2 = game.getAliens2().iterator(); 
	while(iter2.hasNext()) {
		if(iter2.next().colision(x_position, y_position)) {
		//je�li kolizja nast�pi�a
			player.getShots().remove(this); //usu� strza� z listy gracza
			this.stop(); //zako�cz w�tek strza�u
		}
	}
}

public void render(Graphics g) { //wy�wietlanie grafiki strza�u
	g.drawImage(shot, x_position, y_position, width, height,null);
}

public synchronized void start(){ //synchronized - zawsze tego uzywamy w metodach start i stop
	if(running)
		return;
	running = true;
	thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
	thread.start();   			//wola metode run
}

public synchronized void stop(){
	if(!running)
		return;
	running = false;
	try {
		thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
}

public void pause(){ // do pauzowania
	if(!running)
		return;
	if(y_position>0) { //
		running = false;
		try {
			thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

public void rePause(){ //do wznawiania po pauzie
	if(running)
		return;
	if(y_position>(-height)) { 
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
		thread.start();   			//wola metode run
	}
}

public boolean isRunning() { //sprawdza aktywno�� w�tku
	return running;
}

}
