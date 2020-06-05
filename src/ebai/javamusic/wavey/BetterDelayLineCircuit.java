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

import com.jsyn.unitgen.SineOscillator;
import com.jsyn.unitgen.InterpolatingDelay;
import com.jsyn.unitgen.PassThrough;
import com.jsyn.unitgen.Circuit;
import com.jsyn.unitgen.Subtract;
import com.jsyn.unitgen.UnitSink;
import com.jsyn.ports.UnitOutputPort;
import com.jsyn.unitgen.MultiPassThrough;
import com.jsyn.unitgen.Add;
import com.jsyn.ports.UnitInputPort;
import com.jsyn.unitgen.Multiply;
import com.jsyn.unitgen.FilterBandPass;

public class BetterDelayLineCircuit extends Circuit implements UnitSink {
    // Declare units and ports.
    PassThrough mInputPassThrough;
    public UnitInputPort input;
    InterpolatingDelay mDelayL;
    FilterBandPass mBandPassL;
    Multiply mFeedbackMultL;
    PassThrough mDelayTimePassThrough;
    public UnitInputPort delayTime;
    PassThrough mFeedbackPassThrough;
    public UnitInputPort feedback;
    InterpolatingDelay mDelayR;
    Multiply mFeedbackMultR;
    FilterBandPass mBandPassR;
    SineOscillator mModOsc;
    MultiPassThrough mAOutput2PassThrough;
    public UnitOutputPort aOutput2;
    Add mAplus;
    Subtract mAminus;
    PassThrough mOutputPassThrough;

    // Declare inner classes for any child circuits.

    public BetterDelayLineCircuit() {
        // Create unit generators.
        add(mInputPassThrough = new PassThrough());
        addPort(input = mInputPassThrough.input, "input");
        add(mDelayL = new InterpolatingDelay());
        add(mFeedbackMultL = new Multiply());
        add(mBandPassL = new FilterBandPass());
        add(mDelayTimePassThrough = new PassThrough());
        addPort(delayTime = mDelayTimePassThrough.input, "delayTime");
        add(mFeedbackPassThrough = new PassThrough());
        addPort(feedback = mFeedbackPassThrough.input, "feedback");
        add(mDelayR = new InterpolatingDelay());
        add(mFeedbackMultR = new Multiply());
        add(mBandPassR = new FilterBandPass());
        add(mModOsc = new SineOscillator());
        add(mAOutput2PassThrough = new MultiPassThrough(2));
        addPort( aOutput2 = mAOutput2PassThrough.output, "output2");
        add(mAplus = new Add());
        add(mAminus = new Subtract());
        add(mOutputPassThrough = new PassThrough());
        // Connect units and ports.
        mInputPassThrough.output.connect(mDelayL.input);
        mInputPassThrough.output.connect(mDelayR.input);
        mDelayL.output.connect(mBandPassL.input);
        mBandPassL.output.connect(mFeedbackMultL.inputA);
        mBandPassL.output.connect(mAOutput2PassThrough.input);
        mBandPassL.output.connect(mOutputPassThrough.input);
        mFeedbackMultL.output.connect(mDelayL.input);
        mDelayTimePassThrough.output.connect(mAplus.inputA);
        mDelayTimePassThrough.output.connect(mAminus.inputA);
        mFeedbackPassThrough.output.connect(mFeedbackMultL.inputB);
        mFeedbackPassThrough.output.connect(mFeedbackMultR.inputB);
        mDelayR.output.connect(mBandPassR.input);
        mFeedbackMultR.output.connect(mDelayR.input);
        mBandPassR.output.connect(0, mAOutput2PassThrough.input, 1);
        mBandPassR.output.connect(mFeedbackMultR.inputA);
        mModOsc.output.connect(mAplus.inputB);
        mModOsc.output.connect(mAminus.inputB);
        mAplus.output.connect(mDelayL.delay);
        mAminus.output.connect(mDelayR.delay);
        // Setup
        input.setup(0.0, 0.6473085296272819, 1.0);
        mDelayL.allocate(44100);
        mBandPassL.frequency.set(1000.0);
        mBandPassL.amplitude.set(0.5);
        mBandPassL.Q.set(0.5);
        delayTime.setup(0.0, 0.39458798856513927, 1.0);
        feedback.setup(0.0, 0.9, 0.9);
        mDelayR.allocate(44100);
        mBandPassR.frequency.set(1000.0);
        mBandPassR.amplitude.set(0.5);
        mBandPassR.Q.set(0.5);
        mModOsc.frequency.set(1.0);
        mModOsc.amplitude.set(0.001651330133303623);
    }
    
    public UnitOutputPort getOutput() {
        return aOutput2;
    }

	public UnitInputPort getInput() {
		return input;
	}
}
