package ch.fhnw.cuie.project.template_simplecontrol.demo;

import ch.fhnw.cuie.project.template_simplecontrol.Lifter;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.NumberStringConverter;

class DemoPane extends BorderPane {

    private final PresentationModel pm;

    // declare the custom control
    private Lifter cc;

    // all controls
    private TextField dragLifts;
    private TextField chairLifts;
    private TextField gondolas;

    public DemoPane(PresentationModel pm) {
        this.pm = pm;
        initializeControls();
        layoutControls();
        setupBindings();
    }

    private void initializeControls() {
        setPadding(new Insets(10));

        cc = new Lifter();

        dragLifts = new TextField();
        chairLifts = new TextField();
        gondolas = new TextField();
    }

    private void layoutControls() {
        VBox controlPane = new VBox(new Label("Lifter Properties"),new Label("Drag Lifts"),
                                    dragLifts, new Label("Chair Lifts"), chairLifts, new Label("Gondolas"), gondolas);
        controlPane.setPadding(new Insets(0, 50, 0, 50));
        controlPane.setSpacing(10);

        setCenter(cc);
        setRight(controlPane);
    }

    private void setupBindings() {
        dragLifts.textProperty().bindBidirectional(pm.dragLiftsProperty(), new NumberStringConverter());
        chairLifts.textProperty().bindBidirectional(pm.chairLiftsProperty(), new NumberStringConverter());
        gondolas.textProperty().bindBidirectional(pm.gondolasProperty(), new NumberStringConverter());

        cc.gondolasProperty().bindBidirectional(pm.gondolasProperty());
        cc.dragLiftsProperty().bindBidirectional(pm.dragLiftsProperty());
        cc.chairLiftsProperty().bindBidirectional(pm.chairLiftsProperty());

    }

}
