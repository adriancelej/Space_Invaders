package loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class TextLoader {
	
	public static String loadFileAsString(String path){
		StringBuilder builder = new StringBuilder();
		
		try{
			FileReader file= new FileReader(path);
			if(file==null)
				return null;
			BufferedReader br = new BufferedReader(file);
			String line;
			while((line = br.readLine()) != null)
				builder.append(line + "\n");
			
			br.close();
			file.close();
		}catch(IOException e){
			System.out.println(e.getMessage());
			return null;
		}
		
		return builder.toString();
	}
	
	public static int parseInt(String number){
		try{
			return Integer.parseInt(number);
		}catch(NumberFormatException e){
			e.printStackTrace();
			return 0;
		}
	}

}
