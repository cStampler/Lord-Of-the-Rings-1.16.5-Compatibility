package lotr.common.entity.npc.data.name;

public class NPCNameGenerators {
	public static final NPCNameGenerator NAMELESS_THING = (rand, male) -> null;
	private static final SimpleGenderedNameGenerator HOBBIT_FORENAME = new SimpleGenderedNameGenerator(NameBankManager.fullPath("hobbit_forename_male"), NameBankManager.fullPath("hobbit_forename_female"));
	private static final SimpleNameGenerator HOBBIT_SURNAME = new SimpleNameGenerator(NameBankManager.fullPath("hobbit_surname"));
	private static final SimpleGenderedNameGenerator BREE_FORENAME = new SimpleGenderedNameGenerator(NameBankManager.fullPath("bree_forename_male"), NameBankManager.fullPath("bree_forename_female"));
	private static final SimpleNameGenerator BREE_SURNAME = new SimpleNameGenerator(NameBankManager.fullPath("bree_surname"));
	public static final NPCNameGenerator HOBBIT;
	public static final NPCNameGenerator ORC;
	public static final NPCNameGenerator GONDOR;
	public static final NPCNameGenerator ROHAN;
	public static final NPCNameGenerator DALE;
	public static final NPCNameGenerator BREE;
	public static final NPCNameGenerator BREE_HOBBIT;
	public static final NPCNameGenerator DUNLENDING;
	public static final NPCNameGenerator HARNENNOR;
	public static final NPCNameGenerator UMBAR;
	public static final NPCNameGenerator COAST_SOUTHRON;
	public static final NPCNameGenerator SINDARIN;
	public static final NPCNameGenerator QUENYA;
	public static final NPCNameGenerator DWARF;

	static {
		HOBBIT = new SurnamedNameGenerator(HOBBIT_FORENAME, HOBBIT_SURNAME);
		ORC = new PresuffixNameGenerator(NameBankManager.fullPath("orc_prefix"), NameBankManager.fullPath("orc_suffix"));
		GONDOR = new SimpleGenderedNameGenerator(NameBankManager.fullPath("gondor_male"), NameBankManager.fullPath("gondor_female"));
		ROHAN = new SimpleGenderedNameGenerator(NameBankManager.fullPath("rohan_male"), NameBankManager.fullPath("rohan_female"));
		DALE = new SimpleGenderedNameGenerator(NameBankManager.fullPath("dale_male"), NameBankManager.fullPath("dale_female"));
		BREE = new SurnamedNameGenerator(BREE_FORENAME, BREE_SURNAME);
		BREE_HOBBIT = new SurnamedNameGenerator(WeightedNameGenerator.builder().add(BREE_FORENAME, 2).add(HOBBIT_FORENAME, 1).build(), WeightedNameGenerator.builder().add(BREE_SURNAME, 2).add(HOBBIT_SURNAME, 1).build());
		DUNLENDING = new SimpleGenderedNameGenerator(NameBankManager.fullPath("dunlending_male"), NameBankManager.fullPath("dunlending_female"));
		HARNENNOR = new SimpleGenderedNameGenerator(NameBankManager.fullPath("harnennor_male"), NameBankManager.fullPath("harnennor_female"));
		UMBAR = new SimpleGenderedNameGenerator(NameBankManager.fullPath("umbar_male"), NameBankManager.fullPath("umbar_female"));
		COAST_SOUTHRON = WeightedNameGenerator.builder().add(HARNENNOR, 2).add(UMBAR, 1).build();
		SINDARIN = new SimpleGenderedNameGenerator(NameBankManager.fullPath("sindarin_male"), NameBankManager.fullPath("sindarin_female"));
		QUENYA = new SurnamedNameGenerator(NameBankManager.fullPath("quenya_male"), NameBankManager.fullPath("quenya_female"), NameBankManager.fullPath("quenya_title"), 0.2F);
		DWARF = new ParentonymicGenderedNameGenerator(NameBankManager.fullPath("dwarf_male"), NameBankManager.fullPath("dwarf_female"));
	}
}
