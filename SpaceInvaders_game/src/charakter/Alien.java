package charakter;

import java.awt.image.BufferedImage;
import java.awt.Graphics;
import game.Game;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList; //nie wyrzuca ConcurrentModificationException

public class Alien extends Charakter {
	private int health_points; //pkt ¿ycia potworka
	private int height; //wysokoœæ stworka w px
	private int width;  //szerokoœæ stworka w px
	private int frameWidth, frameHeight; //rozmiary obszaru wyœwietlania
	private int shotingFreq; //im mniejsza wartoœæ tym stworek czêœciej strzela
	private static int FREQ=2; //sta³a zwi¹zana z czêstoœci¹ strzelania swtorka, im wiêksza tym czêœciej 
	private boolean running; //aktywnoœæ w¹tku stworka
	private Thread thread; //w¹tek stworka
	private BufferedImage alien_apperence; //grafika stworka
	private Game game; //gra, do której nale¿y stworek
	private CopyOnWriteArrayList<AlienShot> shots= new CopyOnWriteArrayList<AlienShot>(); //strza³y wygenerowane przez stworka
	
	public Alien(int x, int y, int speed, int health, int freq, BufferedImage apperence,Game current) {
		super(x,y,speed);
		health_points=health; 
		shotingFreq=freq;
		alien_apperence=apperence; 
		game=current;
		width=game.getWIDTH()/10;
		height=game.getHEIGHT()/10;
	}
	
	@Override
	public void run() {//w¹tek stworka
    while (running) {
    	try {
    		this.shot(); //generuje strza³ 
    		this.move(); //ruch stworka
   			this.playerColision(game.getPlayer().getX_position(), game.getPlayer().getY_position(), game.getPlayer().getWidth(), game.getPlayer().getHeight());
   			Thread.sleep(40); //odœwie¿anie co 40 ms (25 razy na sekunde)	
    		}
    	catch(InterruptedException e) {//wyj¹tek zwi¹zany z usupianiem w¹tku
    			e.printStackTrace();
    		}
    	}
	}
	
	@Override
	protected void move() {//ruch stworka
		if(x_position<=0 && move_speed<0) { //lewa granica okna
			game.reverseSpeed(); //odwraca kierunek ruchu wszystkich stworków w grze 
		}
		if(x_position>=(game.getWIDTH()-width) && move_speed>0) { //prawa granica okna
			game.reverseSpeed();
		}
		x_position+=move_speed; //porusz stworka o move_speed pixeli
	}
	
	
	public void render(Graphics g) {//wyœwietlanie stworka
		g.drawImage(alien_apperence, x_position, y_position, width, height,null);
		// wyœwietlanie strza³ów stworka
		Iterator<AlienShot> iter = shots.iterator(); 
		while(iter.hasNext()) 
		iter.next().render(g);
	}
	
	public boolean isRunning() { //czy w¹tek aktywny
		return running;
	}
	
	public boolean colision(int x, int y) { // czy nast¹pi³a kolizja stworka i strza³u gracza
		if(x_position>= (x-width/2) && x_position<=(x+ width/4) && y_position>=(y-height/2) && y_position<= (y+height/2)){
			game.getAliens().remove(this); //usuñ stworka z gry
			game.addScore(1); //zwiêksz punkty
			game.addShots(getShots());
			this.stop(); // zakoñcz w¹tek stworka
			return true; //kolizja nast¹pi³a
		}
		return false; //kolizja nie nast¹pi³a
	}
	
	public void playerColision(int x, int y, int width, int height) { //sprawdza czy nast¹pi³a kolizja z graczem lub doln¹ krawêdzi¹ ekranu
		if((x_position+this.width/2)> x && x_position<(x+width/2) && (y_position + this.height/2)> y || (y_position+this.height)> game.getHEIGHT()) {
			game.getPlayer().killPlayer();
		}
			
	}
	
	private void shot() { //strza³ storka
		double rand= Math.random()*shotingFreq; //odbywa siê losowo
		if(rand<=FREQ) {
			//utwórz nowy pocisk (strza³) - dodaj do listy
			shots.add(new AlienShot(x_position+width/4,y_position+height/4,15,game.getImage().getAlienShot(),game,this));
		}
	}
	
	public CopyOnWriteArrayList<AlienShot> getShots(){ //dostêp do strza³ów
		return shots;
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
	
	public synchronized void rePause(){ //u¿ywane do wznawiania gry po pauzie 
		if(running)
			return;
		shots.forEach(element->element.rePause()); //wznów strza³y nale¿ace do stworka
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza obiekt klasy Alien
		thread.start();   			//wola metode run
	}
	
	public synchronized void pause(){ //do pauzowania
		if(!running)
			return;
		running = false;
		shots.forEach(element->element.pause()); // pauzuje strza³y stworka
		try {
			thread.join();		//bezpieczne zatrzymanie watku <= musi to byc zaimplementowane w wyjatku
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
