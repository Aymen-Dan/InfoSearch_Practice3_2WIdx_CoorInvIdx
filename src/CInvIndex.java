import java.awt.*;
import java.io.*;
import java.util.*;

public class CInvIndex {

    // The main data structure representing the inverted index
    HashMap<String, HashMap<Integer,ArrayList<Integer>>> index;

    // Variables to keep track of the current position and the number of files processed
    private int position;
    private int files_num;

    // Constructor that builds the inverted index from a specified folder of text documents
    public CInvIndex(String folder){
        // Initialize the index and file count
        index = new HashMap();
        files_num = 0;

        // Create a File object for the specified folder
        File dir = new File(folder);

        // List all files in the folder
        File[] files = dir.listFiles();

        // Counter for document IDs
        int doc=-1;

        // Ensure files is not null
        assert files != null;

        //Iterate through each file in the folder
        for (File file : files) {
            doc++;
            files_num++;
            position= -1;

            // Check if the current item is a file
            if(file.isFile()) {
                BufferedReader br = null;
                String line;
                try {
                    //Read each line from the file and process it
                    br = new BufferedReader(new FileReader(file));
                    while ((line = br.readLine()) != null) {
                        addLine(doc,line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    // Close the BufferedReader in a final block to ensure it gets closed
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
        this.saveToFile();
    }

    // Helper method to split a line into words and call addWord method
    private void addLine(int doc, String line){
        if(line.equals("")) return;

        // Split the line into words using regular expression
        String[] temp = line.split("[^a-zA-Z0-9_]+");

        // Iterate through each word and add it to the index
        for (String s : temp) {
            if (s.matches("[a-zA-Z0-9_]+")) {
                position++;
                addWord(doc, s.toLowerCase());
            }
        }
    }

    // Helper method to add a word to the index
    private void addWord(int doc, String word) {
        // Check if the word is already in the index
        if(!index.containsKey(word)){
            // If not, create a new entry in the index
            ArrayList<Integer> pos = new ArrayList();
            pos.add(position);
            HashMap<Integer,ArrayList<Integer>> idMap = new HashMap();
            idMap.put(doc,pos);
            index.put(word,idMap);
        }
        // If the word is in the index, update the existing entry
        else if(!index.get(word).containsKey(doc)){
            ArrayList<Integer> pos = new ArrayList();
            pos.add(position);
            index.get(word).put(doc,pos);
        }
        // If the word and document ID are already present, add the position to the list
        else index.get(word).get(doc).add(position);
    }

    // Method to print the inverted index
    public void print() {
        for (Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> entry : index.entrySet()) {
            String word = entry.getKey();
            System.out.println("Word: " + word);

            for (Map.Entry<Integer, ArrayList<Integer>> docEntry : entry.getValue().entrySet()) {
                int docId = docEntry.getKey();
                ArrayList<Integer> positions = docEntry.getValue();

                System.out.println("  Document: Doc" + (docId + 1));
                System.out.println("  Positions: " + positions);
            }

            System.out.println("------------------------------");
        }
    }

    // Method to search for phrases with specified distances
    public HashMap<Integer, ArrayList<Integer>> search(String input, int distance) throws Exception {
        // Convert the input to lowercase and remove extra whitespaces
        input = input.toLowerCase();
        String input_test = input.replaceAll("\\s+", "");

        // Check if the input matches the expected format
        if (!input_test.matches("[\\w]+(((/)[0-9]+)?[\\w]+)*"))
            throw new Exception("Incorrect format.");

        // Split the input into an array of words
        String[] temp = input.split("[\\s]+");

        // Initialize the result HashMap
        HashMap<Integer, ArrayList<Integer>> res = new HashMap<>();

        // Get the initial result based on the first word in the query
        res = index.get(temp[0]);
        if (res == null) res = new HashMap<>();

        int i = 1;
        // Iterate through the remaining words in the query
        while (i < temp.length) {
            // Check if the current word includes a distance specification
            if (temp[i].matches("(/)[0-9]+")) {
                // Remove the '/' and convert the remaining part to an integer
                temp[i] = temp[i].replaceAll("/", "");
                distance = Integer.valueOf(temp[i]);
                i++;
            }
            // Get the inverted index for the current word in the query
            HashMap<Integer, ArrayList<Integer>> temp_hash = new HashMap<>();
            temp_hash = index.get(temp[i]);
            if (temp_hash == null) temp_hash = new HashMap<>();

            // Intersect the current result with the inverted index for the current word
            res = intersect(distance, res, temp_hash);
            i++;
        }

        return res;
    }

    // Helper method to print search results in the new format
    public void printSearchResults(String input, int distance) throws Exception {
        HashMap<Integer, ArrayList<Integer>> results = search( input,  distance); if (results.isEmpty()) {
            System.out.println("No matches for the specified input or distance!");
        } else {

        for (Map.Entry<Integer, ArrayList<Integer>> entry : results.entrySet()) {
            int docId = entry.getKey();
            ArrayList<Integer> positions = entry.getValue();

                for (int position : positions) {
                    System.out.println("Doc ID: " + docId + ", Position Number: " + position);
                }
            }
        }
    }

    // Method to compute the intersection of two sets of positions with a specified distance
    public HashMap<Integer, ArrayList<Integer>> intersect(int dist, HashMap<Integer,
            ArrayList<Integer>> first, HashMap<Integer, ArrayList<Integer>> second) {
        // Initialize the result HashMap
        HashMap<Integer, ArrayList<Integer>> res = new HashMap();

        // Iterate through the document IDs
        for (int i = 0; i < files_num; i++) {
            ArrayList<Integer> res_list = new ArrayList();
            res.put(i, res_list);

            // Check if the document ID is present in both sets
            if (first.containsKey(i) && second.containsKey(i)) {
                // Iterate through the positions in the first set
                for (int j : first.get(i)) {
                    // Iterate through the specified distance
                    for (int a = 1; a <= dist; a++) {
                        int t = j + a;
                        // Check if the position in the second set is within the specified distance
                        if (second.get(i).contains(t)) {
                            res.get(i).add(j + dist);
                        }
                    }
                }
            }
            // If the result for a document ID is empty, remove it from the result HashMap
            if (res.get(i).isEmpty()) res.remove(i);
        }
        return res;
    }

    /**SAVE INTO CINVINDEX.TXT FILE*/
    public void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/results/CInvIndex.txt"))) {
            // Header
            writer.write(String.format("%-20s | %-20s | %-20s%n", "Word", "Document", "Positions"));
            writer.write("--------------------------------------------------------------\n");

            // Iterate through each term in the index
            for (Map.Entry<String, HashMap<Integer, ArrayList<Integer>>> entry : index.entrySet()) {
                String word = entry.getKey();
                // Iterate through the document IDs and positions for each term
                for (Map.Entry<Integer, ArrayList<Integer>> docEntry : entry.getValue().entrySet()) {
                    int docId = docEntry.getKey();
                    ArrayList<Integer> positions = docEntry.getValue();

                    // Word, Document, and Positions
                    writer.write(String.format("%-20s | %-20s | %-20s%n", word, "Doc" + (docId + 1), positions));
                }
            }

            System.out.println("Index saved to src/results/CInvIndex.txt");
        } catch (IOException e) {
            System.out.println("Error saving index to file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**OPEN A CINVINDEX.TXT FILE*/
    public void openCInvIndexTXT(String filePath) throws IOException {
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


