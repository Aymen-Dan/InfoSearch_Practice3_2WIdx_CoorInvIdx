import java.io.*;
import java.util.*;

public class TwoWordIndex {

    HashMap<String, ArrayList<Integer>> index;

    public TwoWordIndex(String folder){
        index = new HashMap();

        File dir = new File(folder);
        File[] files = dir.listFiles();

        int doc=-1;
        assert files != null;
        for (File file : files) {
            doc++;
            if(file.isFile()) {
                BufferedReader br = null;
                String line;
                try {
                    br = new BufferedReader(new FileReader(file));
                    while ((line = br.readLine()) != null) {
                        addLine(doc,line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
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

    //splits a line into words and calls addWords methods
    private void addLine(int doc, String line){
        if(line.equals("")) return;
        String[] temp = line.split("[^a-zA-Z0-9_]+");
        for(int i=0;i<temp.length-1;i++){
            if(temp[i].matches("[a-zA-Z0-9_]+")) {
                String words = temp[i]+" "+temp[i+1];
                addWords(doc,words.toLowerCase());
            }
        }
    }

    //adds words to hashmap
    private void addWords(int doc, String words) {
        if(!index.containsKey(words)){
            ArrayList IDs = new ArrayList();
            IDs.add(doc);
            index.put(words,IDs);
        }
        else if (!index.get(words).contains(doc)) index.get(words).add(doc);
    }

    public int size(){
        return index.size();
    }
    public void print(){
        OutputStream out = new BufferedOutputStream( System.out );

        Set entrySet = index.entrySet();
        Iterator it = entrySet.iterator();
        while(it.hasNext()){
            Map.Entry me = (Map.Entry)it.next();
            try {
                out.write(("<"+ me.getKey() +"> : "+me.getValue()+"\n").getBytes());
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

    //TODO Implement k-distance search
    public ArrayList<Integer> search(String input) throws Exception {
        input = input.toLowerCase();
        String input_test = input.replaceAll("\\s+","");

        if (!input_test.matches("[\\w]+(((/)[0-9]+)?[\\w]+)*"))
            throw new Exception("Incorrect format.");

        String[] temp = input.split("[\\s]+");
        ArrayList<Integer> res = new ArrayList();

        if(temp.length>1) {
            String s1 = temp[0] + " " + temp[1];
            res = index.get(s1);

            for (int i = 1; i < temp.length - 1; i++) {
                if (res == null) res = new ArrayList();
                String s2 = temp[i] + " " + temp[i + 1];
                res = intersection(res, index.get(s2));

            }
        }
        else{
            int counter=0;
            for(String s3 : index.keySet()){
                if(s3.contains(temp[0])){
                    if(counter==0) {
                        res = index.get(s3);
                        counter++;
                    }
                    else res = add(res,index.get(s3));

                }
                if (res == null) res = new ArrayList();
            }
        }
        return res;

    }

    public ArrayList<Integer> intersection(ArrayList<Integer> first, ArrayList<Integer> second){
        ArrayList<Integer> res = new ArrayList();

        for(Integer i : first){
            if(second.contains(i)) res.add(i);
        }

        return res;
    }

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