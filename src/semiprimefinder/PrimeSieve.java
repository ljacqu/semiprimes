package semiprimefinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Class implementing the famous sieve algorithm to
 * find prime numbers.
 */
public class PrimeSieve {
	
	/** List of numbers indicating whether it is divisible or not (i.e. a prime) */
	private boolean[] isDivisible;
	/** The highest number that was examined; the last field in `isDivisible` is isDivisible[size] */
	private int size;

	/**
	 * Creates a PrimeSieve object and fills the list of primes up to `size`.
	 * @param size The highest number to examine
	 */
	public PrimeSieve(int size) {
		isDivisible = new boolean[size+1];
		this.size = size;
		isDivisible[0] = true;  // Ugly fix so 0 and 1
		isDivisible[1] = true;  // aren't primes :)
		fillSieve();
	}
	
	public static void main(String[] args) {
		PrimeSieve ps = new PrimeSieve(100000);
		ps.printList();
	}
	
	/**
	 * Indicates whether or not a given number `n` is a prime number.
	 * Guaranteeing that n <= size is in the responsibility of the user.
	 * @param n The number to examine.
	 * @return True if it is a prime number, false otherwise.
	 */
	public boolean isPrime(int n) {
		return n>0 && !isDivisible[n];
	}
	
	/**
	 * Fills `isDivisible` with the proper values. If a number is not
	 * a prime number, its corresponding field is set to true.
	 */
	private void fillSieve() {
		int limit = (int) Math.sqrt(size);
		registerAsPrime(2);
		for (int i = 3; i <= limit; i += 2) {
			if (!isDivisible[i]) registerAsPrime(i);
		}
	}
	
	/**
	 * Return next prime number that is bigger than <code>start</code>.
	 * @param start The number the prime number needs to be bigger & closest to.
	 * @return The next prime number, or 0 upon error (size exceeded).
	 */
	public int nextPrime(int start) {
		while (++start <= size) {
			if (isPrime(start)) return start;
		}
		return 0;
	}
	
	/**
	 * Sets all multiples <= `size` to true, indicating that the fields
	 * are not prime numbers.
	 * @param n The number to use as a prime number.
	 */
	private void registerAsPrime(int n) {
		// We can start at n*n, (e.g. registerAsPrime(5) starts at 5*5)
		// because smaller multiples have already been dealt with
		int i = n;
		int multiple;
		while ((multiple = i*n) <= size) {
			isDivisible[multiple] = true;
			++i;
		}
	}
	
	/**
	 * Unused. Creates a List with the prime numbers.
	 * @return List of the prime numbers found.
	 */
	public List<Integer> toList() {
		List<Integer> list = new ArrayList<Integer>();
		list.add(2);
		for (int i = 3; i <= size; i += 2) {
			if (!isDivisible[i]) list.add(i);
		}
		return list;
	}
	
	
	// --------------
	// Print list (debug)
	// --------------
	public void printList() {
		int primePerLine = 10;
		int maxLength = (int) Math.log10(isDivisible.length)+1;
		createPadding(maxLength-1);
		System.out.print("2, ");
		int displayedPrimes = 1;
		for (int i = 3; i < isDivisible.length; i += 2) {
			if (!isDivisible[i]) {
				String s = new Integer(i).toString();
				createPadding(maxLength - s.length());
				System.out.print(i + ", ");
				if (++displayedPrimes == primePerLine) {
					System.out.println();
					displayedPrimes = 0;
				}
			}
		}
		System.out.println();
	}
	
	private void createPadding(int length) {
		for (int i = 0; i < length; ++i) {
			System.out.print(" ");
		}
	}


}