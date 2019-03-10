package charakter;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import game.Game;

public class AlienShot extends Charakter{
private BufferedImage shot; //grafika strz³u
private boolean running; //aktywnoœæ
private int width,height; // rozmiaru w px
private Thread thread; //w¹tek 
private Game game; //gra do której nale¿y
private Alien owner; //stworek do którego nale¿y

	
public AlienShot(int x, int y, int speed, BufferedImage shot, Game game, Alien owner){
		super(x,y,speed);
		this.shot=shot;
		this.game=game;
		width=game.getWIDTH()/40;
		height=game.getHEIGHT()/10;
		this.owner=owner;
		start();
	}

public void run() {
	while (running) {
    	try {
    		Thread.sleep(50); //odœwie¿ co 40 ms
    		move();//ruch pocisku
    		checkColision();//kolizja z graczem
    	}
    	catch(InterruptedException e) { //od usypiania w¹tku
    		e.printStackTrace();
    	}
    }
}

public void move() { //ruch pocisku
	y_position+= move_speed; 
	if(y_position>(game.getHEIGHT()+height)) {
		//jeœli pocisk znajdzie siê poza obszrem gry 
		owner.getShots().remove(this); //usuñ go z listy pocisków stworka
		stop(); //zakoñcz w¹tek
	}
}

private void checkColision() { //sprawd¿ kolizjê z graczem
	if(isRunning()) {
		if(game.getPlayer().colision(x_position, y_position)) { //czy kolizja nast¹pi³a
			owner.getShots().remove(this); //usuñ strza³ z listy pocisków gracza
			game.getAliensShot().remove(this);
			game.getPlayer().decreaseHealth(1); //zmniejsz pkt ¿ycia gracza
			stop(); // zakoñcz w¹tek
			}
		}
}

public void render(Graphics g) { //wyœwietlanie grafiki pocisku
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

public boolean isRunning() {//aktywnoœæ w¹tku pocisku
	return running;
}

public void rePause(){ //do wznawiania po pauzie
	if(running)
		return;
	if(y_position<(game.getHEIGHT()+height)) { //jeœli pocisk znajdujê siê w polu gry
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
		thread.start();   			//wola metode run
	}
}

public void pause(){ //do pauzowania
	if(!running)
		return;
	if(y_position<(game.getHEIGHT()+height)) { //jeœli pocisk znajduje siê w polu gry
		running = false;
		try {
			thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

}