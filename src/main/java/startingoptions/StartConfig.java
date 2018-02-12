package startingoptions;

import java.util.ArrayList;

public class StartConfig {

	public enum CharacterClass {
		IRONCLAD, SILENT;
	}
	
	public ArrayList<String> deck;
	public ArrayList<String> relics;
	public CharacterClass charClass;
	public String loadoutName;
	
	public StartConfig(ArrayList<String> deck, ArrayList<String> relics, CharacterClass charClass, String loadoutName) {
		this.deck = deck;
		this.relics = relics;
		this.charClass = charClass;
		this.loadoutName = loadoutName;
	}
	
}
