package saver;
import java.io.PrintWriter;

public class ResultsSaver { //zapisuje tablice wynik�w do pliku
	PrintWriter output;
	
	public void saveResults(String resultsFile) {
		try {
			output=new PrintWriter("src/resocures/bestResults.save");
		}catch(Exception e) {
			e.getMessage();
		}
		output.print(resultsFile);
		output.close();
	}

}
