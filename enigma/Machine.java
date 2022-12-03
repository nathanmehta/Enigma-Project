package enigma;

import java.util.Collection;


/** Class that represents a complete enigma machine.
 *  @author Nathan Mehta
 */
class Machine {
    /** num rots. */
    private int _numRots;
    /** num pawls. */
    private int _numPawls;
    /** all rotor. */
    private Object[] _RotArr;
    /** rotor array. */
    private Rotor[] _rotarr;
    /** plug. */
    private Permutation _plugboard;

    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRots = numRotors;
        _numPawls = pawls;
        _RotArr = allRotors.toArray();
        _rotarr = new Rotor[_numRots];
    }
    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRots;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _numPawls;
    }

    /** Return Rotor #K, where Rotor #0 is the reflector, and Rotor
     *  #(numRotors()-1) is the fast Rotor.  Modifying this Rotor has
     *  undefined results. */
    Rotor getRotor(int k) {
        return _rotarr[k];
    }

    Alphabet alphabet() {
        return _alphabet;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        for (int i = 0; i < rotors.length; i++) {
            for (int j = 0; j < _RotArr.length; j++) {
                if ((((Rotor) _RotArr[j]).name()).equals(rotors[i])) {
                    _rotarr[i] = ((Rotor) _RotArr[j]);
                }
            }
        }
        if (rotors.length != _rotarr.length) {
            throw new EnigmaException("invalid rotors");
        }
        if (_rotarr[(numRotors() - numPawls()) - 1].rotates()) {
            throw new EnigmaException("Moving Rotor in wrong place");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != numRotors() - 1) {
            throw new EnigmaException("incorrect setting string length.");
        }
        for (int i = 1; i < numRotors(); i++) {
            if (!alphabet().contains(setting.charAt(i - 1))) {
                throw new EnigmaException(" set. not in alphabet");
            }
            _rotarr[i].set(setting.charAt(i - 1));
        }
    }

    /** Return the current plugboard's permutation. */
    Permutation plugboard() {
        return _plugboard;
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        advanceRotors();
        if (Main.verbose()) {
            System.err.printf("[");
            for (int r = 1; r < numRotors(); r += 1) {
                System.err.printf("%c",
                        alphabet().toChar(getRotor(r).setting()));
            }
            System.err.printf("] %c -> ", alphabet().toChar(c));
        }
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c -> ", alphabet().toChar(c));
        }
        c = applyRotors(c);
        c = plugboard().permute(c);
        if (Main.verbose()) {
            System.err.printf("%c%n", alphabet().toChar(c));
        }
        return c;
    }

    /** Advance all rotors to their next position. */
    private void advanceRotors() {
        boolean[] advance = new boolean[numRotors()];
        for (int i = 0; i < numRotors(); i++) {
            if (i == numRotors() - 1) {
                advance[i] = true;
            } else if (!_rotarr[i].rotates()) {
                advance[i] = false;
            } else if (_rotarr[i + 1].atNotch()) {
                advance[i] = true;
                advance[i + 1] = true;
            }
        }
        for (int i = 0; i < numRotors(); i++) {
            if (advance[i]) {
                _rotarr[i].advance();
            }
        }
    }

    /** Return the result of applying the rotors to the character C (as an
     *  index in the range 0..alphabet size - 1). */
    private int applyRotors(int c) {
        int nConvFor = c;
        for (int i = _rotarr.length - 1; i >= 0; i -= 1) {
            nConvFor = _rotarr[i].convertForward(nConvFor);
        }
        int nConvBack = nConvFor;
        for (int i = 1; i < _rotarr.length; i += 1) {
            nConvBack = _rotarr[i].convertBackward(nConvBack);
        }
        return nConvBack;
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String encodedMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            int converted = convert(alphabet().toInt(msg.charAt(i)));
            encodedMsg += alphabet().toChar(converted);
        }
        return encodedMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;
}
