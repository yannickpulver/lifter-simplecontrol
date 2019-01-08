package ch.fhnw.cuie.project.template_simplecontrol.demo;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class PresentationModel {
    private final IntegerProperty chairLifts   = new SimpleIntegerProperty(5);
    private final IntegerProperty dragLifts    = new SimpleIntegerProperty(5);
    private final IntegerProperty gondolas     = new SimpleIntegerProperty(4);

    public int getChairLifts() {
        return chairLifts.get();
    }

    public IntegerProperty chairLiftsProperty() {
        return chairLifts;
    }

    public void setChairLifts(int chairLifts) {
        this.chairLifts.set(chairLifts);
    }

    public int getDragLifts() {
        return dragLifts.get();
    }

    public IntegerProperty dragLiftsProperty() {
        return dragLifts;
    }

    public void setDragLifts(int dragLifts) {
        this.dragLifts.set(dragLifts);
    }

    public int getGondolas() {
        return gondolas.get();
    }

    public IntegerProperty gondolasProperty() {
        return gondolas;
    }

    public void setGondolas(int gondolas) {
        this.gondolas.set(gondolas);
    }
}
