import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    static HashMap<Integer, ArrayList<String>> buildWordListByLength(String fileName) throws IOException {

        HashMap<Integer, ArrayList<String>> dict = new HashMap<>();

        BufferedReader dictWordsReader = new BufferedReader(new FileReader(fileName));
        String s = "";
        while ((s = dictWordsReader.readLine()) != null) {
            s = s.trim().toLowerCase();

            if (dict.containsKey(s.length())) {
                dict.get(s.length()).add(s);
            } else {
                dict.put(s.length(), new ArrayList<>());
                dict.get(s.length()).add(s);
            }
        }
        dictWordsReader.close();
        return dict;
    }

    public static String mapCipher(String cipher, HashMap<Character, Character> map) {
        String s = "";
        for (char c :
                cipher.toCharArray()) {
            if (map.get(c) != null) {
                s += map.get(c);
            } else {
                // a char that was not mapped (like spaces mainly)
                s += c;
            }
        }
        return s;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        HashMap<Integer, ArrayList<String>> dict = buildWordListByLength("common.txt");

        Scanner kbReader = new Scanner(System.in);

        System.out.print("Enter the Cipher (no: , ; or '): ");
        String cipher = kbReader.nextLine().toLowerCase();

        // solver
        ArrayList<HashMap<Character, Character>> maps = new Cipher_Solver().solve(cipher, dict, 12);

        // map and print our results
        for (HashMap<Character, Character> cipherMaps :
                maps) {
            System.out.println(mapCipher(cipher, cipherMaps));
        }
    }
}