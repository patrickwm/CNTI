package br.com.pwm.custom;

import javafx.beans.binding.NumberBinding;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.util.List;
import java.util.stream.Collectors;

public class CustomTableView<s> extends StackPane {
    private TableView<s> table;

    @SuppressWarnings("rawtypes")
    public CustomTableView() {
        this.table = new TableView<s>();
        final GridPane grid = new GridPane();
        this.table.getColumns().addListener((ListChangeListener) arg0 -> changeListener(grid));
        getChildren().addAll(grid, table);
    }

    private void changeListener(GridPane grid) {
        grid.getColumnConstraints().clear();
        ColumnConstraints[] arr1 = new ColumnConstraints[CustomTableView.this.table.getColumns().size()];
        StackPane[] arr2 = new StackPane[CustomTableView.this.table.getColumns().size()];
        int i = 0;

        List<CustomTableColumn> colunas = CustomTableView.this.table
            .getColumns()
            .stream()
            .map(c -> ((CustomTableColumn) c))
            .collect(Collectors.toList());

        for (CustomTableColumn col : colunas) {
            ColumnConstraints consta = new ColumnConstraints();
            consta.setPercentWidth(col.getPercentWidth());
            StackPane sp = new StackPane();
            if (i == 0) {
                NumberBinding diff = sp.widthProperty().subtract(3.75);
                col.prefWidthProperty().bind(diff);
            } else {
                col.prefWidthProperty().bind(sp.widthProperty());
            }
            arr1[i] = consta;
            arr2[i] = sp;
            i++;
        }
        grid.getColumnConstraints().addAll(arr1);
        grid.addRow(0, arr2);
    }

    public void setItems(ObservableList<s> itens) {
        this.table.setItems(itens);
    }

    public ObservableList<s> getItems() {
        return this.table.getItems();
    }

    public TableView<s> getTableView() {
        return this.table;
    }
}
