package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;
/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void checkPermSize() {
        Alphabet alphaT = new Alphabet();
        Permutation permT = new Permutation("", alphaT);
        assertEquals(26, permT.size());

        Alphabet alpha2 = new Alphabet("ABCD");
        Permutation perm1 = new Permutation("", alpha2);
        assertEquals(4, perm1.size());
    }

    @Test
    public void checkPermute() {
        Alphabet alphaT = new Alphabet();
        Permutation permT = new Permutation("", alphaT);
        char permuted = 'C';
        assertEquals('C', permT.permute(permuted));

        Permutation perm2 = new Permutation("(CYOF)", alphaT);
        assertEquals('Y', perm2.permute('C'));
        assertEquals('C', perm2.permute('F'));

        Permutation perm3 =
                new Permutation("(BNST) (LERF) (D) (XCVMK)", alphaT);
        assertEquals('X', perm3.permute('K'));
        assertEquals('D', perm3.permute('D'));
        assertEquals('G', perm3.permute('G'));

    }

    @Test
    public void checkInvert() {
        Alphabet alphaT = new Alphabet();
        Permutation permT = new Permutation("", alphaT);
        char inverted = 'C';
        assertEquals('C', permT.invert(inverted));

        Permutation perm2 = new Permutation("(CYOF)", alphaT);
        assertEquals('F', perm2.invert('C'));
        assertEquals('O', perm2.invert('F'));

        Permutation perm3 =
                new Permutation("(BNST) (LERF) (D) (XCVMK)", alphaT);
        assertEquals('M', perm3.invert('K'));
        assertEquals('F', perm3.invert('L'));
        assertEquals('D', perm3.invert('D'));
        assertEquals('G', perm3.invert('G'));
    }

    @Test
    public void derangeTest() {
        Alphabet alphaT = new Alphabet();
        Permutation permT = new Permutation("", alphaT);
        assertFalse(permT.derangement());

        Alphabet alpha1 = new Alphabet("ABCD");
        Permutation perm1 = new Permutation("(ABCD)", alpha1);
        assertTrue(perm1.derangement());

        Permutation perm3 = new Permutation("(QWER) (POIU) (MNH) (L)", alphaT);
        assertFalse(perm3.derangement());

    }

    @Test
    public void testAlphContains() {
        Alphabet alphT = new Alphabet("ABCDEF");
        assertTrue(alphT.contains('C'));

        Alphabet alph1 = new Alphabet("");
        assertFalse(alph1.contains('C'));

    }

    @Test
    public void testAlphToChar() {
        Alphabet alphT = new Alphabet("ABCDEFGHIJKL");
        assertEquals('A', alphT.toChar(0));
        assertEquals('L', alphT.toChar(11));
        assertEquals('E', alphT.toChar(4));
    }

    @Test
    public void testAlphToInt() {
        Alphabet alphT = new Alphabet("ABCDEFGHIJKL");
        assertEquals(0, alphT.toInt('A'));
        assertEquals(11, alphT.toInt('L'));
        assertEquals(4, alphT.toInt('E'));
    }



}
