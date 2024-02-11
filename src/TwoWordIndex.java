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

}
