package ebai.javamusic.wavey;

import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.UnitVoice;

public interface PlayableUnitVoice extends UnitVoice {
	
	public UnitInputPort getFreq();
	
	public UnitInputPort getAmp();

	public UnitInputPort getModFreq();
}
