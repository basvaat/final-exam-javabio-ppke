
public class Palindrome {	
	private String sequence;
	
	private int start;
	private int end;
	
	public Palindrome(String sequence, int start, int end) {
		this.sequence = sequence;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public String toString() {
		return "sequence: " + sequence + "\nstart: " + start + "\tend: " + end + "\tlength: " + (end-start+1);
	}

	public String getSequence() {
		return sequence;
	}
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public int getStart() {
		return start;
	}
	public void setStart(int start) {
		this.start = start;
	}
	public int getEnd() {
		return end;
	}
	public void setEnd(int end) {
		this.end = end;
	}
	
}
