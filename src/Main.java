import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    
	static Stage stage;
	MenuBar menubar;
	Menu filemenu;
	MenuItem open;
	MenuItem save;
	MenuItem exit;
	Label label;
	Button analButton; 
	static TextArea textarea;
	VBox vbox;
	static int minimum = 2;
	private static String sequence = "";

	private final static Map<Character, Character> revComplementer = new HashMap<Character, Character>() {
		{
			put('a', 't');
			put('c', 'g');
			put('g', 'c');
			put('t', 'a');
		}
	};

	static ArrayList<Palindrome> palindromes = new ArrayList<Palindrome>();

	public static void main(String[] args) {
		launch(args);
	}
	
	public void start(Stage primaryStage) throws Exception {
		stage = primaryStage;
		stage.setTitle("Palidromes");

		open = new MenuItem("Open");
		save = new MenuItem("Save");
		exit = new MenuItem("Exit");

		open.setOnAction(e -> {
			sequence = FileHelper.readFromFile(stage).toLowerCase();
			textarea.clear();
			if (sequence.isEmpty()){
				textarea.appendText("Please read in a sequence.\n");
			} else{
				if(isNucleotide()) {
					textarea.appendText(sequence + "\n");
					label.setText("Sequence is read in.\n"); 
					analButton.setVisible(true);
				} else {
					textarea.appendText("This is not a nucleotide sequence, please read in a new sequence.\n");
					analButton.setVisible(false);
					label.setText("No sequence is read in.\n"); 
				}
			}
		});
		save.setOnAction(e -> {
			if(palindromes.isEmpty()){
				textarea.appendText("Data is not analysed.\n");
			}
			else{
				try {
					FileHelper.writefile(stage, palindromes, minimum);
					textarea.clear();
					analButton.setVisible(false);

					textarea.appendText("Data is saved\nYou can open a new file.\n");
					label.setText("No sequence is read in."); 
					palindromes.clear();
					sequence = "";
				} catch (IOException exception) {
					textarea.appendText(exception.getMessage());
				}
				
				
			}
		});
		exit.setOnAction(e -> {
			Platform.exit();
		});

		filemenu = new Menu("File");
		filemenu.getItems().addAll(open, save, exit);

		menubar = new MenuBar();
		menubar.getMenus().addAll(filemenu);

		label = new Label();
		label.setText("No sequence is read in.");
		
		Label minimumLengthLabel = new Label();
		minimumLengthLabel.setText("2");
		
		analButton = new Button();
		analButton.setText("Analyse");
		
		analButton.setOnAction(e -> {
			if(sequence != null && sequence.length() > 0) {
				analyse();
			}
		});
		
		analButton.setVisible(false);
		
	    Label sliderLabel = new Label("Setting minimum length of palindromes");

		Slider slider = new Slider();
		slider.setMin(2);
		slider.setMax(100);
		slider.setValue(2);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		slider.setMajorTickUnit(50);
		slider.setMinorTickCount(5);
		slider.setBlockIncrement(1);
	
		 slider.valueProperty().addListener(new ChangeListener<Number>() {
	            public void changed(ObservableValue<? extends Number> ov,
	                Number old_val, Number new_val) {
	            	minimum = (Math.round(new_val.floatValue()));
	            	minimumLengthLabel.setText(minimum + "");
	            }
	        });
		
		textarea = new TextArea();
		textarea.setPrefSize(400, 400);
		textarea.setEditable(false);

		vbox = new VBox();
		vbox.getChildren().addAll(menubar, label, analButton, sliderLabel, minimumLengthLabel, slider, textarea);

		Scene scene = new Scene(vbox);
		stage.setScene(scene);
		stage.show();

	}
	
	static boolean isNucleotide() {
		return sequence.matches("[a|g|t|c]+");
	}
	static boolean isIn(int charPosition, int distance) {
		return charPosition+distance < sequence.length() && charPosition-distance >= -1;
	}
	
	static void savePalindrome(int start, int end) {
		Palindrome palindrome = new Palindrome(sequence.substring(start, end+1), start, end);
		palindromes.add(palindrome);
	}
	
	
	static boolean evenPalindrome(int charPosition, int distance, Character karakter) {
		if(distance == 0) return true;
		
		else if(distance == 1) {
			Character next = sequence.charAt(charPosition+1);
			
			if(karakter.equals(revComplementer.get(next))) {
				savePalindrome(charPosition, charPosition+1);
				return true;
			}
			
			return false;
		} else {
			
			Character next = sequence.charAt(charPosition + distance);
			Character previous = sequence.charAt(charPosition - (distance - 1));
			
			if(previous.equals(revComplementer.get(next))) {
				savePalindrome(charPosition - (distance - 1), charPosition + distance);

				return true;
			}
		}
		
		return false;
	}

	
	
	public static void analyse(){
		int len = sequence.length();
		textarea.clear();
		palindromes.clear();
		for(int i = 1; i < len; i++) {
			Character actualChar = sequence.charAt(i);
			
			int j = 1;
			
			while (isIn(i,j)){
				boolean even = evenPalindrome(i, j, actualChar);
				if(!even) break;
				
				j++;
			}
		}
		int i=0;
		for(Palindrome p : palindromes) {
			if(p.getSequence().length() >= minimum) {
				i++;
			}
		}
		textarea.appendText(i + " palindromic seqeunce is found\n");
		if(i!=0){
			piechart();
		}
	}
	
	
	
	public static void piechart() {
		if (palindromes.isEmpty()) {
			textarea.appendText("There is no analysed data. Please read in sequences.\n");
		} else {
			ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
			VBox vb = new VBox();
			Stage stage1 = new Stage();
			vb.setAlignment(Pos.CENTER);
			Scene scene = new Scene(vb);

			
			Map<String, Long> result = palindromes
					.stream()
					.map(p -> p.getSequence())
					.filter(sequence -> sequence.length() >= minimum)
	                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
			
			result.forEach((seq, size) -> {
				pieChartData.add(new PieChart.Data(seq + "(" + size + ")", size));
			});

			
			PieChart chart = new PieChart(pieChartData);
			chart.setTitle("Palindromic sequence abundance");
			chart.setMinSize(800, 600);

			((VBox) scene.getRoot()).getChildren().add(chart);

			stage1.setTitle("PieChart");
			stage1.setScene(scene);
			stage1.setMinHeight(600);
			stage1.setMinWidth(800);
			stage1.setResizable(true);
			stage1.show();

		}
	}


}
