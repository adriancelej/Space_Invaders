package game;

import java.awt.Graphics; //grafika
import java.awt.event.*; //zdarzenia timera, u�ytkownika
import java.awt.Image; //bufor graficzny
import java.awt.image.*; //tworzenie bufora
import javax.swing.Timer; //do generowania zdarze� od�wie�ania grafiki
import java.util.concurrent.CopyOnWriteArrayList; //lista wskaznikowa na stworki, strza�y
import java.util.Iterator; //przegl�danie kolekcji
import charakter.*; //postacie
import display.*; //tworzenie okna
import loader.*;//�adowanie plik�w konfiguracyjnych gry, grafiki
import keyboard.KeyManager;//obsluga klawiatury
import network.*; //funkcjonalno�� sieciowa

public class Game implements Runnable, ActionListener { //g��wna klasa gry
	
	private Display_1 display; //okno
	private int width, height; //rozmiar okna
	private final static int WIDTH=1366; //szeroko�� grafiki bufora
	private final static int HEIGHT=768; //wysoko�� grafiki bufora
	private final int fps=1000/30; //czas od�wie�ania grafiki
	private String title; //tytu� okna
	
	private boolean running=false; //aktywno�� gry
	private boolean gameOver=false;
	private Player player; //gracz
	private CopyOnWriteArrayList<Alien> aliens= new CopyOnWriteArrayList<Alien>(); //lista wszystkich stwork�w istniej�cyh w grze
	private CopyOnWriteArrayList<Alien2> aliens2= new CopyOnWriteArrayList<Alien2>();
	private Thread thread;//w�tek g��wny gry
	private int score; //wynik gracza
	private int actualLevel; //aktualny poziom gry
	private int delay; //migotanie grafiki gameOver
	private final int blink=20; //migotanie grafiki gameOver
	
	private Image buffer; //bufor graficzny, klatka do wy�wietlenia
	private Graphics bufferedGraphics; //element graficzny bufora
	private Timer timer; //generuje zdarzenia od�wie�aj�ce grafik�
	private LoadedImage images=new LoadedImage();//grafiki t�a,gracza,stwork�w
	private KeyManager keymanager=new KeyManager();//obsluga zdarzen klawiatury
	private WorldLoader worldloader=new WorldLoader(); //�aduje pocz�tkowe po�o�enia stwork�w z pliku
	private Network network; //obs�uga gry sieciowej
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
	
	private int getWidth() { //zwraca aktualn� szeroko�� okna
		return display.getGameGraphics().getWidth();
	}
	
	private int getHeight() { //zwraca aktualn� wysoko�� okna
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
	
	private void updateScore() { //aktualizuje wynik do wy�wietlenia
		display.setScore(score);
	}
	
	private void updateHealth() { //aktualizuje pkt �ycia do wy�wietlenia
		display.setHealth(player.getHealthPoints());
	}
	
	private void setBackground(Graphics g) {//rysuje t�o
		g.drawImage(images.getBackground(actualLevel),0,0,getWIDTH(),getHEIGHT(),null);
	}
	
	public CopyOnWriteArrayList<Alien> getAliens(){ //dost�p do wszystkich stwork�w w grze
		return aliens;
	}
	
	public CopyOnWriteArrayList<Alien2> getAliens2(){ //dost�p do wszystkich stwork�w w grze
		return aliens2;
	}
	
	public CopyOnWriteArrayList<AlienShot> getAliensShot(){ //dost�p do wszystkich stwork�w w grze
		return shots;
	}
	
	public CopyOnWriteArrayList<AlienShot2> getAliensShot2(){ //dost�p do wszystkich stwork�w w grze
		return shots2;
	}
	
	public void addShots(CopyOnWriteArrayList<AlienShot> sh) { //dodaje strza�y stwork�w Alien, wykorzystywane gdy stworek zgin��
		shots.addAll(sh);
	}
	
	public void addShots2(CopyOnWriteArrayList<AlienShot2> sh) { //dodaje strza�y stwork�w Alien2, wykorzystywane gdy stworek zgin��
		shots2.addAll(sh);
	}
	
	public Player getPlayer() { //dost�p do gracza
		return player;
	}
	
	public LoadedImage getImage() { //dost�p do grafik u�ywanych w grze (obrazy png)
		return images;
	}
	
	public void reverseSpeed() { //odwraca kierunek ruchu wszystkich stwork�w i obni�a ich po�o�enie
		aliens.forEach(element->element.reverse_Speed(getHeight()));
	}
	
	public void reverseSpeed2() { //odwraca kierunek ruchu wszystkich stwork�w i obni�a ich po�o�enie
		aliens2.forEach(element->element.reverse_Speed(getHeight()));
	}
	
	private void newGame() { // uruchamia now� gr�
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
	
	private void loadAliens() {//tworzy obiekty stwork�w i rozmieszcza je na planszy
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
		aliens.forEach(element->element.start());//uruchamia w�tki stwork�w, inicjuje ich ruch
		aliens2.forEach(element->element.start());//uruchamia w�tki stwork�w, inicjuje ich ruch
		player.start();
		timer.start();
	}
	
	private void renderAliens(Graphics g) { //wy�wietlanie wszystkich stwork�w
		Iterator<Alien> iter = aliens.iterator(); 
			while(iter.hasNext()) {
			iter.next().render(g);
		}
			Iterator<Alien2> iter2 = aliens2.iterator(); 
			while(iter2.hasNext()) {
			iter2.next().render(g);
		}
	}
	
	private void renderAliensShots(Graphics g) { //wy�wietlanie strza��w stwork�w, kt�re zgine�y
		Iterator<AlienShot> iter = shots.iterator(); 
			while(iter.hasNext()) {
			iter.next().render(g);
		}
			Iterator<AlienShot2> iter2 = shots2.iterator(); 
			while(iter2.hasNext()) {
			iter2.next().render(g);
		}
	}
		
	
	private void gameInterface() { // obs�uga pozycji menu
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
					 initNetworkGame(); //inicjuj gr� sieciow�
				 }
			}
		});
	}
	
	private void initGame() {//przygotowanie gry
		networkGame=false;
		setBuffer(); //utw�rz bufor
		images.load(); //�aduje obrazy u�ywane w grze
		display = new Display_1(title, width, height);//wyswietlanie ramki
		display.addKeyListener(keymanager);	//sterowanie klawiszami w ramce
		String nick=display.getPlayerName();
		timer = new Timer(fps,this);
		player=new Player(nick,getWIDTH()/2,getHEIGHT(),8,3,images.getPlayer(), images.getShot(),this);//tworzy gracza(x,y,pr�dko��,�ycie,sk�rka,gra)
		worldloader.loadWorld(1);//�adowanie pliku �wiata
		this.loadAliens();//tworzy obiekty stwork�w i rozmieszcza je na planszy
		aliens.forEach(element->element.start());//uruchamia w�tki stwork�w, inicjuje ich ruch
		aliens2.forEach(element->element.start());//uruchamia w�tki stwork�w, inicjuje ich ruch
		player.start();
		this.gameInterface();
		actualLevel=1;
		gameOver=false;
		timer.start();
		resultsTable=new ResultsTable();
	}
	
	private void isGameOver() { //sprawdza czy gracz posiada pkt �ycia, je�li nie zaka�cza gr� i wy�wietla grafik� gameOver
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
	
	private void isNetworkGameOver() { //sprawdza czy gracz posiada pkt �ycia, je�li nie zaka�cza gr� i wy�wietla grafik� gameOver
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
	
	private void showGameOver(Graphics g) { //wy�wietla grafik� gameOver
		if(delay>=0 && delay<=blink/2 && gameOver) { //miganie grafiki gameOver
			g.drawImage(images.getGameOver(),getWIDTH()/4,getHEIGHT()/4,getWIDTH()/2,getHEIGHT()/2,null);
		}
	}
	
	private void loadNextLevel() { //�aduje nast�pny poziom
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
	
	private void setBuffer() { //przygotowuj� bufor - klatke, kt�ra b�dzie wy�wietlana
		buffer= new BufferedImage(getWIDTH(),getHEIGHT(),BufferedImage.TYPE_INT_ARGB);
		buffer.setAccelerationPriority(1);
		bufferedGraphics = buffer.getGraphics(); //pobiera element graficzny buufora
		}
	
	@Override
	public void run(){//g��wny w�tek gry
		this.initGame();//inicjuj gr� 
		
		while(running){
			try {
				this.updateScore(); //aktualizuje wynik
				this.updateHealth(); //aktualizuje �ycie
				this.loadNextLevel(); //�aduje kolejny poziom, je�li uko�czono aktualny	
				this.networkLoadNextLevel();
				keymanager.check(); //sprawdza zdarzenia klawiatury
				isGameOver(); //sprawdza koniec gry
				isNetworkGameOver();
				Thread.sleep(100);//u�pij w�tek 
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
		
		setBackground(bufferedGraphics); //narysuj t�o
		player.render(bufferedGraphics); //narysuj gracza
		this.renderAliens(bufferedGraphics); //narysuj stworki
		this.renderAliensShots(bufferedGraphics); //narysuj strza�y stwork�w, kt�re zgine�y
		this.showGameOver(bufferedGraphics); //rysuje grafik� gameOver
		if(display.getGameGraphics().getGraphics()!=null)
		display.getGameGraphics().getGraphics().drawImage(buffer, 0, 0, getWidth(), getHeight(), null); //wy�wietla klatk� gry
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
	
	public KeyManager getKeyManager() { //dost�p do obs�ugi klawiatury
			return keymanager;
		}

}
