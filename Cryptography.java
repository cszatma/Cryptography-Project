import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Scanner;

public class Cryptography {
	
	static boolean isLastLine = false;
	
	public static void main(String[] args) {
		
		if (args.length != 4) {
			showMessage();
			return;
		}
		
		String inputText = "";
		String outputText = "";
		String remainderText = "";
		
		//Gets the inputed text from the user
		Scanner input = new Scanner(System.in);
		PrintWriter output = null;
		
		String command = args[0]; //Finds out whether the user wants to encypt or decrypt
		String key = args[1]; //Gets the key from the user
		
		
		
		try {
			input = new Scanner(new File(args[2]));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			showMessage();
		}
		
		try {
			//output = new PrintWriter("output.txt");
			output = new PrintWriter(args[3]);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			showMessage();
		}

		
		//Runs the decryter/encrypter as long as the file has text available
		while (input.hasNextLine()) {
			inputText = remainderText + input.nextLine();
			inputText = inputText.toUpperCase();
			int numOfSteps = inputText.length() / 64;
			int remainder = inputText.length() % 64;
			if (remainder > 0) { 
				remainderText = inputText.substring(64 * numOfSteps, 64 * numOfSteps + remainder) + " ";
			} else {
				remainderText = "";
			}
			String currentStepText = new String();
			
			for (int i = 0; i <= numOfSteps; i++) {
				if (i < numOfSteps) {
					currentStepText = inputText.substring(64 * i, 64 * (i + 1));
				} else if (i == numOfSteps && !(remainderText.equals("")) && input.hasNextLine() == false) {
					currentStepText = remainderText;
				} else {
					break;
				}
				
				if (command.toLowerCase().equals("encrypt")) {
					outputText = encrypt(currentStepText, key);
					output.println(outputText);
				} else if (command.toLowerCase().equals("decrypt")) {
					if (input.hasNextLine() == false) {
						isLastLine = true;
					}
					outputText = decrypt(currentStepText, key);
					output.println(outputText);
				}
			}
		}
		input.close();
		output.close();
		System.out.println("Process was successful. Please check the output.txt file for the result.");
	}
	
	//Decrypts the given encrypted text using the given key
	public static String decrypt(String inputText, String key) {
		int hash = key.hashCode();
		StringBuffer text = new StringBuffer(inputText);
		text = performTransposition(text, hash, "decrypt");
		performSubstitution(text, hash, "decrypt");
		return text.toString();
	}
	
	//Encrypts the given text using the given key
	public static String encrypt(String inputText, String key) {
		int hash = key.hashCode();
		StringBuffer text = new StringBuffer(inputText);
		removeSpecialCharacters(text);
		performSubstitution(text, hash, "encrypt");
		
		text = performTransposition(text, hash, "encrypt");
		return text.toString();
	}
	
	//Performs the substitution in either order
	public static StringBuffer performSubstitution(StringBuffer text, int hash, String command) {
		Random randomNum = new Random(hash);
		int[] firstNumbers = new int[100];
		int[] secondNumbers = new int[100];
		StringBuffer alphabet = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
		
		//Gets all the necessary numbers for the substitution
		for (int i = 0; i < 100; i++) {
			firstNumbers[i] = randomNum.nextInt(27);
			secondNumbers[i] = randomNum.nextInt(27);
		}
		
		for (int i = 0; i < 100; i++) {
			int firstNum = firstNumbers[i];
			int secondNum = secondNumbers[i];
			char firstChar = alphabet.charAt(firstNum);
			char secondChar = alphabet.charAt(secondNum);
			alphabet = swapCharacters(alphabet, firstChar, secondChar);
			
			if (command.equals("encrypt")) {
				text = swapCharacters(text, firstChar, secondChar);
			}
		}
		
		if (command.equals("decrypt")) {
			
			if (isLastLine) {
				for (int i = text.length() - 1; i >= 0; i--) {
					if (text.charAt(i) == ' ') {
						text.deleteCharAt(i);
					} else {
						break;
					}
				}
			}
			
			for (int i = 99; i >= 0; i--) {
				int firstNum = firstNumbers[i];
				int secondNum = secondNumbers[i];
				char firstChar = alphabet.charAt(firstNum);
				char secondChar = alphabet.charAt(secondNum);
				alphabet = swapCharacters(alphabet, firstChar, secondChar);
				text = swapCharacters(text, firstChar, secondChar);
			}
		}
		
		return text;
	}
	
	public static StringBuffer performTransposition(StringBuffer str, int hash, String command) {
		Random randomNum = new Random(hash);
		int[] numbers = {0,1,2,3,4,5,6,7};
		StringBuffer resultText = new StringBuffer();
		int textLength = str.length();
		for (int i = 0; i < 100; i++) {
			int firstRandom = randomNum.nextInt(8);
			int secondRandom = randomNum.nextInt(8);
			int firstNum = numbers[firstRandom];
			numbers[firstRandom] = numbers[secondRandom];
			numbers[secondRandom] = firstNum;
		}
		
		if (textLength < 64) {
			for (int i = 0; i < 64 - textLength; i++) {
				str.append(" ");
			}
		}
		
		if (command.equals("encrypt")) {
			char grid[][] = generateGrid(str);
			resultText = gridToString(grid, numbers, command);
		} else if (command.equals("decrypt")) {
			char grid[][] = generateGrid(str);
			resultText = gridToString(grid, numbers, command);
		}
		
		
		
		return resultText;
	}
	
	//Swaps the given characters at all locations in the given string
	public static StringBuffer swapCharacters(StringBuffer str, char char1, char char2) {
		for (int i = 0; i < str.length(); i++) {
			char currentChar = str.charAt(i);
			if (currentChar == char1) {
				str.replace(i, i + 1, String.valueOf(char2));
			} else if (currentChar == char2) {
				str.replace(i, i + 1, String.valueOf(char1));
			}
		}
		return str;
	}
	
	//Replaces any special characters in the string with a space
	public static StringBuffer removeSpecialCharacters(StringBuffer str) {
		StringBuffer alphabet = new StringBuffer("ABCDEFGHIJKLMNOPQRSTUVWXYZ ");
		for (int i = 0; i < str.length(); i++) {
			if (alphabet.indexOf(String.valueOf(str.charAt(i))) == -1) {
				str.replace(i, i + 1, " ");
			}
		}
		return str;
	}
	
	//Used to ensure that the transposition step generated the correct sequence
	public static void printArray(int[] array) {
		for (int i = 0; i < array.length; i++) {
			System.out.printf("%d, ", array[i]);
		}
	}
	
	//Reverses the order of integer values in the given array
	public static int[] reverseNumbers(int[] nums) {
		int[] reversed = new int[nums.length];
		for (int i = 0; i < nums.length; i ++) {
			reversed[i] = nums[(nums.length - 1) - i];
		}
		return reversed;
	}
	
	//Turns the given text into a 2D array of characters
	public static char[][] generateGrid(StringBuffer text) {
		char grid[][] = new char[8][8];
		int stringIndex = 0;
		for (int row = 0; row < 8; row++) {
			for (int column = 0; column < 8; column++) {
				grid[row][column]= text.charAt(stringIndex);
				stringIndex++;
			}
		}
		return grid;
	}
	
	//Takes the 2D array of chars and turns it into a string by reading the columns in the given order
	public static StringBuffer gridToString(char grid[][], int order[], String command) {
		StringBuffer resultText = new StringBuffer();
		if (command.equals("decrypt")) {
			grid = rearrangeGrid(grid, order);
		}
		char transpose[][] = getGridTranspose(grid);
		for (int i = 0; i < transpose.length; i++) {
			StringBuffer currentRow = new StringBuffer();
			for (int j = 0; j < grid.length; j++) {
				if (command.equals("encrypt")) {
					currentRow.append(transpose[order[i]][j]);
				} else if (command.equals("decrypt")) {
					currentRow.append(transpose[i][j]);
				}
			}
			resultText.append(currentRow);
		}
		return resultText;
	}
	
	//Switches the rows and columns of the grid
	public static char[][] getGridTranspose(char grid[][]) {
		int numOfRows = grid.length;
		int numOfCol = grid[0].length;
		char gridTranspose[][] = new char[numOfCol][numOfRows];
		
		for (int i = 0; i < numOfCol; i++) {
			char column[] = new char[numOfRows];
			for (int n = 0; n < numOfRows; n++) {
				column[n] = grid[n][i];
			}
			gridTranspose[i] = column;
		}
		
		return gridTranspose;
	}
	
	//Rearranges the rows of the grid based on the given order
	public static char[][] rearrangeGrid(char grid[][], int sequence[]) {
		char resultGrid[][] = new char[8][8];
		for (int i = 0; i < 8; i++) {
			resultGrid[sequence[i]] = grid[i];
		}
		return resultGrid;
	}
	
	public static void showMessage() {
		System.out.println("Correct usage: java Cryptography key inputfile outputfile");
	}
	
}
