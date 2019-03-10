package loader;
import saver.ResultsSaver;

public class ResultsLoader implements Runnable{
	
	private String file;  
	private ResultsSaver saver;
	private TextLoader textLoader;
	private volatile String results[];
	private volatile String[] playerNames;
	private volatile String[] playerResults;
	private volatile boolean loaded=false;
	private volatile boolean resultAdded=false;
	private Thread thread;
	
	public ResultsLoader() {
		start();
	}
	
	public ResultsLoader(String playerName, int playerResult) {
		saver=new ResultsSaver();
		textLoader = new TextLoader();
		results=new String[10];
		loadResults();
		splitResults();
		addResults(playerName, playerResult);
	}
	
	public synchronized void run() {
		saver=new ResultsSaver();
		textLoader = new TextLoader();
		results=new String[10];
		
		loadResults();
		splitResults();
		sortResults();
		loaded= true;
		notifyAll();
	}
	
	private void start() {
		thread=new Thread(this);
		thread.start();
	}

	private void loadResults(){
		file= textLoader.loadFileAsString("src/resocures/bestResults.save");
		results = file.split("(\r\n|\r|\n)", -1); // laduje do tablicy results kolejne elementy oddzielone now¹ lini¹
		playerNames=new String[results.length-1];
		playerResults=new String[results.length-1];
	}
	
	private void splitResults() { //rozdziela nazwê i wynik gracza
		for(int i=0;i<results.length-1;++i) {
			playerNames[i]=results[i].split("\\s")[0];
			playerResults[i]=results[i].split("\\s")[1];
		}
	}
	
	private void sortResults() { //sortuje wyniki malej¹co
		String bufR, bufP;
		boolean isSorted=false;
		
		//prosty BubbleSort
		while(!isSorted) { 
			isSorted=true;
			for(int i=0;i<9; ++i) {
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
	}
	
	public synchronized void addResult(String playerName, int score) { //dodaje wynik gracza do tablicy wyników jeœli mieœci siê w najlepszej 10
		while(!loaded) {
			try {
				wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		new Thread("adder") {
			public synchronized void run() {
				thread=this.currentThread();
				if(Integer.parseInt(playerResults[results.length-2])<score) {
					playerResults[results.length-2]=Integer.toString(score);
					playerNames[results.length-2]=playerName;
					sortResults();
					saveResults();
					resultAdded=true;
					notifyAll();
				}
			}
		}.start();
	}
	
	private void addResults(String playerName, int score) {
		if(Integer.parseInt(playerResults[results.length-2])<score) {
			playerResults[results.length-2]=Integer.toString(score);
			playerNames[results.length-2]=playerName;
			sortResults();
			saveResults();
			resultAdded=true;
		}
	}
	
	private void saveResults() {
		StringBuilder builder=new StringBuilder();
		sortResults();
		for(int i=0;i<results.length-1;++i) {
			builder.append(playerNames[i]);
			builder.append(" ");
			builder.append(playerResults[i]);
			builder.append('\n');
		}
		saver.saveResults(builder.toString());
	}
	
	public synchronized String getResults() {
		while(!loaded||!resultAdded) {
			try {
			wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		StringBuilder builder=new StringBuilder();
		for(int i=0;i<results.length-1;++i) {
			builder.append(playerNames[i]);
			builder.append(' ');
			builder.append(playerResults[i]);
			builder.append(' ');
		}
		return builder.toString();
	}
	
	public synchronized String getLoadedResults() {
		while(!loaded) {
			try {
			wait();
			}catch(Exception e) {
				e.getLocalizedMessage();
			}
		}
		StringBuilder builder=new StringBuilder();
		for(int i=0;i<results.length-1;++i) {
			builder.append(playerNames[i]);
			builder.append(' ');
			builder.append(playerResults[i]);
			builder.append(' ');
		}
		return builder.toString();
	}

}
