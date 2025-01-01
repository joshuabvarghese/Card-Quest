package pl.csanecki.memory.ui.menu;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import pl.csanecki.memory.ui.dialogs.AboutDialog;
import pl.csanecki.memory.ui.panels.CurrentGameState;
import pl.csanecki.memory.ui.panels.GamePanelSubscriber;

public class MainOptionsMenu extends JMenu implements GamePanelSubscriber {

    private final Collection<MainOptionsMenuSubscriber> subscribers = new ArrayList<>();
    private final JMenuItem resetItem;
    private final AboutDialog aboutDialog;

    public MainOptionsMenu(JFrame owner) {
        super("File");

        this.aboutDialog = new AboutDialog(owner);
        this.resetItem = createMenuItem("Reset", event -> subscribers.forEach(subscriber -> subscriber.update(MainOptions.Reset)));
        this.resetItem.setEnabled(false);

        JMenuItem aboutItem = createMenuItem("About", event -> aboutDialog.setVisible(true));
        JMenuItem exitItem = createMenuItem("Exit", event -> System.exit(0));

        add(resetItem);
        addSeparator();
        add(aboutItem);
        add(exitItem);
    }

    public void registerSubscriber(MainOptionsMenuSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    private static JMenuItem createMenuItem(String text, ActionListener listener) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.addActionListener(listener);
        return menuItem;
    }

    @Override
    public void update(CurrentGameState gameState) {
        resetItem.setEnabled(gameState != CurrentGameState.Idle);
    }
}
