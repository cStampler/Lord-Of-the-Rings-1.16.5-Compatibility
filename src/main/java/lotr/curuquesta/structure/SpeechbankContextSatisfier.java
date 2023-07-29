package lotr.curuquesta.structure;

import lotr.curuquesta.*;

public interface SpeechbankContextSatisfier<C extends SpeechbankContextProvider> {
	boolean satisfiesContext(SpeechbankContext<C> var1);
}
