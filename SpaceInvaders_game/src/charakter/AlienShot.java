package charakter;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import game.Game;

public class AlienShot extends Charakter{
private BufferedImage shot; //grafika strz�u
private boolean running; //aktywno��
private int width,height; // rozmiaru w px
private Thread thread; //w�tek 
private Game game; //gra do kt�rej nale�y
private Alien owner; //stworek do kt�rego nale�y

	
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
    		Thread.sleep(50); //od�wie� co 40 ms
    		move();//ruch pocisku
    		checkColision();//kolizja z graczem
    	}
    	catch(InterruptedException e) { //od usypiania w�tku
    		e.printStackTrace();
    	}
    }
}

public void move() { //ruch pocisku
	y_position+= move_speed; 
	if(y_position>(game.getHEIGHT()+height)) {
		//je�li pocisk znajdzie si� poza obszrem gry 
		owner.getShots().remove(this); //usu� go z listy pocisk�w stworka
		stop(); //zako�cz w�tek
	}
}

private void checkColision() { //sprawd� kolizj� z graczem
	if(isRunning()) {
		if(game.getPlayer().colision(x_position, y_position)) { //czy kolizja nast�pi�a
			owner.getShots().remove(this); //usu� strza� z listy pocisk�w gracza
			game.getAliensShot().remove(this);
			game.getPlayer().decreaseHealth(1); //zmniejsz pkt �ycia gracza
			stop(); // zako�cz w�tek
			}
		}
}

public void render(Graphics g) { //wy�wietlanie grafiki pocisku
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

public boolean isRunning() {//aktywno�� w�tku pocisku
	return running;
}

public void rePause(){ //do wznawiania po pauzie
	if(running)
		return;
	if(y_position<(game.getHEIGHT()+height)) { //je�li pocisk znajduj� si� w polu gry
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
		thread.start();   			//wola metode run
	}
}

public void pause(){ //do pauzowania
	if(!running)
		return;
	if(y_position<(game.getHEIGHT()+height)) { //je�li pocisk znajduje si� w polu gry
		running = false;
		try {
			thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}

}