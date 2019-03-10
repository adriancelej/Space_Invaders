package charakter;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import game.Game;
import java.util.Iterator;

public class Shot extends Charakter{ //klasa strza³u gracza
private BufferedImage shot; //grafika strza³u
private boolean running; //aktywnoœæ w¹tku strza³u
private int width,height; //szerokoœæ, wysokoœæ
private Thread thread; //w¹tek strza³u
private Game game; //gra do której nale¿y
private Player player;  //gracz do którego nale¿y

	
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
    		this.move(); //ruch strza³u
    		this.checkColision(); //sprawd¿ kolizjê ze stworkami
    		Thread.sleep(50); //odœwie¿aj co 50 ms
    	}
    	catch(InterruptedException e) { //wynika z uspiania
    		e.printStackTrace();
    	}
    }
}

public void move() { //ruch pocisku
	y_position-= move_speed;
	if(y_position<(-height)) { //jeœli pocisk jest poza obaszarem gry
		player.getShots().remove(this); //usuñ strza³ z listy gracza
		this.stop(); //zakoñcz w¹tek strza³u
	}	
}

private void checkColision() { // sprawdza kolizjê ze stworkami
	Iterator<Alien> iter = game.getAliens().iterator(); 
	while(iter.hasNext()) {
		if(iter.next().colision(x_position, y_position)) {
		//jeœli kolizja nast¹pi³a
			player.getShots().remove(this); //usuñ strza³ z listy gracza
			this.stop(); //zakoñcz w¹tek strza³u
		}
	}
	Iterator<Alien2> iter2 = game.getAliens2().iterator(); 
	while(iter2.hasNext()) {
		if(iter2.next().colision(x_position, y_position)) {
		//jeœli kolizja nast¹pi³a
			player.getShots().remove(this); //usuñ strza³ z listy gracza
			this.stop(); //zakoñcz w¹tek strza³u
		}
	}
}

public void render(Graphics g) { //wyœwietlanie grafiki strza³u
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

public boolean isRunning() { //sprawdza aktywnoœæ w¹tku
	return running;
}

}
