package loader;


public class WorldLoader {
	
	int width,height;//liczba alienów w poziomie i pionie
	int alienSpeed; //prêdkoœæ stworków
	int shootingFreq; //odpowiada za czêstoœæ stzrelania stworka, im ni¿sza tym stworek czêœciej strzela
	int tiles[][];   //rozmieszczenie stworków na mapie
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
					System.out.println("Nie odnaleziono pliku tego levelu, u¿ywam poprzedniego");
					path=new StringBuilder("world");
					path.append(--level);
					path.append(".txt");
					file = textloader.loadFileAsString(path.toString());
				}
		}
		
		String[] tokens = file.split("\\s+");	// laduje do tablicy tokens kolejne elementy oddzielone spacja
		width = TextLoader.parseInt(tokens[0]);	//maksymalna liczba stworków w p³aszczyznie x
		height = TextLoader.parseInt(tokens[1]); //maksymalna liczba stworków w p³aszczyznie y
		alienSpeed = TextLoader.parseInt(tokens[2]);
		shootingFreq = TextLoader.parseInt(tokens[3]);
		
		tiles = new int[width][height];	//tablica rozmieszczenia stworkow na planszy
		for(int y = 0;y < height;y++){
			for(int x = 0;x < width;x++){
				tiles[x][y] = TextLoader.parseInt(tokens[(x + y * width) + 4]);	//pobieram int stworka z tablicy tokens	
			}																//0-brak stworka 1 - jest stworek
		}																	//tablice przechowuja inty zwrocone przez parseInt
	
	}
	
	public int[][] getInitAliensLocation(){//zwróæ po³o¿enia logiczne stworków
		return tiles;
	}
	
	public int getWidth(){//zwróæ maksymalna iloœæ stwoeków w p³aszczyznie x
		return width;
	}
	
	public int getHeight() {//-||- w p³aszczyznie y
		return height;
	}
	
	public int getAlienSpeed() {
		return alienSpeed;
	}
	
	public int getShotingFreq() {
		return shootingFreq;
	}
}