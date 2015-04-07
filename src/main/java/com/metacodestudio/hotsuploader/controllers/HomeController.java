package com.metacodestudio.hotsuploader.controllers;

import com.metacodestudio.hotsuploader.files.FileHandler;
import com.metacodestudio.hotsuploader.files.ReplayFile;
import com.metacodestudio.hotsuploader.files.Status;
import com.metacodestudio.hotsuploader.providers.HotSLogs;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.paint.Paint;

import javax.annotation.PostConstruct;
import java.util.Map;

@ViewController(value = "../views/Home.fxml", iconPath = "../../../../images/logo.png", title = "HotSLogs Uploader")
public class HomeController {

    @FXMLViewFlowContext
    private ViewFlowContext viewFlowContext;

    @FXML
    private TitledPane newReplaysTitlePane;
    @FXML
    private TitledPane uploadedReplaysTitlePane;
    @FXML
    private TitledPane exceptionReplaysTitlePane;

    @FXML
    private ListView<ReplayFile> newReplaysView;

    @FXML
    private ListView<ReplayFile> uploadedReplaysView;

    @FXML
    private ListView<ReplayFile> exceptionReplaysView;

    @FXML
    private Label status;

    private FileHandler fileHandler;


    @PostConstruct
    public void init() {
        fileHandler = viewFlowContext.getRegisteredObject(FileHandler.class);
        bindLists();
        setFileHandlerOnSucceeded();
        fileHandler.start();
    }

    private void setFileHandlerOnSucceeded() {
        fileHandler.setOnSucceeded(event -> {
            if (HotSLogs.isMaintenance()) {
                setMaintenance();
            } else if (fileHandler.isIdle()) {
                setIdle();
            } else {
                setUploading();
            }

            fileHandler.restart();
        });
    }

    private void bindLists() {
        Map<Status, ObservableList<ReplayFile>> fileMap = fileHandler.getFileMap();
        final String newReplaysTitle = newReplaysTitlePane.textProperty().get();
        ObservableList<ReplayFile> newReplays = fileMap.get(Status.NEW);
        newReplays.addListener((ListChangeListener<ReplayFile>) c -> newReplaysTitlePane.setText(newReplaysTitle + " (" + newReplays.size() + ")"));
        newReplaysView.setItems(newReplays);
        final String uploadedReplaysTitle = uploadedReplaysTitlePane.textProperty().get();
        ObservableList<ReplayFile> uploadedReplays = fileMap.get(Status.UPLOADED);
        newReplays.addListener((ListChangeListener<ReplayFile>) c -> uploadedReplaysTitlePane.setText(uploadedReplaysTitle + " (" + uploadedReplays.size() + ")"));
        uploadedReplaysView.setItems(uploadedReplays);
        final String exceptionReplaysTitle = exceptionReplaysTitlePane.textProperty().get();
        ObservableList<ReplayFile> exceptionReplays = fileMap.get(Status.EXCEPTION);
        newReplays.addListener((ListChangeListener<ReplayFile>) c -> exceptionReplaysTitlePane.setText(exceptionReplaysTitle + " (" + exceptionReplays.size() + ")"));
        exceptionReplaysView.setItems(exceptionReplays);
    }

    private void setIdle() {
        status.setText("Idle");
        status.textFillProperty().setValue(Paint.valueOf("#00008f"));
    }

    private void setMaintenance() {
        status.setText("Maintenance");
        status.textFillProperty().setValue(Paint.valueOf("#FF0000"));
    }

    private void setUploading() {
        status.setText("Uploading");
        status.textFillProperty().setValue(Paint.valueOf("#008f00"));
    }

}