package mandelbrot.utilities;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberAwareStringComparator implements Comparator<CharSequence> {
    public static final NumberAwareStringComparator INSTANCE =
        new NumberAwareStringComparator();

    private static final Pattern PATTERN = Pattern.compile("(\\D*)(\\d*)");

    private NumberAwareStringComparator() {
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(CharSequence sequence1, CharSequence sequence2) {
        Matcher matcher1 = PATTERN.matcher(sequence1);
        Matcher matcher2 = PATTERN.matcher(sequence2);

        while (matcher1.find() && matcher2.find()) {
            int nonDigitCompare = matcher1.group(1).compareTo(matcher2.group(1));
            if (0 != nonDigitCompare) {
                return nonDigitCompare;
            }

            // matcher.group(2) fetches any digits captured by the
            // second parentheses in PATTERN.
            if (matcher1.group(2).isEmpty()) {
                return matcher2.group(2).isEmpty() ? 0 : -1;
            } else if (matcher2.group(2).isEmpty()) {
                return +1;
            }

            BigInteger n1 = new BigInteger(matcher1.group(2));
            BigInteger n2 = new BigInteger(matcher2.group(2));
            int numberCompare = n1.compareTo(n2);
            if (0 != numberCompare) {
                return numberCompare;
            }
        }

        return matcher1.hitEnd() && matcher2.hitEnd() ? 0 :
               matcher1.hitEnd()                ? -1 : +1;
    }
}