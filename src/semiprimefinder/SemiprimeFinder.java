package semiprimefinder;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds semiprimes by generating all possible numbers
 * whose factors are only prime numbers and then adding
 * or subtracting -1 to see if it is a prime number.
 * 
 * Note: We're actually dealing with numbers that are a
 * semiprime+1 or semiprime-1. We call those semiprimes
 * within this program for brevity, and we call real
 * semiprimes, unoriginally, "real semiprimes."
 */
public class SemiprimeFinder {
	/** The highest number that is being investigated */
	private int size;
	/** floor(sqrt(size)): the highest number to consider as factor for a number */
	private final int LIMIT;
	/** Boolean list indicating which numbers are semiprimes */
	private boolean[] isSemiprime;
	/** 
	 * 2d array containing the factors of the field. The first field indicates
	 * whether we added or subtracted 1 (1 or -1).
	 * For example, semiprimeFactors[5] = {-1, 2, 3}, meaning 5 = 2*3-1
	 */
	private Integer[][] semiprimeFactors;
	/** A prime sieve object to get a list of prime numbers */
	private PrimeSieve primeSieve;

	/**
	 * Constructs a new SemiprimeFinder object and generates the
	 * semiprime information.
	 * @param size The highest number to check
	 */
	public SemiprimeFinder(int size) {
		this.size        = size;
		LIMIT            = (int) Math.sqrt(size);
		isSemiprime      = new boolean[size+1];
		semiprimeFactors = new Integer[size+1][];
		primeSieve       = new PrimeSieve(size);
		getAllSemiprimes();
	}
	
	public static void main(String[] args) {
		SemiprimeFinder sps = new SemiprimeFinder(10_000_000);
		//sps.primeSieve.printList();
		sps.getAllSemiprimes();
		sps.printSemiprimes();
	}
	
	/**
	 * Fills `isSemiprime` and `semiprimeFactors` with data up to `size`.
	 */
	private void getAllSemiprimes() {
		int currentPrime = 2;
		while (currentPrime < LIMIT && getCombinations(currentPrime)) {
			currentPrime = nextPrime(currentPrime);
		}
	}
	
	public boolean getCombinations(int start) {
		int size = 0;
		while (++size < this.size && getCombinations(start, size));
		// if size == 1, we couldn't generate any
		// combination with `start`, so we return false
		return (size != 1); 
	}
	
	public boolean getCombinations(int start, int size) {
		List<Integer> history = new ArrayList<Integer>(size);
		history.add(start);
		return getCombinations(start, start, size, history);
	}
	
	/**
	 * Construct all possible combinations of `size` prime numbers bigger/equals
	 * to `startPrime`, multiplied with `start`. This is done in a recursive fashion.
	 * 
	 * For example, getCombo(6, 5, 2, ...) could call `registerSemiprime` for
	 * the value 6*5*5, 6*5*7, 6*5*11, 6*7*7, 6*7*11 and 6*11*11.
	 * @param start The number to multiply the other factors with
	 * @param startPrime The number all new factors must be bigger/equals to
	 * @param size The number of additional prime number factors required
	 * @param history A List saving all of the individual factors, i.e. `start`
	 *  is the multiplication of all elements in `history`
	 * @return True if at least one combination could be produced which is smaller
	 *  than `this.size`. False indicates that increasing the parameter `size`
	 *  with the same arguments will not produce any results anymore. 
	 */
	private boolean getCombinations(int start, int startPrime, int size, List<Integer> history) {
		if (size == 0) {
			if (start > this.size) {
				return false;
			} else {
				registerSemiprime(start, history);
				return true;
			}
		}
		else {
			--size;
			for (int prime = startPrime; prime != 0; prime = nextPrime(prime)) {
				List<Integer> newHistory = new ArrayList<Integer>(history);
				newHistory.add(prime);
				int newStart = start * prime;
				if (getCombinations(newStart, prime, size, newHistory) == false) {
					return (prime != startPrime);
				}
			}
			return true;
		}
	}
	
	/**
	 * Checks whether `realSemiprime`+1 and/or `realSemiprime`-1 are prime numbers
	 * and saves the result to `isSemiprime` if true. `realSemiprime` is never
	 * bigger than `this.size`
	 * @param realSemiprime The number to investigate with -1 / +1
	 * @param history List of the factors of `realSemiprime`
	 */
	private void registerSemiprime(int realSemiprime, List<Integer> history) {
		boolean[] isPrime = {realSemiprime+1 <= size && primeSieve.isPrime(realSemiprime+1), 
							 primeSieve.isPrime(realSemiprime-1)};
		if (isPrime[0]) {
			List<Integer> copiedHistory = isPrime[1] ? history : new ArrayList<Integer>(history);
			registerSemiprime(realSemiprime, 1, copiedHistory);
		}
		if (isPrime[1]) {
			// don't need copy constructor for `history` here
			// because we don't need `history` anymore after this
			registerSemiprime(realSemiprime, -1, history);
		}
	}
	
	private void registerSemiprime(int semiprime, int additive, List<Integer> history) {
		semiprime += additive;
		isSemiprime[semiprime] = true;
		
		history.add(0, additive);
		Integer[] factors = history.toArray(new Integer[history.size()]);
		semiprimeFactors[semiprime] = factors;
	}
	
	
	private int nextPrime(int start) {
		return primeSieve.nextPrime(start);
	}
	
	private void printSemiprimes() {
		int i = -1;
		for (Integer[] factors : semiprimeFactors) {
			++i;
			if (!isSemiprime[i]) continue;
			System.out.print(i + ": ");
			for (int factor: factors) {
				System.out.print(factor + ", ");
			}
			System.out.println();
		}
	}
	
}
