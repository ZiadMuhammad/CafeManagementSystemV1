package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;

public class Main extends Application {

    DataOutputStream toServer;
    DataInputStream fromServer;

    @Override
    public void start(Stage primaryStage) throws Exception {


            Socket socket = new Socket("localhost", 8000);
            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());

            //Login System Design...
            VBox loginScreen = new VBox();

            HBox labelContainer = new HBox();
            HBox userNameContainer = new HBox();
            HBox passwordContainer = new HBox();
            HBox loginContainer = new HBox();
            HBox registerContainer = new HBox();

            TextField userName = new TextField();
            PasswordField password = new PasswordField();
            userName.setPromptText("Username");
            password.setPromptText("Password");

            Button login = new Button("Login");
            Button register = new Button("Register");

            Label label = new Label("Cafe Management System V.1.0");

            labelContainer.getChildren().add(label);
            userNameContainer.getChildren().add(userName);
            passwordContainer.getChildren().add(password);
            loginContainer.getChildren().add(login);
            registerContainer.getChildren().add(register);

            loginScreen.getChildren().addAll(labelContainer, userNameContainer, passwordContainer, loginContainer, registerContainer);

            loginScreen.setAlignment(Pos.CENTER);
            loginScreen.setSpacing(10);

            labelContainer.setAlignment(Pos.CENTER);
            userNameContainer.setAlignment(Pos.CENTER);
            passwordContainer.setAlignment(Pos.CENTER);
            loginContainer.setAlignment(Pos.CENTER);
            registerContainer.setAlignment(Pos.CENTER);

            Scene loginMain = new Scene(loginScreen, 300, 275);


            //Manager Screen Design
            Button manageMenu = new Button("Manage Menu");
            Button allInvoices = new Button("All Invoices");
            Button profitAnalysis = new Button("Profit Analysis");
            Button manageEmployees = new Button("Manage Employees");
            Button logOutManager = new Button("Logout");

            HBox manageMenuContainer = new HBox();
            HBox allInvoicesContainer = new HBox();
            HBox profitAnalysisContainer = new HBox();
            HBox manageEmployeesContainer = new HBox();
            HBox logOutManagerContainer = new HBox();

            manageMenuContainer.getChildren().add(manageMenu);
            manageMenuContainer.setAlignment(Pos.CENTER);
            allInvoicesContainer.getChildren().add(allInvoices);
            allInvoicesContainer.setAlignment(Pos.CENTER);
            profitAnalysisContainer.getChildren().add(profitAnalysis);
            profitAnalysisContainer.setAlignment(Pos.CENTER);
            manageEmployeesContainer.getChildren().add(manageEmployees);
            manageEmployeesContainer.setAlignment(Pos.CENTER);
            logOutManagerContainer.getChildren().add(logOutManager);
            logOutManagerContainer.setAlignment(Pos.CENTER);

            VBox vBox3 = new VBox();
            vBox3.setPadding(new Insets(10, 10, 10, 10));

            vBox3.getChildren().addAll(manageMenuContainer, allInvoicesContainer, profitAnalysisContainer,manageEmployeesContainer, logOutManagerContainer);

            Scene managerMenu = new Scene(vBox3, 200, 180);

            manageMenu.setOnAction(e -> {
                try {
//                    Socket socket = new Socket("localhost", 8000);
//                    fromServer = new DataInputStream(socket.getInputStream());
//                    toServer = new DataOutputStream(socket.getOutputStream());

                    toServer.writeInt(3);

                    VBox vBox4 = new VBox();

                    Button managerBack = new Button("Back");
                    Button managerSelect = new Button("Select");
                    Button managerView = new Button("View");
                    Button addItem = new Button("Add Item");
                    Button removeItem = new Button("Remove Item");

                    ListView<String> menuList = new ListView<>();

                    vBox4.getChildren().addAll(managerBack, managerView, managerSelect, addItem, removeItem, menuList);

                    Scene managerMenuItems = new Scene(vBox4, 300, 300);

                    primaryStage.setScene(managerMenuItems);

                    String serverMessage = "";
                    while (!serverMessage.equals("done")) {
                        serverMessage = fromServer.readUTF();
                        menuList.getItems().add(serverMessage);
                    }

                    menuList.getItems().remove("done");

                    managerBack.setOnAction(e1 -> {
                        primaryStage.setScene(managerMenu);
                    });


                    addItem.setOnAction(ev -> {
                        try {
                            VBox vBox6 = new VBox();

                            TextField setCategory2 = new TextField();
                            setCategory2.setPromptText("Category");
                            TextField setItemName2 = new TextField();
                            setItemName2.setPromptText("Item Name");
                            TextField setSalesPrice2 = new TextField();
                            setSalesPrice2.setPromptText("Sales Price");
                            TextField setProfitValue2 = new TextField();
                            setProfitValue2.setPromptText("Profit Value");

                            TextField setAvailable2 = new TextField();
                            setAvailable2.setPromptText("Availablity");
                            Button updateValues2 = new Button("Update");
                            Button back2 = new Button("Back");

                            vBox6.getChildren().addAll(setCategory2, setItemName2, setSalesPrice2, setProfitValue2, setAvailable2, updateValues2, back2);

                            Scene updateValuesScene2 = new Scene(vBox6, 300, 300);
                            primaryStage.setScene(updateValuesScene2);

//                        updateValues2.setOnAction(eo -> {
//
//                        });

                            back2.setOnAction(ep -> {
                                primaryStage.setScene(managerMenuItems);
                            });

                            updateValues2.setOnAction(ep -> {
                                try {
                                    int salesPrice = Integer.parseInt(setSalesPrice2.getText());
                                    int profitValue = Integer.parseInt(setProfitValue2.getText());
//                                    Socket newSocket = new Socket("localhost", 8000);
//                                    fromServer = new DataInputStream(newSocket.getInputStream());
//                                    toServer = new DataOutputStream(newSocket.getOutputStream());

                                    toServer.writeInt(5);

                                    toServer.writeUTF(setCategory2.getText());
                                    toServer.writeUTF(setItemName2.getText());
                                    toServer.writeInt(salesPrice);
                                    toServer.writeInt(profitValue);
                                    toServer.writeInt(salesPrice + profitValue);
                                    toServer.writeUTF(setAvailable2.getText());

                                    Socket newSocket2 = new Socket("localhost", 8000);
                                    fromServer = new DataInputStream(newSocket2.getInputStream());
                                    toServer = new DataOutputStream(newSocket2.getOutputStream());

                                    menuList.getItems().clear();
                                    toServer.writeInt(3);

                                    String serverMessageNew = "";
                                    while (!serverMessageNew.equals("done")) {
                                        serverMessageNew = fromServer.readUTF();
                                        menuList.getItems().add(serverMessageNew);
                                    }

                                    menuList.getItems().remove("done");


                                    primaryStage.setScene(managerMenuItems);

                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                            });


                        } catch (Exception e11) {
                            e11.printStackTrace();
                        }
                    });

                    managerSelect.setOnAction(e3 -> {
                        VBox vBox5 = new VBox();

                        TextField setCategory = new TextField();
                        setCategory.setPromptText("Category");
                        TextField setItemName = new TextField();
                        setItemName.setPromptText("Item Name");
                        TextField setSalesPrice = new TextField();
                        setSalesPrice.setPromptText("Sales Price");
                        TextField setProfitValue = new TextField();
                        setProfitValue.setPromptText("Profit Value");

                        TextField setAvailable = new TextField();
                        setAvailable.setPromptText("Availablity");
                        Button updateValues = new Button("Update");
                        Button back = new Button("Back");

                        vBox5.getChildren().addAll(setCategory, setItemName, setSalesPrice, setProfitValue, setAvailable, updateValues, back);

                        Scene updateValuesScene = new Scene(vBox5, 300, 300);
                        primaryStage.setScene(updateValuesScene);

                        String foodName = menuList.getSelectionModel().getSelectedItem();

                        updateValues.setOnAction(ep -> {
                            try {
                                int salesPrice = Integer.parseInt(setSalesPrice.getText());
                                int profitValue = Integer.parseInt(setProfitValue.getText());
//                                Socket newSocket = new Socket("localhost", 8000);
//                                fromServer = new DataInputStream(newSocket.getInputStream());
//                                toServer = new DataOutputStream(newSocket.getOutputStream());
                                toServer.writeInt(4);
                                toServer.writeUTF(foodName);
                                toServer.writeUTF(setCategory.getText());
                                toServer.writeUTF(setItemName.getText());
                                toServer.writeInt(salesPrice);
                                toServer.writeInt(profitValue);
                                toServer.writeInt(salesPrice + profitValue);
                                toServer.writeUTF(setAvailable.getText());

//                                Socket newSocket2 = new Socket("localhost", 8000);
//                                fromServer = new DataInputStream(newSocket2.getInputStream());
//                                toServer = new DataOutputStream(newSocket2.getOutputStream());

                                menuList.getItems().clear();
                                toServer.writeInt(3);

                                String serverMessageNew = "";
                                while (!serverMessageNew.equals("done")) {
                                    serverMessageNew = fromServer.readUTF();
                                    menuList.getItems().add(serverMessageNew);
                                }

                                menuList.getItems().remove("done");


                                primaryStage.setScene(managerMenuItems);

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        });

                        back.setOnAction(e2 -> {
                            primaryStage.setScene(managerMenuItems);
                        });
                    });
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            profitAnalysis.setOnAction(e -> {
                try {
                    toServer.writeInt(12);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Profit Analysis of Last Month");
                    alert.setHeaderText("Profit Analysis Summary");
                    alert.setContentText("The Profits for the last month are: " + fromServer.readInt() + " LE");
                    alert.showAndWait().ifPresent(rs -> {
                        if (rs == ButtonType.OK) {
                            System.out.println("Pressed OK.");
                        }
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            logOutManager.setOnAction(e -> {
                userName.setText("");
                password.setText("");
                primaryStage.setScene(loginMain);
            });

            //Customer Menu
            Label itemPrice = new Label("Price");
            Label itemPriceR = new Label("0");
            Label totalPrice = new Label("Total Price");
            Label totalPriceR = new Label("0");
            Label customerName = new Label("Welcome");

            ListView<String> customerDrinksList = new ListView<>();

            ListView<String> customerFoodList = new ListView<>();

            ListView<String> customerCartList = new ListView<>();

            Button getPrice = new Button("Get Price");
            Button checkoutBtn = new Button("Checkout");
            Button addToCart = new Button("Add To Cart");
            Button removeFromCart = new Button("Remove from Cart");

            TabPane customerScreen = new TabPane();
            Tab drinks = new Tab("drinks");
            Tab food = new Tab("food");
            Tab cart = new Tab("cart");

            drinks.setContent(customerDrinksList);
            food.setContent(customerFoodList);
            cart.setContent(customerCartList);

            HBox itemPriceWithLabel = new HBox();
            itemPriceWithLabel.setSpacing(10);
            itemPriceWithLabel.setAlignment(Pos.CENTER);
            HBox totalPriceWithLabel = new HBox();
            totalPriceWithLabel.setSpacing(10);
            totalPriceWithLabel.setAlignment(Pos.CENTER);

            itemPriceWithLabel.getChildren().addAll(itemPrice, itemPriceR);
            totalPriceWithLabel.getChildren().addAll(totalPrice, totalPriceR);

            VBox vBox7 = new VBox();
            vBox7.setSpacing(10);
            vBox7.setAlignment(Pos.CENTER);
            vBox7.setPadding(new Insets(10, 10, 10, 10));
            vBox7.getChildren().addAll(customerName, itemPriceWithLabel, totalPriceWithLabel, customerScreen, getPrice, addToCart, removeFromCart, checkoutBtn);

            customerScreen.getTabs().addAll(drinks, food, cart);

            Scene customerScene = new Scene(vBox7, 400, 400);

            //Manager Invoices Viewer Design;
            ListView<String> invoices = new ListView<>();

            Button viewInvoice = new Button("View Invoice");
            Button getBack = new Button("Back");

            HBox viewInvoiceContainer = new HBox();
            HBox getBackContainer = new HBox();

            viewInvoiceContainer.getChildren().add(viewInvoice);
            viewInvoiceContainer.setAlignment(Pos.CENTER);
            getBackContainer.getChildren().add(getBack);
            getBackContainer.setAlignment(Pos.CENTER);

            VBox invoiceSceneContainer = new VBox();
            invoiceSceneContainer.setPadding(new Insets(10, 10, 10, 10));
            invoiceSceneContainer.setSpacing(10);
            invoiceSceneContainer.getChildren().addAll(invoices, viewInvoiceContainer, getBackContainer);

            Scene invoicesScene = new Scene(invoiceSceneContainer, 300, 300);


            checkoutBtn.setOnAction(e -> {
                try {
//                    Socket socket = new Socket("localhost", 8000);
//                    fromServer = new DataInputStream(socket.getInputStream());
//                    toServer = new DataOutputStream(socket.getOutputStream());
                    toServer.writeInt(7);
                    int i = 0;
                    toServer.writeUTF(customerName.getText());
                    while(i < customerCartList.getItems().size()) {
                        toServer.writeUTF(customerCartList.getItems().get(i));
                        i++;
                    }

                    toServer.writeUTF("done");
                } catch (IOException p) {
                    p.getStackTrace();
                }
            });

            //Invoice Viewer
            Stage invoiceStage = new Stage();
            TextArea invoiceContent = new TextArea();
            Scene invoiceViewer = new Scene(invoiceContent, 200, 200);
            invoiceStage.setScene(invoiceViewer);


        allInvoices.setOnAction(o -> {
            try {
                invoices.getItems().clear();
                toServer.writeInt(8);
                String idReceived = "";

                while(!idReceived.equals("done")) {
                    idReceived = fromServer.readUTF();
                    invoices.getItems().add(idReceived);
                }

                invoices.getItems().remove("done");


            } catch (IOException l) {
                l.printStackTrace();
            }

            primaryStage.setScene(invoicesScene);

            viewInvoice.setOnAction(e -> {
                try {
                    ArrayList<String> itemName = new ArrayList<>();
                    ArrayList<String> itemID = new ArrayList<>();
                    ArrayList<String> itemPrices = new ArrayList<>();

                    toServer.writeInt(9);

                    String messageGot = "";
                    while(!messageGot.equals("done")) {
                        messageGot = fromServer.readUTF();
                        if(!messageGot.equals("done")) {
                            itemID.add(messageGot);
                            itemName.add(fromServer.readUTF());
                            itemPrices.add(fromServer.readUTF());
                        }
                    }

                    for(String ix : itemID) {
                        System.out.println(ix);
                    }
                    for (String ix : itemName) {
                        System.out.println(ix);
                    }

                    toServer.writeUTF(invoices.getSelectionModel().getSelectedItem());

                    String messageReceived = "";
                    invoiceStage.setTitle("#Invoice " + invoices.getSelectionModel().getSelectedItem());
                    invoiceContent.clear();
                    invoiceContent.appendText("#Invoice " + invoices.getSelectionModel().getSelectedItem() + "\n");

                    int totalPrices = 0;

                    while(!messageReceived.equals("end of invoice...")) {
                        messageReceived = fromServer.readUTF();
                        if(!messageReceived.equals("end of invoice...")) {
                            String qty = fromServer.readUTF();
                            String item = itemName.get(itemID.indexOf(fromServer.readUTF()));
                            int price = Integer.parseInt(itemPrices.get(itemName.indexOf(item))) * Integer.parseInt(qty);
                            totalPrices += price;
                            invoiceContent.appendText(messageReceived + "- " + qty + "x ");
                            invoiceContent.appendText(item + ".........." + price);
                            invoiceContent.appendText("\n");
                            System.out.println("doing");
                        }
                    }
                    invoiceContent.appendText("\n Total Price.........." + totalPrices);

                    invoiceStage.show();
                } catch(IOException e3) {
                    e3.printStackTrace();
                }
            });

            getBack.setOnAction(e -> {
                primaryStage.setScene(managerMenu);
            });
        });

        //Manage Salesmen
        ListView<String> salesmenNames = new ListView<>();
        Button addSalesman = new Button("Add Salesman");
        Button removeSalesman = new Button("Remove Salesman");
        Button goBack = new Button("Back");

        HBox addSalesmanContainer = new HBox();
        HBox removeSalesmanContainer = new HBox();
        HBox goBackContainer = new HBox();

        addSalesmanContainer.setAlignment(Pos.CENTER);
        removeSalesmanContainer.setAlignment(Pos.CENTER);
        goBackContainer.setAlignment(Pos.CENTER);

        addSalesmanContainer.getChildren().add(addSalesman);
        removeSalesmanContainer.getChildren().add(removeSalesman);
        goBackContainer.getChildren().add(goBack);

        VBox vBox9 = new VBox();
        vBox9.setSpacing(10);
        vBox9.setPadding(new Insets(10, 10, 10, 10));
        vBox9.setAlignment(Pos.CENTER);
        vBox9.getChildren().addAll(salesmenNames, addSalesmanContainer, removeSalesmanContainer, goBackContainer);

        Scene manageSalesmen = new Scene(vBox9, 300, 300);

        //Add Salesman Menu Design
        Label label12 = new Label("Salesman Registration Form:");
        TextField setFirstName2 = new TextField();
        setFirstName2.setPromptText("First Name");
        TextField setLastName2 = new TextField();
        setLastName2.setPromptText("Last Name");
        TextField setAge2 = new TextField();
        setAge2.setPromptText("Age");
        TextField setEmail2 = new TextField();
        setEmail2.setPromptText("Email");
        TextField setUserName2 = new TextField();
        setUserName2.setPromptText("Username");
        PasswordField setPassword2 = new PasswordField();
        setPassword2.setPromptText("Password");
        Button setRegister2 = new Button("Register");
        Button exitRegister2 = new Button("Exit");

        HBox container12 = new HBox();
        HBox container22 = new HBox();
        HBox container32 = new HBox();
        HBox container42 = new HBox();
        HBox container52 = new HBox();
        HBox container62 = new HBox();
        HBox container72 = new HBox();
        HBox container82 = new HBox();
        HBox container92 = new HBox();

        container12.getChildren().add(label12);
        container22.getChildren().add(setFirstName2);
        container32.getChildren().add(setLastName2);
        container42.getChildren().add(setAge2);
        container52.getChildren().add(setEmail2);
        container62.getChildren().add(setUserName2);
        container72.getChildren().add(setPassword2);
        container82.getChildren().add(setRegister2);
        container92.getChildren().add(exitRegister2);

        container12.setAlignment(Pos.CENTER);
        container22.setAlignment(Pos.CENTER);
        container32.setAlignment(Pos.CENTER);
        container42.setAlignment(Pos.CENTER);
        container52.setAlignment(Pos.CENTER);
        container62.setAlignment(Pos.CENTER);
        container72.setAlignment(Pos.CENTER);
        container82.setAlignment(Pos.CENTER);
        container92.setAlignment(Pos.CENTER);

        VBox vBox8 = new VBox();

        vBox8.getChildren().addAll(container12, container22, container32, container42, container52, container62, container72, container82, container92);
        vBox8.setAlignment(Pos.CENTER);
        vBox8.setSpacing(10);

        Scene addSalesmenMenu = new Scene(vBox8, 400, 400);

        //Add Salesman Menu
        manageEmployees.setOnAction(e -> {
            try {
                toServer.writeInt(10);
                salesmenNames.getItems().clear();
                String messageReceived = "";
                while(!messageReceived.equals("done")) {
                    messageReceived = fromServer.readUTF();
                    if(!messageReceived.equals("done")) {
                        salesmenNames.getItems().add(messageReceived);
                    }
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
            primaryStage.setScene(manageSalesmen);

            setRegister2.setOnAction(p -> {
                try {
                    toServer.writeInt(2);
                    toServer.writeUTF(setFirstName2.getText());
                    toServer.writeUTF(setLastName2.getText());
                    toServer.writeInt(Integer.parseInt(setAge2.getText()));
                    toServer.writeUTF(setEmail2.getText());
                    toServer.writeUTF(setUserName2.getText());
                    toServer.writeUTF(setPassword2.getText());
                    toServer.writeUTF("salesman");

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            addSalesman.setOnAction(p -> {
                primaryStage.setScene(addSalesmenMenu);

                exitRegister2.setOnAction(x -> {
                    try {
                        toServer.writeInt(10);
                        salesmenNames.getItems().clear();
                        String messageReceived = "";
                        while(!messageReceived.equals("done")) {
                            messageReceived = fromServer.readUTF();
                            if(!messageReceived.equals("done")) {
                                salesmenNames.getItems().add(messageReceived);
                            }
                        }

                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    setFirstName2.clear();
                    setLastName2.clear();
                    setAge2.clear();
                    setEmail2.clear();
                    setUserName2.clear();
                    setPassword2.clear();
                    primaryStage.setScene(manageSalesmen);
                });

            });

            goBack.setOnAction(p -> {
                primaryStage.setScene(managerMenu);
            });

            removeSalesman.setOnAction(p -> {
                try {
                    toServer.writeInt(11);
                    toServer.writeUTF(salesmenNames.getSelectionModel().getSelectedItem());
                    salesmenNames.getItems().remove(salesmenNames.getSelectionModel().getSelectedItem());

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            });

        });

            //Login System Backend...
            login.setOnAction(e -> {
                try {
//                    Socket socket = new Socket("localhost", 8000);
//                    fromServer = new DataInputStream(socket.getInputStream());
//                    toServer = new DataOutputStream(socket.getOutputStream());
                    toServer.writeInt(1);
                    toServer.writeUTF(userName.getText());
                    toServer.writeUTF(password.getText());
                    toServer.flush();
                    String userType = fromServer.readUTF();

                    if (userType.equals("manager")) {
//                    userName.getText();
                        primaryStage.setScene(managerMenu);
                    } else if (userType.equals("customer")) {
//                        Socket socketM = new Socket("localhost", 8000);
//                        fromServer = new DataInputStream(socketM.getInputStream());
//                        toServer = new DataOutputStream(socketM.getOutputStream());
                        customerName.setText(userName.getText());
                        toServer.writeInt(6);
                        String itemType;

                        ArrayList<Integer> foodItemsPrices = new ArrayList<>();
                        ArrayList<Integer> drinksItemsPrices = new ArrayList<>();
                        ArrayList<String> foodItemsNames = new ArrayList<>();
                        ArrayList<String> drinksItemsNames = new ArrayList<>();

                        while (true) {
                            itemType = fromServer.readUTF();

                            if (itemType.equals("Food")) {
                                String itemName = fromServer.readUTF();
                                customerFoodList.getItems().add(itemName);
                                foodItemsNames.add(itemName);
                                foodItemsPrices.add(Integer.parseInt(fromServer.readUTF()));

                            } else if (itemType.equals("Drinks")) {
                                String itemName = fromServer.readUTF();
                                customerDrinksList.getItems().add(itemName);
                                drinksItemsNames.add(itemName);
                                drinksItemsPrices.add(Integer.parseInt(fromServer.readUTF()));
                            } else {
                                System.out.println("Breaked");
                                break;
                            }
                        }

                        addToCart.setOnAction(l -> {
                            if(food.isSelected()) {
                                customerCartList.getItems().add(customerFoodList.getSelectionModel().getSelectedItem());
                                Integer price = Integer.parseInt(totalPriceR.getText());
                                Integer itemPrice2 = foodItemsPrices.get(foodItemsNames.indexOf(customerFoodList.getSelectionModel().getSelectedItem()));
                                Integer total = price + itemPrice2;
                                totalPriceR.setText(total.toString());
                            }
                            else {
                                customerCartList.getItems().add(customerDrinksList.getSelectionModel().getSelectedItem());
                                Integer price = Integer.parseInt(totalPriceR.getText());
                                Integer itemPrice2 = drinksItemsPrices.get(drinksItemsNames.indexOf(customerDrinksList.getSelectionModel().getSelectedItem()));
                                Integer total = price + itemPrice2;
                                totalPriceR.setText(total.toString());
                            }
                        });

                        getPrice.setOnAction(p -> {
                            if(food.isSelected()) {
                                itemPriceR.setText(foodItemsPrices.get(foodItemsNames.indexOf(customerFoodList.getSelectionModel().getSelectedItem())).toString());
                            }
                            else if(drinks.isSelected()) {
                                itemPriceR.setText(drinksItemsPrices.get(drinksItemsNames.indexOf(customerDrinksList.getSelectionModel().getSelectedItem())).toString());
                            }

                        });

                        removeFromCart.setOnAction(l -> {
                            if(foodItemsNames.contains(customerCartList.getSelectionModel().getSelectedItem())) {
                                Integer price = Integer.parseInt(totalPriceR.getText());
                                Integer itemPrice2 = foodItemsPrices.get(foodItemsNames.indexOf(customerCartList.getSelectionModel().getSelectedItem()));
                                Integer total = price - itemPrice2;
                                totalPriceR.setText(total.toString());
                            }
                            else if(drinksItemsNames.contains(customerCartList.getSelectionModel().getSelectedItem())) {
                                Integer price = Integer.parseInt(totalPriceR.getText());
                                Integer itemPrice2 = drinksItemsPrices.get(drinksItemsNames.indexOf(customerCartList.getSelectionModel().getSelectedItem()));
                                Integer total = price - itemPrice2;
                                totalPriceR.setText(total.toString());
                            }
                            customerCartList.getItems().remove(customerCartList.getSelectionModel().getSelectedItem());
                        });


                        primaryStage.setScene(customerScene);

                    }

                    else if(userType.equals("salesman")) {
                        System.out.println("Salesman logged in!");
                    }

                    else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Log in Failed!");
                        alert.setHeaderText("Log in Failed!");
                        alert.setContentText("Please enter correct username and password!");
                        alert.showAndWait().ifPresent(rs -> {
                            if (rs == ButtonType.OK) {
                                System.out.println("Pressed OK.");
                            }
                        });
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            //Registration Form Design...
            Label label1 = new Label("Registration Form:");
            TextField setFirstName = new TextField();
            setFirstName.setPromptText("First Name");
            TextField setLastName = new TextField();
            setLastName.setPromptText("Last Name");
            TextField setAge = new TextField();
            setAge.setPromptText("Age");
            TextField setEmail = new TextField();
            setEmail.setPromptText("Email");
            TextField setUserName = new TextField();
            setUserName.setPromptText("Username");
            PasswordField setPassword = new PasswordField();
            setPassword.setPromptText("Password");
            TextField setAccType = new TextField();
            setAccType.setPromptText("manager or customer or salesman?");
            Button setRegister = new Button("Register");
            Button exitRegister = new Button("Exit");

            HBox container1 = new HBox();
            HBox container2 = new HBox();
            HBox container3 = new HBox();
            HBox container4 = new HBox();
            HBox container5 = new HBox();
            HBox container6 = new HBox();
            HBox container7 = new HBox();
            HBox container8 = new HBox();
            HBox container9 = new HBox();
            HBox container10 = new HBox();

            container1.getChildren().add(label1);
            container2.getChildren().add(setFirstName);
            container3.getChildren().add(setLastName);
            container4.getChildren().add(setAge);
            container5.getChildren().add(setEmail);
            container6.getChildren().add(setUserName);
            container7.getChildren().add(setPassword);
            container8.getChildren().add(setAccType);
            container9.getChildren().add(setRegister);
            container10.getChildren().add(exitRegister);

            container1.setAlignment(Pos.CENTER);
            container2.setAlignment(Pos.CENTER);
            container3.setAlignment(Pos.CENTER);
            container4.setAlignment(Pos.CENTER);
            container5.setAlignment(Pos.CENTER);
            container6.setAlignment(Pos.CENTER);
            container7.setAlignment(Pos.CENTER);
            container8.setAlignment(Pos.CENTER);
            container9.setAlignment(Pos.CENTER);
            container10.setAlignment(Pos.CENTER);

            VBox vBox2 = new VBox();

            vBox2.getChildren().addAll(container1, container2, container3, container4, container5, container6, container7, container8, container9, container10);
            vBox2.setAlignment(Pos.CENTER);
            vBox2.setSpacing(10);

            Scene registration = new Scene(vBox2, 400, 400);

            //Action Handlers
            register.setOnAction(e -> {
                primaryStage.setScene(registration);
            });


            //Backend of Registration
            setRegister.setOnAction(e -> {
                try {
//                    Socket socket = new Socket("localhost", 8000);
//                    fromServer = new DataInputStream(socket.getInputStream());
//                    toServer = new DataOutputStream(socket.getOutputStream());
                    toServer.writeInt(2);
                    toServer.writeUTF(setFirstName.getText());
                    toServer.writeUTF(setLastName.getText());
                    toServer.writeInt(Integer.parseInt(setAge.getText()));
                    toServer.writeUTF(setEmail.getText());
                    toServer.writeUTF(setUserName.getText());
                    toServer.writeUTF(setPassword.getText());
                    toServer.writeUTF(setAccType.getText());

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            });

            exitRegister.setOnAction(e -> {
                primaryStage.setScene(loginMain);
            });
        //Primary Scene
        primaryStage.setTitle("Cafe System V1.0.exe");
        primaryStage.setScene(loginMain);
        primaryStage.show();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
