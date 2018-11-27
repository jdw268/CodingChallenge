/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swingdatacodingchallenge;

import java.io.File;

/**
 *
 * @author Jill
 */
public class SwingDataCodingChallenge {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //test the object's methods 
        File testFile = new File("D:\\SwingDataCodingChallenge\\latestSwing.csv");
        Swing myCompleteSwing = new Swing(testFile);
        
        
        System.out.println(myCompleteSwing.searchContinuityAboveValue("ax", 0, 86, 1, 10));       

        System.out.println(myCompleteSwing.backSearchContinuityWithinRange("ax", 10, 0, -1.5, -0.5 , 10));

        System.out.println(myCompleteSwing.searchContinuityAboveValueTwoSignals("ax", "ay", 0, 50, 0, .5 , 10));
        
        System.out.println(myCompleteSwing.searchMultiContinuityWithinRange("ax", 0, 86, -.5, 1.5, 10));   
    }
    
}
