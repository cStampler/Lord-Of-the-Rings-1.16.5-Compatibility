package lotr.common.entity.npc.data.name;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import lotr.common.LOTRLog;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

public class NameBankManager extends ReloadListener {
	public static final NameBankManager INSTANCE = new NameBankManager();
	private static final NameBank MISSING_NAME_BANK = new NameBank(new ResourceLocation("lotr", "npcs/names/missing_name_bank"), ImmutableList.of("???"));
	private Map loadedNameBanks;

	@Override
	protected void apply(Object map, IResourceManager resMgr, IProfiler profiler) {
		loadedNameBanks = (Map) map;
	}

	public NameBank fetchLoadedNameBank(ResourceLocation bankName) {
		if (loadedNameBanks.containsKey(bankName)) {
			return (NameBank) loadedNameBanks.get(bankName);
		}
		LOTRLog.warn("Failed to fetch name bank %s - not loaded", bankName);
		return MISSING_NAME_BANK;
	}

	@Override
	protected Map prepare(IResourceManager resMgr, IProfiler profiler) {
		Map map = new HashMap();
		for (ResourceLocation res : resMgr.listResources("npcs/names", filename -> filename.endsWith(".txt"))) {
			try {
				IResource resource = resMgr.getResource(res);
				Throwable var7 = null;

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
					Throwable var9 = null;

					try {
						List names = reader.lines().map(String::trim).collect(Collectors.toList());
						if (!names.isEmpty()) {
							NameBank bank = new NameBank(res, names);
							map.put(res, bank);
						} else {
							LOTRLog.error("Failed to load name bank %s - name list was empty", res);
						}
					} catch (Throwable var35) {
						var9 = var35;
						throw var35;
					} finally {
						if (reader != null) {
							if (var9 != null) {
								try {
									reader.close();
								} catch (Throwable var34) {
									var9.addSuppressed(var34);
								}
							} else {
								reader.close();
							}
						}

					}
				} catch (Throwable var37) {
					var7 = var37;
					throw var37;
				} finally {
					if (resource != null) {
						if (var7 != null) {
							try {
								resource.close();
							} catch (Throwable var33) {
								var7.addSuppressed(var33);
							}
						} else {
							resource.close();
						}
					}

				}
			} catch (IOException var39) {
				LOTRLog.error("Failed to load name bank %s from file", res);
				var39.printStackTrace();
			}
		}

		return map;
	}

	public static ResourceLocation fullPath(String filename) {
		return new ResourceLocation("lotr", String.format("%s/%s%s", "npcs/names", filename, ".txt"));
	}
}
