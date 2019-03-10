package charakter;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import game.Game;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList; //pozwala na pracê z wieloma w¹tkami

public class Player extends Charakter{

	private int health_points; //punkty ¿ycia
	private int height,width; //rozmiary w px
	private BufferedImage player_apperence, shot; //grafika gracza i jego strza³u
	private Game game; //gra do której nale¿y gracz
	private boolean running; //aktywnoœæ w¹tku gracza
	private Thread thread; //w¹tek gracza
	private CopyOnWriteArrayList<Shot> shots = new CopyOnWriteArrayList<Shot>(); //lista strza³ów gracza
	private int delay; // wykorzystywane do opó¿nienia czêstotliwoœci strza³ów gracza
	private final static int canShot=10; // jak wy¿ej
	private final String playerName;
	
	public Player(String name, int x, int y, int speed, int health, BufferedImage apperence, BufferedImage shot, Game current) {
		super(x,y,speed);
		playerName=name;
		health_points=health; 
		player_apperence=apperence; 
		game=current;
		height= current.getHEIGHT()/5;
		width= current.getWIDTH()/8;
		this.shot=shot;
		y_position= game.getHEIGHT()-(height);
	}
	
	@Override
	public void run() {
		while (running) {
	    	try {
	    		this.updatePosition(); //nie pozwala stworkom wyjœæ poza granicê ekranu
	    		this.move(); //ruch 
	    		this.shoot(); //odpowiedzialna za oddawanie strza³ów przez gracza
	    		Thread.sleep(50); //odœwie¿aj co 50ms
	    	}
	    	catch(InterruptedException e) { //wyj¹tek wynikaj¹cy z usypiania w¹tku 
	    		e.printStackTrace();
	    	}
	    }
	}
	
	
	@Override
	protected void move() { //ruch gracza
		if(game.getKeyManager().left) { //wciœniêto klawisz "w lewo"
			if(x_position>0)
				x_position -= move_speed; 
		}
		if(game.getKeyManager().right) { // "w prawo"
			if(x_position<(game.getWIDTH()-width))
				x_position += move_speed;
		}
	}
	
	private void updatePosition() { //nie pozwla stworkom wyjœæ poza ekran
		if(x_position>(game.getWIDTH()-width)) {
			x_position=game.getWIDTH()-width;
		}
	}
	public CopyOnWriteArrayList<Shot> getShots() { //dostêp do strza³ów gracza
		return shots; 	
	}
	
	protected void shoot() { //strzelanie
		if(game.getKeyManager().space) { // wciœniêto "spacje"
			if(delay>canShot) {
				//jeœli opó¿nienie osi¹gne³o wartoœæ wiêksz od granicznej - canShot
			shots.add(new Shot((x_position+(width/4)), y_position,15, shot, game, this)); //utwórz obiekt strza³u i dodaj go do listy
			delay=0;
			}
		}
		delay++;
	}
	
	public boolean colision(int x, int y) { //sprawdza kolizje z pociskiem stworka
		if(x >= x_position  && x <= x_position+width && y_position>=y-(height/2) && y_position<= y){
			return true; //nast¹pi³a
		}
			return false; // nie nast¹pi³a
	}
	
	public int getHealthPoints() { //zwraca liczbê pkt ¿ycia
		return health_points;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public String getName() {
		return playerName;
	}
	
	public void decreaseHealth(int hp) { //zmniejsza liczbê punktów ¿ycia
		health_points-=hp;
	}
	
	public void killPlayer() {
		health_points=0;
	}
	
	public void render(Graphics g) { //wyœwietlanie grafiki gracza i jego strza³ów
		g.drawImage(player_apperence, x_position, y_position, width, height,null);
		Iterator<Shot> iter = shots.iterator(); 
		while(iter.hasNext())
		iter.next().render(g);
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
	
	public synchronized void pause(){ //do pauzowania
		if(!running)
			return;
		running = false;
		shots.forEach(element->element.pause()); //pauzuje straz³y gracza
		try {
			thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void rePause(){ //do wznawiania po pauzie
		if(running)
			return;
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
		thread.start();   			//wola metode run
		shots.forEach(element->element.rePause()); //wznawia strza³y gracza
	}
	
}
