package ch.fhnw.cuie.project.template_simplecontrol;

import java.util.*;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleablePropertyFactory;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;
import javafx.util.converter.NumberStringConverter;

/**
 * Lifter ist ein CustomControl zum verwalten der Skilifte eines Skiresorts.
 * Durch die grafische Darstellung und Drag&Drop können neue Lifte erschaffen und bei Klick darauf gelöscht werden.
 * Die Lifte werden aus Darstellungsgründen als ein Teilelement dargestellt. Sprich => 1 Gondel enspricht einem Gondellift.
 * <p>*
 * @author Yannick Puler und Cédric Merz
 */
public class Lifter extends Region {
    // needed for StyleableProperties
    private static final StyleablePropertyFactory<Lifter> FACTORY = new StyleablePropertyFactory<>(Region.getClassCssMetaData());

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return FACTORY.getCssMetaData();
    }

    private static final Locale CH = new Locale("de", "CH");

    private static final double ARTBOARD_WIDTH = 500;
    private static final double ARTBOARD_HEIGHT = 293;

    private static final double ASPECT_RATIO = ARTBOARD_WIDTH / ARTBOARD_HEIGHT;

    private static final double MINIMUM_WIDTH = 25;
    private static final double MINIMUM_HEIGHT = MINIMUM_WIDTH / ASPECT_RATIO;

    private static final double MAXIMUM_WIDTH = 800;

    private static final int MAX_LIFTS = 30;

    private static final int DISTANCE_BETWEEN_LIFTS = 80;

    private static boolean canValueChange = true;

    private static String GONDOLA_TAG = "Gondolas";
    private static String DRAGLIFT_TAG = "DragLift";
    private static String CHAIRLIFT_TAG = "ChairLift";

    private final DataFormat labelFormat = new DataFormat();



    private ArrayList<Pane> gondolasLiftList = new ArrayList<>();
    private ArrayList<Pane> chairLiftList = new ArrayList<>();
    private ArrayList<Pane> dragLiftList = new ArrayList<>();

    private Polygon mountain1Shade;
    private Polygon mountain1White;
    private Polygon mountain2Shade;
    private Polygon mountain2White;

    private SVGPath line;
    private SVGPath lineTwo;
    private SVGPath lineThree;
    String linePath = "M445.745383,45.8443272 C386.850127,81.4147782 325.797888,99.2000037 262.588663,99.2000037 C199.379439,99.2000037 135.651556,81.4147782 71.4050132,45.8443272";

    private Rectangle poleRight;
    private Rectangle poleLeft;
    private Circle poleLeftCircle;
    private Circle poleRightCircle;

    private Pane gondolaDrag;
    private Pane dragLiftDrag;
    private Pane chairLiftDrag;

    private Label gondolasLabel;
    private Label dragLiftLabel;
    private Label chairliftLabel;

    private Pane droppingAreaDragLift;
    private Pane droppingAreaChairLift;
    private Pane droppingAreaGondola;


    private final DoubleProperty dragLifts = new SimpleDoubleProperty();
    private final DoubleProperty chairLifts = new SimpleDoubleProperty();
    private final DoubleProperty gondolas = new SimpleDoubleProperty();

    private static final CssMetaData<Lifter, Color> BASE_COLOR_META_DATA = FACTORY.createColorCssMetaData("-base-color", s -> s.baseColor);

    private final StyleableObjectProperty<Color> baseColor = new SimpleStyleableObjectProperty<Color>(BASE_COLOR_META_DATA, this, "baseColor") {
        @Override
        protected void invalidated() {
            setStyle(getCssMetaData().getProperty() + ": " + colorToCss(get()) + ";");
            applyCss();
        }
    };


    // needed for resizing
    private Pane drawingPane;

    public Lifter() {
        initializeSelf();
        initializeParts();
        initializeDrawingPane();
        layoutParts();
        setupEventHandlers();
        setupValueChangeListeners();
        setupBindings();
    }

    private void initializeSelf() {
        // load stylesheets
        String fonts = getClass().getResource("/fonts/fonts.css").toExternalForm();
        getStylesheets().add(fonts);

        String stylesheet = getClass().getResource("style_lifter.css").toExternalForm();
        getStylesheets().add(stylesheet);

        getStyleClass().add("lifter");
    }

    private void initializeParts() {
        mountain1Shade = new Polygon();
        mountain1Shade.setTranslateX(32.209763);
        mountain1Shade.setTranslateY(126.175462);
        mountain1Shade.getPoints().addAll(163.47, 0.0, 77.0, 48.32, 0.0, 166.1, 170.55, 166.1, 151.51, 103.23, 163.47, 0.0);
        mountain1Shade.getStyleClass().add("mountain--shade");

        mountain1White = new Polygon();
        mountain1White.setTranslateX(32.209763);
        mountain1White.setTranslateY(126.175462);
        mountain1White.getPoints().addAll(170.398165, 166.102315, 151.550132, 103.23219, 163.588143, -1.13686838e-13, 201.209199, 54.4824971, 237.673279, 69.4277238, 284.027349, 166.102315);
        mountain1White.getStyleClass().add("mountain--white");

        mountain2Shade = new Polygon();
        mountain2Shade.setTranslateX(257.000000);
        mountain2Shade.setTranslateY(134.000000);
        mountain2Shade.getPoints().addAll(107.252548, 0.255936675, 0.308707124, 120.778204, 23.2128339, 158.228969, 125.618322, 158.228969, 114.190344, 120.778204);
        mountain2Shade.getStyleClass().add("mountain--shade");

        mountain2White = new Polygon();
        mountain2White.setTranslateX(257.000000);
        mountain2White.setTranslateY(134.000000);
        mountain2White.getPoints().addAll(125.502349, 158.228969, 114.184926, 120.778204, 106.83905, 0.255936675, 157.713061, 21.8410551, 180.0, 64.5699208, 220.0, 103.58883, 228.314914, 158.228969);
        mountain2White.getStyleClass().add("mountain--white");

        line = new SVGPath();
        line.setContent(linePath);
        line.getStyleClass().add("line-path");

        lineTwo = new SVGPath();
        lineTwo.setContent(linePath);
        lineTwo.setTranslateY(DISTANCE_BETWEEN_LIFTS);
        lineTwo.getStyleClass().add("line-path");

        lineThree = new SVGPath();
        lineThree.setContent(linePath);
        lineThree.setTranslateY(2 * DISTANCE_BETWEEN_LIFTS);
        lineThree.getStyleClass().add("line-path");

        poleRight = new Rectangle(2.42612137, 5.51451187, 7.91556728, 243.073879);
        poleRight.setTranslateX(442.000000);
        poleRight.setTranslateY(43.000000);
        poleRight.getStyleClass().add("pole");

        poleLeft = new Rectangle(2.12796834, 6.00923483, 7.91556728, 242.744063);
        poleLeft.setTranslateX(64.000000);
        poleLeft.setTranslateY(43.000000);
        poleLeft.getStyleClass().add("pole");

        poleLeftCircle = new Circle(5.75593668, 6.33905013, 7.60686016);
        poleLeftCircle.setTranslateX(64);
        poleLeftCircle.setTranslateY(43);
        poleLeftCircle.getStyleClass().add("pole-circle");

        poleRightCircle = new Circle(6.05408971, 5.84432718, 7.60686016);
        poleRightCircle.setTranslateX(442);
        poleRightCircle.setTranslateY(43);
        poleRightCircle.getStyleClass().add("pole-circle");

        dragLiftDrag = putDragLift(5,0);
        dragLiftDrag.setScaleX(0.8);
        dragLiftDrag.setScaleY(0.8);

        chairLiftDrag = putChairlift(18, 20);
        chairLiftDrag.setScaleX(0.8);
        chairLiftDrag.setScaleY(0.8);

        gondolaDrag = putGondola(11, 40);
        gondolaDrag.setScaleX(0.8);
        gondolaDrag.setScaleY(0.8);

        dragLiftLabel = new Label();

        chairliftLabel = new Label();
        chairliftLabel.setTranslateY(chairLiftDrag.getTranslateY());

        gondolasLabel = new Label();
        gondolasLabel.setTranslateY(gondolaDrag.getTranslateY());

        droppingAreaDragLift = new Pane();
        droppingAreaDragLift.setPrefSize(360,60);
        droppingAreaDragLift.setTranslateX(80);
        droppingAreaDragLift.setTranslateY(50);

        droppingAreaChairLift = new Pane();
        droppingAreaChairLift.setPrefSize(360,60);
        droppingAreaChairLift.setTranslateX(80);
        droppingAreaChairLift.setTranslateY(130);


        droppingAreaGondola = new Pane();
        droppingAreaGondola.setPrefSize(360,60);
        droppingAreaGondola.setTranslateX(80);
        droppingAreaGondola.setTranslateY(210);


        for (int i = 0; i < MAX_LIFTS; i++) {

            float percentage = (float)i / MAX_LIFTS;

            Pane paneGondola = getLift(0, percentage);
            paneGondola.setScaleX(0.5);
            paneGondola.setScaleY(0.5);
            paneGondola.setVisible(false);
            gondolasLiftList.add(paneGondola);

            Pane paneDrag = getLift(1, percentage);
            paneDrag.setScaleX(0.5);
            paneDrag.setScaleY(0.5);
            paneDrag.setVisible(false);
            dragLiftList.add(paneDrag);

            Pane paneChair = getLift(2, percentage);
            paneChair.setScaleX(0.5);
            paneChair.setScaleY(0.5);
            paneChair.setVisible(false);
            chairLiftList.add(paneChair);
        }

        Collections.shuffle(gondolasLiftList);
        Collections.shuffle(dragLiftList);
        Collections.shuffle(chairLiftList);

    }


    private void initializeDrawingPane() {
        drawingPane = new Pane();
        drawingPane.getStyleClass().add("drawing-pane");
        drawingPane.setMaxSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setMinSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
        drawingPane.setPrefSize(ARTBOARD_WIDTH, ARTBOARD_HEIGHT);
    }

    private void layoutParts() {
        drawingPane.getChildren().addAll(
                mountain1Shade, mountain2Shade, mountain1White, mountain2White,
                line, lineTwo, lineThree,
                poleRight, poleLeft, poleLeftCircle, poleRightCircle,
                dragLiftDrag, chairLiftDrag, gondolaDrag,
                gondolasLabel, dragLiftLabel, chairliftLabel,
                droppingAreaDragLift, droppingAreaChairLift ,droppingAreaGondola);

        drawingPane.getChildren().addAll(gondolasLiftList);
        drawingPane.getChildren().addAll(dragLiftList);
        drawingPane.getChildren().addAll(chairLiftList);

        //System.out.println(gondolasLiftList.size());

        //for (int i = 0; i < 10; i++) {
        //    Pane pane = getRandomLift();
        //    pane.setScaleX(0.5);
        //    pane.setScaleY(0.5);
        //    drawingPane.getChildren().add(pane);
        //}

        getChildren().add(drawingPane);
    }

    private float getValueOnStrip(float x, int min, int max) {
        return x * (max - min) + min;
    }

    private Pane getLift(int lift, float percentage) {
        //double x = getRandom(80, 420);
        double x = getValueOnStrip(percentage, 80, 420);
        double y = getHeight(x, 80, 420);

        //Random rand = new Random();
        //rand.nextInt(3);

        switch (lift) {
            case 0:
                return putGondola(x, y);
            case 1:
                return putDragLift(x, y);
            case 2:
                return putChairlift(x, y);
        }
        return new Pane();
    }


    private double getRandom(int min, int max) {
        return new Random().nextDouble() * (max - min) + min;
    }

    private double getHeight(double val, int min, int max) {
        double mid = (max - min) / 2f + min;
        double theval = Math.abs(val - mid);


        double extra = 0;

        double tresh = 120;
        if (theval > tresh) {
            double amount = mid - tresh - min;
            extra = (amount - Math.abs(theval - mid + min)) / 2;
        }

        return -extra + 85 - (40 / mid * theval);
    }

    private void setupEventHandlers() {
        //---------------MouseOnClick Event for Deleting----------------//
        gondolasLiftList.forEach(gondola -> {
            gondola.setOnMouseClicked(event -> {
                deleteClickedLift(gondola, 0);
            });
        });

        dragLiftList.forEach(drag -> {
            drag.setOnMouseClicked(event -> {
                deleteClickedLift(drag, 1);
            });
        });

        chairLiftList.forEach(chair -> {
            chair.setOnMouseClicked(event -> {
                deleteClickedLift(chair, 2);
            });
        });


        //---------------DragLift Drag And Drop----------------//
        dragLiftDrag.setOnDragDetected(event -> {
            Dragboard db = dragLiftDrag.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(DRAGLIFT_TAG);
            db.setContent(cc);
            event.consume();
        });

        dragLiftDrag.setOnDragDone(Event::consume);

        droppingAreaDragLift.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        droppingAreaDragLift.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.getString().equals(DRAGLIFT_TAG)) {
                dragLifts.setValue(dragLifts.getValue() + 1);
            }
            event.consume();
        });


        //---------------ChairLift Drag And Drop----------------//
        chairLiftDrag.setOnDragDetected(event -> {
            Dragboard db = chairLiftDrag.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(CHAIRLIFT_TAG);
            db.setContent(cc);
            event.consume();
        });

        chairLiftDrag.setOnDragDone(Event::consume);

        droppingAreaChairLift.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        droppingAreaChairLift.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.getString().equals(CHAIRLIFT_TAG)) {
                chairLifts.setValue(chairLifts.getValue() + 1);
            }
            event.consume();
        });


        //---------------Gondola Drag And Drop----------------//
        gondolaDrag.setOnDragDetected(event -> {
            Dragboard db = gondolaDrag.startDragAndDrop(TransferMode.ANY);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(GONDOLA_TAG);
            db.setContent(cc);
            event.consume();
        });

        gondolaDrag.setOnDragDone(Event::consume);

        droppingAreaGondola.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.ANY);
            event.consume();
        });

        droppingAreaGondola.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.getString().equals(GONDOLA_TAG)) {
                gondolas.setValue(gondolas.getValue() + 1);
            }
            event.consume();
        });


    }

    private void deleteClickedLift(Pane pane, int lift) {
        if (lift > 3) return;

        int count = 0;

        canValueChange = false;
        switch (lift) {
            case 0:
                gondolas.setValue(gondolas.getValue() - 1);
                count = gondolas.intValue();
                break;
            case 1:
                dragLifts.setValue(dragLifts.getValue() - 1);
                count = dragLifts.intValue();
                break;
            case 2:
                chairLifts.setValue(chairLifts.getValue() - 1);
                count = chairLifts.intValue();
                break;
        }
        canValueChange = true;

        if (count <= MAX_LIFTS) {
            pane.setVisible(false);
        }
    }

    private void setupValueChangeListeners() {
        gondolasProperty().addListener((observable, oldValue, newValue) -> updateLifts(newValue.intValue(), 0));
        dragLiftsProperty().addListener((observable, oldValue, newValue) -> updateLifts(newValue.intValue(), 1));
        chairLiftsProperty().addListener((observable, oldValue, newValue) -> updateLifts(newValue.intValue(), 2));
    }

    private void updateLifts(int newValue, int lift) {
        if (!canValueChange) return;

        ArrayList<Pane> lifts = null;

        switch (lift) {
            case 0:
                lifts = getGondolasLiftList();
                break;
            case 1:
                lifts = getDragLiftList();
                break;
            case 2:
                lifts = getChairLiftList();
                break;
        }

        if (lifts == null) return;

        int count = newValue;
        if (count > MAX_LIFTS) count = MAX_LIFTS;

        lifts.forEach(gondola -> gondola.setVisible(false));
        for (int i = 0; i < count; i++) {
            lifts.get(i).setVisible(true);
        }
    }

    private void setupBindings() {
        gondolasLabel.textProperty().bindBidirectional(gondolasProperty(), new NumberStringConverter());
        dragLiftLabel.textProperty().bindBidirectional(dragLiftsProperty(), new NumberStringConverter());
        chairliftLabel.textProperty().bindBidirectional(chairLiftsProperty(), new NumberStringConverter());
    }


    //resize by scaling
    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        resize();
    }

    private void resize() {
        Insets padding = getPadding();
        double availableWidth = getWidth() - padding.getLeft() - padding.getRight();
        double availableHeight = getHeight() - padding.getTop() - padding.getBottom();

        double width = Math.max(Math.min(Math.min(availableWidth, availableHeight * ASPECT_RATIO), MAXIMUM_WIDTH), MINIMUM_WIDTH);

        double scalingFactor = width / ARTBOARD_WIDTH;

        if (availableWidth > 0 && availableHeight > 0) {
            relocateDrawingPaneCentered();
            drawingPane.setScaleX(scalingFactor);
            drawingPane.setScaleY(scalingFactor);
        }
    }

    private void relocateDrawingPaneCentered() {
        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, (getHeight() - ARTBOARD_HEIGHT) * 0.5);
    }

    private void relocateDrawingPaneCenterBottom(double scaleY, double paddingBottom) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double visualSpace = getHeight() - visualHeight;
        double y = visualSpace + (visualHeight - ARTBOARD_HEIGHT) * 0.5 - paddingBottom;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    private void relocateDrawingPaneCenterTop(double scaleY, double paddingTop) {
        double visualHeight = ARTBOARD_HEIGHT * scaleY;
        double y = (visualHeight - ARTBOARD_HEIGHT) * 0.5 + paddingTop;

        drawingPane.relocate((getWidth() - ARTBOARD_WIDTH) * 0.5, y);
    }

    private String colorToCss(final Color color) {
        return color.toString().replace("0x", "#");
    }


    // compute sizes

    @Override
    protected double computeMinWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return MINIMUM_WIDTH + horizontalPadding;
    }

    @Override
    protected double computeMinHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return MINIMUM_HEIGHT + verticalPadding;
    }

    @Override
    protected double computePrefWidth(double height) {
        Insets padding = getPadding();
        double horizontalPadding = padding.getLeft() + padding.getRight();

        return ARTBOARD_WIDTH + horizontalPadding;
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets padding = getPadding();
        double verticalPadding = padding.getTop() + padding.getBottom();

        return ARTBOARD_HEIGHT + verticalPadding;
    }


    //Custom Panel Components


    private Pane putGondola(double x, double y) {
        Rectangle gondolaBody = new Rectangle(1.64907652, 25.3957784, 26.8799472, 32.9815303);
        gondolaBody.getStyleClass().addAll("gondola-body", "stroke");
        gondolaBody.setArcHeight(10);
        gondolaBody.setArcWidth(10);
        Rectangle gondolaWindow = new Rectangle(1.64907652, 35.4551451, 26.8799472, 9.72955145);
        gondolaWindow.getStyleClass().addAll("gondola-window", "stroke");
        SVGPath path = new SVGPath();
        path.setContent("M15.0890501,25.1484169 L15.0890501,11.7084433");
        path.getStyleClass().add("stroke");
        Circle holder = new Circle(15.0890501, 5.85422164, 5.85422164);
        holder.getStyleClass().addAll("lift-connector", "stroke");

        Pane pane = new Pane();
        pane.getChildren().addAll(gondolaBody, gondolaWindow, path, holder);
        pane.setTranslateX(x);
        pane.setTranslateY(y + 2 * DISTANCE_BETWEEN_LIFTS);
        return pane;
    }

    private Pane putChairlift(double x, double y) {
        SVGPath handRail = new SVGPath();
        handRail.setContent("M5.85422164,11.7908971 L5.85422164,27.8693931");
        handRail.getStyleClass().add("stroke");

        SVGPath chairPart1 = new SVGPath();
        chairPart1.setContent("M17.8924802,43.3707124 L17.8924802,55.0791557");
        chairPart1.getStyleClass().add("chair-lift-stroke");

        SVGPath chairPart2 = new SVGPath();
        chairPart2.setContent("M5.85422164,27.8693931 L5.85422164,55.0791557");
        chairPart2.getStyleClass().add("chair-lift-stroke");

        SVGPath chairPart3 = new SVGPath();
        chairPart3.setContent("M6.01912929,43.3707124 L17.974934,43.3707124");
        chairPart3.getStyleClass().add("chair-lift-stroke");

        Circle holder = new Circle(5.85422164, 5.85422164, 5.85422164);
        holder.getStyleClass().addAll("lift-connector", "stroke");

        Pane pane = new Pane();
        pane.getChildren().addAll(handRail, chairPart1, chairPart2, chairPart3, holder);
        pane.setTranslateX(x);
        pane.setTranslateY(y + DISTANCE_BETWEEN_LIFTS);
        return pane;
    }

    private Pane putDragLift(double x, double y) {

        SVGPath handRail = new SVGPath();
        handRail.setContent("M19.5415567,45.3496042 L19.5415567,11.7084433");
        handRail.getStyleClass().add("stroke");

        SVGPath bottomBar = new SVGPath();
        bottomBar.setContent("M38.341029,45.3496042 C32.060796,46.8872207 25.7805629,47.6560289 19.5003298,47.6560289 C13.2200967,47.6560289 6.93986368,46.8872207 0.659630607,45.3496042");
        bottomBar.getStyleClass().add("drag-lift-stroke");

        SVGPath accent1 = new SVGPath();
        accent1.setContent("M2.30870712,45.3496042 L-0.98944591,45.3496042");
        accent1.getStyleClass().add("drag-lift-stroke");
        accent1.setRotate(-75);

        SVGPath accent2 = new SVGPath();
        accent2.setContent("M40.9795515,45.3496042 L37.6813984,45.3496042");
        accent2.getStyleClass().add("drag-lift-stroke");
        accent2.setRotate(-99);

        Circle holder = new Circle(19.5415567, 5.85422164, 5.85422164);
        holder.getStyleClass().addAll("lift-connector", "stroke");

        Pane pane = new Pane();
        pane.getChildren().addAll(handRail, bottomBar, accent1, accent2, holder);
        pane.setTranslateX(x);
        pane.setTranslateY(y);
        return pane;
    }


    public double getDragLifts() {
        return dragLifts.get();
    }

    public DoubleProperty dragLiftsProperty() {
        return dragLifts;
    }

    public void setDragLifts(double dragLifts) {
        this.dragLifts.set(dragLifts);
    }

    public double getChairLifts() {
        return chairLifts.get();
    }

    public DoubleProperty chairLiftsProperty() {
        return chairLifts;
    }

    public void setChairLifts(double chairLifts) {
        this.chairLifts.set(chairLifts);
    }

    public double getGondolas() {
        return gondolas.get();
    }

    public DoubleProperty gondolasProperty() {
        return gondolas;
    }

    public void setGondolas(double gondolas) {
        this.gondolas.set(gondolas);
    }

    public ArrayList<Pane> getGondolasLiftList() {
        return gondolasLiftList;
    }

    public void setGondolasLiftList(ArrayList<Pane> gondolasLiftList) {
        this.gondolasLiftList = gondolasLiftList;
    }

    public ArrayList<Pane> getChairLiftList() {
        return chairLiftList;
    }

    public void setChairLiftList(ArrayList<Pane> chairLiftList) {
        this.chairLiftList = chairLiftList;
    }

    public ArrayList<Pane> getDragLiftList() {
        return dragLiftList;
    }

    public void setDragLiftList(ArrayList<Pane> dragLiftList) {
        this.dragLiftList = dragLiftList;
    }
}
