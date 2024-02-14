
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        String filePath = "C:\\Users\\armad\\OneDrive\\Desktop\\IntelliJ IDEA Community Edition 2021.1.1\\IdeaProjects\\InfoSearch_Practice3_2WIdx_CoorInvIdx\\src\\res";

        long time_twi =  System.nanoTime();
        TwoWordIndex twi = new TwoWordIndex(filePath);
        time_twi = System.nanoTime()-time_twi;


        long time_cii =  System.nanoTime();
        CInvIndex cii = new CInvIndex(filePath);
        time_cii = System.nanoTime()-time_cii;

        Scanner in = new Scanner(System.in);

        System.out.println("\n1 - Print TWI in console;\n2 - Print CII in console;\n3 - Compare time for TWI & CII;\n4 - Search TWI;\n5 - Search CII;\n6 - Show TWI in text file;\n7) Show CII in text file;\n-1) Exit\n");

        int i= in.nextInt();

        while(i!=-1) {
            switch (i) {
                case 1:
                    twi.print();
                    break;
                case 2:
                    /**TODO: see if a more comprehensible CII format is available*/
                    cii.print();
                    break;
                case 3:
                    System.out.println("Time for vocabulary.txt: " + time_twi + " ns, or " + time_twi / 1_000_000.0 + " ms, or " + time_twi / 1_000_000_000.0 + " s");
                    System.out.println("Time for vocabulary.txt: " + time_cii + " ns, or " + time_cii / 1_000_000.0 + " ms, or " + time_cii / 1_000_000_000.0 + " s");
                    break;
                case 4:
                    /**TODO: check if TWI work on input*/
                    System.out.println("Enter TWI query:");
                    in.nextLine();
                    String input = in.nextLine();
                    System.out.println("Enter Levenshtein distance value (must be >= 0):");
                    int k = in.nextInt();
                    if ( k >= 0) {
                        System.out.println(twi.search(input, k));
                    } else {
                        System.out.println("Levenshtein distance value must be >= 0.");
                    }
                    break;
                case 5:
                    /**TODO: check if CII search works correctly*/
                    System.out.println("Enter CII:");
                    in.nextLine();
                    input = in.nextLine();
                    System.out.println(cii.search(input));
                    break;
                case 6:
                    twi.open2WIndexTXT("src/results/2WIndex.txt");
                    break;
                case 7:
                    /**TODO: create CII.txt showing methods*/
                    System.out.println("FILE SHOW CII IN PROGRESS");
                    break;

                default:
                    System.out.println("Input format is incorrect.");
            }
            System.out.println("\n1 - Print TWI in console;\n2 - Print CII in console;\n3 - Compare time for TWI & CII;\n4 - Search TWI;\n5 - Search CII;\n6 - Show TWI in text file;\n7) Show CII in text file;\n-1) Exit\n");
            i = in.nextInt();
        }


    }
}