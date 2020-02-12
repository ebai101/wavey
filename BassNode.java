package ebai.javamusic.wavey;

public class BassNode {

	private int pitch;
	private double filterFreq;
	
	public BassNode (int pitch, double filterFreq)
	{
		this.pitch = pitch;
		this.filterFreq = filterFreq;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public double getFilterFreq() {
		return filterFreq;
	}

	public void setFilterFreq(double filterFreq) {
		this.filterFreq = filterFreq;
	}
}
