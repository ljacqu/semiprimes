package semiprimefinder;

import java.util.List;
import java.util.Map;


/**
 * Prints lists with proper formatting 
 * @author ljacqu
 */
public class PrintHelper {

	
	public static void printPrimes(boolean[] array, int entriesPerLine) {
		int maxLength = (int) Math.log10(array.length);
		createPadding(maxLength-1);
		System.out.print("2, ");
		
		int displayedPrimes = 1;
		for (int i = 3; i < array.length; i += 2) {
			if (!array[i]) {
				String s = new Integer(i).toString();
				createPadding(maxLength - s.length());
				System.out.print(i + ", ");
				if (++displayedPrimes == entriesPerLine) {
					System.out.println();
					displayedPrimes = 0;
				}
			}
		}
		System.out.println();
		
	}
	
	public static void printSemiprimes(Map<Integer, List<Sequence>> semiprimes) {
		for (Map.Entry<Integer, List<Sequence>> entry : semiprimes.entrySet()) {
			List<Sequence> factorCombinations = entry.getValue();
			
			System.out.print(entry.getKey());
			for (Sequence sequence: factorCombinations) {
				System.out.print("\t" + sequence.getSign() + "\t");
				for (Integer factor : sequence.getFactors()) {
					System.out.print(factor + " ");
				}
				System.out.println();
			}
		}
	}
	
	private static void createPadding(int length) {
		for (int i = 0; i < length; ++i) {
			System.out.print(" ");
		}
	}
	
}
