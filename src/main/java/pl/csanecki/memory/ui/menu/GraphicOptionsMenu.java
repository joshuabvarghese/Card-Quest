package pl.csanecki.memory.ui.menu;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import pl.csanecki.memory.config.CustomConfig;
import pl.csanecki.memory.config.GameSize;
import pl.csanecki.memory.config.NumberOfCardsInGroup;
import pl.csanecki.memory.config.ObversesTheme;
import pl.csanecki.memory.config.ReverseTheme;
import pl.csanecki.memory.ui.panels.CurrentGameState;
import pl.csanecki.memory.ui.panels.GamePanelSubscriber;

public class GraphicOptionsMenu extends JMenu implements GamePanelSubscriber {
    private final GameSizeMenu gameSizeMenu;
    private final ReverseThemeMenu reverseThemeMenu;
    private final ObversesThemeMenu obversesThemeMenu;
    private final NumberOfCardsInGroupMenu numberOfCardsInGroupMenu;

    public GraphicOptionsMenu(CustomConfig customConfig) {
        super("Options"); // Changed from "Opcje" to "Options"
        gameSizeMenu = new GameSizeMenu(customConfig);
        reverseThemeMenu = new ReverseThemeMenu(customConfig);
        obversesThemeMenu = new ObversesThemeMenu(customConfig);
        numberOfCardsInGroupMenu = new NumberOfCardsInGroupMenu(customConfig);
        
        add(gameSizeMenu);
        add(reverseThemeMenu);
        add(obversesThemeMenu);
        add(numberOfCardsInGroupMenu);
    }

    public void registerSubscriber(GraphicOptionsMenuSubscriber subscriber) {
        gameSizeMenu.registerSubscriber(subscriber);
        reverseThemeMenu.registerSubscriber(subscriber);
        obversesThemeMenu.registerSubscriber(subscriber);
        numberOfCardsInGroupMenu.registerSubscriber(subscriber);
    }

    @Override
    public void update(CurrentGameState gameState) {
        setEnabled(gameState == CurrentGameState.Idle);
    }

    private static class GameSizeMenu extends JMenu {
        private final Collection<GraphicOptionsMenuSubscriber> subscribers = new ArrayList<>();
        private final Collection<JMenuItem> items = new ArrayList<>();

        private GameSizeMenu(CustomConfig customConfig) {
            super("Board Size"); // Changed from "Rozmiar planszy" to "Board Size"
            JMenuItem gameSizeMenuSmall = new JMenuItem("Small"); // Changed from "Mały" to "Small"
            JMenuItem gameSizeMenuMedium = new JMenuItem("Medium"); // Changed from "Średni" to "Medium"
            JMenuItem gameSizeMenuLarge = new JMenuItem("Large"); // Changed from "Duży" to "Large"

            items.add(gameSizeMenuSmall);
            items.add(gameSizeMenuMedium);
            items.add(gameSizeMenuLarge);

            gameSizeMenuSmall.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeGameSize(GameSize.Small);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                gameSizeMenuSmall.setEnabled(false);
            }));

            gameSizeMenuMedium.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeGameSize(GameSize.Medium);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                gameSizeMenuMedium.setEnabled(false);
            }));

            gameSizeMenuLarge.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeGameSize(GameSize.Large);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                gameSizeMenuLarge.setEnabled(false);
            }));

            items.forEach(this::add);

            switch (customConfig.gameSize) {
                case Small -> gameSizeMenuSmall.setEnabled(false);
                case Medium -> gameSizeMenuMedium.setEnabled(false);
                case Large -> gameSizeMenuLarge.setEnabled(false);
            }
        }

        private void registerSubscriber(GraphicOptionsMenuSubscriber subscriber) {
            this.subscribers.add(subscriber);
        }
    }

    private static class ReverseThemeMenu extends JMenu {
        private final Collection<GraphicOptionsMenuSubscriber> subscribers = new ArrayList<>();
        private final Collection<JMenuItem> items = new ArrayList<>();

        private ReverseThemeMenu(CustomConfig customConfig) {
            super("Reverse Theme"); // Changed from "Motyw rewersu" to "Reverse Theme"
            JMenuItem reverseThemeMenuEarth = new JMenuItem("Earth"); // Changed from "Ziemia" to "Earth"
            JMenuItem reverseThemeMenuJungle = new JMenuItem("Jungle"); // Changed from "Dżungla" to "Jungle"
            JMenuItem reverseThemeMenuPremierLeague = new JMenuItem("Premier League"); // Kept the same

            items.add(reverseThemeMenuEarth);
            items.add(reverseThemeMenuJungle);
            items.add(reverseThemeMenuPremierLeague);

            reverseThemeMenuEarth.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeReverseTheme(ReverseTheme.Earth);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                reverseThemeMenuEarth.setEnabled(false);
            }));

            reverseThemeMenuJungle.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeReverseTheme(ReverseTheme.Jungle);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                reverseThemeMenuJungle.setEnabled(false);
            }));

            reverseThemeMenuPremierLeague.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeReverseTheme(ReverseTheme.PremierLeague);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                reverseThemeMenuPremierLeague.setEnabled(false);
            }));

            items.forEach(this::add);

            switch (customConfig.reverseTheme) {
                case Earth -> reverseThemeMenuEarth.setEnabled(false);
                case Jungle -> reverseThemeMenuJungle.setEnabled(false);
                case PremierLeague -> reverseThemeMenuPremierLeague.setEnabled(false);
            }
        }

        private void registerSubscriber(GraphicOptionsMenuSubscriber subscriber) {
            this.subscribers.add(subscriber);
        }
    }

    private static class ObversesThemeMenu extends JMenu {
        private final Collection<GraphicOptionsMenuSubscriber> subscribers = new ArrayList<>();
        private final Collection<JMenuItem> items = new ArrayList<>();

        private ObversesThemeMenu(CustomConfig customConfig) {
            super("Obverse Theme"); // Changed from "Motyw awersu" to "Obverse Theme"
            JMenuItem obversesThemeMenuEnglishClubs = new JMenuItem("English Clubs"); // Changed from "Kluby angielskie" to "English Clubs"
            JMenuItem obversesThemeMenuAnimals = new JMenuItem("Animals"); // Changed from "Zwierzęta" to "Animals"

            items.add(obversesThemeMenuEnglishClubs);
            items.add(obversesThemeMenuAnimals);

            obversesThemeMenuEnglishClubs.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeObversesTheme(ObversesTheme.EnglishClubs);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                obversesThemeMenuEnglishClubs.setEnabled(false);
            }));

            obversesThemeMenuAnimals.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeObversesTheme(ObversesTheme.Animals);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                obversesThemeMenuAnimals.setEnabled(false);
            }));

            items.forEach(this::add);

            switch (customConfig.obversesTheme) {
                case EnglishClubs -> obversesThemeMenuEnglishClubs.setEnabled(false);
                case Animals -> obversesThemeMenuAnimals.setEnabled(false);
            }
        }

        private void registerSubscriber(GraphicOptionsMenuSubscriber subscriber) {
            this.subscribers.add(subscriber);
        }
    }

    private static class NumberOfCardsInGroupMenu extends JMenu {
        private final Collection<GraphicOptionsMenuSubscriber> subscribers = new ArrayList<>();
        private final Collection<JMenuItem> items = new ArrayList<>();

        private NumberOfCardsInGroupMenu(CustomConfig customConfig) {
            super("Number of Cards in Group"); // Changed from "Ilość kart w grupie" to "Number of Cards in Group"
            
            JMenuItem numberOfCardsInGroupTwo = new JMenuItem("Two"); // Changed from "Dwie" to "Two"
            JMenuItem numberOfCardsInGroupThree = new JMenuItem("Three"); // Changed from "Trzy" to "Three"
            JMenuItem numberOfCardsInGroupFour = new JMenuItem("Four"); // Changed from "Cztery" to "Four"

            items.add(numberOfCardsInGroupTwo);
            items.add(numberOfCardsInGroupThree);
            items.add(numberOfCardsInGroupFour);

            numberOfCardsInGroupTwo.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeNumberOfCardsInGroup(NumberOfCardsInGroup.Two);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                numberOfCardsInGroupTwo.setEnabled(false);
            }));

            numberOfCardsInGroupThree.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeNumberOfCardsInGroup(NumberOfCardsInGroup.Three);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                numberOfCardsInGroupThree.setEnabled(false);
            }));

            numberOfCardsInGroupFour.addActionListener(event -> subscribers.forEach(subscriber -> {
                customConfig.changeNumberOfCardsInGroup(NumberOfCardsInGroup.Four);
                subscriber.update(customConfig);
                items.forEach(item -> item.setEnabled(true));
                numberOfCardsInGroupFour.setEnabled(false);
            }));

            items.forEach(this::add);

            switch (customConfig.numberOfCardsInGroup) {
                case Two -> numberOfCardsInGroupTwo.setEnabled(false); 
                case Three -> numberOfCardsInGroupThree.setEnabled(false); 
                case Four -> numberOfCardsInGroupFour.setEnabled(false); 
            }
        }

        private void registerSubscriber(GraphicOptionsMenuSubscriber subscriber) {
           this.subscribers.add(subscriber); 
       }
   }
}
