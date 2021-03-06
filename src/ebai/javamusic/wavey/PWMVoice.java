package ebai.javamusic.wavey;

/**************
** WARNING - this code automatically generated by Syntona.
** The real source is probably a Syntona patch.
** Do NOT edit this file unless you copy it to another directory and change the name.
** Otherwise it is likely to get clobbered the next time you
** export Java source code from Syntona.
**
** Syntona is available from: http://www.softsynth.com/syntona/
*/

import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.RangeConverter;
import com.jsyn.unitgen.PulseOscillatorBL;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.SineOscillator;
import com.softsynth.shared.time.TimeStamp;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.FilterLowPass;
import com.jsyn.unitgen.EnvelopeDAHDSR;
import com.jsyn.unitgen.Circuit;

public class PWMVoice extends Circuit implements PlayableUnitVoice {
    // Declare units and ports.
    PassThrough mFrequencyPassThrough;
    public UnitInputPort frequency;
    public UnitInputPort amplitude;
    public UnitInputPort filterFreq;
    public UnitInputPort pwmFreq;
    public UnitOutputPort output;
    PassThrough mAmplitudePassThrough;
    PassThrough mOutputPassThrough;
    PulseOscillatorBL mPulseOscBL;
    SineOscillator mSineOsc;
    FilterLowPass mLowPass;
    EnvelopeDAHDSR mDAHDSR;
    Multiply mAtimes;
    PassThrough mFilterFreqPassThrough;
    PassThrough mPwmFreqPassThrough;
    SineOscillator mSineOsc2;
    RangeConverter mARanger;

    // Declare inner classes for any child circuits.

    public PWMVoice() {
        // Create unit generators.
        add(mFrequencyPassThrough = new PassThrough());
        addPort(frequency = mFrequencyPassThrough.input, "frequency");
        add(mAmplitudePassThrough = new PassThrough());
        addPort(amplitude = mAmplitudePassThrough.input, "amplitude");
        add(mOutputPassThrough = new PassThrough());
        addPort( output = mOutputPassThrough.output, "output");
        add(mPulseOscBL = new PulseOscillatorBL());
        add(mSineOsc = new SineOscillator());
        add(mLowPass = new FilterLowPass());
        add(mDAHDSR = new EnvelopeDAHDSR());
        add(mAtimes = new Multiply());
        add(mFilterFreqPassThrough = new PassThrough());
        addPort(filterFreq = mFilterFreqPassThrough.input, "filterFreq");
        add(mPwmFreqPassThrough = new PassThrough());
        addPort(pwmFreq = mPwmFreqPassThrough.input, "pwmFreq");
        add(mSineOsc2 = new SineOscillator());
        add(mARanger = new RangeConverter());
        // Connect units and ports.
        mFrequencyPassThrough.output.connect(mPulseOscBL.frequency);
        mAmplitudePassThrough.output.connect(mDAHDSR.amplitude);
        mPulseOscBL.output.connect(mAtimes.inputA);
        mSineOsc.output.connect(mPulseOscBL.width);
        mLowPass.output.connect(mOutputPassThrough.input);
        mDAHDSR.output.connect(mAtimes.inputB);
        mAtimes.output.connect(mLowPass.input);
        mFilterFreqPassThrough.output.connect(mSineOsc2.frequency);
        mPwmFreqPassThrough.output.connect(mSineOsc.frequency);
        mSineOsc2.output.connect(mARanger.input);
        mARanger.output.connect(mLowPass.frequency);
        // Setup
        frequency.setup(40.0, 261.6255653005986, 8000.0);
        amplitude.setup(0.0, 0.5, 1.0);
        mPulseOscBL.amplitude.set(1.0);
        mSineOsc.amplitude.set(0.5);
        mLowPass.amplitude.set(1.0);
        mLowPass.Q.set(2.506467677844837);
        mDAHDSR.input.set(1.0);
        mDAHDSR.delay.set(0.0);
        mDAHDSR.attack.set(0.0);
        mDAHDSR.hold.set(0.0);
        mDAHDSR.decay.set(0.0);
        mDAHDSR.sustain.set(1.0);
        mDAHDSR.release.set(2.0);
        filterFreq.setup(0.1, 0.1, 8000.0);
        pwmFreq.setup(0.2, 0.2, 2.0);
        mSineOsc2.amplitude.set(1.0);
        mARanger.min.set(40.0);
        mARanger.max.set(8000.0);
        this.amplitude.set(0.0);
    }

    public void noteOn(double frequency, double amplitude, TimeStamp timeStamp) {
        this.frequency.set(frequency, timeStamp);
        this.amplitude.set(amplitude, timeStamp);
        mDAHDSR.input.on(timeStamp);
    }

    public void noteOff(TimeStamp timeStamp) {
        mDAHDSR.input.off(timeStamp);
    }
    
    public UnitOutputPort getOutput() {
        return output;
    }

	public UnitInputPort getFreq() {
		return frequency;
	}

	public UnitInputPort getAmp() {
		return amplitude;
	}
	
	public UnitInputPort getModFreq() {
		return filterFreq;
	}
}
