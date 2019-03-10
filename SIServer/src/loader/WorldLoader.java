package loader;


public class WorldLoader implements Runnable{
	
	private int width,height;//liczba alien�w w poziomie i pionie
	private volatile int alienSpeed; //pr�dko�� stwork�w
	private volatile int shootingFreq; //odpowiada za cz�sto�� stzrelania stworka, im ni�sza tym stworek cz�ciej strzela
	private int tiles[][];   //rozmieszczenie stwork�w na mapie
	private volatile String[] aliens;
	private String file;    
	private TextLoader textloader;
	private volatile boolean loaded=false;
	private int level;
	private Thread loader;

	public synchronized void loadWorld(int level ){
		this.level=level;
		loader=new Thread(this);
		loader.start();
	}
	
	public synchronized void run() {
		StringBuilder path=new StringBuilder("src/resocures/world");
		path.append(level);
		path.append(".txt");
		textloader=new TextLoader();
		if(level==1) {
			file = textloader.loadFileAsString(path.toString()); //wczytanie pliku konfiguracyjnego
		}else {
			file=textloader.loadFileAsString(path.toString());
			 
				while(file==null) {
					System.out.println("Nie odnaleziono pliku tego levelu, u�ywam poprzedniego");
					path=new StringBuilder("src/resocures/world");
					path.append(--level);
					path.append(".txt");
					file = textloader.loadFileAsString(path.toString());
				}
		}
			
		aliens = file.split("\\s+");	// laduje do tablicy tokens kolejne elementy oddzielone spacja
		width = TextLoader.parseInt(aliens[0]);	//maksymalna liczba stwork�w w p�aszczyznie x
		height = TextLoader.parseInt(aliens[1]); //maksymalna liczba stwork�w w p�aszczyznie y
		alienSpeed = TextLoader.parseInt(aliens[2]);
		shootingFreq = TextLoader.parseInt(aliens[3]);
			
		tiles = new int[width][height];	//tablica rozmieszczenia stworkow na planszy
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				tiles[x][y] = TextLoader.parseInt(aliens[(x + y * width) + 4]);	//pobieram int stworka z tablicy tokens	
			}																//0-brak stworka 1 - jest stworek
		}
		loaded=true;
		notifyAll();
	}
	
	public synchronized int[][] getInitAliensLocation(){//zwr�� po�o�enia logiczne stwork�w
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		
		return tiles;
	}
	
	public synchronized int getWidth(){//zwr�� maksymalna ilo�� stwoek�w w p�aszczyznie x
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return width;
	}
	
	public synchronized int getHeight() {//-||- w p�aszczyznie y
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return height;
	}
	
	public synchronized int getAlienSpeed() {
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		
		return alienSpeed;
	}
	
	public synchronized int getShotingFreq() {
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		return shootingFreq;
	}
	
	public synchronized String getMap() {
		StringBuilder builder;
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		builder = new StringBuilder();
		builder.append(width);
		builder.append(" ");
		builder.append(height);
		
		for(int i=4;i<aliens.length;++i) {
			builder.append(" ");
			builder.append(aliens[i]);
		}
		builder.append('\n');
		return builder.toString();
	}
}