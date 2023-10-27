/*
 * Decompiled with CFR 0.148.
 *
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.util.ResourceLocation
 */
package lotr.client.speech;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.curuquesta.SpeechbankContextProvider;
import lotr.curuquesta.SpeechbankEngine;
import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.condition.predicate.SpeechbankConditionAndPredicate;
import lotr.curuquesta.structure.AlternativeConditionSets;
import lotr.curuquesta.structure.Speechbank;
import lotr.curuquesta.structure.SpeechbankConditionSet;
import lotr.curuquesta.structure.SpeechbankEntry;
import net.minecraft.util.ResourceLocation;

public class SpeechbankLoader<C extends SpeechbankContextProvider> {
	private final SpeechbankEngine<C> speechbankEngine;

	public SpeechbankLoader(SpeechbankEngine<C> speechbankEngine) {
		this.speechbankEngine = speechbankEngine;
	}

	public Speechbank<C> load(ResourceLocation speechbankName, JsonObject json, SpeechbankResourceManager.ParentSpeechbankLoader<C> parentLoader) {
		ArrayList<SpeechbankEntry<C>> entries = new ArrayList<>();
		JsonArray entriesArray = json.get("speech").getAsJsonArray();
		for (JsonElement entryElem : entriesArray) {
			JsonObject entryObj = entryElem.getAsJsonObject();
			SpeechbankEntry<C> entry = this.loadEntry(speechbankName, entryObj);
			entries.add(entry);
		}
		this.warnOfDuplicateLines(speechbankName, entries);
		this.loadAndInheritParentLines(speechbankName, json, entries, parentLoader);
		return new Speechbank<>(speechbankName.toString(), entries);
	}

	private AlternativeConditionSets<C> loadAlternatives(ResourceLocation speechbankName, JsonArray alternativesArray) {
		ArrayList alternatives = new ArrayList();
		for (JsonElement elem : alternativesArray) {
			JsonObject conditionsObj = elem.getAsJsonObject();
			HashSet conditionsAndPredicates = new HashSet();
			for (Map.Entry e : conditionsObj.entrySet()) {
				SpeechbankConditionAndPredicate<?, C> conditionAndPredicate = this.loadConditionAndPredicate(speechbankName, (String) e.getKey(), (JsonElement) e.getValue());
				if (conditionAndPredicate == null) {
					continue;
				}
				conditionsAndPredicates.add(conditionAndPredicate);
			}
			SpeechbankConditionSet conditionSet = new SpeechbankConditionSet(conditionsAndPredicates);
			alternatives.add(conditionSet);
		}
		return new AlternativeConditionSets(alternatives);
	}

	private void loadAndInheritParentLines(ResourceLocation speechbankName, JsonObject json, List<SpeechbankEntry<C>> entries, SpeechbankResourceManager.ParentSpeechbankLoader<C> parentLoader) {
		if (json.has("parent")) {
			try {
				ResourceLocation parentSpeechbankName = new ResourceLocation(json.get("parent").getAsString());
				Optional<Speechbank<C>> loadedParent = parentLoader.getOrLoadParentResource(parentSpeechbankName);
				if (loadedParent.isPresent()) {
					List<SpeechbankEntry<C>> parentEntries = loadedParent.get().getEntriesView();
					this.warnOfDuplicateLinesAfterInheritance(speechbankName, parentSpeechbankName, entries, parentEntries);
					entries.addAll(parentEntries);
				} else {
					LOTRLog.error("Failed to load parent speechbank for speechbank %s - parent not found", speechbankName);
				}
			} catch (Exception e) {
				LOTRLog.error("Failed to load parent speechbank for speechbank %s", speechbankName);
				e.printStackTrace();
			}
		}
	}

	private SpeechbankConditionAndPredicate loadConditionAndPredicate(ResourceLocation speechbankName, String key, JsonElement elem) {
		String predicateString = elem.getAsString();
		return this.loadConditionAndPredicate(speechbankName, key, predicateString);
	}

	private SpeechbankConditionAndPredicate loadConditionAndPredicate(ResourceLocation speechbankName, String conditionName, String predicateString) {
		SpeechbankCondition condition = this.speechbankEngine.getCondition(conditionName);
		if (condition == null) {
			LOTRLog.warn("Error loading speechbank %s: condition '%s' does not exist!", speechbankName, conditionName);
			return null;
		}
		Predicate predicate = condition.parsePredicateFromJsonString(predicateString);
		return SpeechbankConditionAndPredicate.of(condition, predicate);
	}

	private SpeechbankEntry<C> loadEntry(ResourceLocation speechbankName, JsonObject json) {
		HashSet contextSatisfiers = new HashSet();
		if (json.has("conditions")) {
			JsonObject conditionsObj = json.get("conditions").getAsJsonObject();
			for (Object e : conditionsObj.entrySet()) {
				String key = (String) ((Entry) e).getKey();
				JsonElement elem = (JsonElement) ((Entry) e).getValue();
				if ("alternatives".equalsIgnoreCase(key)) {
					JsonArray alternativesArray = elem.getAsJsonArray();
					AlternativeConditionSets<C> alternatives = this.loadAlternatives(speechbankName, alternativesArray);
					if (alternatives == null) {
						continue;
					}
					contextSatisfiers.add(alternatives);
					continue;
				}
				SpeechbankConditionAndPredicate<?, C> conditionAndPredicate = this.loadConditionAndPredicate(speechbankName, key, elem);
				if (conditionAndPredicate == null) {
					continue;
				}
				contextSatisfiers.add(conditionAndPredicate);
			}
		}
		ArrayList<String> lines = new ArrayList<>();
		JsonArray linesArray = json.get("lines").getAsJsonArray();
		for (JsonElement lineElem : linesArray) {
			lines.add(lineElem.getAsString());
		}
		return new SpeechbankEntry(contextSatisfiers, lines);
	}

	private void warnOfDuplicateLines(ResourceLocation speechbankName, List<SpeechbankEntry<C>> entries) {
		HashSet trackedLines = new HashSet();
		for (SpeechbankEntry<C> entry : entries) {
			entry.streamLines().forEach(line -> {
				if (!trackedLines.contains(line)) {
					trackedLines.add(line);
				} else {
					LOTRLog.error("Found duplicate line in speechbank %s - line duplicated is '%s'. Speechbanks should not have duplicate lines!", speechbankName, line);
				}
			});
		}
	}

	private void warnOfDuplicateLinesAfterInheritance(ResourceLocation speechbankName, ResourceLocation parentSpeechbankName, List<SpeechbankEntry<C>> entriesBeforeMerge, List<SpeechbankEntry<C>> entriesFromParent) {
		Set allLinesFromParent = entriesFromParent.stream().flatMap(SpeechbankEntry::streamLines).collect(Collectors.toSet());
		HashSet trackedLines = new HashSet();
		for (SpeechbankEntry<C> entry : entriesBeforeMerge) {
			entry.streamLines().forEach(line -> {
				if (!trackedLines.contains(line)) {
					trackedLines.add(line);
					if (allLinesFromParent.contains(line)) {
						LOTRLog.error("Found duplicate line in speechbank %s after merging in lines from parent %s - line duplicated is '%s'. Speechbanks should not have duplicate lines!", speechbankName, parentSpeechbankName, line);
					}
				}
			});
		}
	}
}
