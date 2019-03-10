package game;

import java.awt.Graphics; //grafika
import java.awt.event.*; //zdarzenia timera, u¿ytkownika
import java.awt.Image; //bufor graficzny
import java.awt.image.*; //tworzenie bufora
import javax.swing.Timer; //do generowania zdarzeñ odœwie¿ania grafiki
import java.util.concurrent.CopyOnWriteArrayList; //lista wskaznikowa na stworki, strza³y
import java.util.Iterator; //przegl¹danie kolekcji
import charakter.*; //postacie
import display.*; //tworzenie okna
import loader.*;//³adowanie plików konfiguracyjnych gry, grafiki
import keyboard.KeyManager;//obsluga klawiatury
import network.*; //funkcjonalnoœæ sieciowa

public class Game implements Runnable, ActionListener { //g³ówna klasa gry
	
	private Display_1 display; //okno
	private int width, height; //rozmiar okna
	private final static int WIDTH=1366; //szerokoœæ grafiki bufora
	private final static int HEIGHT=768; //wysokoœæ grafiki bufora
	private final int fps=1000/30; //czas odœwie¿ania grafiki
	private String title; //tytu³ okna
	
	private boolean running=false; //aktywnoœæ gry
	private boolean gameOver=false;
	private Player player; //gracz
	private CopyOnWriteArrayList<Alien> aliens= new CopyOnWriteArrayList<Alien>(); //lista wszystkich stworków istniej¹cyh w grze
	private CopyOnWriteArrayList<Alien2> aliens2= new CopyOnWriteArrayList<Alien2>();
	private Thread thread;//w¹tek g³ówny gry
	private int score; //wynik gracza
	private int actualLevel; //aktualny poziom gry
	private int delay; //migotanie grafiki gameOver
	private final int blink=20; //migotanie grafiki gameOver
	
	private Image buffer; //bufor graficzny, klatka do wyœwietlenia
	private Graphics bufferedGraphics; //element graficzny bufora
	private Timer timer; //generuje zdarzenia odœwie¿aj¹ce grafikê
	private LoadedImage images=new LoadedImage();//grafiki t³a,gracza,stworków
	private KeyManager keymanager=new KeyManager();//obsluga zdarzen klawiatury
	private WorldLoader worldloader=new WorldLoader(); //³aduje pocz¹tkowe po³o¿enia stworków z pliku
	private Network network; //obs³uga gry sieciowej
	private CopyOnWriteArrayList<AlienShot> shots = new CopyOnWriteArrayList<AlienShot>();
	private CopyOnWriteArrayList<AlienShot2> shots2 = new CopyOnWriteArrayList<AlienShot2>();
	private ResultsTable resultsTable;
	private String IPadress, portNumber;
	private boolean networkGame=false; 

	
	public Game(String title, int width, int height){ //konstruktor gry
		this.width = width;
		this.height = height;
		this.title = title;
	}
	
	private int getWidth() { //zwraca aktualn¹ szerokoœæ okna
		return display.getGameGraphics().getWidth();
	}
	
	private int getHeight() { //zwraca aktualn¹ wysokoœæ okna
		return display.getGameGraphics().getHeight();
	}
	
	public static int getWIDTH() {
		return WIDTH;
	}
	
	public static int getHEIGHT() {
		return HEIGHT;
	}
	
	public void addScore(int sc){ //dodaje wynik
		score+=sc;
	}
	
	private void updateScore() { //aktualizuje wynik do wyœwietlenia
		display.setScore(score);
	}
	
	private void updateHealth() { //aktualizuje pkt ¿ycia do wyœwietlenia
		display.setHealth(player.getHealthPoints());
	}
	
	private void setBackground(Graphics g) {//rysuje t³o
		g.drawImage(images.getBackground(actualLevel),0,0,getWIDTH(),getHEIGHT(),null);
	}
	
	public CopyOnWriteArrayList<Alien> getAliens(){ //dostêp do wszystkich stworków w grze
		return aliens;
	}
	
	public CopyOnWriteArrayList<Alien2> getAliens2(){ //dostêp do wszystkich stworków w grze
		return aliens2;
	}
	
	public CopyOnWriteArrayList<AlienShot> getAliensShot(){ //dostêp do wszystkich stworków w grze
		return shots;
	}
	
	public CopyOnWriteArrayList<AlienShot2> getAliensShot2(){ //dostêp do wszystkich stworków w grze
		return shots2;
	}
	
	public void addShots(CopyOnWriteArrayList<AlienShot> sh) { //dodaje strza³y stworków Alien, wykorzystywane gdy stworek zgin¹³
		shots.addAll(sh);
	}
	
	public void addShots2(CopyOnWriteArrayList<AlienShot2> sh) { //dodaje strza³y stworków Alien2, wykorzystywane gdy stworek zgin¹³
		shots2.addAll(sh);
	}
	
	public Player getPlayer() { //dostêp do gracza
		return player;
	}
	
	public LoadedImage getImage() { //dostêp do grafik u¿ywanych w grze (obrazy png)
		return images;
	}
	
	public void reverseSpeed() { //odwraca kierunek ruchu wszystkich stworków i obni¿a ich po³o¿enie
		aliens.forEach(element->element.reverse_Speed(getHeight()));
	}
	
	public void reverseSpeed2() { //odwraca kierunek ruchu wszystkich stworków i obni¿a ich po³o¿enie
		aliens2.forEach(element->element.reverse_Speed(getHeight()));
	}
	
	private void newGame() { // uruchamia now¹ grê
		display.setNewGameListener(new ActionListener() {
			 public void actionPerformed(ActionEvent event) {
				 if(display.showNewGameDialog()==1) {
					 aliens.forEach(element->element.stop());
					 aliens2.forEach(element->element.stop());
					 player.stop();
					 aliens.clear();
					 aliens2.clear();
					 shots.clear();
					 shots2.clear();
					 score=0;
					 delay=0;
					 actualLevel=1;
					 gameOver=false;
					 networkGame=false;
					 display.dispose();
					 initGame(); //inicjuj 1-wszy poziom
				 }
			}
		});
	}
	
	private void loadAliens() {//tworzy obiekty stworków i rozmieszcza je na planszy
		int table[][]=worldloader.getInitAliensLocation();
		for(int y=0;y<worldloader.getHeight();++y) {
			for(int x=0;x<worldloader.getWidth();++x) {
				if(table[x][y]==1) {
					aliens.add(new Alien((x*this.getWIDTH()/worldloader.getWidth())+10*y,(y*48)+5,worldloader.getAlienSpeed(), 1, worldloader.getShotingFreq(),images.getAlien(),this));
				}
				if(table[x][y]==2) {
					aliens2.add(new Alien2((x*this.getWIDTH()/worldloader.getWidth())+10*y,(y*48)+5,worldloader.getAlienSpeed(), 3, worldloader.getShotingFreq(),images.getAlien2(),this));
				}
			}
		}
	}
	
	private void loadAliensFromNetwork() {
		int table[][]= network.getMap();
		for(int y=0;y< network.getHeight();++y) {
			for(int x=0;x< network.getWidth();++x) {
				if(table[x][y]==1) {
					aliens.add(new Alien((x*this.getWIDTH()/network.getWidth())+10*y,(y*48)+5,network.getAlienSpeed(), 1, network.getShootingFreq(),images.getAlien(),this));
				}
				if(table[x][y]==2) {
					aliens2.add(new Alien2((x*this.getWIDTH()/network.getWidth())+10*y,(y*48)+5,network.getAlienSpeed(), 3, network.getShootingFreq(),images.getAlien2(),this));
				}
			}
		}
	}
	
	private void initNetworkGame() {
		networkGame=true;
		actualLevel=1;
		network=new Network(actualLevel);
		display= new Display_1(title, width, height);
		display.addKeyListener(keymanager);
		IPadress= display.getIPAdress();
		portNumber= display.getPortNumber();
		network.bind(IPadress, portNumber);
		network.start();
		images.load();
		this.gameInterface();
		String nick = display.getPlayerName();
		player= new Player(nick, getWIDTH()/2, getHEIGHT()/2, 8, 3, images.getPlayer(), images.getShot(), this);
		setBuffer();
		timer= new Timer(fps, this);
		gameOver=false;
		loadAliensFromNetwork();
		resultsTable=new ResultsTable(network.getPlayerNames(), network.getPlayerResults());
		aliens.forEach(element->element.start());//uruchamia w¹tki stworków, inicjuje ich ruch
		aliens2.forEach(element->element.start());//uruchamia w¹tki stworków, inicjuje ich ruch
		player.start();
		timer.start();
	}
	
	private void renderAliens(Graphics g) { //wyœwietlanie wszystkich stworków
		Iterator<Alien> iter = aliens.iterator(); 
			while(iter.hasNext()) {
			iter.next().render(g);
		}
			Iterator<Alien2> iter2 = aliens2.iterator(); 
			while(iter2.hasNext()) {
			iter2.next().render(g);
		}
	}
	
	private void renderAliensShots(Graphics g) { //wyœwietlanie strza³ów stworków, które zgine³y
		Iterator<AlienShot> iter = shots.iterator(); 
			while(iter.hasNext()) {
			iter.next().render(g);
		}
			Iterator<AlienShot2> iter2 = shots2.iterator(); 
			while(iter2.hasNext()) {
			iter2.next().render(g);
		}
	}
		
	
	private void gameInterface() { // obs³uga pozycji menu
		this.pause();
		this.rePause();
		this.newGame();
		this.showBestResultsTable();
		this.startNetworkGame();
	}
	
	private synchronized void pause() { //pauzownaie
			display.setPauseListener(new ActionListener() {
				 public void actionPerformed(ActionEvent event) {
					 aliens.forEach(element->element.pause());
					 aliens2.forEach(element->element.pause());
					 shots.forEach(element->element.pause());
					 shots2.forEach(element->element.pause());
					 player.pause();
					 display.setPauseVisibility(false);
					 display.setRePauseVisibility(true);
				}
			});
	}
	
	private synchronized void rePause() { //wznawianie gry po pauzie
		display.setRePauseListener(new ActionListener() {
			 public void actionPerformed(ActionEvent event) {
				 aliens.forEach(element->element.rePause());
				 aliens2.forEach(element->element.rePause());
				 shots.forEach(element->element.rePause());
				 shots2.forEach(element->element.rePause());
				 player.rePause();
				 display.setPauseVisibility(true);
				 display.setRePauseVisibility(false);
			}
		});
	}
	
	private void showBestResultsTable() {
		display.setBestResultsListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent event) {
				resultsTable.createResultsTable();
				display.createResultsFrame(resultsTable);
			}
		});
	}
	
	private void startNetworkGame() {
		display.setNetworkListener(new ActionListener( ) {
			public void actionPerformed(ActionEvent event) {
				if(display.showNetworkGameDialog()==1) {
					 aliens.forEach(element->element.stop());
					 aliens2.forEach(element->element.stop());
					 player.stop();
					 aliens.clear();
					 aliens2.clear();
					 shots.clear();
					 shots2.clear();
					 score=0;
					 delay=0;
					 actualLevel=1;
					 gameOver=false;
					 display.dispose();
					 initNetworkGame(); //inicjuj grê sieciow¹
				 }
			}
		});
	}
	
	private void initGame() {//przygotowanie gry
		networkGame=false;
		setBuffer(); //utwórz bufor
		images.load(); //³aduje obrazy u¿ywane w grze
		display = new Display_1(title, width, height);//wyswietlanie ramki
		display.addKeyListener(keymanager);	//sterowanie klawiszami w ramce
		String nick=display.getPlayerName();
		timer = new Timer(fps,this);
		player=new Player(nick,getWIDTH()/2,getHEIGHT(),8,3,images.getPlayer(), images.getShot(),this);//tworzy gracza(x,y,prêdkoœæ,¿ycie,skórka,gra)
		worldloader.loadWorld(1);//³adowanie pliku œwiata
		this.loadAliens();//tworzy obiekty stworków i rozmieszcza je na planszy
		aliens.forEach(element->element.start());//uruchamia w¹tki stworków, inicjuje ich ruch
		aliens2.forEach(element->element.start());//uruchamia w¹tki stworków, inicjuje ich ruch
		player.start();
		this.gameInterface();
		actualLevel=1;
		gameOver=false;
		timer.start();
		resultsTable=new ResultsTable();
	}
	
	private void isGameOver() { //sprawdza czy gracz posiada pkt ¿ycia, jeœli nie zakañcza grê i wyœwietla grafikê gameOver
		if(player.getHealthPoints()<=0 && !gameOver && !networkGame) {
			gameOver=true;
			player.pause();
			aliens.forEach(element->element.pause());
			aliens2.forEach(element->element.pause());
			display.setPauseVisibility(false);
			display.setRePauseVisibility(false);
			resultsTable.addResult(player.getName(), score);
			resultsTable.createResultsTable();
			resultsTable.saveResults();
			display.createResultsFrame(resultsTable);
		}
			if(delay>=blink && gameOver)
				delay=0;
			++delay;	
	}
	
	private void isNetworkGameOver() { //sprawdza czy gracz posiada pkt ¿ycia, jeœli nie zakañcza grê i wyœwietla grafikê gameOver
		if(player.getHealthPoints()<=0 && !gameOver && networkGame) {
			gameOver=true;
			player.pause();
			aliens.forEach(element->element.pause());
			aliens2.forEach(element->element.pause());
			display.setPauseVisibility(false);
			display.setRePauseVisibility(false);
			network=new Network();
			network.bind(IPadress, portNumber);
			network.setPlayerResult(player.getName(), score);
			network.start();
			resultsTable=new ResultsTable(network.getPlayerNames(), network.getPlayerResults());
			display.createResultsFrame(resultsTable);
		}
			if(delay>=blink && gameOver==true)
				delay=0;
			++delay;	
	}
	
	private void showGameOver(Graphics g) { //wyœwietla grafikê gameOver
		if(delay>=0 && delay<=blink/2 && gameOver) { //miganie grafiki gameOver
			g.drawImage(images.getGameOver(),getWIDTH()/4,getHEIGHT()/4,getWIDTH()/2,getHEIGHT()/2,null);
		}
	}
	
	private void loadNextLevel() { //³aduje nastêpny poziom
		if(aliens.isEmpty() && aliens2.isEmpty() && this.score!=0 && networkGame==false) {
			++actualLevel;
			worldloader.loadWorld(actualLevel);
			this.loadAliens();
			aliens.forEach(element->element.start());
			aliens2.forEach(element->element.start());
		}
	}
	
	private void networkLoadNextLevel() {
		if(aliens.isEmpty() && aliens2.isEmpty() && this.score!=0 && networkGame==true && gameOver==false) {
			++actualLevel;
			networkGame=true;
			network=new Network(actualLevel);
			network.bind(IPadress, portNumber);
			network.start();
			this.loadAliensFromNetwork();
			aliens.forEach(element->element.start());
			aliens2.forEach(element->element.start());
		}
	}
	
	private void setBuffer() { //przygotowujê bufor - klatke, która bêdzie wyœwietlana
		buffer= new BufferedImage(getWIDTH(),getHEIGHT(),BufferedImage.TYPE_INT_ARGB);
		buffer.setAccelerationPriority(1);
		bufferedGraphics = buffer.getGraphics(); //pobiera element graficzny buufora
		}
	
	@Override
	public void run(){//g³ówny w¹tek gry
		this.initGame();//inicjuj grê 
		
		while(running){
			try {
				this.updateScore(); //aktualizuje wynik
				this.updateHealth(); //aktualizuje ¿ycie
				this.loadNextLevel(); //³aduje kolejny poziom, jeœli ukoñczono aktualny	
				this.networkLoadNextLevel();
				keymanager.check(); //sprawdza zdarzenia klawiatury
				isGameOver(); //sprawdza koniec gry
				isNetworkGameOver();
				Thread.sleep(100);//uœpij w¹tek 
	    	}
	    	catch(InterruptedException e) {
	    		e.printStackTrace();
	    	}
		}
		this.stop();
		bufferedGraphics.dispose(); //zwraca zasoby do systemu operacyjnego
		display.dispose();
	}
	
	public void actionPerformed(ActionEvent evt){
		
		setBackground(bufferedGraphics); //narysuj t³o
		player.render(bufferedGraphics); //narysuj gracza
		this.renderAliens(bufferedGraphics); //narysuj stworki
		this.renderAliensShots(bufferedGraphics); //narysuj strza³y stworków, które zgine³y
		this.showGameOver(bufferedGraphics); //rysuje grafikê gameOver
		if(display.getGameGraphics().getGraphics()!=null)
		display.getGameGraphics().getGraphics().drawImage(buffer, 0, 0, getWidth(), getHeight(), null); //wyœwietla klatkê gry
		display.menuUpdate(); //aktualizuje kontekst graficzny menu gry
    }
	
	//metody startujace i zatrzymujace watek
	
	public synchronized void start(){ //synchronized - zawsze tego uzywamy w metodach start i stop
		if(running)
			return;
		running = true;
		thread = new Thread(this);	//tworzymy nowy watek. this oznacza klase game
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
	
	public KeyManager getKeyManager() { //dostêp do obs³ugi klawiatury
			return keymanager;
		}

}
