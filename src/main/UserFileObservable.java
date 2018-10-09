package main;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import models.DataFile;

public class UserFileObservable {

    private DataFile dataFile;
    private DoubleProperty progress;
    private IntegerProperty type;

    public UserFileObservable(DataFile dataFile) {
        this.dataFile = dataFile;
        progress = new SimpleDoubleProperty();
        type = new SimpleIntegerProperty();
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public DoubleProperty progressProperty() {
        progress.set(0);
        if (dataFile.getFileDownloader() != null) {
            progress.set(((ConnectionHandler.FileDownloader) dataFile.getFileDownloader()).progress.get());
        }
        return progress;
    }

    public IntegerProperty typeProperty() {
        type.set(dataFile.getValue());
        return type;
    }
}
