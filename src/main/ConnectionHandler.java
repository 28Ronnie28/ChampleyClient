package main;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.*;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.List;

public class ConnectionHandler {

    public static final int PORT = 1521;
    public static String LOCAL_ADDRESS = "127.0.0.1"; //TODO "pcuniverse.ddns.net"
    public UserObservable user = new UserObservable(null);
    public volatile ObservableList<Supplier> suppliers = FXCollections.observableArrayList();
    public volatile ObservableList<Product> products = FXCollections.observableArrayList();
    public volatile ObservableList<DataFile> quotations = FXCollections.observableArrayList();
    public volatile ObservableList<DataFile> invoices = FXCollections.observableArrayList();
    public volatile ObservableList<DataFile> documents = FXCollections.observableArrayList();
    public volatile ObservableList<String> categories = FXCollections.observableArrayList();
    public volatile ObservableList<Object> outputQueue = FXCollections.observableArrayList();
    public volatile ObservableList<Object> inputQueue = FXCollections.observableArrayList();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Boolean logOut = false;

    public ConnectionHandler() {
        connect();
    }

    //<editor-fold desc="Connection">
    private void connect() {
        System.out.println("Trying to connect to local server...");
        try {
            //System.setProperty("javax.net.ssl.trustStore", Display.APPLICATION_FOLDER + "/studentlive.store");//TODO
            /*socket = SSLSocketFactory.getDefault().createSocket();
            socket.connect(new InetSocketAddress(LOCAL_ADDRESS, PORT), 1000);*/
            socket = new Socket(LOCAL_ADDRESS, PORT);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            System.out.println("Socket is connected");
            new InputProcessor().start();
            new OutputProcessor().start();
        } catch (Exception ex) {
            UserNotification.showErrorMessage("Connection Error", "Failed to connect to Nomdla Enterprise Servers! (" + LOCAL_ADDRESS + ")\nPlease check your network connection and try again!");
            System.out.println("Exiting..");
            System.exit(0);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Commands">
    public Boolean authorise(String username, String password) {
        outputQueue.add("au:" + username + ":" + password);
        return getStringReply("au:");
    }

    public Boolean changePassword(String prevPassword, String newPassword) {
        outputQueue.add("cp:" + prevPassword + ":" + newPassword);
        return getStringReply("cp:");
    }

    public void forgotPassword(String email) {
        outputQueue.add("fsp:" + email);
    }

    public void deleteFile(String fileType, String fileName) {
        new File(Main.LOCAL_CACHE + "/" + fileType + "/" + fileName).delete();
        updateSavedFiles();
    }

    public Boolean sendEmail(String email, String emailSubject, String emailMessage, String documentType, String document){
        outputQueue.add("se:" + email + ":" + emailSubject + ":" + emailMessage + ":" + documentType + ":" + document);
        return getStringReply("se:");
    }

    public void logOut() {
        sendData("lgt:");
        logOut = true;
    }

    public void sendData(Object data) {
        try {
            objectOutputStream.writeObject(data);
            objectOutputStream.flush();
            System.out.println("Sent data: " + data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object getReply() {
        try {
            Object input;
            while ((input = objectInputStream.readObject()) == null) ;
            return input;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (!logOut) {
                System.exit(0);
            }
        }
        return null;
    }

    public void updateSavedFiles() {
        /*Boolean updated = false;
        for (ClassResultAttendance car : user.getUser().getClassResultAttendances()) {
            for (DataFile cf : car.getUserClass().getFiles()) {
                File f;
                if ((f = new File(Display.LOCAL_CACHE + "/" + cf.getFileType() + "/" + cf.getFileName())).exists() && f.length() == cf.getFileLength()) {
                    if (cf.getValue() != 1) {
                        cf.setValue(1);
                        updated = true;
                    }
                } else if (cf.getValue() == 1) {
                    cf.setValue(0);
                    updated = true;

                }
            }
            try {
                File classFolder = new File(Display.LOCAL_CACHE + "/" + car.getStudentClass().getClassID());
                for (File file : classFolder.listFiles()) {
                    Boolean found = false;
                    for (DataFile cf : car.getUserClass().getFiles()) {
                        if (cf.getFileName().equals(file.getName()) && cf.getFileLength() == file.length()) {
                            found = true;
                        }
                    }
                    if (!found) {
                        Files.delete(file.toPath());
                        System.out.println("Deleted file: " + file.getName());
                    }
                }
            } catch (Exception ex) {
            }
        }
        if (updated) {
            Platform.runLater(() -> user.update());
            System.out.println("Files Updated");
        }*/
    }

    public Boolean getStringReply(String startsWith) {
        Boolean result;
        Object objectToRemove;
        ReturnResult:
        while (true) {
            for (int i = 0; i < inputQueue.size(); i++) {
                Object object = inputQueue.get(i);
                if (object instanceof String) {
                    String in = (String) object;
                    if (in.startsWith(startsWith)) {
                        objectToRemove = object;
                        result = in.charAt(startsWith.length()) == 'y';
                        break ReturnResult;
                    }
                }
            }
        }
        inputQueue.remove(objectToRemove);
        return result;
    }

    public Boolean userInitialized() {
        return user.getUser() != null;
    }

    private class InputProcessor extends Thread {
        public void run() {
            while (!logOut) {
                Object input;
                if ((input = getReply()) != null) {
                    if (input instanceof User) {
                        user.setUser((User) input);
                        updateSavedFiles();
                        user.update();
                        System.out.println("Updated User");
                    } else if (input instanceof List<?>) {
                        List list = (List) input;
                        if (!list.isEmpty() && list.get(0) instanceof Supplier) {
                            suppliers.clear();
                            if (!((Supplier) list.get(0)).getName().equals("NoSuppliers")) {
                                suppliers.addAll(list);
                            }
                            System.out.println("Updated Suppliers");
                        } else if (!list.isEmpty() && list.get(0) instanceof Product) {
                            products.clear();
                            if (!((Product) list.get(0)).getDescription().equals("NoProducts")) {
                                products.addAll(list);
                            }
                            System.out.println("Updated Products");
                        } else if (!list.isEmpty() && list.get(0) instanceof DataFile) {
                            if (((DataFile) list.get(0)).getFileType().matches("Quotations")) {
                                quotations.clear();
                                if (!((DataFile) list.get(0)).getFileName().equals("NoQuotation")) {
                                    quotations.addAll(list);
                                }
                                System.out.println("Updated Quotations (" + quotations.size() + ")");
                            } else if (((DataFile) list.get(0)).getFileType().matches("Invoices")) {
                                invoices.clear();
                                if (!((DataFile) list.get(0)).getFileName().equals("NoInvoices")) {
                                    invoices.addAll(list);
                                }
                                System.out.println("Updated Invoices (" + invoices.size() + ")");
                            } else if (((DataFile) list.get(0)).getFileType().matches("Documents")) {
                                //documents.clear();
                                //documents.set
                                if (!((DataFile) list.get(0)).getFileName().equals("NoDocuments")) {
                                    documents.setAll(list);
                                }
                                System.out.println("Updated Documents (" + documents.size() + ")");
                            }
                        } else if (!list.isEmpty() && list.get(0) instanceof String) {
                            categories.clear();
                            if (!list.get(0).equals("NoCategories")) {
                                categories.addAll(list);
                            }
                            System.out.println("Updated Categories");
                        }
                    } else {
                        inputQueue.add(input);
                    }
                }
            }
        }
    }

    private class OutputProcessor extends Thread {
        public void run() {
            while (true) {
                if (!outputQueue.isEmpty()) {
                    sendData(outputQueue.get(0));
                    outputQueue.remove(0);
                }
            }
        }
    }

    public class FileDownloader extends Thread {

        public volatile IntegerProperty size;
        public volatile DoubleProperty progress;
        volatile BooleanProperty done = new SimpleBooleanProperty(false);
        DataFile file;
        byte[] bytes;
        File f;

        public FileDownloader(DataFile file) {
            this.file = file;
            bytes = new byte[file.getFileLength()];
            size = new SimpleIntegerProperty(0);
            progress = new SimpleDoubleProperty(0);
            f = new File(Main.LOCAL_CACHE + "/" + file.getFileType() + "/" + file.getFileName() + file.getFileExtension());
        }

        @Override
        public void run() {
            outputQueue.add("gf:" + file.getFileType() + ":" + file.getFileName());
            Done:
            while (true) {
                FilePart filePartToRemove = null;
                BreakSearch:
                for (int i = inputQueue.size() - 1; i > -1; i--) {
                    try {
                        Object object = inputQueue.get(i);
                        if (object instanceof FilePart) {
                            FilePart filePart = (FilePart) object;
                            if (filePart.getFileName().equals(file.getFileName())) {
                                filePartToRemove = filePart;
                                break BreakSearch;
                            }
                        }
                    } catch (IndexOutOfBoundsException ex) {
                    }
                }
                if (filePartToRemove != null) {
                    for (int i = 0; i < filePartToRemove.getFileBytes().length; i++) {
                        bytes[size.get() + i] = filePartToRemove.getFileBytes()[i];
                    }
                    size.set(size.get() + filePartToRemove.getFileBytes().length);
                    progress.set(1D * size.get() / bytes.length);
                    Platform.runLater(() -> user.update());
                    inputQueue.remove(filePartToRemove);
                }
                if (size.get() == file.getFileLength()) {
                    System.out.println("File successfully downloaded!");
                    File f = new File(Main.LOCAL_CACHE + "/" + file.getFileType() + "/" + file.getFileName());
                    f.getParentFile().mkdirs();
                    try {
                        Files.write(f.toPath(), bytes);
                        updateSavedFiles();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    done.setValue(true);
                    break Done;
                }
            }
        }
    }

}
