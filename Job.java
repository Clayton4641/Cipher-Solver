import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Job {
    public HashMap<Character, Character> charMap;
    public int word_index;

    public Job() {
        word_index = 0;
        charMap = new HashMap<>();
    }

    public Job(int word_index, HashMap<Character, Character> charMap) {
        this.word_index = word_index;
        this.charMap = charMap;
    }
}
