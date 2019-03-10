package loader;

public class ResultsLoader {
	
	private String file;     
	private TextLoader textloader=new TextLoader();
	private String results[]=new String[10];
	private int numberOfResults;
	
	public ResultsLoader() {
		loadResults();
	}

	public String[] loadResults(){
		file= textloader.loadFileAsString("bestResults.save");
		results = file.split("(\r\n|\r|\n)", -1); // laduje do tablicy results kolejne elementy oddzielone now¹ lini¹
		numberOfResults=results.length-1;
		return results;
	}
	
	public String getLoadedResults(int i) {
		return results[i];
	}
	
	public String[] getLoadedResults() {
		return results;
	}
	
	public int getNumberOfResults() {
		return numberOfResults;
	}

}
