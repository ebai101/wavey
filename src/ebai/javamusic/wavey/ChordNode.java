package ebai.javamusic.wavey;

public class ChordNode {
	
	private double amplitude;
	private double arpPeriod;
	private int pitch;
	private int quality;
	
	public ChordNode (double amplitude, double arpPeriod, int pitch, int quality)
	{
		this.amplitude = amplitude;
		this.arpPeriod = arpPeriod;
		this.pitch = pitch;
		this.quality = quality;
	}
	
	public double getAmplitude() {
		return amplitude;
	}

	public void setAmplitude(double amplitude) {
		this.amplitude = amplitude;
	}

	public double getArpPeriod() {
		return arpPeriod;
	}

	public void setArpPeriod(double arpPeriod) {
		this.arpPeriod = arpPeriod;
	}

	public int getPitch() {
		return pitch;
	}

	public void setPitch(int pitch) {
		this.pitch = pitch;
	}

	public int getQuality() {
		return quality;
	}

	public void setQuality(int quality) {
		this.quality = quality;
	}

	public String toString()
	{
		//return String.format("%.2f", amplitude);
		return String.format("[%f, %f, %d, %d]", amplitude, arpPeriod, pitch, quality);
	}
}