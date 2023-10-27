package lotr.curuquesta.structure;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lotr.curuquesta.SpeechbankContext;
import lotr.curuquesta.SpeechbankContextProvider;

public class SpeechbankEntry<C extends SpeechbankContextProvider> {
	private final Set<SpeechbankContextSatisfier<C>> contextSatisfiers;
	private final List<String> lines;

	public SpeechbankEntry(Set<SpeechbankContextSatisfier<C>> contextSatisfiers, List<String> lines) {
		this.contextSatisfiers = contextSatisfiers;
		this.lines = lines;
	}

	public boolean doesContextSatisfyConditions(SpeechbankContext<C> context) {
		try {
			return this.contextSatisfiers.stream().allMatch(p -> p.satisfiesContext(context));
		} catch (Exception e) {
			throw new IllegalStateException(String.format("Error while processing speech entry conditions for entry %s", this.toString()), e);
		}
	}

	public Stream<String> streamLines() {
		return this.lines.stream();
	}

	@Override
	public String toString() {
		return String.format("SpeechbankEntry: [contextSatisfiers = %s, lines = %s]", this.contextSatisfiers.stream().map(Object::toString).collect(Collectors.joining(", ")), this.lines.stream().collect(Collectors.joining(", ")));
	}
}
