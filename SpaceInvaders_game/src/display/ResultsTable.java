package display;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import loader.ResultsLoader;
import saver.ResultsSaver;

public class ResultsTable extends JPanel{

	private final static int WIDTH=300; //rozmiar panelu
	private final static int HEIGHT=800;
	private final static int NUMBER_OF_RESULTS=10; //liczba wyników
	private final static int FONT_SIZE=30; //rozmiar czcionki u¿ywanej do napisów
	private JLabel playerNameLabel[]; //etykiety nazw graczy
	private JLabel playerScoreLabel[]; //etykiety wyników 
	private ResultsLoader loader; //³adowanie wynikiów z pliku tekstowego
	private String[] playerNames = new String[NUMBER_OF_RESULTS]; //nazwy graczy
	private String[] playerResults = new String[NUMBER_OF_RESULTS]; //wyniki graczy
	private ResultsSaver saver= new ResultsSaver();
	
	public ResultsTable() {
		super();
		loader= new ResultsLoader();
		playerNameLabel=new JLabel[NUMBER_OF_RESULTS];
		playerScoreLabel= new JLabel[NUMBER_OF_RESULTS];
		splitResults();
		this.setVisible(true);
		this.setSize(WIDTH, HEIGHT);
		this.setEnabled(true);
		this.setOpaque(true);
		this.setBorder(BorderFactory.createRaisedBevelBorder()); //ramka wokó³ tabeli
		this.setBackground(new Color(102,240,255)); //kolor t³a tabeli
	}
	
	public ResultsTable(String[] playerNames, String[] playerResults) {
		super();
		playerNameLabel=new JLabel[NUMBER_OF_RESULTS];
		playerScoreLabel= new JLabel[NUMBER_OF_RESULTS];
		this.setVisible(true);
		this.setSize(WIDTH, HEIGHT);
		this.setEnabled(true);
		this.setOpaque(true);
		this.setBorder(BorderFactory.createRaisedBevelBorder()); //ramka wokó³ tabeli
		this.setBackground(new Color(102,240,255)); //kolor t³a tabeli
		this.playerNames= playerNames;
		this.playerResults=playerResults;
	}
	
	private void splitResults() { //rozdziela nazwê i wynik gracza
		String temp[]= loader.getLoadedResults();
		for(int i=0;i<temp.length-1;++i) {
			playerNames[i]=temp[i].split("\\s")[0];
			playerResults[i]=temp[i].split("\\s")[1];
		}
	}
	
	private void createLabelResults() { //tworzy etykiety nazw graczy i ich wyników
		for(int i=0;i<playerNames.length;++i) {
			playerNameLabel[i]=new JLabel(playerNames[i]);
			playerScoreLabel[i]=new JLabel(playerResults[i]);
		}
	}
	
	public void createResultsTable() { //tworzy wyœwietlaln¹ tablicê wyników
		this.removeAll();
		this.sortResults(); //posortuj
		this.setLayout(new GridLayout(playerNames.length+1,3)); //3 kolumny 11 wierszy
		
		JLabel lp =new JLabel("L.p."); //kolumna liczby porz¹dkowej
		lp.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,FONT_SIZE));
		lp.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.add(lp);
		
		JLabel title =new JLabel("Nazwa gracza:     "); //kolumna nazwy gracza
		title.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,FONT_SIZE));
		title.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.add(title);
		
		JLabel title2=new JLabel("Wynik:"); //kolumna wyników gracza
		title2.setFont(new Font(Font.SANS_SERIF,Font.CENTER_BASELINE,FONT_SIZE));
		title2.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		this.add(title2);
		
		//wype³nianie tabeli danymi
		int length= playerNames.length; 
		for(int i = 0;i<length;++i) {
			//tworzy kolumne z numerami porz¹dkowymi
			JLabel number= new JLabel(Integer.toString(i+1)+'.');
			this.add(number);
			number.setVisible(true);
			number.setFont(new Font(Font.SANS_SERIF,Font.ITALIC,FONT_SIZE));
			number.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			//tworzy kolumnê z nazwami graczy
			this.add(playerNameLabel[i]);
			playerNameLabel[i].setVisible(true);
			playerNameLabel[i].setFont(new Font(Font.SANS_SERIF,Font.ITALIC,FONT_SIZE));
			playerNameLabel[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
			//tworzy kolumnê z wynikami graczy
			this.add(playerScoreLabel[i]);
			playerScoreLabel[i].setVisible(true);
			playerScoreLabel[i].setFont(new Font(Font.SANS_SERIF,Font.ITALIC,FONT_SIZE));
			playerScoreLabel[i].setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		}
	}
	
	private void sortResults() { //sortuje wyniki malej¹co
		String bufR, bufP;
		boolean isSorted=false;
		
		//prosty BubbleSort
		while(!isSorted) { 
			isSorted=true;
			for(int i=0;i<NUMBER_OF_RESULTS-1; ++i) {
				if(Integer.parseInt(playerResults[i])<Integer.parseInt(playerResults[i+1])){
					bufR=playerResults[i];
					playerResults[i]=playerResults[i+1];
					playerResults[i+1]=bufR;
				
					bufP=playerNames[i];
					playerNames[i]=playerNames[i+1];
					playerNames[i+1]=bufP;
					isSorted=false;
				}
			}
		}
		createLabelResults(); //tworzy etykiety z posortowanych danych
	}
	
	public void addResult(String playerName, int score) { //dodaje wynik gracza do tablicy wyników jeœli mieœci siê w najlepszej 10
		if(Integer.parseInt(playerResults[NUMBER_OF_RESULTS-1])<score) {
			playerResults[NUMBER_OF_RESULTS-1]=Integer.toString(score);
			playerNames[NUMBER_OF_RESULTS-1]=playerName;
		}
	}
	
	public void saveResults() {
		StringBuilder builder=new StringBuilder();
		sortResults();
		for(int i=0;i<NUMBER_OF_RESULTS;++i) {
			builder.append(playerNames[i]);
			builder.append(" ");
			builder.append(playerResults[i]);
			builder.append('\n');
		}
		saver.saveResults(builder.toString());
	}
	
}
