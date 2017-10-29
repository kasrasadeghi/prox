package sam.prox;

import java.math.BigInteger;
import java.util.Random;

public class Utils {

  public static BigInteger randRelPrime(int numBits, BigInteger n) {
    boolean notRP = true;
    BigInteger result = BigInteger.valueOf(0);
    while (notRP) {
      result = new BigInteger(numBits, new Random());
      if (result.gcd(n).equals(1)) {
        notRP=false;
      }
    }
    return result;
  }
}
