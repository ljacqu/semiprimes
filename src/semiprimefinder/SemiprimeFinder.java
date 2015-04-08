package semiprimefinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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

	/** 
	 * 2d array containing the factors of the field. The first field indicates
	 * whether we added or subtracted 1 (1 or -1).
	 * For example, semiprimeFactors[5] = {-1, 2, 3}, meaning 5 = 2*3-1
	 */
	private Map<Integer, List<Sequence>> semiprimes;
	
	/** A prime sieve object to get a list of prime numbers */
	private PrimeSieve primeSieve;

	/**
	 * Constructs a new SemiprimeFinder object and generates the
	 * semiprime information.
	 * @param size The highest number to check
	 */
	public SemiprimeFinder(int size) throws Exception {
		this.size  = size;
		semiprimes = new TreeMap<Integer, List<Sequence>>();
		primeSieve = new PrimeSieve(size);
		getAllSemiprimes();
	}
	
	public static void main(String[] args) throws Exception {
		SemiprimeFinder sps = new SemiprimeFinder(10_000_000);
		sps.getAllSemiprimes();
//		PrintHelper.printSemiprimes(sps.semiprimes);
	}
	
	/**
	 * Fills `isSemiprime` and `semiprimeFactors` with data up to `size`.
	 */
	private void getAllSemiprimes() {
		int currentPrime = 2;
		int upperBound = (int) Math.sqrt(size);
		while (currentPrime < upperBound && getCombinations(currentPrime)) {
			currentPrime = nextPrime(currentPrime);
		}
	}
	
	/**
	 * Handles all possible numbers which have prime numbers as factors
	 * that are equals to or bigger than `start`.
	 * @param start The smallest possible factor
	 * @return True if there were possible numbers (i.e. at least one number
	 *  could be constructed that is smaller than `size`)
	 */
	public boolean getCombinations(int start) {
		int length = 0;
		// Actually, the check should be: ++length <= log(size)/log(start)
		// because we will never have a factor smaller than `start`
		while (++length <= size && getCombinations(start, length));
		// if size == 1, we couldn't generate any
		// combination with `start`, so we return false
		return (length > 1); 
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
	 * the value 6*5*7, 6*5*11, and 6*7*11.
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
			for (int prime = nextPrime(startPrime); prime != 0; prime = nextPrime(prime)) {
				List<Integer> newHistory = new ArrayList<Integer>(history);
				newHistory.add(prime);
				int newStart = start * prime;
				if (getCombinations(newStart, prime, size, newHistory) == false) {
					return (prime != nextPrime(startPrime));
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
		
		if (isPrime[0]) registerSemiprime(realSemiprime,  1, history);
		if (isPrime[1]) registerSemiprime(realSemiprime, -1, history);
	}
	
	private void registerSemiprime(int semiprime, int sign, List<Integer> factors) {
		Sequence sequence = new Sequence(sign, factors);
		semiprime += sign;

		List<Sequence> existingSequences = semiprimes.get(semiprime);
		boolean foundSequence = false;
		if (existingSequences == null) {
			semiprimes.put(semiprime, new ArrayList<Sequence>());
		}
		else {
			for (Sequence s : existingSequences) {
				if (sequence.equals(s)) {
					foundSequence = true;
					break;
				}
			}
		}
		if (!foundSequence) {
			semiprimes.get(semiprime).add(sequence);
		}
	}
	
	
	private int nextPrime(int start) {
		return primeSieve.nextPrime(start);
	}
	
}
