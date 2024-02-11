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



    // TODO: Implement k-distance search
    public ArrayList<Integer> search(String input) throws Exception {
        // Convert the input to lowercase and remove extra whitespaces
        input = input.toLowerCase();
        String input_test = input.replaceAll("\\s+","");

        // Check if the input matches the expected format
        if (!input_test.matches("[\\w]+(((/)[0-9]+)?[\\w]+)*"))
            throw new Exception("Incorrect format.");

        String[] temp = input.split("[\\s]+");
        ArrayList<Integer> res = new ArrayList();

        // Handle the case where there are more than one word in the query
        if(temp.length > 1) {
            String s1 = temp[0] + " " + temp[1];
            res = index.get(s1);

            // Iterate through the remaining words in the query
            for (int i = 1; i < temp.length - 1; i++) {
                if (res == null) res = new ArrayList();
                String s2 = temp[i] + " " + temp[i + 1];
                res = intersection(res, index.get(s2));
            }
        }
        // Handle the case where there is only one word in the query
        else{
            int counter=0;
            for(String s3 : index.keySet()){
                if(s3.contains(temp[0])){
                    if(counter == 0) {
                        res = index.get(s3);
                        counter++;
                    }
                    else res = add(res, index.get(s3));
                }
                if (res == null) res = new ArrayList();
            }
        }
        return res;
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

}
