package lotr.curuquesta.structure;

import lotr.curuquesta.SpeechbankContext;
import lotr.curuquesta.SpeechbankContextProvider;

public interface SpeechbankContextSatisfier<C extends SpeechbankContextProvider> {
	boolean satisfiesContext(SpeechbankContext<C> var1);
}
