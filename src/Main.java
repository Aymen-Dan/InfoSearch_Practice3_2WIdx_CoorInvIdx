
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

        System.out.println("\n1 - Print TWI in console;\n2 - Print CII in console;\n3 - Show time for TWI;\n4 - Show time for TWI;" +
                "\n5 - Show TWI in text file;\n6 - Show CII in text file;\n7) Search TWI;\n8) Search CII;\n-1) Exit\n");
        int i= in.nextInt();

        while(i!=-1) {
            switch (i) {
                case 1:
                    /**TODO: see if a more comprehensible TWI format is available*/
                    twi.print();
                    break;
                case 2:
                    /**TODO: see if a more comprehensible CII format is available*/
                    cii.print();
                    break;
                case 3:
                        /**TODO: change TWI time format*/
                    System.out.println("time: "+time_twi*1.0E-9+" s");
                    break;
                case 4:
                    /**TODO: change CII time format*/
                    System.out.println("\ntime: "+time_cii*1.0E-9+" s\n");
                    break;
                case 5:
                    /**TODO: check if TWI search works correctly*/
                    System.out.println("Enter TWI:");
                    in.nextLine();
                    String input = in.nextLine();
                   // System.out.println(twi.search(input));
                    break;
                case 6:
                    /**TODO: check if CII search works correctly*/
                    System.out.println("Enter CII:");
                    in.nextLine();
                    input = in.nextLine();
                    System.out.println(cii.search(input));
                    break;
                case 7:
                    /**TODO: create TWI.txt showing methods*/
                    System.out.println("FILE SHOW TWI IN PROGRESS");
                    break;
                case 8:
                    /**TODO: create CII.txt showing methods*/
                    System.out.println("FILE SHOW CII IN PROGRESS");
                    break;
                default:
                    System.out.println("Input format is incorrect.");
            }
            System.out.println("\n1 - Print TWI in console;\n2 - Print CII in console;\n3 - Show time for TWI;\n4 - Show time for TWI;" +
                    "\n5 - Show TWI in text file;\n6 - Show CII in text file;\n7) Search TWI;\n8) Search CII;\n-1) Exit\n");
            i = in.nextInt();
        }


    }
}