
package swingdatacodingchallenge;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used to create and store a swing object and the associated data
 * with said swing. Each swing object stores several samples of one swing with data for
 * the timestamp, a three-axis accelerometer, and a three-axis gyroscope. The sensors
 * collecting this data create a csv file of the information recorded based on the timestamp.
 */
public class Swing {
    
    //instance variable to collectively store each sample of the swing
    //format of each double array is: timestamp, ax, ay, az, wx, wy, wz
    private ArrayList<Double[]> swingData = new ArrayList<>();
    
    /**
    * Constructor
    * Reads the csv dataFile and populates the swingData ArrayList of all the samples from one swing
    * @param dataFile The csv file that contains the swing data sample points 
    */
    public Swing(File dataFile){
        //for each line in the csv file, create an array of that data
        //and add that array to the swingData arraylist        
        //try catch if file doesn't exist
        ArrayList<Double[]> testData = null;
        try{
            //reader for the csv file            
            BufferedReader reader = new BufferedReader(new FileReader(dataFile));
            
            //temp variable to store each line in the csv file
            String line = null;
        
            //loop through each line of the file to create an array to put into the swing data
            while((line = reader.readLine()) != null){
                //get each sample point separated by commas for one line
                String[] sampleStringArray = line.split(",");
                
                //create a Double array from string sample array 
                //format is: timestamp, ax, ay, az, wx, wy, wz
                //could wait to do this when calculations are needed
                Double[] sampleDoubleArray = new Double[sampleStringArray.length];
                
                for(int i = 0; i < sampleDoubleArray.length; i++){
                    sampleDoubleArray[i] = Double.parseDouble(sampleStringArray[i]);                    
                }
              
                //add the sample array of doubles to the arrayList of all samples               
                this.swingData.add(sampleDoubleArray);   
            }
            
            //close the reader
            reader.close();
            
            //check the size of the arraylist compared to rows in csv file
            //System.out.println("size of the arraylist is " + this.swingData.size());
        }
        catch(FileNotFoundException e){
            System.out.println("File: " + dataFile.getPath() + " was not found.");
        }
        catch(IOException e){
             e.printStackTrace();
        }            
    }  //end constructor    
    
    /**
    * This method searches for data values that are higher than a specified threshold and returns
    * the first index where data has values that meet this criteria for a number of samples
    * @param data the column to search
    * @param indexBegin where to start the search of the ArrayList of data swing sample points
    * @param indexEnd where to end the search of the ArrayList of data swing sample points
    * @param threshold search data values above this value
    * @param winLength the number of samples that must be above this threshold
    * @return the index of data that meets the criteria
    */
    public String searchContinuityAboveValue(String data, int indexBegin, int indexEnd, double threshold, int winLength){
        //initialize to -1 for searching results (-1 means did not find criteria)
        int dataIndex = -1;   
        
        //interpret the column data to analyze relative to the index of Double array of each sample
        int dataColumn = getDataColumnToAnalyze(data);
        
        //keep track of how many samples meet critera
        int samples =0;
        
        boolean continueSearch = true;
        
        //loop through arraylist of swing data samples from indexBegin to indexEnd to search for criteria
        //good place for recursion
        while((indexBegin < indexEnd) && continueSearch){
            //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
            if((indexEnd - indexBegin) < (winLength - samples)){
                continueSearch = false;
            }
            //get the value at dataColumn and compare to threshold            
            else if((this.swingData.get(indexBegin))[dataColumn] > threshold){
                //store that index of arraylist
                dataIndex = indexBegin;               
                
                //need to search if next winLength of samples meet the criteria                
                indexBegin++;
                samples = 1; //first index is a sample  
                
                //loop through remaining indices as long as hasn't reached end or winLength
                while(indexBegin < indexEnd){
                    //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
                    if((indexEnd - indexBegin) < (winLength - samples)){
                        dataIndex = -1;
                        continueSearch = false;                        
                    }
                    
                    //if samples equals the number of winLength then we've met the criteria
                    //the current dataIndex is the first instance
                    else if(samples == winLength){
                        //get out of while loops
                        continueSearch = false;
                        break;  //from inner while loop
                    }
                    
                    //get the value at dataColumn and compare to threshold            
                    else if((this.swingData.get(indexBegin))[dataColumn] > threshold){
                        //update samples and index
                        samples++;
                        indexBegin++;
                    }
                    
                    //break from this inner while loop and restart arraylist search at updated indexBegin
                    //value is not greater than threshold
                    else{
                        //update dataIndex back to -1 for not found
                        dataIndex  = -1;
                        break;  //from inner while loop                       
                    }
                } //end inner while   
            }//end if
            else{
                //advance to next arraylist index
                indexBegin++;
            }   
            
        } //end while 
        
        //interpret final dataIndex value
        return getDataResult(dataIndex);
    }
    
     /**
    * This method searches for data values that are higher than a specified threshold and returns
    * the first index where data has values that meet this criteria for a number of samples
    * @param data the column to search
    * @param indexBegin where to start the search of the ArrayList of data swing sample points
    * @param indexEnd where to end the search of the ArrayList of data swing sample points
    * @param thresholdLo search data values below this value
    * * @param thresholdHi search data values above this value
    * @param winLength the number of samples that must be above this threshold
    * @return the index of data that meets the criteria
    */
    public String backSearchContinuityWithinRange(String data, int indexBegin, int indexEnd, double thresholdLo,double thresholdHi, int winLength){
        //initialize to -1 for searching results (-1 means did not find criteria)
        int dataIndex = -1; 
        
        //interpret the column data to analyze relative to the index of Double array of each sample
        int dataColumn = getDataColumnToAnalyze(data);
        
        //keep track of how many samples meet critera
        int samples = 0;
        
        boolean continueSearch = true;
        
        //loop through arraylist of swing data samples from indexBegin to indexEnd to search for criteria
        //good place for recursion
        while((indexBegin > indexEnd) && continueSearch){
             //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
            if((indexBegin - indexEnd) < (winLength - samples)){
                continueSearch = false;
            }
            
            //get the value at dataColumn and compare to threshold            
            else if(((this.swingData.get(indexBegin))[dataColumn] > thresholdLo) && ((this.swingData.get(indexBegin))[dataColumn] < thresholdHi)){
                //store that index of arraylist
                dataIndex = indexBegin;               
                
                //need to search if next winLength of samples meet the criteria                
                indexBegin--;
                samples = 1; //first index is a sample  
                
                //loop through remaining indices as long as hasn't reached end or winLength
                while(indexBegin > indexEnd){
                     //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
                    if((indexBegin - indexEnd) < (winLength - samples)){
                        dataIndex = -1;
                        continueSearch = false;                        
                    }
                    
                    //if samples equals the number of winLength then we've met the criteria
                    //the current dataIndex is the first instance
                    else if(samples == winLength){
                        //get out of while loops
                        continueSearch = false;
                        break;  //from inner while loop
                    }
                    
                    //get the value at dataColumn and compare to threshold   
                    else if(((this.swingData.get(indexBegin))[dataColumn] > thresholdLo) && ((this.swingData.get(indexBegin))[dataColumn] < thresholdHi))
                    {
                        //update samples and index
                        samples++;
                        indexBegin--;
                    }
                    
                    //break from this inner while loop and restart arraylist search at updated indexBegin
                    //value is not greater than threshold
                    else{
                        //update dataIndex back to -1 for not found
                        dataIndex  = -1;
                        break;  //from inner while loop                       
                    }
                } //end inner while   
            }//end if
            else{
                //advance to next arraylist index
                indexBegin--;
            }   
            
        } //end while 
        
        //interpret final dataIndex value
        return getDataResult(dataIndex);
    }
    
    /**
    * This method searches for data values that are higher than a specified threshold and returns
    * the first index where data has values that meet this criteria for a number of samples
    * @param data1 the first column to search
    * @param data2 the second column to search
    * @param indexBegin where to start the search of the ArrayList of data swing sample points
    * @param indexEnd where to end the search of the ArrayList of data swing sample points
    * @param threshold1 search data values above this value
    * @param threshold2 search data values above this value
    * @param winLength the number of samples that must be above this threshold
    * @return the index of data that meets the criteria
    */
    public String searchContinuityAboveValueTwoSignals(String data1, String data2, int indexBegin, int indexEnd, double threshold1, double threshold2, int winLength){
        //initialize to -1 for searching results (-1 means did not find criteria)
        int dataIndex = -1; 
        
        //interpret the column data to analyze relative to the index of Double array of each sample
        int dataColumn1 = getDataColumnToAnalyze(data1);
        int dataColumn2 = getDataColumnToAnalyze(data2);
        
        //keep track of how many samples meet critera
        int samples =0;
        
        boolean continueSearch = true;
        
        //loop through arraylist of swing data samples from indexBegin to indexEnd to search for criteria
        //good place for recursion
        while((indexBegin < indexEnd) && continueSearch){
            //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
            if((indexEnd - indexBegin) < (winLength - samples)){
                continueSearch = false;
            }
            //get the value at dataColumn and compare to threshold            
            else if((this.swingData.get(indexBegin))[dataColumn1] > threshold1 &&(this.swingData.get(indexBegin))[dataColumn2] > threshold2){
                //store that index of arraylist
                dataIndex = indexBegin;               
                
                //need to search if next winLength of samples meet the criteria                
                indexBegin++;
                samples = 1; //first index is a sample  
                
                //loop through remaining indices as long as hasn't reached end or winLength
                while(indexBegin < indexEnd){
                    //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
                    if((indexEnd - indexBegin) < (winLength - samples)){
                        dataIndex = -1;
                        continueSearch = false;                        
                    }
                    
                    //if samples equals the number of winLength then we've met the criteria
                    //the current dataIndex is the first instance
                    else if(samples == winLength){
                        //get out of while loops
                        continueSearch = false;
                        break;  //from inner while loop
                    }
                    
                    //get the value at dataColumn and compare to threshold            
                    else if((this.swingData.get(indexBegin))[dataColumn1] > threshold1 && (this.swingData.get(indexBegin))[dataColumn2] > threshold2 ){
                        //update samples and index
                        samples++;
                        indexBegin++;
                    }
                    
                    //break from this inner while loop and restart arraylist search at updated indexBegin
                    //value is not greater than threshold
                    else{
                        //update dataIndex back to -1 for not found
                        dataIndex  = -1;
                        break;  //from inner while loop                       
                    }
                } //end inner while   
            }//end if
            else{
                //advance to next arraylist index
                indexBegin++;
            }   
            
        } //end while 
        
        //interpret final dataIndex value
        return getDataResult(dataIndex);
    }
    
  /**
    * This method searches for data values that are higher than a specified threshold and returns
    * the first index where data has values that meet this criteria for a number of samples
    * @param data the column to search
    * @param indexBegin where to start the search of the ArrayList of data swing sample points
    * @param indexEnd where to end the search of the ArrayList of data swing sample points
    * @param thresholdLo search data values below this value
    * * @param thresholdHi search data values above this value
    * @param winLength the number of samples that must be above this threshold
    * @return the index of data that meets the criteria
    */
    public String searchMultiContinuityWithinRange(String data, int indexBegin, int indexEnd, double thresholdLo,double thresholdHi, int winLength){
        //initialize to -1 for searching results (-1 means did not find criteria)
        int dataIndexBegin = -1;    
        int dataIndexEnd = -1;  
        String dataResult;
        
        //interpret the column data to analyze relative to the index of Double array of each sample
        int dataColumn = getDataColumnToAnalyze(data);
        
        //keep track of how many samples meet critera
        int samples = 0;
        
        boolean continueSearch = true;
        
        //loop through arraylist of swing data samples from indexBegin to indexEnd to search for criteria
        //good place for recursion
        while((indexEnd > indexBegin) && continueSearch){
             //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
            if((indexEnd - indexBegin) < (winLength - samples)){
                continueSearch = false;
            }
            
            //get the value at dataColumn and compare to threshold            
            else if(((this.swingData.get(indexBegin))[dataColumn] > thresholdLo) && ((this.swingData.get(indexBegin))[dataColumn] < thresholdHi)){
                //store that index of arraylist
                dataIndexBegin = indexBegin;               
                
                //need to search if next winLength of samples meet the criteria                
                indexBegin++;
                samples = 1; //first index is a sample  
                
                //loop through remaining indices as long as hasn't reached end or winLength
                while(indexEnd > indexBegin){
                     //if number of indices to check will not cover number of samples for winLength then no example criteria can be found
                    if((indexEnd - indexBegin) < (winLength - samples)){
                        dataIndexBegin = -1;
                        continueSearch = false;                        
                    }
                    
                    //if samples equals the number of winLength then we've met the criteria
                    //the current dataIndex is the first instance
                    else if(samples == winLength){
                        //store the ending index (indexBegin is being moved towards end index)
                        //back up one b/c incremented before this check
                        dataIndexEnd = indexBegin - 1;
                        
                        //get out of while loops
                        continueSearch = false;
                        break;  //from inner while loop
                    }
                    
                    //get the value at dataColumn and compare to threshold   
                    else if(((this.swingData.get(indexBegin))[dataColumn] > thresholdLo) && ((this.swingData.get(indexBegin))[dataColumn] < thresholdHi))
                    {
                        //update samples and index
                        samples++;
                        indexBegin++;
                    }
                    
                    //break from this inner while loop and restart arraylist search at updated indexBegin
                    //value is not greater than threshold
                    else{
                        //update dataIndex back to -1 for not found
                        dataIndexBegin  = -1;
                        break;  //from inner while loop                       
                    }
                } //end inner while   
            }//end if
            else{
                //advance to next arraylist index
                indexBegin++;
            }   
            
        } //end while 
        
        //interpret final dataIndex value       
        dataResult = "index: (" + getDataResult(dataIndexBegin) + ","  + getDataResult(dataIndexEnd) + ")";
        return dataResult;
    }    
    
    
  //a helper method to see if an index was found or not
    private String getDataResult(int dataIndex){
        String dataResult;
        
        if(dataIndex == -1){
            dataResult = "could not find index to meet criteria";
        }
        else{
            dataResult = Integer.toString(dataIndex);
        }
        
        return dataResult;
    }
    
  //a helper method to determine what exact column of each sample to analyze
  //format is: timestamp, ax, ay, az, wx, wy, wz
  private int getDataColumnToAnalyze(String data){
    int dataColumn;
    
    //switch case to determine which corresponding index to return
    switch(data){
            case "timestamp":
                dataColumn = 0;
                break;
            case "ax":
                dataColumn = 1;
                break;
            case "ay":
                dataColumn = 2;
                break;
            case "az":
                dataColumn = 3;
                break;
            case "wx":
                dataColumn = 4;
                break;
            case "wy":
                dataColumn = 5;
                break;
            case "wz":
                dataColumn = 6;
                break;
            default:
                System.out.println("invalid index");
                
                //error checking
                dataColumn = -1;
    }
   return dataColumn;  
  }
}
