package semiprimefinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Processes prime numbers based on some criteria.
 */
public class PrimeNumberEvaluator {

  /**
   * Minimum numbers of consecutive prime numbers with the same ending digit to
   * consider.
   */
  private static final int MIN_CONSECUTIVE_ENDINGS = 3;

  public static void main(String[] args) {
    PrimeSieve ps = new PrimeSieve(50_000_000);
    Map<Integer, List<List<Integer>>> sequences = findConsecutiveEndingDigits(ps
        .toList());

    for (Map.Entry<Integer, List<List<Integer>>> entry : sequences.entrySet()) {
      System.out.println(entry.getKey() + "\n-------------------");
      if (entry.getValue().size() > 100) {
        // Just output the number of found sequences if there are really many
        System.out.println("Size: " + entry.getValue().size());
      } else {
        for (List<Integer> sequence : entry.getValue()) {
          System.out.println(" " + sequence);
        }
      }
    }
  }

  /**
   * Processes a list of prime numbers and finds sequences of numbers which have
   * the same ending digit.
   * @param list The list to analyze
   * @return Collection of sequences where the key is the size
   */
  public static Map<Integer, List<List<Integer>>> findConsecutiveEndingDigits(
      Iterable<Integer> list) {
    Map<Integer, List<List<Integer>>> sameDigitSequences = new HashMap<>();

    int lastDigit = 0;
    List<Integer> currentSequence = new ArrayList<>();
    for (Integer primeNumber : list) {
      int currentLastDigit = primeNumber % 10;
      if (currentLastDigit == lastDigit) {
        currentSequence.add(primeNumber);
      } else {
        saveConsecutiveEndings(currentSequence, sameDigitSequences);
        lastDigit = currentLastDigit;
        currentSequence.clear();
      }
    }
    saveConsecutiveEndings(currentSequence, sameDigitSequences);
    return sameDigitSequences;
  }

  /**
   * Saves a sequence of prime numbers with the same ending digit to
   * `collection` if they meet the threshold.
   * @param numbers The number sequence
   * @param collection The collection to save the sequence to, if relevant
   */
  private static void saveConsecutiveEndings(List<Integer> numbers,
      Map<Integer, List<List<Integer>>> collection) {
    int size = numbers.size();
    if (size >= MIN_CONSECUTIVE_ENDINGS) {
      if (collection.get(size) == null) {
        collection.put(size, new ArrayList<List<Integer>>());
      }
      List<Integer> listCopy = new ArrayList<Integer>(numbers);
      collection.get(size).add(listCopy);
    }
  }
}
