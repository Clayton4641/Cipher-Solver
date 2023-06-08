import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Cipher_Solver {
    public Queue<Job> jobQueue = new LinkedList<>();
    public ArrayList<HashMap<Character, Character>> finishedJobs = new ArrayList<>();
    public HashMap<Integer, ArrayList<String>> dict = null;
    public ArrayList<String> cipherWords = null;
    private Lock jobQueueLock = new ReentrantLock();
    private Lock completedMapLocks = new ReentrantLock();

    // returns character maps for cipher
    public ArrayList<HashMap<Character, Character>> solve(String cipher, HashMap<Integer, ArrayList<String>> dict, int threadCount) throws InterruptedException {
        this.dict = dict;

        cipherWords = new ArrayList<>(List.of(cipher.split(" ")));

        ArrayList<Thread> threads = new ArrayList<>();

        // create empty job to get started
        jobQueue.add(new Job());

        // loop to ensure all jobs are completed and threads didnt just stop with more work to do
        while (!jobQueue.isEmpty()) {

            for (int i = 0; i < threadCount; ++i) {
                threads.add(new Thread(new worker()));
                threads.get(i).start();
            }

            for (int i = 0; i < threadCount; ++i) {
                threads.get(i).join();
            }
        }
        return finishedJobs;
    }

    class worker implements Runnable {
        @Override
        public void run() {
            jobQueueLock.lock();
            while (!jobQueue.isEmpty()) {
                Job currentJob = jobQueue.remove();
                jobQueueLock.unlock();

                // the current job is complete
                if (currentJob.word_index == cipherWords.size()) {
                    completedMapLocks.lock();
                    finishedJobs.add(currentJob.charMap);
                    completedMapLocks.unlock();
                    jobQueueLock.lock();
                    continue;
                }

                // the word we want to solve for
                String currentCipherWord = cipherWords.get(currentJob.word_index);

                // get words that are the same length as the one we want to solve for
                ArrayList<String> fittingWords = dict.get(currentCipherWord.length());

                for (String wordToTest : fittingWords) {

                    boolean wordPassed = true;
                    // make deep copy of the map, we need a fresh copy as we will test lots of words with the current job map
                    HashMap<Character, Character> newJobMap = new HashMap<>(currentJob.charMap);

                    for (int i = 0; i < currentCipherWord.length(); ++i) {
                        // if we have mapped the char before and the mapping doesnt match
                        if (newJobMap.containsKey(currentCipherWord.charAt(i)) && newJobMap.get(currentCipherWord.charAt(i)) != wordToTest.charAt(i)) {
                            wordPassed = false;
                            break;
                        } else {
                            // no mapping, add the mapping so we can continue testing the word
                            newJobMap.put(currentCipherWord.charAt(i), wordToTest.charAt(i));
                        }
                    }

                    // this word works with the current mapping, move to next word
                    if (wordPassed) {
                        jobQueueLock.lock();
                        jobQueue.add(new Job(currentJob.word_index + 1, newJobMap));
                        jobQueueLock.unlock();

                    }

                }
                jobQueueLock.lock();
            }
            // make sure the thread releases the lock on the job q
            jobQueueLock.unlock();
        }
    }
}