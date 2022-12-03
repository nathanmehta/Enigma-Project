package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Nathan Mehta
 */
class Permutation {
    /** cycs array. */
    private String[] cycsArray;
    /** cycs. */
    private String  _cycles;
    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        String trimmed = cycles.trim();
        trimmed = trimmed.replace("(", " ");
        trimmed = trimmed.replace(")", " ");
        cycsArray = trimmed.split(" ");
    }


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        String[] withNewCyc = new String[cycsArray.length + 1];
        System.arraycopy(cycsArray, 0, withNewCyc, 0, cycsArray.length);
        withNewCyc[cycsArray.length + 1] = cycle;
        cycsArray = withNewCyc;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return alphabet().size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char permutedChar = alphabet().toChar(wrap(p));
        char permutedTo = 't';
        for (int i = 0; i < cycsArray.length; i++) {
            for (int j = 0; j < cycsArray[i].length(); j++) {
                if (cycsArray[i].charAt(j) == permutedChar) {
                    permutedTo = cycsArray[i].charAt((j + 1)
                            % (cycsArray[i].length()));
                    return alphabet().toInt(permutedTo);
                }
            }
        } return p;

    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char permutedChar = alphabet().toChar(wrap(c));
        char permutedTo = 't';
        for (int i = 0; i < cycsArray.length; i++) {
            for (int j = 0; j < cycsArray[i].length(); j++) {
                if (cycsArray[i].charAt(j) == permutedChar) {
                    if (cycsArray[i].length() == 1) {
                        permutedTo = cycsArray[i].charAt(j);
                        return alphabet().toInt(permutedTo);
                    } else if (j == 0) {
                        permutedTo = cycsArray[i].charAt(((j - 1)
                                % (cycsArray[i].length()))
                                + cycsArray[i].length());
                        return alphabet().toInt(permutedTo);
                    } else {
                        permutedTo = cycsArray[i].charAt((j - 1)
                                % (cycsArray[i].length()));
                        return alphabet().toInt(permutedTo);
                    }
                }
            }
        } return c;

    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        int permuteToInd = permute(alphabet().toInt(p));
        char permuteToChar = alphabet().toChar(permuteToInd);
        return permuteToChar;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        int permuteToInd = invert(alphabet().toInt(c));
        char permuteToChar = alphabet().toChar(permuteToInd);
        return permuteToChar;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < alphabet().size(); i++) {
            if (permute(alphabet().toChar(i)) == alphabet().toChar(i)) {
                return false;
            }
        } return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

}
