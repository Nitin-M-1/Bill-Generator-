import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.HashMap;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.PrintWriter;

class ElectricityBillGeneratorApplet extends JApplet implements ActionListener 
 {

    private JTextField nameField, idField, previousUnitField, currentUnitField;
    private JComboBox<String> connectionTypeChoice, monthComboBox, dateComboBox, yearComboBox;
    private JButton calculateButton, searchButton, deleteButton, logoutButton; 
    private JTextArea resultArea;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JPanel loginPanel;
    private JPanel mainPanel;

    
    private HashMap<Integer, String[]> dataMap = new HashMap<>();

    private static final String FILE_NAME = "electricity_data.txt";

    public void init() 
	{
        
        loginPanel = new JPanel(new GridLayout(16, 2));
        loginPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(20);
        loginPanel.add(usernameField);
        loginPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField);
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        loginPanel.add(new JLabel());
        loginPanel.add(loginButton);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(loginPanel);
        add(mainPanel);
    }

   
    public void actionPerformed(ActionEvent e) 
	  {
        int id;
        String name, month, date, year;
        double previousUnit, currentUnit;

        if (e.getSource() == loginButton) 
		{
            performLogin();
        } 
		else if (e.getSource() == logoutButton) 
		{ 
            mainPanel.removeAll();
            mainPanel.add(loginPanel);
            revalidate();
        }
		else 
		{
            try 
			 {
                id = Integer.parseInt(idField.getText());
                name = nameField.getText();
                date = (String) dateComboBox.getSelectedItem();
                month = (String) monthComboBox.getSelectedItem();
                year = (String) yearComboBox.getSelectedItem();
                previousUnit = Double.parseDouble(previousUnitField.getText());
                currentUnit = Double.parseDouble(currentUnitField.getText());
            } 
			catch (NumberFormatException ex)
			{
                resultArea.setText("Invalid input. Please enter valid data.");
                return;
            }

            int connectionType = connectionTypeChoice.getSelectedIndex();

            if (e.getSource() == calculateButton) 
			{
                calculateBill(id, name, connectionType, date, month, year, previousUnit, currentUnit);
            } 
			else if (e.getSource() == searchButton) 
			{
                try
				{
                    id = Integer.parseInt(idField.getText());
                    searchBill(id, name, connectionType, date, month, year, previousUnit, currentUnit);
                }
				catch (NumberFormatException ex) 
				{
                    resultArea.setText("Invalid input. Please enter a valid ID.");
                }
            }
			else if (e.getSource() == deleteButton) 
			{
                deleteData();
            }
        }
    }

    private void performLogin() 
	{
        String enteredUsername = usernameField.getText();
        String enteredPassword = new String(passwordField.getPassword());
        String correctUsername = "admin"; 
        String correctPassword = "password123"; 

        if (enteredUsername.equals(correctUsername) && enteredPassword.equals(correctPassword)) 
		{
            initializeBillPanel();
        }
		else 
		{
            JOptionPane.showMessageDialog(this, "Invalid username or password. Please try again.");
        }
    }

    private void initializeBillPanel() 
	{
        mainPanel.remove(loginPanel);

        JPanel inputPanel = new JPanel(new GridLayout(9, 2));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField(20);
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("ID:"));
        idField = new JTextField(20);
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Connection Type:"));
        connectionTypeChoice = new JComboBox<>(new String[]{"Domestic", "Commercial"});
        inputPanel.add(connectionTypeChoice);

        inputPanel.add(new JLabel("Date:"));
        dateComboBox = new JComboBox<>(generateNumbersArray(1, 31));
        inputPanel.add(dateComboBox);

        inputPanel.add(new JLabel("Month:"));
        monthComboBox = new JComboBox<>(new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"});
        inputPanel.add(monthComboBox);

        inputPanel.add(new JLabel("Year:"));
        yearComboBox = new JComboBox<>(generateNumbersArray(2023, 2042));
        inputPanel.add(yearComboBox);

        inputPanel.add(new JLabel("Previous Unit:"));
        previousUnitField = new JTextField(20);
        inputPanel.add(previousUnitField);

        inputPanel.add(new JLabel("Current Unit:"));
        currentUnitField = new JTextField(20);
        inputPanel.add(currentUnitField);

        calculateButton = new JButton("Calculate Bill");
        searchButton = new JButton("Search Bill");
        deleteButton = new JButton("Delete Data");
        logoutButton = new JButton("Logout"); 
        logoutButton.addActionListener(this); 

        calculateButton.addActionListener(this);
        searchButton.addActionListener(this);
        deleteButton.addActionListener(this);

        resultArea = new JTextArea(10, 30);
        resultArea.setEditable(false);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(calculateButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton); 

        mainPanel.removeAll();  
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(inputPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(resultArea);

        revalidate();

        loadDataFromFile();
        add(mainPanel);
    }

    private void calculateBill(int id, String name, int connectionType, String date, String month, String year, double previousUnit, double currentUnit) 
   	{
    double rate = 0.0;
    double unitsConsumed = currentUnit;

    if (connectionType == 0) 
	{
        if (unitsConsumed <= 100) 
		{
            rate = 1.0;
        } 
		else if (unitsConsumed <= 200) 
		{
            rate = 2.5;
        } 
		else if (unitsConsumed <= 500)
		{
            rate = 4.0;
        } 
		else 
		{
            rate = 6.0;
        }
    }
	else if (connectionType == 1)
		{
        if (unitsConsumed <= 100) 
		{
            rate = 2.0;
        } 
		else if (unitsConsumed <= 200) 
		{
            rate = 4.5;
        } 
		else if (unitsConsumed <= 500) 
		{
            rate = 6.0;
        }
		else 
		{
            rate = 7.0;
        }
    } 
	else 
	{
        resultArea.setText("Invalid connection type");
        return;
    }

    double billAmount = unitsConsumed * rate;
 
      String[] billData = new String[]{name, Integer.toString(connectionType), date, month, year, Double.toString(previousUnit), Double.toString(currentUnit), Double.toString(billAmount)};
      dataMap.put(id, billData);

    resultArea.setText("\nElectricity Bill for " + name + " (ID): " + id + "\n");
    resultArea.append("Connection Type: " + getConnectionTypeString(connectionType) + "\n");
    resultArea.append("Date: " + date + "\n");
    resultArea.append("Month: " + month + "\n");
    resultArea.append("Year: " + year + "\n");
    resultArea.append("Previous Unit: " + previousUnit + "\n");
    resultArea.append("Current Unit: " + currentUnit + "\n");
    resultArea.append("Units Consumed: " + unitsConsumed + "\n");
    resultArea.append(String.format("Rate: Rs. %.2f per unit\n", rate)); // Format as currency
    resultArea.append(String.format("Total Bill Amount: Rs. %.2f\n", billAmount)); // Format as currency

    saveDataToFile();
 }

private void searchBill(int id, String name, int connectionType, String date, String month, String year, double previousUnit, double currentUnit) 
 {
    try {
        int idToSearch = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID to search:"));

        if (dataMap.containsKey(idToSearch)) 
		{
            String[] billData = dataMap.get(idToSearch);

            
            String resultName = billData[0];
            int resultConnectionType = Integer.parseInt(billData[1]);
            String resultDate = billData[2];
            String resultMonth = billData[3];
            String resultYear = billData[4];
            double resultPreviousUnit = Double.parseDouble(billData[5]);
            double resultCurrentUnit = Double.parseDouble(billData[6]);
            double bill = Double.parseDouble(billData[7]);

            resultArea.setText("\nElectricity Bill for ID: " + idToSearch + "\n");
            resultArea.append("Connection Type: " + getConnectionTypeString(resultConnectionType) + "\n");
            resultArea.append("Date: " + resultDate + "\n");
            resultArea.append("Month: " + resultMonth + "\n");
            resultArea.append("Year: " + resultYear + "\n");
            resultArea.append("Previous Unit: " + resultPreviousUnit + "\n");
            resultArea.append("Current Unit: " + resultCurrentUnit + "\n");
            resultArea.append("Total Bill Amount: " + bill + "\n");
        }
		else 
		{
            resultArea.setText("ID not found in the data.");
        }
    }
	catch (NumberFormatException ex) 
	{
        resultArea.setText("Invalid input. Please enter a valid ID.");
    }
}


     private void deleteData() 
	 {
        try
		{
            int idToDelete = Integer.parseInt(JOptionPane.showInputDialog("Enter the ID to delete:"));

            if (dataMap.containsKey(idToDelete)) 
			{
                dataMap.remove(idToDelete);
                resultArea.setText("Data for ID " + idToDelete + " deleted.");
            } 
			else 
			{
                resultArea.setText("ID not found in the data.");
            }

            saveDataToFile();
        } 
		catch (NumberFormatException ex) 
		{
            resultArea.setText("Invalid input. Please enter a valid ID.");
        }
    }

    private String getConnectionTypeString(int connectionType) 
	{
        if (connectionType == 0) 
		{
            return "Domestic";
        } 
		else if (connectionType == 1) 
		{
            return "Commercial";
        }
		else 
		{
            return "Unknown";
        }
    }

private void loadDataFromFile() 
{
    try (BufferedReader reader = new BufferedReader(new FileReader("electricity_data.txt")))
	{
        String line;
        reader.readLine();
        
        while ((line = reader.readLine()) != null)
			{
            String[] parts = line.split(",");
            if (parts.length == 9) 
			{ 
                try {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    int connectionType = Integer.parseInt(parts[2]);
                    String date = parts[3];
                    String month = parts[4];
                    String year = parts[5];
                    double previousUnit = Double.parseDouble(parts[6]);
                    double currentUnit = Double.parseDouble(parts[7]);
                    double billAmount = Double.parseDouble(parts[8]);

                    // Store all fields in the dataMap as String array
                    dataMap.put(id, new String[]{name, Integer.toString(connectionType), date, month, year, Double.toString(previousUnit), Double.toString(currentUnit), Double.toString(billAmount)});
                } 
				catch (NumberFormatException e)
				{
                    System.err.println("Error parsing line: " + line);
                }
            } 
			else 
			{
                System.err.println("Invalid line format: " + line);
            }
        }
    } 
	catch (IOException e) 
	{
        e.printStackTrace();
        createDataFile();
    }
}

    private void createDataFile() 
	{
    try (PrintWriter writer = new PrintWriter(new FileWriter("electricity_data.txt")))
	{
        
        writer.println("  ID  ,  Name  ,  ConnectionType  ,  Date  ,  Month  ,  Year  ,  PreviousUnit  ,  CurrentUnit  ,  BillAmount");
    } 
	catch (IOException e) 
	{
        e.printStackTrace();
    }
}

   private void saveDataToFile() 
   {
    try (PrintWriter writer = new PrintWriter(new FileWriter("electricity_data.txt"))) 
	{
       
        writer.println("ID,Name,ConnectionType,Date,Month,Year,PreviousUnit,CurrentUnit,BillAmount");
        for (int id : dataMap.keySet()) 
		{
            String[] billData = dataMap.get(id);
            writer.printf("%d,%s,%d,%s,%s,%s,%.2f,%.2f,%.2f%n",
                    id,
                    billData[0],  // Name
                    Integer.parseInt(billData[1]),  // ConnectionType
                    billData[2],  // Date
                    billData[3],  // Month
                    billData[4],  // Year
                    Double.parseDouble(billData[5]),  // PreviousUnit
                    Double.parseDouble(billData[6]),  // CurrentUnit
                    Double.parseDouble(billData[7]));  // BillAmount
        }
    } 
	catch (IOException e) 
	{
        e.printStackTrace();
    }
}


    private String[] generateNumbersArray(int start, int end) 
	{
        String[] numbers = new String[end - start + 1];
        for (int i = start; i <= end; i++) 
		{
            numbers[i - start] = Integer.toString(i);
        }
        return numbers;
    }
}

public class ElectricityBillGeneratorApp1 
 {
    public static void main(String[] args) 
	{
        SwingUtilities.invokeLater(() -> 
		{
            ElectricityBillGeneratorApplet applet = new ElectricityBillGeneratorApplet();
            applet.init();
            JFrame frame = new JFrame("Electricity Bill Generator");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(applet);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
