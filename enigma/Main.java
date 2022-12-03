package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import ucb.util.CommandArgs;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Nathan Mehta
 */
public final class Main {
    /** all rotors array. */
    private ArrayList<Rotor> allRotors;

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            CommandArgs options =
                new CommandArgs("--verbose --=(.*){1,3}", args);
            if (!options.ok()) {
                throw error("Usage: java enigma.Main [--verbose] "
                            + "[INPUT [OUTPUT]]");
            }

            _verbose = options.contains("--verbose");
            new Main(options.get("--")).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Open the necessary files for non-option arguments ARGS (see comment
      *  on main). */
    Main(List<String> args) {
        _config = getInput(args.get(0));

        if (args.size() > 1) {
            _input = getInput(args.get(1));
        } else {
            _input = new Scanner(System.in);
        }

        if (args.size() > 2) {
            _output = getOutput(args.get(2));
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        printMessageLine("");
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String alphT = _config.next();
            if (alphT.contains("(")
                    || alphT.contains(")") || alphT.contains("*")) {
                throw new EnigmaException("invalid alphabet");
            }
            _alphabet = new Alphabet(alphT);
            int numRots = _config.nextInt();
            int numPawls = _config.nextInt();
            allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRots, numPawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            String rotCycs = " ";
            String rotorName = _config.next();
            String rotorType = _config.next();
            while (_config.hasNext("\\(.*\\)")) {
                rotCycs += _config.next();
                rotCycs += " ";
            }
            Permutation rotPerm = new Permutation(rotCycs, _alphabet);
            if (rotorType.charAt(0) == 'M') {
                String rotNotches = "";
                for (int i = 1; i < rotorType.length(); i++) {
                    rotNotches += rotorType.charAt(i);
                }
                return new MovingRotor(rotorName, rotPerm, rotNotches);
            } else if (rotorType.charAt(0) == 'N') {
                return new FixedRotor(rotorName, rotPerm);
            } else if (rotorType.charAt(0) == 'R') {
                return new Reflector(rotorName, rotPerm);
            } else {
                throw new EnigmaException("rotor invalid");
            }

        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] inputSettings = settings.split(" ");
        boolean isReflector = false;
        if (!inputSettings[0].equals("*")) {
            throw new EnigmaException("invalid input settings line");
        }
        for (int i = 0; i < allRotors.size(); i++) {
            if (allRotors.get(i).reflecting()) {
                if (allRotors.get(i).name().equals(inputSettings[1])) {
                    isReflector = true;
                    break;
                }
            }
        }
        if (M.numRotors() + 2 > inputSettings.length) {
            throw new EnigmaException("invalid length machine");
        }
        if (!isReflector) {
            throw new EnigmaException("first input rotor should be reflector");
        }
        String rotorSets = inputSettings[M.numRotors() + 1];
        String[] rotsInSettings = new String[M.numRotors()];
        String plugCycles = " ";
        if (inputSettings[0].equals("*")) {
            for (int i = 0; i < rotsInSettings.length; i++) {
                rotsInSettings[i] = inputSettings[i + 1];
            }
            for (int i = M.numRotors() + 2; i < inputSettings.length; i++) {
                plugCycles += inputSettings[i];
                plugCycles += " ";
            }
            Permutation plugPerm = new Permutation(plugCycles, _alphabet);
            M.setPlugboard(plugPerm);
            M.insertRotors(rotsInSettings);
            M.setRotors(rotorSets);
        } else {
            throw new EnigmaException("wrong input file settings");
        }
    }

    /** Return true iff verbose option specified. */
    static boolean verbose() {
        return _verbose;
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        String setting = _input.nextLine();
        Machine machine = readConfig();
        setUp(machine, setting);
        while (_input.hasNextLine()) {
            String inputNxtLine = _input.nextLine();
            if (inputNxtLine.contains("*")) {
                setUp(machine, inputNxtLine);
            } else {
                msg = machine.convert(inputNxtLine.replaceAll(" ", ""));
                String printedMsg = "";
                while (msg.length() >= 6) {
                    printedMsg += msg.substring(0, 5) + " ";
                    msg = msg.substring(5);
                }
                printedMsg += msg;
                _output.println(printedMsg);
            }
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** True if --verbose specified. */
    private static boolean _verbose;
}
