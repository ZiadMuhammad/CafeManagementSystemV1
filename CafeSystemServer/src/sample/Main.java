package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Main extends Application {

    Connection myConn;
    Statement myStmt;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

            myConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/cafe", "ziad", "1234");
            myStmt = myConn.createStatement();

//            while(myRs.next()) {
//                System.out.println(myRs.getString("user_name") + " , " + myRs.getString("pass"));
//            }

            VBox vBox = new VBox();
            vBox.setSpacing(10);

            HBox hBox1 = new HBox();
            HBox hBox2 = new HBox();
            HBox hBox3 = new HBox();

            Button startServer = new Button("Start Server");
            Button getConnection = new Button("Receive Connection");
            TextArea textArea = new TextArea();
            textArea.setMaxWidth(300);

            hBox1.getChildren().add(startServer);
            hBox2.getChildren().add(textArea);
            hBox3.getChildren().add(getConnection);

            vBox.getChildren().add(hBox2);
            vBox.getChildren().add(hBox1);
            vBox.getChildren().add(hBox3);

            hBox1.setAlignment(Pos.CENTER);
            hBox3.setAlignment(Pos.CENTER);

            startServer.setOnAction(e -> {
                textArea.setText("Server Started...");
                new Thread(() -> {
                    try {

                        ServerSocket serverSocket = new ServerSocket(8000);

                        while(true) {
                            Socket socket = serverSocket.accept();

                            new Thread(new HandleAClient(socket)).start();
                        }

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    }).start();
            });

            getConnection.setOnAction(e -> {
                textArea.setText(textArea.getText() + "\nConnection Received...");
            });


            primaryStage.setTitle("CafeServer.exe");
            primaryStage.setScene(new Scene(vBox, 300, 275));
            primaryStage.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
     }


    public static void main(String[] args) {
        launch(args);
    }

    class HandleAClient implements Runnable {
        private Socket socket;

        public HandleAClient(Socket socket) {
            this.socket = socket;
        }

        public synchronized void run() {
            try {
                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                int clientActionID = 0;

                while (true) {
                    clientActionID = inputFromClient.readInt();

                    //Send Profit Analysis
                    if(clientActionID == 12) {
                        ArrayList<String> purchasedLastMonth = new ArrayList<>();
                        ArrayList<String> itemsPurchasedLM = new ArrayList<>();
                        ArrayList<Integer> qty = new ArrayList<>();

                        int profitSumLM = 0;

                        try {
                            ResultSet myRs = myStmt.executeQuery("SELECT * FROM invoices WHERE  purchase_date > (NOW() - INTERVAL 1 MONTH);");
                            while(myRs.next()) {
                                purchasedLastMonth.add(myRs.getString("id"));
                            }

                            myRs = myStmt.executeQuery("SELECT * FROM invoiceItem");

                            while(myRs.next()) {
                                if(purchasedLastMonth.contains(myRs.getString("invoice_id"))) {
                                    if(itemsPurchasedLM.contains(myRs.getString("product_id"))) {
                                        qty.set(itemsPurchasedLM.indexOf(myRs.getString("product_id")), qty.get(itemsPurchasedLM.indexOf(myRs.getString("product_id"))) + Integer.parseInt(myRs.getString("qty")));
                                    }
                                    else {
                                        itemsPurchasedLM.add(myRs.getString("product_id"));
                                        qty.add(Integer.parseInt(myRs.getString("qty")));
                                    }
                                }
                            }

                            myRs = myStmt.executeQuery("SELECT * FROM products");

                            while(myRs.next()) {
                                if(itemsPurchasedLM.contains(myRs.getString("id"))) {
                                    profitSumLM += Integer.parseInt(myRs.getString("profit_value")) * qty.get(itemsPurchasedLM.indexOf(myRs.getString("id")));
                                }
                            }

                            outputToClient.writeInt(profitSumLM);
                            System.out.println(profitSumLM);

//                            for(String ix : itemsPurchasedLM) {
//                                System.out.println(ix);
//                            }
//                            for(Integer ix : qty) {
//                                System.out.println(ix);
//                            }


                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    //Delete Salesman
                    if(clientActionID == 11) {
                        String userName = inputFromClient.readUTF();

                        try {
                            PreparedStatement stmt = myConn.prepareStatement("DELETE FROM USERS WHERE user_name = ?");
                            stmt.setString(1, userName);
                            stmt.executeUpdate();

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    //Get Salesman
                    if(clientActionID == 10) {
                        try {
                            ResultSet myRs = myStmt.executeQuery("select * from users");

                            while(myRs.next()) {
                                if(myRs.getString("acc_type").equals("salesman")) {
                                    outputToClient.writeUTF(myRs.getString("user_name"));
                                }
                            }
                            outputToClient.writeUTF("done");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                    }
                    //Send invoice
                    if(clientActionID == 9) {

                        try {
                            ResultSet myRs2 = myStmt.executeQuery("select * from products");

                            while(myRs2.next()) {
                                outputToClient.writeUTF(myRs2.getString("id"));
                                outputToClient.writeUTF(myRs2.getString("item_name"));
                                outputToClient.writeUTF(myRs2.getString("total_price"));
                            }

                            outputToClient.writeUTF("done");

                            ResultSet myRs = myStmt.executeQuery("select * from invoiceItem");

                            String invoiceID = inputFromClient.readUTF();

                            while(myRs.next()) {
                                if(invoiceID.equals(myRs.getString("invoice_id"))) {
                                    outputToClient.writeUTF(myRs.getString("item_no"));
                                    outputToClient.writeUTF(myRs.getString("qty"));
                                    outputToClient.writeUTF(myRs.getString("product_id"));
                                }
                            }
                            outputToClient.writeUTF("end of invoice...");

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    //Receive invoice items
                    if(clientActionID == 8) {
                        try {
                            ResultSet myRs = myStmt.executeQuery("select * from invoices");
                            while (myRs.next()) {
                                outputToClient.writeUTF(myRs.getString("id"));
                            }
                            outputToClient.writeUTF("done");
                        } catch(SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    //Order
                    if(clientActionID == 7) {
                        ArrayList<String> itemAlreadyExists = new ArrayList<>();
                        ArrayList<Integer> itemQty = new ArrayList<>();
                        ArrayList<Integer> itemsID = new ArrayList<>();

                        String userName = inputFromClient.readUTF();
                        String messageSent = inputFromClient.readUTF();;

                        while(!messageSent.equals("done")) {
                            System.out.println(messageSent);
                            if(itemAlreadyExists.contains(messageSent)) {
                                itemQty.set(itemAlreadyExists.indexOf(messageSent), itemQty.get(itemAlreadyExists.indexOf(messageSent)) + 1);
                            }
                            else {
                                itemAlreadyExists.add(messageSent);
                                itemQty.add(1);
                            }
                            messageSent = inputFromClient.readUTF();
                        }
                        System.out.println("Bought by " + userName);

                        try {
                            ResultSet myRs = myStmt.executeQuery("SELECT * FROM users");
                            String userID = "";
                            while(myRs.next()) {
                                if(myRs.getString("user_name").equals(userName)) {
                                    userID = myRs.getString("id");
                                }
                            }
                            ResultSet myRs2 = myStmt.executeQuery("SELECT * FROM products");

                            int i = 0;

                            while(i < itemAlreadyExists.size()) {
                                itemsID.add(1);
                                i++;
                            }

                            System.out.println("here");

                            while (myRs2.next()) {
                                if(itemAlreadyExists.contains(myRs2.getString("item_name"))) {
                                    itemsID.set(itemAlreadyExists.indexOf(myRs2.getString("item_name")), Integer.parseInt(myRs2.getString("id")));
                                }
                                System.out.println("x");
                            }

                            for(Integer ix : itemQty) {
                                System.out.println(ix);
                            }

                            System.out.println(userID);


                            PreparedStatement stmt = myConn.prepareStatement("INSERT INTO invoices (buyer_id) \n"
                                    + "values(?);");
                            stmt.setString(1, userID);

                            stmt.executeUpdate();

                            int j = 0;

                            ResultSet myRs4 = myStmt.executeQuery("SELECT LAST_INSERT_ID()");
                            while(myRs4.next()) {
                                j = Integer.parseInt(myRs4.getString("LAST_INSERT_ID()"));
                            }

                            int y = 1;

                            for(Integer ix : itemsID) {
                                stmt = myConn.prepareStatement("INSERT INTO invoiceItem(invoice_id, product_id, item_no) \n"
                                                                + "values(?, ?, ?)");

                                stmt.setInt(1, j);
                                stmt.setInt(2, ix);
                                stmt.setInt(3, y);
                                y++;
                                stmt.executeUpdate();
                            }
                            y = 0;

                            for(Integer ix : itemQty) {
                                stmt = myConn.prepareStatement("UPDATE invoiceItem SET qty = ? WHERE product_id = ?");
                                stmt.setInt(1, ix);
                                stmt.setInt(2, itemsID.get(y));
                                y++;
                                stmt.executeUpdate();
                            }


                        } catch (SQLException esq) {
                            esq.printStackTrace();
                        }
                    }
                     //Send Items to menu
                    if (clientActionID == 6) {
                        try {
                            ResultSet myRs2 = myStmt.executeQuery("select * from products");
                            while (myRs2.next()) {
                                if(myRs2.getString("available").equals("yes") || myRs2.getString("available").equals("Yes")) {
                                    outputToClient.writeUTF(myRs2.getString("category"));
                                    outputToClient.writeUTF(myRs2.getString("item_name"));
                                    outputToClient.writeUTF(myRs2.getString("total_price"));
                                }
                            }
                            outputToClient.writeUTF("done");
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }

                    }

                    //Add Item
                    if (clientActionID == 5) {
                        String setCategory = inputFromClient.readUTF();
                        String setItemName = inputFromClient.readUTF();
                        int salesPrice = inputFromClient.readInt();
                        int profitValue = inputFromClient.readInt();
                        int totalValue = inputFromClient.readInt();
                        String setAvailable = inputFromClient.readUTF();

                        try {
                            PreparedStatement stmt = myConn.prepareStatement("INSERT INTO products (category, item_name, sales_price, profit_value, total_price, available) \n"
                                    + "values(?, ?, ?, ?, ?, ?);");

                            stmt.setString(1, setCategory);
                            stmt.setString(2, setItemName);
                            stmt.setInt(3, salesPrice);
                            stmt.setInt(4, profitValue);
                            stmt.setInt(5, totalValue);
                            stmt.setString(6, setAvailable);

                            stmt.executeUpdate();

                        } catch (SQLException esq) {
                            esq.printStackTrace();
                        }

                    }

                    //Update Values of Received Item to the Received Values
                    if (clientActionID == 4) {
                        String foodName = inputFromClient.readUTF();
                        String setCategory = inputFromClient.readUTF();
                        String setItemName = inputFromClient.readUTF();
                        int salesPrice = inputFromClient.readInt();
                        int profitValue = inputFromClient.readInt();
                        int totalValue = inputFromClient.readInt();
                        String setAvailable = inputFromClient.readUTF();

                        try {
                            PreparedStatement stmt = myConn.prepareStatement("UPDATE products SET category = ?, item_name = ?, sales_price = ?, profit_value = ?, total_price = ?, available = ?\n" +
                                    "WHERE item_name = ?; ");

                            stmt.setString(1, setCategory);
                            stmt.setString(2, setItemName);
                            stmt.setInt(3, salesPrice);
                            stmt.setInt(4, profitValue);
                            stmt.setInt(5, totalValue);
                            stmt.setString(6, setAvailable);
                            stmt.setString(7, foodName);

                            stmt.executeUpdate();

                        } catch (SQLException esq) {
                            esq.printStackTrace();
                        }
                    }

                    //Send Item Names to the Client
                    if (clientActionID == 3) {
                        try {
                            ResultSet myRs2 = myStmt.executeQuery("select * from products");

                            while (myRs2.next()) {
                                    outputToClient.writeUTF(myRs2.getString("item_name"));
                            }
                            outputToClient.writeUTF("done");
                        } catch (SQLException sqe) {
                            sqe.printStackTrace();
                        }
                    }

                    //Receive Registration Information
                    if (clientActionID == 2) {
                        String firstName = inputFromClient.readUTF();
                        String lastName = inputFromClient.readUTF();
                        int age = inputFromClient.readInt();
                        String email = inputFromClient.readUTF();
                        String user = inputFromClient.readUTF();
                        String pass = inputFromClient.readUTF();
                        String acc = inputFromClient.readUTF();
                        try {
                            PreparedStatement stmt = myConn.prepareStatement("INSERT INTO users (first_name, last_name, age, email, user_name, pass, acc_type)\n" +
                                    "values(?, ?, ?, ?, ?, ?, ?); ");
                            stmt.setString(1, firstName);
                            stmt.setString(2, lastName);
                            stmt.setInt(3, age);
                            stmt.setString(4, email);
                            stmt.setString(5, user);
                            stmt.setString(6, pass);
                            stmt.setString(7, acc);

                            stmt.executeUpdate();

                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    }

                    //Recieve Login Information
                    if (clientActionID == 1) {
                        String username = inputFromClient.readUTF();
                        String password = inputFromClient.readUTF();
                        try {
                            ResultSet myRs = myStmt.executeQuery("select * from users");
                            int flag = 0;

                            while (myRs.next()) {
                                if (username.equals(myRs.getString("user_name")) && password.equals(myRs.getString("pass"))) {
                                    System.out.println("\n" + username + " signed in");
                                    outputToClient.writeUTF(myRs.getString("acc_type"));
                                    flag = 1;
                                }
                            }
                            if(flag == 0) {
                                outputToClient.writeUTF("Login Failed...");
                            }
                        } catch (SQLException sqe) {
                            sqe.printStackTrace();
                        }
                    }

                    if (clientActionID == 0) {
                        break;
                    }
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

        }


    }

}


