package loader;


public class WorldLoader {
	
	int width,height;//liczba alien�w w poziomie i pionie
	int alienSpeed; //pr�dko�� stwork�w
	int shootingFreq; //odpowiada za cz�sto�� stzrelania stworka, im ni�sza tym stworek cz�ciej strzela
	int tiles[][];   //rozmieszczenie stwork�w na mapie
	String file;     
	TextLoader textloader;

	public void loadWorld(int level ){
		StringBuilder path=new StringBuilder("world");
		path.append(level);
		path.append(".txt");
		textloader=new TextLoader();
		if(level==1) {
			file = textloader.loadFileAsString(path.toString()); //wczytanie pliku konfiguracyjnego
		}else {
			file=textloader.loadFileAsString(path.toString());
			 
				while(file==null) {
					System.out.println("Nie odnaleziono pliku tego levelu, u�ywam poprzedniego");
					path=new StringBuilder("world");
					path.append(--level);
					path.append(".txt");
					file = textloader.loadFileAsString(path.toString());
				}
		}
		
		String[] tokens = file.split("\\s+");	// laduje do tablicy tokens kolejne elementy oddzielone spacja
		width = TextLoader.parseInt(tokens[0]);	//maksymalna liczba stwork�w w p�aszczyznie x
		height = TextLoader.parseInt(tokens[1]); //maksymalna liczba stwork�w w p�aszczyznie y
		alienSpeed = TextLoader.parseInt(tokens[2]);
		shootingFreq = TextLoader.parseInt(tokens[3]);
		
		tiles = new int[width][height];	//tablica rozmieszczenia stworkow na planszy
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				tiles[x][y] = TextLoader.parseInt(tokens[(x + y * width) + 4]);	//pobieram int stworka z tablicy tokens	
			}																//0-brak stworka 1 - jest stworek
		}																	//tablice przechowuja inty zwrocone przez parseInt
	
	}
	
	public int[][] getInitAliensLocation(){//zwr�� po�o�enia logiczne stwork�w
		return tiles;
	}
	
	public int getWidth(){//zwr�� maksymalna ilo�� stwoek�w w p�aszczyznie x
		return width;
	}
	
	public int getHeight() {//-||- w p�aszczyznie y
		return height;
	}
	
	public int getAlienSpeed() {
		return alienSpeed;
	}
	
	public int getShotingFreq() {
		return shootingFreq;
	}
}