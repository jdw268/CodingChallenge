/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swingdatacodingchallenge;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is used to create and store a swing object and the associated data
 * with said swing. Each swing object stores several samples of one swing with
 * data for the timestamp, a three-axis accelerometer, and a three-axis
 * gyroscope. The sensors collecting this data create a csv file of the
 * information recorded based on the timestamp.
 */
public class Swing {

    //instance variable to collectively store each sample of the swing
    //format of each double array is: timestamp, ax, ay, az, wx, wy, wz
    private List<List<String>> swingData = new ArrayList<>();

    //public ArrayList<Integer[]> resultIndices = new ArrayList<>();
    /**
     * Constructor Reads the csv dataFile and populates the swingData ArrayList
     * of all the samples from one swing
     *
     * @param dataFilePath The csv file that contains the swing data sample
     * points
     */
    public Swing(String dataFilePath) {
        //for each line in the csv file, create an array of that data
        //and add that array to the swingData arraylist     
        //reference: https://stackoverflow.com/questions/49660669/parsing-csv-file-using-java-8-stream
        String fileName = dataFilePath;
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            swingData = lines.map(line -> Arrays.asList(line.split(","))).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }  //end constructor  

    /**
     * This method searches for data values that are higher than a specified
     * threshold and returns the first index where data has values that meet
     * this criteria for a number of samples
     *
     * @param data the column to search
     * @param indexBegin where to start the search of the ArrayList of data
     * swing sample points
     * @param indexEnd where to end the search of the ArrayList of data swing
     * sample points
     * @param threshold search data values above this value
     * @param winLength the number of samples that must be above this threshold
     * @return the index of data that meets the criteria
     */
    public int searchContinuityAboveValue(int data, int indexBegin, int indexEnd, double threshold, int winLength) {
        int resultIndex;
        //call search helper method - returns stop index of match or -1 of now match
        //highest double value for thresholdHi to keep it a non-factor in helper method
        int searchResult = searchData(data, indexBegin, indexEnd, threshold, Double.MAX_VALUE, winLength, 1);
        
        //if no match found, set method's return to -1 for no match
        if (searchResult == -1) {
            resultIndex = -1;
        } else {
            //a match was found - return the starting index
            resultIndex = searchResult - winLength;
        }
        return resultIndex;
    }

    /**
     * This method searches for data values that are higher than a specified
     * threshold and returns the first index where data has values that meet
     * this criteria for a number of samples
     *
     * @param data1 the first column to search
     * @param data2 the second column to search
     * @param indexBegin where to start the search of the ArrayList of data
     * swing sample points
     * @param indexEnd where to end the search of the ArrayList of data swing
     * sample points
     * @param threshold1 search data values above this value
     * @param threshold2 search data values above this value
     * @param winLength the number of samples that must be above this threshold
     * @return the index of data that meets the criteria
     */
    public int searchContinuityAboveValueTwoSignals(int data1, int data2, int indexBegin, int indexEnd, double threshold1, double threshold2, int winLength) {

        int resultIndex = -1;
        boolean continueSearch = true;

        while (continueSearch) {
            //first search for index of data 1
            int searchResultData1 = searchData(data1, indexBegin, indexEnd, threshold1, Double.MAX_VALUE, winLength, 1);
 
            //then search for index of data 2
            int searchResultData2 = searchData(data2, indexBegin, indexEnd, threshold2, Double.MAX_VALUE, winLength, 1);
          
            //see if they match - if yes return, if not continue search
            if (searchResultData1 == -1 || searchResultData2 == -1) {
                //end search
                continueSearch = false;
            } else if (searchResultData1 == searchResultData2) {
                
                //match found end search
                resultIndex = searchResultData1 - winLength;
                continueSearch = false;
            } //no match, keep looking
            else //start at higher search index
            if (searchResultData1 > searchResultData2) //searchResult index is the last index of the search so back it up
            {
                indexBegin = searchResultData1 - winLength;
            } else {
                indexBegin = searchResultData2 - winLength;
            }
        }
        return resultIndex;
    }

    /**
     * This method searches for data values that are higher than a specified
     * threshold and returns the first index where data has values that meet
     * this criteria for a number of samples
     *
     * @param data the column to search
     * @param indexBegin where to start the search of the ArrayList of data
     * swing sample points
     * @param indexEnd where to end the search of the ArrayList of data swing
     * sample points
     * @param thresholdLo search data values below this value
     * * @param thresholdHi search data values above this value
     * @param winLength the number of samples that must be above this threshold
     * @return the index of data that meets the criteria
     */
    public ArrayList<Integer[]> searchMultiContinuityWithinRange(int data, int indexBegin, int indexEnd, double thresholdLo, double thresholdHi, int winLength) {
        int resultStartIndex = 0;
        int resultEndIndex = 0;
        ArrayList<Integer[]> resultIndices = new ArrayList<>();

        //run search until -1 is the return
        while ((resultEndIndex != -1) && (indexBegin < indexEnd)) {
            //call search helper method - returns stop index of match or -1 of now match
            //call search method until all indices checked
            int searchResultStopIndex = searchData(data, indexBegin, indexEnd, thresholdLo, thresholdHi, winLength, 1);

            //if no match found, set method's return to -1 for no match
            if (searchResultStopIndex == -1) {
                resultEndIndex = -1;
                resultStartIndex = -1;

                Integer[] resultIndex = {resultStartIndex, resultEndIndex};
                //add to the list
                resultIndices.add(resultIndex);

            } else {
                //a match was found - set starting and ending indices
                resultEndIndex = searchResultStopIndex - 1;
                resultStartIndex = searchResultStopIndex - winLength;

                Integer[] resultIndex = {resultStartIndex, resultEndIndex};
                //add to the list
                resultIndices.add(resultIndex);

                //update indexBegin with after end of last result index
                indexBegin = searchResultStopIndex;
            }
        }
        return resultIndices;
    }

    /**
     * This method searches for data values that are higher than a specified
     * threshold and returns the first index where data has values that meet
     * this criteria for a number of samples
     *
     * @param data the column to search
     * @param indexBegin where to start the search of the ArrayList of data
     * swing sample points
     * @param indexEnd where to end the search of the ArrayList of data swing
     * sample points
     * @param thresholdLo search data values below this value
     * * @param thresholdHi search data values above this value
     * @param winLength the number of samples that must be above this threshold
     * @return the index of data that meets the criteria
     */
    public int backSearchContinuityWithinRange(int data, int indexBegin, int indexEnd, double thresholdLo, double thresholdHi, int winLength) {
        int resultIndex;
        //call search helper method - returns stop index of match or -1 of now match
        //swap indexBegin and indexEnd for helper method b/c going from bottom up
        int searchResult = searchData(data, indexBegin, indexEnd, thresholdLo, thresholdHi, winLength, 0);

        //if no match found, set method's return to -1 for no match
        if (searchResult == -1) {
            resultIndex = -1;
        } else {
            //a match was found - return the starting index
            resultIndex = searchResult + winLength;
        }
        return resultIndex;
    }

    /**
     * This is a helper search method called by the public search methods of the
     * Swing class. Inputs help determine which index to return given criteria    *
     *
     * @param data
     * @param indexBegin
     * @param indexEnd
     * @param thresholdLo
     * @param thresholdHi
     * @param direction - search data column from top to bottom or bottom to top
     * @param winLength
     * @return the index of data that meets the criteria
     */
    //search helper method
    private int searchData(int data, int indexBegin, int indexEnd, double thresholdLo, double thresholdHi, int winLength, int direction) {
        //initialize to -1 for searching results (-1 means did not find criteria)  
        int dataIndexStop = -1;
        int samples = 0;

        //check if going from bottom up of data or top down
        if (direction == 1) { //bottom up

            //loop while they're are enough data points left to check - that are greater than the winLength threshold to find
            while ((indexEnd - (indexBegin - 1)) >= (winLength - samples)) {

                //other stop condition (match found) is if samples == winLength             
                if (samples == winLength) {
                    //set the index end point and exit the while loop
                    dataIndexStop = indexBegin;
                    //exit the while loop
                    break;
                }

                //see if current index value is greater than the thresholdLo and lower than the thresholdHi
                if ((Double.parseDouble(this.swingData.get(indexBegin).get(data)) > thresholdLo)
                        && (Double.parseDouble(this.swingData.get(indexBegin).get(data)) < thresholdHi)) {
                    //increment samples
                    samples++;

                } //reset samples to 0 to restart search for winLength samples
                else {
                    samples = 0;
                }

                //increment the loop
                indexBegin++;
            } //end while
        } //end bottom up direction
        else { //top down

            //loop while they're are enough data points left to check - that are greater than the winLength threshold to find
            while ((indexBegin + 1) - (indexEnd) >= (winLength - samples)) {

                //other stop condition (match found) is if samples == winLength             
                if (samples == winLength) {
                    //set the index end point and exit the while loop
                    dataIndexStop = indexBegin;
                    //exit the while loop
                    break;
                }

                //see if current index value is greater than the thresholdLo and lower than the thresholdHi
                if ((Double.parseDouble(this.swingData.get(indexBegin).get(data)) > thresholdLo)
                        && (Double.parseDouble(this.swingData.get(indexBegin).get(data)) < thresholdHi)) {
                    //increment samples
                    samples++;
                } //reset samples to 0 to restart search for winLength samples
                else {
                    samples = 0;
                }

                //increment the loop
                indexBegin--;
            } //end while
        } //end down direction

        return dataIndexStop;
    } //end search helper

}
