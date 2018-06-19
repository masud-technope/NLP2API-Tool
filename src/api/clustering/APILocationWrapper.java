package api.clustering;

import java.util.ArrayList;
import org.apache.commons.math3.ml.clustering.Clusterable;
import edu.stanford.nlp.util.ArrayUtils;

public class APILocationWrapper implements Clusterable {

	private double[] points;
	private String APIName;

	public APILocationWrapper(String key, ArrayList<Double> points) {
		// TODO Auto-generated constructor stub
		this.APIName = key;
		this.points = ArrayUtils.asPrimitiveDoubleArray(points);
	}

	public String getAPIName() {
		return this.APIName;
	}

	@Override
	public double[] getPoint() {
		// TODO Auto-generated method stub
		return points;
	}
}
