package semiprimefinder;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class SemiprimeEvaluator {

  public static void main(String[] args) {
    int[] sizes = { 100_000 };
    for (int size : sizes) {
      conductEvaluation(size);
    }
  }

  public static void conductEvaluation(int size) {
    SemiprimeFinder finder = new SemiprimeFinder(size);
    Map<Integer, Sequence> semiprimes = finder.getSemiprimes();
    List<Integer> primes = finder.getPrimeSieve().toList();

    List<String> fileContents = prepareCsvFile(primes, semiprimes);
    writeCsvFile(size + "_eval.csv", fileContents);
    System.out.println("Wrote evaluation data to files for size = " + size);

    /*
     * List<Integer> missingPrimes = findMissingPrimes(primes, semiprimes);
     * Map<Integer, Integer> factorOccurrences =
     * numberOfOccurrences(semiprimes); Integer[] digitCount =
     * lastDigitOfFactors(semiprimes);
     * 
     * writeNumbersToFile(size + "_missingPrimes.txt", missingPrimes);
     * writeFactorCountToFile(size + "_factorCount.txt", factorOccurrences);
     * writeDigitCountToFile(size + "_digitCount.txt", digitCount);
     */
  }

  /**
   * Creates CSV data based on an Excel template. Prime, Prefix, Allowed?, k
   * partitions, partitions (n fields)
   * @param primes List of all prime numbers in a given interval
   * @param semiprimes List of all semiprimes in the same interval
   * @return List of lines to write
   */
  public static List<String> prepareCsvFile(List<Integer> primes,
      Map<Integer, Sequence> semiprimes) {
    int maxSequenceLength = maxSequenceLength(semiprimes);
    Map<Integer, Boolean> allowed = constructAllowedList(semiprimes);
    List<String> lines = new ArrayList<String>(primes.size() + 1);
    for (Integer prime : primes) {
      if (semiprimes.containsKey(prime)) {
        Boolean semiprimeAllowed = allowed.get(prime);
        semiprimeAllowed = (semiprimeAllowed != null && semiprimeAllowed);
        lines.add(semiprimeLine(prime, maxSequenceLength,
            semiprimes.get(prime), semiprimeAllowed));
      } else {
        lines.add(nonSemiprimeLine(prime, maxSequenceLength));
      }
    }
    lines.add(0, "Prime,Prefix,Allowed?,k partitions,partitions");
    return lines;
  }

  private static String nonSemiprimeLine(int prime, int maxSequenceLength) {
    // Prime, prefix, allowed?, empty k-partition, empty partitions
    return prime + ",0,0," + repeatString(",", maxSequenceLength);
  }

  private static String semiprimeLine(int prime, int maxSequenceLength,
      Sequence sequence, boolean allowed) {
    String line = prime + "," + sequence.getSign() + ","
        + (allowed ? "1" : "0") + "," + (sequence.getFactors().size() - 2)
        + ",";
    for (Integer factor : sequence.getFactors()) {
      line += factor + ",";
    }
    return line
        + repeatString(",", maxSequenceLength - sequence.getFactors().size());
  }

  private static int maxSequenceLength(Map<Integer, Sequence> semiprimes) {
    int max = 0;
    for (Sequence seq : semiprimes.values()) {
      if (seq.getFactors().size() > max) {
        max = seq.getFactors().size();
      }
    }
    return max;
  }

  private static String repeatString(String s, int times) {
    String result = "";
    for (int i = 0; i < times; ++i)
      result += s;
    return result;
  }

  private static Map<Integer, Boolean> constructAllowedList(
      Map<Integer, Sequence> semiprimes) {
    Map<Integer, Boolean> allowed = new HashMap<Integer, Boolean>(
        semiprimes.size());
    allowed.put(2, true);
    allowed.put(3, true);
    for (Map.Entry<Integer, Sequence> entry : semiprimes.entrySet()) {
      boolean isAllowed = true;
      for (Integer factor : entry.getValue().getFactors()) {
        Boolean factorAllowed = allowed.get(factor);
        if (factorAllowed == null || factorAllowed.equals(false)) {
          isAllowed = false;
          break;
        }
      }
      allowed.put(entry.getKey(), isAllowed);
    }
    return allowed;
  }

  /**
   * Find the primes for which we have not found a "semiprime" combination.
   * @param primes List of all prime numbers in a given interval
   * @param semiprimes List of all semiprimes in the same interval
   * @return List with the missing prime numbers.
   */
  public static List<Integer> findMissingPrimes(List<Integer> primes,
      Map<Integer, Sequence> semiprimes) {
    List<Integer> missingPrimes = new ArrayList<Integer>(primes);
    missingPrimes.removeAll(semiprimes.keySet());
    return missingPrimes;
  }

  /**
   * Find how many times each factor is used.
   * @param semiprimes Map of semiprimes
   * @return
   */
  public static Map<Integer, Integer> numberOfOccurrences(
      Map<Integer, Sequence> semiprimes) {
    Map<Integer, Integer> factorOccurrence = new TreeMap<Integer, Integer>();
    for (Sequence sequence : semiprimes.values()) {
      for (Integer factor : sequence.getFactors()) {
        Integer oldValue = factorOccurrence.get(factor);
        if (oldValue == null) {
          factorOccurrence.put(factor, 1);
        } else {
          factorOccurrence.put(factor, ++oldValue);
        }
      }
    }
    return factorOccurrence;
  }

  /**
   * Count how many times the last digit occurs (in factors of each semiprime)
   * @param semiprimes The Map of semiprimes & their factorization
   * @return Integer array where the key corresponds to the last digit.
   */
  public static Integer[] lastDigitOfFactors(Map<Integer, Sequence> semiprimes) {
    Integer[] digitCount = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    for (Sequence sequence : semiprimes.values()) {
      for (Integer factor : sequence.getFactors()) {
        int lastDigit = factor % 10;
        ++digitCount[lastDigit];
      }
    }
    return digitCount;
  }

  private static void writeCsvFile(String filename, List<String> lines) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(filename, "UTF-8");
      for (String line : lines) {
        writer.println(line);
      }
      writer.close();
    } catch (IOException e) {
      System.out.print("Encountered exception of type "
          + e.getClass().getSimpleName());
      System.out.println(" with message: " + e.getMessage());
    }
  }

  private static void writeNumbersToFile(String filename, List<Integer> list) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(filename, "UTF-8");
      for (Integer i : list) {
        writer.println(i);
      }
      writer.close();
    } catch (IOException e) {
      System.out.print("Encountered exception of type "
          + e.getClass().getSimpleName());
      System.out.println(" with message: " + e.getMessage());
    }
  }

  private static void writeFactorCountToFile(String filename,
      Map<Integer, Integer> factors) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(filename, "UTF-8");
      for (Map.Entry<Integer, Integer> entry : factors.entrySet()) {
        writer.println(entry.getKey() + "\t" + entry.getValue());
      }
      writer.close();
    } catch (IOException e) {
      System.out.print("Encountered exception of type "
          + e.getClass().getSimpleName());
      System.out.println(" with message: " + e.getMessage());
    }
  }

  private static void writeDigitCountToFile(String filename, Integer[] list) {
    PrintWriter writer;
    try {
      writer = new PrintWriter(filename, "UTF-8");
      for (int i = 0; i < list.length; ++i) {
        writer.println(i + "\t" + list[i]);
      }
      writer.close();
    } catch (IOException e) {
      System.out.print("Encountered exception of type "
          + e.getClass().getSimpleName());
      System.out.println(" with message: " + e.getMessage());
    }
  }
}
