package ebai.javamusic.wavey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class WaveDataFetcher {

	private String buoyUrl;
	private ArrayList<WaveDataPoint> waveDataList;

	public WaveDataFetcher(String url) {
		this.buoyUrl = url;
		this.waveDataList = new ArrayList<WaveDataPoint>();
	}

	public void fetch() throws IOException {
		// attempts to get the latest data from the supplied URL
		// populates the waveDataList with new data points
		
		System.out.print("Fetching wave data...");
		URL url = new URL(this.buoyUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		String line;

		while ((line = rd.readLine()) != null) {
			if (line.charAt(0) == '#')
				continue;
			line = line.replaceAll("\\s+", " ");
			waveDataList.add(new WaveDataPoint(line.split(" ")));
			// return; // DEBUG
		}
		
		// prevent missing data
		int lastWindDir = 0;
		String lastSwellDir = "N";
		String lastWindWaveDir = "N";
		for (WaveDataPoint wdp : waveDataList)
		{
			if (wdp.mainWindDirection == -99)
			{
				wdp.mainWindDirection = lastWindDir;
			}
			else
			{
				lastWindDir = wdp.mainWindDirection;
			}
			
			if (wdp.swellDirection.equals("MM"))
			{
				wdp.swellDirection = lastSwellDir;
			}
			else
			{
				lastSwellDir = wdp.swellDirection;
			}
			
			if (wdp.windWaveDirection.equals("MM"))
			{
				wdp.windWaveDirection = lastWindWaveDir;
			}
			else
			{
				lastWindWaveDir = wdp.windWaveDirection;
			}
		}
		
		System.out.println("success.");
		rd.close();
	}

	public ArrayList<WaveDataPoint> getWaveData() {
		return this.waveDataList;
	}
}

class WaveDataPoint {

	public final String timeStamp;
	public final double waveHeight;
	public final double swellHeight;
	public final double swellPeriod;
	public final double windWaveHeight;
	public final double windWavePeriod;
	public String swellDirection;
	public String windWaveDirection;
	public final String steepness;
	public final double averageWavePeriod;
	public int mainWindDirection;

	public WaveDataPoint(String[] data) {
		// assembles a new data point given a string from the raw data
		
		timeStamp = String.format("%s/%s/%s %s:%s", data[0], data[1], data[2], data[3], data[4]);
		waveHeight = Double.parseDouble(data[5]);
		swellHeight = Double.parseDouble(data[6]);
		swellPeriod = Double.parseDouble(data[7]);
		windWaveHeight = Double.parseDouble(data[8]);
		windWavePeriod = Double.parseDouble(data[9]);
		swellDirection = data[10];
		windWaveDirection = data[11];
		steepness = data[12];
		averageWavePeriod = Double.parseDouble(data[13]);
		mainWindDirection = Integer.parseInt(data[14]);
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Timestamp: " + timeStamp + "\n");
		sb.append("Wave height: " + waveHeight + " m\n");
		sb.append("Swell height: " + swellHeight + " m\n");
		sb.append("Swell period: " + swellPeriod + " sec\n");
		sb.append("Wind wave height: " + windWaveHeight + " m\n");
		sb.append("Wind wave period: " + windWavePeriod + " sec\n");
		sb.append("Swell direction: " + swellDirection + "\n");
		sb.append("Wind wave direction: " + windWaveDirection + "\n");
		sb.append("Steepness: " + steepness + "\n");
		sb.append("Average wave period: " + averageWavePeriod + " sec\n");
		sb.append("Main wind direction: " + mainWindDirection + "\n");
		return sb.toString();
	}
}
