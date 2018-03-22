package startingoptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;

import basemod.BaseMod;
import basemod.ModPanel;
import basemod.interfaces.PostCreateIroncladStartingDeckSubscriber;
import basemod.interfaces.PostCreateIroncladStartingRelicsSubscriber;
import basemod.interfaces.PostCreateSilentStartingDeckSubscriber;
import basemod.interfaces.PostCreateSilentStartingRelicsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import startingoptions.StartConfig.CharacterClass;

@SpireInitializer
public class StartingOptions implements PostInitializeSubscriber {
	public static final Logger logger = LogManager.getLogger(StartingOptions.class.getName());
	
	public static final String MODNAME = "StartingOptions";
	public static final String AUTHOR = "daviscook447";
	public static final String DESCRIPTION = "v1.0.0 provides a way to customize loadout";
	
	public static final String START_BUILD_DIR = "starts/";
	public static final String XML_EXTENSION = "xml";
	
	private ArrayList<StartConfig> ironcladConfigs;
	private ArrayList<StartConfig> silentConfigs;
	private String ironcladSelectedLoadout;
	private String silentSelectedLoadout;
	
	public static StartConfig parseConfig(File file) {
		Document d;
		
		try {
			d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(file));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			logger.info("could not load config file: " + file.getAbsolutePath());
			return null;
		}
		
		ArrayList<String> cardList = new ArrayList<>();
		NodeList cards = d.getElementsByTagName("card");
		for (int i = 0; i < cards.getLength(); i++) {
			Node card = cards.item(i);
			String cardName = card.getTextContent();
			cardList.add(cardName);
		}
		
		ArrayList<String> relicList = new ArrayList<>();
		NodeList relics = d.getElementsByTagName("relic");
		for (int i = 0; i < relics.getLength(); i++) {
			Node relic = relics.item(i);
			String relicName = relic.getTextContent();
			relicList.add(relicName);
		}
		
		CharacterClass charClass = null;
		switch (d.getDocumentElement().getTagName()) {
		case "ironclad":
			charClass = CharacterClass.IRONCLAD;
			break;
		case "silent":
			charClass = CharacterClass.SILENT;
			break;
		}

		// get name w/o extension (https://stackoverflow.com/questions/924394/how-to-get-the-filename-without-the-extension-in-java)
		String fileName = file.getName();
		int pos = fileName.lastIndexOf(".");
		if (pos > 0 && pos < (fileName.length() - 1)) { // If '.' is not the first or last character.
		    fileName = fileName.substring(0, pos);
		}
		return new StartConfig(cardList, relicList, charClass, fileName);
	}
	
	public StartingOptions() {
		ironcladConfigs = new ArrayList<StartConfig>();
		silentConfigs = new ArrayList<StartConfig>();
		
		logger.info("loading start build files from starts/");
		
		// get all files from config dir
		File folder = new File(START_BUILD_DIR);
		
		// be careful of nonexistent folder
		if (!folder.exists()) {
			folder.mkdir();
		}
		
		logger.info("checking this directory: " + folder.getAbsolutePath());
		for (File file : folder.listFiles()) {
			logger.info("file found: " + file.getAbsolutePath());
			String extension = FileUtils.getFileExtension(file);
			logger.info("file had extension: " + extension);
			if (extension.equals(XML_EXTENSION)) {
				logger.info("extension was " + XML_EXTENSION + " - parsing file");
				StartConfig cfg = parseConfig(file);
				switch (cfg.charClass) {
				case IRONCLAD:
					ironcladConfigs.add(cfg);
					break;
				case SILENT:
					silentConfigs.add(cfg);
					break;
				}
			}
		}
		
		logger.info("finished loading");
		
		for (int i = 0; i < ironcladConfigs.size(); i++) {
			StartConfig cfg = ironcladConfigs.get(i);
			logger.info("ironclad config " + cfg.loadoutName + " is:");
			String cardStr = "[ ";
			for (String card : cfg.deck) {
				cardStr += card + " ";
			}
			cardStr += "]";
			logger.info("cards are: " + cardStr);
			String relicStr = "[ ";
			for (String relic : cfg.relics) {
				relicStr += relic + " ";
			}
			relicStr += "]";
			logger.info("relics are: " + relicStr);
		}
		
		for (int i = 0; i < silentConfigs.size(); i++) {
			StartConfig cfg = silentConfigs.get(i);
			logger.info("silent config " + cfg.loadoutName + " is:");
			String cardStr = "[ ";
			for (String card : cfg.deck) {
				cardStr += card + " ";
			}
			cardStr += "]";
			logger.info("cards are: " + cardStr);
			String relicStr = "[ ";
			for (String relic : cfg.relics) {
				relicStr += relic + " ";
			}
			relicStr += "]";
			logger.info("relics are: " + relicStr);
		}
		
		// no loadout - use defaults
		ironcladSelectedLoadout = null;
		silentSelectedLoadout = null;
		
		// add listener for postInitialize so we can setup our gui for choosing a loadout
		BaseMod.subscribeToPostInitialize(this);
		
		// listen for the deck build event and relic setup event so we can modify the loadout
		IroncladStartBuilder ironcladBuild = new IroncladStartBuilder();
		SilentStartBuilder silentBuild = new SilentStartBuilder();
		BaseMod.subscribeToPostCreateStartingDeck(ironcladBuild);
		BaseMod.subscribeToPostCreateStartingDeck(silentBuild);
		BaseMod.subscribeToPostCreateStartingRelics(ironcladBuild);
		BaseMod.subscribeToPostCreateStartingRelics(silentBuild);
	}
	
	public static void initialize() {
		logger.info("========================= STARTINGOPTIONS INIT =========================");
		
		@SuppressWarnings("unused")
		StartingOptions so = new StartingOptions();
		
		logger.info("================================================================");
	}

	@SuppressWarnings("deprecation")
	@Override
	public void receivePostInitialize() {
		// gui setup here
		ModPanel settingsPanel = new ModPanel();
		for (int i = 0; i < ironcladConfigs.size(); i++) {
			StartConfig cfg = ironcladConfigs.get(i);
			settingsPanel.addLabel(cfg.loadoutName, 350.0f + 125.0f * i, 750.0f, (me) -> {});
		}
		for (int i = 0; i < ironcladConfigs.size(); i++) {
			StartConfig cfg = ironcladConfigs.get(i);
			settingsPanel.addButton(350.0f + 125.0f * i, 650.0f, (me) -> {
				ironcladSelectedLoadout = cfg.loadoutName;
				logger.info("ironclad loadout " + ironcladSelectedLoadout + " picked");
			});
		}
		for (int i = 0; i < silentConfigs.size(); i++) {
			StartConfig cfg = silentConfigs.get(i);
			settingsPanel.addLabel(cfg.loadoutName, 350.0f + 125.0f * i, 600.0f, (me) -> {});
		}
		for (int i = 0; i < silentConfigs.size(); i++) {
			StartConfig cfg = silentConfigs.get(i);
			settingsPanel.addButton(350.0f + 125.0f * i, 500.0f, (me) -> {
				silentSelectedLoadout = cfg.loadoutName;
				logger.info("silent loadout " + silentSelectedLoadout + " picked");
			});
		}
		
		Texture badgeTexture = new Texture(Gdx.files.internal("img/BaseModBadge.png"));
		BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);
	}

	public class IroncladStartBuilder implements PostCreateIroncladStartingDeckSubscriber,
		PostCreateIroncladStartingRelicsSubscriber {

		@Override
		public boolean receivePostCreateStartingDeck(ArrayList<String> arg0) {
			String loadoutName = ironcladSelectedLoadout;
			StartConfig loadoutCfg = null;
			for (StartConfig cfg : ironcladConfigs) {
				if (cfg.loadoutName.equals(loadoutName)) {
					loadoutCfg = cfg;
				}
			}
			
			if (loadoutCfg == null) {return false;}
			
			for (String card : loadoutCfg.deck) {
				arg0.add(card);
			}
			
			return true;
		}

		@Override
		public boolean receivePostCreateStartingRelics(ArrayList<String> arg0) {
			String loadoutName = ironcladSelectedLoadout;
			StartConfig loadoutCfg = null;
			for (StartConfig cfg : ironcladConfigs) {
				if (cfg.loadoutName.equals(loadoutName)) {
					loadoutCfg = cfg;
				}
			}
			
			if (loadoutCfg == null) {return false;}
			
			for (String relic : loadoutCfg.relics) {
				arg0.add(relic);
			}
			
			return true;
		}
		
	}
	
	public class SilentStartBuilder implements PostCreateSilentStartingDeckSubscriber, 
		PostCreateSilentStartingRelicsSubscriber {

		@Override
		public boolean receivePostCreateStartingDeck(ArrayList<String> arg0) {
			String loadoutName = silentSelectedLoadout;
			StartConfig loadoutCfg = null;
			for (StartConfig cfg : silentConfigs) {
				if (cfg.loadoutName.equals(loadoutName)) {
					loadoutCfg = cfg;
				}
			}
			
			if (loadoutCfg == null) {return false;}
			
			for (String card : loadoutCfg.deck) {
				arg0.add(card);
			}
			
			return true;
		}

		@Override
		public boolean receivePostCreateStartingRelics(ArrayList<String> arg0) {
			String loadoutName = silentSelectedLoadout;
			StartConfig loadoutCfg = null;
			for (StartConfig cfg : silentConfigs) {
				if (cfg.loadoutName.equals(loadoutName)) {
					loadoutCfg = cfg;
				}
			}
			
			if (loadoutCfg == null) {return false;}
			
			for (String relic : loadoutCfg.relics) {
				arg0.add(relic);
			}
			
			return true;
		}
		
	}
	
}
