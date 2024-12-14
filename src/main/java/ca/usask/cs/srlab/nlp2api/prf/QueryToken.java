/*****
 * 
 * @author MasudRahman
 *
 * This class represents each query token and its scores
 */

package ca.usask.cs.srlab.nlp2api.prf;

public class QueryToken {
	public String token;
	public double tokenRankScore=0;
	public double posRankScore=0;
	public double totalScore=0;
	public double bordaScore=0;
}
