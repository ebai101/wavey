package ebai.javamusic.wavey;

public class ModNode {
	
	private double reverb;
	private double modPeriod;
	private int randomSeed;
	
	public ModNode (double reverb, double modPeriod, int randomSeed)
	{
		this.reverb = reverb;
		this.modPeriod = modPeriod;
		this.randomSeed = randomSeed;
	}
	
	public double getReverb() {
		return reverb;
	}

	public void setReverb(double reverb) {
		this.reverb = reverb;
	}

	public double getModPeriod() {
		return modPeriod;
	}

	public void setModPeriod(double modPeriod) {
		this.modPeriod = modPeriod;
	}

	public int getRandomSeed() {
		return randomSeed;
	}

	public void setRandomSeed(int randomSeed) {
		this.randomSeed = randomSeed;
	}

	public String toString()
	{
		return String.format("\n%f, %f, %d", reverb, modPeriod, randomSeed);
	}
}