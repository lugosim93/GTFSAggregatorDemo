package application;

import javafx.application.Platform;
import javafx.beans.binding.NumberBinding;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;


import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;


// WEBAPP VERSION

//TODO: Separate event handling in seperate class
@SuppressWarnings({"Duplicates", "unused"})
public class Main extends Application implements EventHandler<ActionEvent>{

    private static final String indexPageUrl = "C:\\Apache24\\htdocs\\index.html";

    private static DateTime start;
    private static DateTime end;
    private static int length = 6;
    private static String originCity;
    private static String endCity;
    private static ArrayList<String> destinations = new ArrayList<>();
    private static int numOfDestCities;
    private static int loginAttempts = 0;


    private Stage window;

    // Scenes
    private Scene startScene;
    private Scene readFromFileScene;
    private Scene manualInputScene;
    private Scene refreshFeedsScene;
    private Scene loggedInScene;
    private Scene loginScene;
    private Scene registrationScene;
    private Scene captchaScene;

    // Buttons
    private Button manualInputButton;
    private Button tripResultsButton;
    private Button mainMenuButton;
    private Button readFromFileButton;
    private Button refreshFeedsButton;
    private Button submitTripButton;
    private Button loginButton;
    private Button guestLoginButton;
    private Button submitLoginButton;
    private Button registerButton;
    private Button returnToMainMenuButton;
    private Button submitRegistrationButton;
    private Button captchaSubmitButton;
    private Button addDestinationButton;
    private Button exitButton;

    // TextInputField
    private TextField startDateInput;
    private TextField endDateInput;
    private TextField startCityInput;
    private TextField destinationsOutput;
    private TextField loginEmailInput;
    private PasswordField loginPasswordInput;
    private TextField registrationEmailInput;
    private PasswordField registrationPasswordInput;
    private PasswordField registrationPasswordConfirmationInput;
    private TextField captchaInputField;


    public static void run(String... args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        window = primaryStage;
        window.setTitle("GTFS Aggregator");
        window.show();


        displayStartScene();

    }

    public void handle(ActionEvent event)  {

        if (event.getSource() == addDestinationButton){

            addDestination();
        }

        if (event.getSource() == readFromFileButton){

            displayReadFromFileScene();

        }

        if (event.getSource() == mainMenuButton){

            displayStartScene();

        }

        if (event.getSource() == manualInputButton){

            displayNewTripScene();

        }

        if (event.getSource() == submitTripButton){

            DateTimeFormatter formatter = DateTimeFormat.forPattern("YYYY-MM-DD");

            start = DateTime.parse(startDateInput.getText(), formatter);
            end = DateTime.parse(endDateInput.getText(), formatter);

            originCity = startCityInput.getText();
            endCity = destinationsOutput.getText();

            System.err.println("Start: " + start.toString()
                    + "\nEnd: " + end.toString()
                    + "\nStart City: " + originCity
                    + "\nDestination City: " + endCity);
        }

        if (event.getSource() == loginButton){
            displayLoginScene();
        }

        if (event.getSource() == returnToMainMenuButton){
            displayStartScene();
        }

        if (event.getSource() == registerButton){
            displayRegistrationScene();
        }

        if (event.getSource() == guestLoginButton){
            loginAttempts = 0;
            displayLoggedInScene();
        }

        if (event.getSource() == submitLoginButton){

            String email = loginEmailInput.getText();
            String password = loginPasswordInput.getText();

            if (loginAttempts< 3 && isValidLoginCred(email, password)){
                loginAttempts = 0;
                displayLoggedInScene();
            } else if(loginAttempts < 3) {

                loginAttempts++;
                displayInvalidLoginAlert();
            } else {
                displayCaptchaScene();
            }
        }

        if (event.getSource() == submitRegistrationButton){
            displayStartScene();
        }

        if (event.getSource() == captchaSubmitButton){
            System.err.println(captchaInputField.getText());
            if(captchaInputField.getText().equals("captcha")){
                loginAttempts = 0;
                displayLoginScene();
            }
        }

        if (event.getSource() == exitButton){
            Platform.exit();
        }

    }

    private void displayCaptchaScene(){

        Label title = new Label("Please complete captcha");

        bindElementVertically(captchaInputField, title, 20);

        captchaSubmitButton = new Button("Submit");
        captchaSubmitButton.setOnAction(this);

        bindElementVertically(captchaSubmitButton, captchaInputField, 20);

        Group root = new Group();

        root.getChildren().addAll(title, captchaInputField, captchaSubmitButton);

        captchaScene = new Scene(root, 1024, 800);

        window.setScene(captchaScene);

    }



    private void displayLoginScene(){

        Label loginLabel = new Label("Please provide your e-mail and password!");

        Label emailLabel = new Label("E-mail: ");

        bindElementVertically(emailLabel, loginLabel, 30);

        loginEmailInput = new TextField("example@e-mail.com");

        bindElementHorizontally(loginEmailInput, emailLabel, 40);

        Label passwordLabel = new Label("Password: ");

        bindElementVertically(passwordLabel, emailLabel, 32);

        loginPasswordInput = new PasswordField();
        loginPasswordInput.setPromptText("Enter password");

        bindElementVertically(loginPasswordInput, loginEmailInput, 20);

        submitLoginButton = new Button("Submit");
        submitLoginButton.setOnAction(this);

        bindElementVertically(submitLoginButton, loginPasswordInput, 40);

        returnToMainMenuButton = new Button("Cancel");
        returnToMainMenuButton.setOnAction(this);

        bindElementHorizontally(returnToMainMenuButton, submitLoginButton, 10);

        Group root = new Group();
        root.getChildren().addAll(loginLabel, emailLabel, loginEmailInput, passwordLabel, loginPasswordInput, returnToMainMenuButton, submitLoginButton);

        loginScene = new Scene(root, 1024, 800);

        window.setScene(loginScene);
    }

    private void displayInvalidLoginAlert(){

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error!");
        alert.setHeaderText("Unauthorized login");
        alert.setContentText("The provided e-mail address and/or password was incorrect.");
        loginAttempts++;

        alert.showAndWait();

    }

    private void displayStartScene(){

        Label greeting = new Label("Welcome!");

        loginButton = new Button("Log in");
        loginButton.setOnAction(this);

        bindElementVertically(loginButton, greeting, 20);

        guestLoginButton = new Button("Guest");
        guestLoginButton.setOnAction(this);
        bindElementHorizontally(guestLoginButton, loginButton, 10);

        registerButton = new Button("Register");
        registerButton.setOnAction(this);

        bindElementHorizontally(registerButton, guestLoginButton, 10);

        exitButton = new Button("Exit");
        exitButton.setOnAction(this);

        bindElementHorizontally(exitButton, registerButton, 10);

        Group root = new Group();

        root.getChildren().addAll(greeting, guestLoginButton, loginButton, registerButton, exitButton);

        startScene = new Scene(root, 1024, 800);

        window.setScene(startScene);

    }

    private void readFromFile(String fileName) throws IOException {

        BufferedReader bf;
        destinations = new ArrayList<String>();

        fileName = "D:/Sandbox/scheduler/" + fileName;

        System.err.println("***********\nDebug Info: \nReading file: " + fileName);

        try{
            bf = new BufferedReader(new FileReader(fileName));
        } catch (FileNotFoundException e){
            System.err.println("Error: ");
            e.printStackTrace();
            return;
        }

        start = DateTime.parse(bf.readLine());
        end = DateTime.parse(bf.readLine());

        length = Integer.parseInt(bf.readLine());
        originCity = bf.readLine();

        numOfDestCities = Integer.parseInt(bf.readLine());

        for (int i = 0; i < numOfDestCities; i++){
            destinations.add(bf.readLine());
        }

        bf.close();
        System.err.println("Start: " + start
                + "\nEnd: " + end
                + "\nlength: " + length
                + "\noriginCity: " + originCity
                + "\nnumOfDestCities: " + numOfDestCities
                + "\nDestinations: " + destinations.toString()
                + "\n***********");
    }

    private void displayLoggedInScene(){

        Label optionsLabel = new Label("Options: ");
        optionsLabel.setLayoutX(10);
        optionsLabel.setLayoutY(10);


        //Read from file button
        readFromFileButton = new Button();
        readFromFileButton.setText("Read from file");
        readFromFileButton.setOnAction(this);

        NumberBinding readFromFileBinding = bindElementHorizontally(readFromFileButton, optionsLabel, 10);

        //Manual input button
        manualInputButton = new Button();
        manualInputButton.setText("New trip");
        manualInputButton.setOnAction(this);

        NumberBinding manualInputBinding = bindElementHorizontally(manualInputButton, readFromFileButton, 10);

        // Feed Reader refresher button
        refreshFeedsButton = new Button();
        refreshFeedsButton.setText("Refresh feeds");
        refreshFeedsButton.setOnAction(this);

        NumberBinding refreshFeedsBinding = bindElementHorizontally(refreshFeedsButton, manualInputButton, 10);

        returnToMainMenuButton = new Button("Log out");
        returnToMainMenuButton.setOnAction(this);

        bindElementHorizontally(returnToMainMenuButton, refreshFeedsButton, 10);

        Group root = new Group();
        root.getChildren().addAll(optionsLabel, manualInputButton, /*refreshFeedsButton,*/ returnToMainMenuButton);

        loggedInScene = new Scene(root, 1024, 800);

        window.setScene(loggedInScene);
    }

    // For unit testing
    private void displayReadFromFileScene(){
        Group root = new Group();
        TextArea result = new TextArea();
        result.setLayoutX(10);
        result.setLayoutY(10);
        result.setMaxWidth(300);
        result.setMaxHeight(300);

        tripResultsButton = new Button();

        NumberBinding tripResultBinding =
                result.layoutXProperty().add(result.widthProperty().add(10));
        tripResultsButton.layoutXProperty().bind(tripResultBinding);
        tripResultsButton.layoutYProperty().bind(result.layoutYProperty());

        tripResultsButton.setOnAction(this);
        tripResultsButton.setText("Calculate possible trips");
        try {
            readFromFile("sample_input.txt");
        } catch (IOException e){
            e.printStackTrace();
        }

        result.setText("Input: \nStart: " + start + "\nEnd: " + end + "\nlength:" + length + "\noriginCity: " + originCity + "\nnumOfDestCities: " + numOfDestCities + "\ndestinations: " + destinations.toString());
        result.setEditable(false);

        root.getChildren().addAll(result, tripResultsButton);
        readFromFileScene = new Scene(root, 1024, 760);

        window.setScene(readFromFileScene);
    }

    private void displayNewTripScene(){

        Group root = new Group();

        Label startDate = new Label("Starting Date: ");
        startDate.setLayoutX(10);
        startDate.setLayoutY(10);

        // TODO: Regex for different formats?
        startDateInput = new TextField("2019-01-01");

        NumberBinding startDateInputBinding = bindElementHorizontally(startDateInput, startDate, 20);
        Label endDate = new Label("End date: ");

        NumberBinding endDateBinding = bindElementVertically(endDate, startDate, 20);

        endDateInput = new TextField("2019-01-01");

        NumberBinding endDateInputBinding = bindElementVertically(endDateInput, startDateInput, 10);

        Label startCityLabel = new Label("Start city: ");

        NumberBinding startCityLabelBinding = bindElementVertically(startCityLabel, endDate, 20);

        startCityInput = new TextField("Budapest");

        NumberBinding startCityInputBinding = bindElementVertically(startCityInput, endDateInput, 10);

        Label endCityLabel = new Label("Destinations: ");

        NumberBinding endCityLabelBinding = bindElementVertically(endCityLabel, startCityLabel, 20);

        destinationsOutput = new TextField("");
        destinationsOutput.setEditable(false);
        NumberBinding endCityInputBinding = bindElementVertically(destinationsOutput, startCityInput, 10);

        addDestinationButton = new Button("Add destination");
        addDestinationButton.setOnAction(this);

        bindElementHorizontally(addDestinationButton, destinationsOutput, 20);

        submitTripButton = new Button("Submit");
        submitTripButton.setOnAction(this);

        NumberBinding submitButtonBinding = bindElementVertically(submitTripButton, destinationsOutput, 60);

        Button cancelTripButton = new Button("Cancel");
        cancelTripButton.setOnAction(actionEvent -> displayLoggedInScene());

        bindElementHorizontally(cancelTripButton, submitTripButton, 10);

        root.getChildren().addAll(startDate, startDateInput, endDate, endDateInput, startCityLabel, startCityInput, endCityLabel, destinationsOutput, submitTripButton, cancelTripButton, addDestinationButton);
        manualInputScene = new Scene(root, 1024, 760);
        window.setScene(manualInputScene);
    }

    private void displayRegistrationScene(){

        Label title = new Label("Registration");

        Label emailLabel = new Label("E-mail: ");

        bindElementVertically(emailLabel, title, 20);

        registrationEmailInput = new TextField("example@e-mail.com");

        bindElementHorizontally(registrationEmailInput, emailLabel, 40);

        Label passwordLabel = new Label("Password: ");

        bindElementVertically(passwordLabel, emailLabel, 32);

        registrationPasswordInput = new PasswordField();
        registrationPasswordInput.setPromptText("Enter password");

        bindElementVertically(registrationPasswordInput, registrationEmailInput, 20);

        submitRegistrationButton = new Button("Submit");
        submitRegistrationButton.setOnAction(this);

        bindElementVertically(submitRegistrationButton, registrationPasswordInput, 30);

        returnToMainMenuButton = new Button("Cancel");
        returnToMainMenuButton.setOnAction(this);

        bindElementHorizontally(returnToMainMenuButton, submitRegistrationButton, 10);


        Group root = new Group();

        root.getChildren().addAll(title, emailLabel, registrationEmailInput, passwordLabel, registrationPasswordInput, returnToMainMenuButton, submitRegistrationButton);

        registrationScene = new Scene(root, 1024, 760);

        window.setScene(registrationScene);

    }

    /*

    private void displayReloadFeedsScene() throws Exception{

        Group root = new Group();

        Label title = new Label("GTFS Feed Reader");

        HashMap<String, String> sources = new HashMap<String, String>();

        // Sample from Google
        sources.put("sample", "./src/main/resources/sample-feed.zip");

        // Budapest BKK
        sources.put("bkk", "./src/main/resources/bkk-feed.zip");

        // Deutsche Bahn
        sources.put("deutsche_bahn" , "./src/main/resources/deutsche-bahn-gtfs.zip");

        // Société nationale des chemins de fer français (French national railway)
        sources.put("sncf", "./src/main/resources/sncf-gtfs.zip");

        // Denmark GTFS Rejseplanen
        sources.put("denmark", "./src/main/resources/denmark-gtfs.zip");

        FeedReader aggregator = new FeedReader(sources);

        aggregator.readAll(true);

        String agencies = aggregator.getAgencies("denmark");
        Label result = new Label(agencies);

        NumberBinding resultBinding = bindElementHorizontally(result, title, 20);

        root.getChildren().addAll(title, result);

        refreshFeedsScene = new Scene(root, 1024, 800);
        window.setScene(refreshFeedsScene);
    }

     */
    private void addDestination(){

        TextInputDialog dialog = new TextInputDialog("");
        dialog.setTitle("New Destination");
        dialog.setHeaderText("Adding new destination to trip");
        dialog.setContentText("Please enter the name of a city: ");

        Optional<String> result = dialog.showAndWait();

        if (result.isPresent()){

            destinations.add(result.toString());

        }

        destinationsOutput.setText(destinations.toString());
    }

    private NumberBinding bindElementHorizontally(Region a, Region b, int c){

        NumberBinding ret =
                b.layoutXProperty().add(b.widthProperty().add(c));
        a.layoutXProperty().bind(ret);
        a.layoutYProperty().bind(b.layoutYProperty());

        return ret;

    }

    private NumberBinding bindElementVertically(Region a, Region b, int  c){

        NumberBinding ret =
                b.layoutYProperty().add(b.heightProperty().add(c));
        a.layoutYProperty().bind(ret);
        a.layoutXProperty().bind(b.layoutXProperty());

        return ret;

    }

    private boolean isValidLoginCred(String email, String pass){

        System.err.println("Login creds: " + email + " " + pass);

        return (email.equals("a@b.com") && pass.equals("asd"));
    }


    public static void main(String[] args) {
        launch(args);
    }
}
