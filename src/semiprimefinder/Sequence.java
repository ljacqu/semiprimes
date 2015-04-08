package semiprimefinder;

import java.util.List;

public class Sequence {

	private int sign; // 1 or -1
	private List<Integer> factors;
	
	public Sequence(int sign, List<Integer> factors) {
		this.sign = sign;
		this.factors = factors;
	}
	
	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Sequence))
			return false;
		else if (other == this)
			return true;
		
		Sequence otherSequence = (Sequence) other;
		return this.sign == otherSequence.sign
				&& this.factors.equals(otherSequence.factors);
	}
	
	public int getSign() {
		return sign;
	}
	
	public void setSign(int sign) {
		this.sign = sign;
	}
	
	public List<Integer> getFactors() {
		return factors;
	}
	
	public void setFactors(List<Integer> factors) {
		this.factors = factors;
	}

}
