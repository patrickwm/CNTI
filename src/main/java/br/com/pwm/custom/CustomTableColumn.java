package br.com.pwm.custom;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TableColumn;

public class CustomTableColumn<s> extends TableColumn<s, s> {
    private SimpleDoubleProperty percentWidth = new SimpleDoubleProperty();
    private SimpleBooleanProperty isPercent = new SimpleBooleanProperty(false);

    public CustomTableColumn(String columnName) {
        super(columnName);
    }

    public SimpleDoubleProperty percentWidth() {
        return percentWidth;
    }

    public double getPercentWidth() {
        return percentWidth.get();
    }

    public void setPercentWidth(double percentWidth) {
        setIsPercent(true);
        this.percentWidth.set(percentWidth);
    }

    public void setIsPercent(boolean isPercent) {
        this.isPercent.setValue(true);
    }

    public boolean isPercent() {
        return this.isPercent.get();
    }


}

