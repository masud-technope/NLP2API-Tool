/*****
 * 
 * @author MasudRahman
 *
 * This class represents each query token and its scores
 */

package code.search.bda;

public class QueryToken {
	public String token;
	public double tokenRankScore=0;
	public double posRankScore=0;
	public double totalScore=0;
	public double bordaScore=0;
}
