/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swingdatacodingchallenge;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
        //try catch for no file
        Swing myCompleteSwing = new Swing("D:\\SwingDataCodingChallenge\\latestSwing.csv");
        
        
       System.out.println(myCompleteSwing.searchContinuityAboveValue(1, 0, 7,-.89 , 2));      
       System.out.println(myCompleteSwing.backSearchContinuityWithinRange(1, 10, 0, -.87, -.5 , 2));   
       System.out.println(myCompleteSwing.searchContinuityAboveValueTwoSignals(1, 2, 0, 50, 0, .5 , 10));

/*
myCompleteSwing.searchMultiContinuityWithinRange(1, 0, 15, -0.89, 0, 2);
for (int i = 0 ; i< myCompleteSwing.resultIndices.size(); i++){  
    Integer[] temp = myCompleteSwing.resultIndices.get(i);
    
    System.out.println(temp[0]);
    System.out.println(temp[1]);
        
       // System.out.println(myCompleteSwing.searchMultiContinuityWithinRange("ax", 0, 86, -.5, 1.5, 10));   
    }
    }
*/
}
}
