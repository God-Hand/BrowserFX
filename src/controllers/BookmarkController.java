package controllers;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import database.BookMarksDataBase;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Callback;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

public class BookmarkController implements Initializable{

    @FXML private TreeView<?> treeView;
    @FXML private JFXTextField search;
    @FXML private JFXTreeTableView<BookmarkStoreView> table;

    private JFXTreeTableColumn<BookmarkStoreView, String> nameCol = new JFXTreeTableColumn<BookmarkStoreView, String>("Name");
    private JFXTreeTableColumn<BookmarkStoreView, String> linkCol = new JFXTreeTableColumn<BookmarkStoreView, String>("Link");
    private JFXTreeTableColumn<BookmarkStoreView, String> timeCol = new JFXTreeTableColumn<BookmarkStoreView, String>("Time");
    private JFXTreeTableColumn<BookmarkStoreView, String> dateCol = new JFXTreeTableColumn<BookmarkStoreView, String>("Date");

    private ObservableList<String> folders = BookMarksDataBase.folders();
    TreeItem parentFolder = new TreeItem<>("All Bookmarks");

    public BookmarkController() {
        // TODO Auto-generated constructor stub
    }

    public void addListInTable(ObservableList list) {
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<BookmarkStoreView, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<BookmarkStoreView, String> param) {
                        return param.getValue().getValue().name;
                    }
                });
        linkCol.setPrefWidth(350);
        linkCol.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<BookmarkStoreView, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<BookmarkStoreView, String> param) {
                        return param.getValue().getValue().link;
                    }
                });
        timeCol.setPrefWidth(100);
        timeCol.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<BookmarkStoreView, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<BookmarkStoreView, String> param) {
                        return param.getValue().getValue().time;
                    }
                });
        dateCol.setPrefWidth(100);
        dateCol.setCellValueFactory(
                new Callback<TreeTableColumn.CellDataFeatures<BookmarkStoreView, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<BookmarkStoreView, String> param) {
                        return param.getValue().getValue().date;
                    }
                });

        final TreeItem<BookmarkStoreView> root = new RecursiveTreeItem<BookmarkStoreView>(list,
                RecursiveTreeObject::getChildren);
        table.getColumns().setAll(nameCol, linkCol, timeCol, dateCol);
        table.setRoot(root);
        table.setShowRoot(false);
    }

    public void addFolders(){
        parentFolder.getChildren().clear();
        folders = BookMarksDataBase.folders();
        for(int i=0 ; i< folders.size();i++){
            parentFolder.getChildren().add(new TreeItem<>(folders.get(i)));
        }
        treeView.setRoot(parentFolder);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addFolders();
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->{
            if(newValue == parentFolder){
                addFolders();
            }
            ResultSet bookmarks = BookMarksDataBase.showBookmarks((String) newValue.getValue());
            ObservableList<BookmarkStoreView> list = FXCollections.observableArrayList();
            try {
                while (bookmarks.next()) {
                    list.add(new BookmarkStoreView(bookmarks.getString(1), bookmarks.getString(2),
                            bookmarks.getString(3), bookmarks.getString(4)));
                }
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
            }
            addListInTable(list);
        });
        treeView.getSelectionModel().select(0);
    }
}

class BookmarkStoreView extends RecursiveTreeObject<BookmarkStoreView> {
    StringProperty name;
    StringProperty link;
    StringProperty time;
    StringProperty date;
    public BookmarkStoreView( String link ,String name ,String time ,String date) {
        this.name = new SimpleStringProperty(name);
        this.link = new SimpleStringProperty(link);
        this.time = new SimpleStringProperty(time);
        this.date = new SimpleStringProperty(date);
    }
}