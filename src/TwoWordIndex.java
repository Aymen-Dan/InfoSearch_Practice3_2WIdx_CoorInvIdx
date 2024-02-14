import java.awt.*;
import java.io.*;
import java.util.*;

public class TwoWordIndex {

    // The main data structure representing the two-word index
    HashMap<String, ArrayList<Integer>> index;

    // Constructor that builds the two-word index from a specified folder of text documents
    public TwoWordIndex(String folder){
        // Initialize the index
        index = new HashMap();

        // Create a File object for the specified folder
        File dir = new File(folder);

        // List all files in the folder
        File[] files = dir.listFiles();

        // Counter for document IDs
        int doc=-1;

        // Ensure files is not null
        assert files != null;

        // Iterate through each file in the folder
        for (File file : files) {
            doc++;

            // Check if the current item is a file
            if(file.isFile()) {
                BufferedReader br = null;
                String line;
                try {
                    // Read each line from the file and process it
                    br = new BufferedReader(new FileReader(file));
                    while ((line = br.readLine()) != null) {
                        addLine(doc,line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Close the BufferedReader in a finally block to ensure it gets closed
                    if (br != null) {
                        try {
                            br.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        // Save to file
        this.saveToFile();
    }

    // Helper method to split a line into two-word phrases and call addWords method
    private void addLine(int doc, String line){
        if(line.equals("")) return;
        String[] temp = line.split("[^a-zA-Z0-9_]+");

        // Iterate through the words in the line and add two-word phrases to the index
        for(int i=0; i < temp.length - 1; i++){
            if(temp[i].matches("[a-zA-Z0-9_]+")) {
                String words = temp[i] + " " + temp[i+1];
                addWords(doc, words.toLowerCase());
            }
        }
    }

    // Helper method to add two-word phrases to the index
    private void addWords(int doc, String words) {
        // Check if the phrase is already in the index
        if(!index.containsKey(words)){
            // If not, create a new entry in the index
            ArrayList<Integer> IDs = new ArrayList();
            IDs.add(doc);
            index.put(words, IDs);
        }
        // If the phrase is in the index, update the existing entry
        else if (!index.get(words).contains(doc)) index.get(words).add(doc);
    }

    // Method to get the size of the index
    public int size(){
        return index.size();
    }

    // Method to print the two-word index
    public void print(){
        OutputStream out = new BufferedOutputStream(System.out);

        // Iterate through each entry in the index
        Set entrySet = index.entrySet();
        Iterator it = entrySet.iterator();
        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            try {
                // Print the two-word phrase and associated document IDs
                out.write(("<" + me.getKey() + "> : " + me.getValue() + "\n").getBytes());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for document IDs based on the input query and Levenshtein distance.
     * @param input The input query.
     * @param k The Levenshtein distance.
     * @return An ArrayList<Integer> containing document IDs.
     * @throws Exception Thrown if the input format is incorrect.*/
    public ArrayList<Integer> search(String input, int k) throws Exception {
        // Convert the input to lowercase and remove extra whitespaces
        input = input.toLowerCase();
        String input_test = input.replaceAll("\\s+", "");

        // Check if the input matches the expected format
        if (!input_test.matches("[\\w]+(((/)[0-9]+)?[\\w]+)*"))
            throw new Exception("Incorrect format.");

        // Split the input into an array of words
        String[] temp = input.split("[\\s]+");
        // Initialize the result ArrayList
        ArrayList<Integer> res = new ArrayList<>();

        // Multiple words in the query
        if (temp.length > 1) {
            // Form the first two-word phrase
            String s1 = temp[0] + " " + temp[1];
            // Get the document IDs associated with the first two-word phrase
            res = index.get(s1);

            // Iterate through the remaining two-word phrases in the query
            for (int i = 1; i < temp.length - 1; i++) {
                // Check if res is null, create a new ArrayList
                if (res == null) res = new ArrayList<>();
                // Form the current two-word phrase
                String s2 = temp[i] + " " + temp[i + 1];
                // Calculate the intersection of res with the document IDs of the current two-word phrase
                res = intersection(res, index.get(s2));
            }

            // Apply k-distance filter to the remaining words of the query
            res = applyKDistance(temp, res, k);
        } else { // Single word in the query
            int counter = 0;
            // Iterate through each two-word phrase in the index
            for (String s3 : index.keySet()) {
                // Check if the current two-word phrase contains the single word in the query
                if (s3.contains(temp[0])) {
                    // Check if it's the first match, set res to the document IDs of the current two-word phrase
                    if (counter == 0) {
                        res = index.get(s3);
                        counter++;
                    } else {
                        // Merge document IDs using the add method
                        res = add(res, index.get(s3));
                    }
                }
                // Check if res is null, create a new ArrayList
                if (res == null) res = new ArrayList<>();
            }
        }

        return res;

    }

    // Helper method to print search results in the new format
    public void printSearchResults(String input, int k) throws Exception {
        ArrayList<Integer> results = search( input,  k);
        if (results.isEmpty()) {
            System.out.println("No matches for the specified input!");
        } else {
            for (Integer docId : results) {
                    System.out.println("Doc number: " + docId);
            }
        }
    }



    // Helper method to compute the intersection of two sets of document IDs
    public ArrayList<Integer> intersection(ArrayList<Integer> first, ArrayList<Integer> second){
        ArrayList<Integer> res = new ArrayList();

        for(Integer i : first){
            if(second.contains(i)) res.add(i);
        }

        return res;
    }

    // Helper method to add two sets of document IDs
    public ArrayList<Integer> add(ArrayList<Integer> first, ArrayList<Integer> second){
        ArrayList<Integer> res = new ArrayList();
        for(Integer i : first){
            if(!res.contains(i)) res.add(i);
        }
        for(Integer i : second){
            if(!res.contains(i)) res.add(i);
        }
        return res;
    }

    /**
     * Applies k-distance filter to the remaining words of the query.
     * @param query The array of words in the query.
     * @param matches The initial set of document IDs.
     * @param k The Levenshtein distance.
     * @return The filtered document IDs after applying the k-distance filter.
     */
    private ArrayList<Integer> applyKDistance(String[] query, ArrayList<Integer> matches, int k) {
        ArrayList<Integer> filteredMatches = new ArrayList<>();

        // Iterate through the initial matches
        for (Integer match : matches) {
            // Check for k-distance variations in the remaining words of the query
            if (checkKDistance(query, 2, match, k)) {
                filteredMatches.add(match);
            }
        }

        return filteredMatches;
    }


    // Helper method to check k-distance variations in the remaining words of the query
    private boolean checkKDistance(String[] query, int queryIndex, int matchIndex, int k) {
        // Dynamic programming matrix for Levenshtein distance
        int[][] dp = new int[query.length - queryIndex + 1][k + 1];

        // Initialize the matrix
        for (int i = 0; i <= k; i++) {
            dp[0][i] = i;
        }

        // Fill the matrix using dynamic programming
        for (int i = 1; i <= query.length - queryIndex; i++) {
            for (int j = 0; j <= k; j++) {
                if (j == 0) {
                    dp[i][j] = i;
                } else {
                    int cost = (query[queryIndex + i - 1].equals(index.get(matchIndex))) ? 0 : 1;
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
                }
            }
        }

        // Check if the Levenshtein distance is within the specified k
        return dp[query.length - queryIndex][k] <= k;
    }



    /**SAVE INTO 2WINDEX.TXT FILE*/
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/results/2WIndex.txt"))) {
            ArrayList<String> res_list = new ArrayList<>(index.keySet());
            Collections.sort(res_list);

            // Header
            writer.write(String.format("%-20s | %-20s%n", "Word", "Present in"));
            writer.write("-------------------------------\n");

            for (String s : res_list) {
                // Word
                writer.write(String.format("%-20s | ", s));

                // Document numbers
                for (int i = 0; i < index.get(s).size(); i++) {
                    int num = index.get(s).get(i) + 1;
                    writer.write("Doc" + num + " ");
                }
                writer.newLine();
            }

            System.out.println("Index saved to src/results/2WIndex.txt");
        } catch (IOException e) {
            System.out.println("Error saving index to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**OPEN AN 2WINDEX.TXT FILE*/
    public void open2WIndexTXT(String filePath) throws IOException {
        File file = new File(filePath);

        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            System.out.println("\nPulling up the file...");

            if (file.exists()) {
                desktop.open(file);
            } else {
                System.out.println("File not found: " + filePath + "; Please restart the program.");
            }
        } else {
            System.out.println("Desktop is not supported.");
        }
    }


}
