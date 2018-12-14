import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class FileHelper {
	public static String readFromFile(Stage stage) {	
		String sequence = "";
		
		FileChooser filechooser = new FileChooser();
		File fastafile = filechooser.showOpenDialog(stage);
		if (fastafile != null) {
			try {
			
				Scanner sc = new Scanner(fastafile);
				String line;
				boolean stop = false;
				while (sc.hasNextLine()) {
					line = sc.nextLine();					
			
					if (line.startsWith(">")) {
						if(stop) {
							break;
						}
						stop = true;
					} else {
						sequence = sequence.concat(line);
					}
				}
				sc.close();
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return sequence;
	}
	

	public static void writefile(Stage stage, ArrayList<Palindrome> palindromes, int minimum) throws IOException {
			FileChooser filechooser = new FileChooser();
			File outfile = filechooser.showSaveDialog(stage);
			if (outfile == null) {
				throw new IOException("No output file selected.");
			}
			writeResult(outfile, palindromes, minimum);

	}
	
	public static void writeResult(File f, ArrayList<Palindrome> palindromes, int minimum) {
		try {

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
		           new FileOutputStream(f), "utf-8"));

				for(Palindrome p : palindromes) {
					if(p.getSequence().length() >= minimum) {
						writer.write(p.toString() + "\n");
					}
				}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
