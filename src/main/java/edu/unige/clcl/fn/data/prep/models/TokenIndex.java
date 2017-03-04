package edu.unige.clcl.fn.data.prep.models;

/**
 * @author Alex Kabbach
 */
public class TokenIndex implements Comparable{
	private int start;
	private int end;

	public TokenIndex(int start, int end) {
		this.start = start;
		this.end = end;
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

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		TokenIndex that = (TokenIndex) o;

		if (start != that.start)
			return false;
		return end == that.end;
	}

	@Override public int hashCode() {
		int result = start;
		result = 31 * result + end;
		return result;
	}

	@Override public int compareTo(Object obj) {
		if(this.equals(obj)){
			return 0;
		}
		return this.getStart() > ((TokenIndex)obj).getStart() ? 1 : -1;
	}
}
