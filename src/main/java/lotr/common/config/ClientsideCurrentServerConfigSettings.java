package lotr.common.config;

public class ClientsideCurrentServerConfigSettings {
	public static final ClientsideCurrentServerConfigSettings INSTANCE = new ClientsideCurrentServerConfigSettings();
	public boolean areasOfInfluence;
	public boolean smallerBees;
	public boolean hasMapFeatures;
	public int forceFogOfWar;

	private ClientsideCurrentServerConfigSettings() {
	}
}
