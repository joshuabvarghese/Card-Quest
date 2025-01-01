package pl.csanecki.memory.ui.panels;

import pl.csanecki.memory.engine.FlatItemId;
import pl.csanecki.memory.engine.GuessResult;
import pl.csanecki.memory.engine.MemoryGame;
import pl.csanecki.memory.engine.state.FlatItemCurrentState;
import pl.csanecki.memory.engine.state.GroupOfFlatItemsCurrentState;
import pl.csanecki.memory.engine.state.MemoryGameCurrentState;
import pl.csanecki.memory.ui.UiConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.*;

import static pl.csanecki.memory.ui.UiConfig.CARDS_PANEL_BACKGROUND_COLOR;

public class CardsPanel extends JPanel {

    private static final int BOUND = 10;

    private final Collection<CardsPanelSubscriber> subscribers = new ArrayList<>();

    private MemoryGame memoryGame;
    private List<GraphicCard> graphicCards;

    private int columns;
    private int rows;

    public CardsPanel(UiConfig uiConfig) {
        prepareCardsPanel(uiConfig);

        graphicCards.forEach(this::add);
        addMouseListener(new ClickMouseListener());

        setLayout(null);
        setLocation(0, 0);
        setBackground(CARDS_PANEL_BACKGROUND_COLOR);
    }

    public void adjustTo(UiConfig uiConfig) {
        prepareCardsPanel(uiConfig);

        removeAll();
        graphicCards.forEach(this::add);

        repaint();
    }

    public void reset() {
        memoryGame.reset();
        MemoryGameCurrentState currentState = memoryGame.currentState();
        refreshAll(currentState);
        Collections.shuffle(graphicCards);
        setGraphicCardsBounds(columns, rows, graphicCards);
        subscribers.forEach(subscriber -> subscriber.update(CurrentGameState.Idle));
    }

    private void prepareCardsPanel(UiConfig uiConfig) {
        MemoryGame memoryGame = MemoryGame.create(uiConfig.countNumbersOfCards(), uiConfig.numberOfCardsInGroup);
        MemoryGameCurrentState currentState = memoryGame.currentState();

        List<GraphicCard> graphicCards = prepareGraphicCards(uiConfig, currentState);
        setGraphicCardsBounds(uiConfig.columns, uiConfig.rows, graphicCards);

        Dimension panelDimension = resolvePanelDimension(uiConfig, graphicCards);
        setSize(panelDimension);

        this.memoryGame = memoryGame;
        this.graphicCards = graphicCards;
        this.columns = uiConfig.columns;
        this.rows = uiConfig.rows;
    }

    public void registerSubscriber(CardsPanelSubscriber subscriber) {
        this.subscribers.add(subscriber);
    }

    private Dimension resolvePanelDimension(UiConfig uiConfig, List<GraphicCard> graphicCards) {
        Integer maxCardsX = graphicCards.stream()
                .map(JComponent::getX)
                .max(Comparator.comparingInt(x -> x))
                .orElse(0);
        Integer maxCardsY = graphicCards.stream()
                .map(JComponent::getY)
                .max(Comparator.comparingInt(y -> y))
                .orElse(0);
        int width = maxCardsX + uiConfig.reverseImage.getIconWidth() + BOUND;
        int height = maxCardsY + uiConfig.reverseImage.getIconWidth() + BOUND;

        return new Dimension(width, height);
    }

    private List<GraphicCard> prepareGraphicCards(UiConfig uiConfig, MemoryGameCurrentState currentState) {
        List<GraphicCard> graphicCards = new ArrayList<>();
        int index = 0;
        for (GroupOfFlatItemsCurrentState groupOfFlatItemsCurrentState : currentState.groupOfFlatItems()) {
            ImageIcon obverseImage = uiConfig.obverseImages.get(index);
            for (FlatItemCurrentState flatItemCurrentState : groupOfFlatItemsCurrentState.flatItems()) {
                graphicCards.add(new GraphicCard(
                        flatItemCurrentState.flatItemId(),
                        uiConfig.reverseImage,
                        obverseImage,
                        flatItemCurrentState.obverse()));
            }
            index++;
        }
        Collections.shuffle(graphicCards);
        return graphicCards;
    }

    private void setGraphicCardsBounds(int columns, int rows, List<GraphicCard> graphicCards) {
        int sizeCards = graphicCards.size();
        for (int column = 0; column < columns; column++) {
            for (int row = 0; row < rows; row++) {
                sizeCards--;
                GraphicCard graphicCard = graphicCards.get(sizeCards);
                graphicCard.setBounds(
                        BOUND * (column + 1) + column * graphicCard.getWidth(),
                        BOUND * (row + 1) + row * graphicCard.getHeight(),
                        graphicCard.getWidth(), graphicCard.getHeight());
            }
        }
    }

    private Optional<GraphicCard> findCardByCoordinates(Point2D point) {
        return graphicCards.stream()
                .filter(graphicCard -> graphicCard.contains(point))
                .findFirst();
    }

    private void refreshAll(MemoryGameCurrentState currentState) {
        graphicCards.forEach(graphicCard -> currentState.groupOfFlatItems()
                .stream()
                .map(GroupOfFlatItemsCurrentState::flatItems)
                .flatMap(Collection::stream)
                .filter(flatItem -> flatItem.flatItemId().equals(graphicCard.flatItemId))
                .findFirst()
                .ifPresent(graphicCard::refresh));
    }

    private class ClickMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent event) {
            findCardByCoordinates(event.getPoint())
                    .ifPresent(graphicCard -> {
                        GuessResult result = memoryGame.turnCard(graphicCard.flatItemId);
                        MemoryGameCurrentState currentState = memoryGame.currentState();
                        if (result == GuessResult.Failure) {
                            graphicCard.turnToObverseUp();
                        } else {
                            refreshAll(currentState);
                        }
                        subscribers.forEach(subscriber -> subscriber.update(
                                currentState.isFinished() ? CurrentGameState.Ended : CurrentGameState.Running));
                    });
        }
    }

    private static class GraphicCard extends JLabel {

        private final FlatItemId flatItemId;
        private final ImageIcon reverseIcon;
        private final ImageIcon obverseIcon;

        GraphicCard(FlatItemId flatItemId, ImageIcon reverseIcon, ImageIcon obverseIcon, boolean obverse) {
            setBounds(new Rectangle(reverseIcon.getIconWidth(), reverseIcon.getIconHeight()));

            this.flatItemId = flatItemId;
            this.reverseIcon = reverseIcon;
            this.obverseIcon = obverseIcon;

            setIcon(currentIcon(obverse));
        }

        void turnToObverseUp() {
            setIcon(currentIcon(true));
        }

        void refresh(FlatItemCurrentState flatItem) {
            setIcon(currentIcon(flatItem.obverse()));
        }

        private ImageIcon currentIcon(boolean obverse) {
            return obverse ? obverseIcon : reverseIcon;
        }

        boolean contains(Point2D point) {
            Rectangle rectangle = new Rectangle(
                    this.getLocation().x, this.getLocation().y,
                    this.getWidth(), this.getHeight());
            return rectangle.contains(point);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            ImageIcon currentImageIcon = (ImageIcon) getIcon();
            g2.drawImage(currentImageIcon.getImage(), 0, 0, null);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (this == obj) return true;
            if (this.getClass() != obj.getClass()) return false;
            GraphicCard graphicCard = (GraphicCard) obj;
            return this.flatItemId.equals(graphicCard.flatItemId);
        }
    }


}
