import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

class Gunshot {
    private double [][] svs;            //support vectors
    private double[][] coeffs;          // some coefficients        


    Gunshot( ){
        svs=new double[428][60];
        coeffs=new double [1][428];         //since array was very very large we have to read from file in constructor

        svs=get_array( "Array.csv",svs);
        coeffs=get_array( "Array_B.csv",coeffs);            //read in to array from CSV
    }   


    public static double [][] get_array(String path ,double [][] a){
        String csvFile =path;
        String line = "";
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            int i=0;
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] data = line.split(cvsSplitBy);

                for(int j=0;j<a[0].length;j++){
                    a[i][j]=Double.parseDouble(data[j]);

                }
                i++;


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return a;



    }



    public int predict(double[] atts) { // Method to predict label 1 for gunshot and 0 for non gunshot

        int[] n_svs = {248, 180};


        double[] inters = {0.3361784228218555};

        // exp(-y|x-x'|^2)
        double[] kernels = new double[428];
        double kernel;
        for (int i = 0; i < 428; i++) {
            kernel = 0.;
            for (int j = 0; j < 60; j++) {
                kernel += Math.pow(svs[i][j] - atts[j], 2);
            }
            kernels[i] = -Math.exp(-0.0008 * kernel);
        }

        int[] starts = new int[2];
        for (int i = 0; i < 2; i++) {
            if (i != 0) {
                int start = 0;
                for (int j = 0; j < i; j++) {
                    start += n_svs[j];
                }
                starts[i] = start;
            } else {
                starts[0] = 0;
            }
        }

        int[] ends = new int[2];
        for (int i = 0; i < 2; i++) {
            ends[i] = n_svs[i] + starts[i];
        }

        double decision = 0.;
        for (int k = starts[1]; k < ends[1]; k++) {
            decision += kernels[k] * coeffs[0][k];
        }
        for (int k = starts[0]; k < ends[0]; k++) {
            decision += kernels[k] * coeffs[0][k];
        }
        decision += inters[0];

        if (decision > 0) {
            return 0;
        }
        return 1;
    }



    }




class Main{





    public static void main(String[] args) throws FileNotFoundException {













        double[] arr={10.8847201091 , 121.740693599 , -33.5280298437 , 3.11972462271 , -11.0020884297 , -1.21290308455 , -11.9418616633 , -2.11360413462 , -10.4781794879 , -3.28119035242 , -8.34658629262 , 0.476931854845 , -5.30066610075 , 0.680669471555 , -5.56006494482 , 1.75660184028 , -3.96168305692 , 2.07281349552 , -4.08444132356 , 1.48381353877 , -3.28960178072 , 1.35890092004 , -4.94327152844 , 1.42759356026 , -3.49860316846 , 2.89296075045 , -1.78042670542 , 2.87213332263 , -2.82861535833 , 1.73334674864 , -1.65220705291 , 1.71787972565 , -2.75117563058 , 1.20395113025 , -2.4438909105 , 1.69149151346 , -2.15641954192 , 2.4151249866 , -1.60978737289 , 1.60626090445 , -2.49178179888 , 2.44233179853 , -0.113381383929 , 4.14636910797 , -0.374239827671 , 1.95971513619 , -1.43500953418 , 1.54100395558 , -1.26125519015 , 1.02126039206 , -1.88512501707 , 0.760510525497 , -0.608824333047 , 1.80692262438 , -0.46853977097 , 2.61970846727 , -0.951079774645 , 0.613947037917 , -1.15423085416 , 0.757079102192};
        Gunshot h= new Gunshot();

        System.out.println("Label is : "+h.predict(arr));  //predicting a sample*



        //h.print();

    }}







