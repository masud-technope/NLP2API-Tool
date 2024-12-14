
/*****
 * 
 * @author MasudRahman
 * Utility class for random sampling from a list.
 * 
 */

package ca.usask.cs.srlab.nlp2api.utility;

import java.util.ArrayList;
import java.util.Collections;

public class RandomSampler {
	public static void main(String[] args){
		ArrayList<Integer> list=new ArrayList<>();
		for(int i=1;i<=175;i++){
			list.add(i);
		}
		Collections.shuffle(list);
		for(int i=0;i<25;i++){
			System.out.println(list.get(i));
		}
	}
}
