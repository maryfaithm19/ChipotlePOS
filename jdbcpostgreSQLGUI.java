import javax.swing.JOptionPane;

//import org.graalvm.compiler.lir.StandardOp.NullCheck;

import java.sql.*;
import java.util.*;

// import javax.swing.JFrame;
//import java.sql.DriverManager;

/*
CSCE 315
9-25-2019
 */
public class jdbcpostgreSQLGUI
{
  public static void main(String args[])
  {
    dbSetup my = new dbSetup();
    // Building the connection
    Connection conn = null;

    // clone connection
    try
    {
      Class.forName("org.postgresql.Driver");
      conn = DriverManager.getConnection("jdbc:postgresql://csce-315-db.engr.tamu.edu/db905_group17_project2", my.user,
        my.pswd);
    }
    catch (Exception e)
    {
      e.printStackTrace();
      System.err.println(e.getClass().getName() + ": " + e.getMessage());
      System.exit(0);

    } // end try catch
    //Recommendation engine
    recommendation rec = new recommendation(); //Start Recommendation Engine (NOT IN USE YET)

    String[] buttons = { "Admin", "Customer" };  // user selection
    int answer = JOptionPane.showOptionDialog(null, "Are you a customer or admin?", "Chipotle Wannabe",
      JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, buttons[0]);

    String sqlStatement = "";
    boolean cont = true;

    if (answer == 1)  // Customer
    {
    	Double price = 0.00;  // total price of order
      String c_name = JOptionPane.showInputDialog("Customer Info \nName: ");  // get customer name

      // Order is filled in the following order: entree, side, drink, dessert
      ArrayList<Integer> entree = new ArrayList<Integer>();
      ArrayList<String> entreeAlt = new ArrayList<String>();
      ArrayList<Integer> side = new ArrayList<Integer>();
      ArrayList<String> sideAlt = new ArrayList<String>();
      ArrayList<Integer> drink = new ArrayList<Integer>();
      ArrayList<String> drinkAlt = new ArrayList<String>();
      ArrayList<Integer> dessert = new ArrayList<Integer>();
      ArrayList<String> dessertAlt = new ArrayList<String>();
      ArrayList<String> orderedItems = new ArrayList<String>();


      try
      {
        while (cont == true)  // continue ordering more items
        {
          String[] food_choice = { "Entree", "Side", "Drink", "Dessert" };
          int item_type = JOptionPane.showOptionDialog(null, "What do you want to order?", "Menu",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, food_choice, food_choice[0]);
          String menu_item = "";

          if (item_type == 0)  // order entree
          {
            // create a statement object
            Statement stmt = conn.createStatement();
            // create an SQL statement
            sqlStatement = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Entree\' ORDER BY \"Item_ID\"";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);

            // retrieve all menu items that are entrees
            ArrayList<String> arr = new ArrayList<String>();
            while (result.next())
            {
              menu_item = result.getString("Name");
              arr.add(menu_item);
            }

            // store entree menu items in array list to display as buttons
            String[] entree_items = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++)
            {
              entree_items[i] = arr.get(i);
            }

            // display entree item buttons
            int e_choice = JOptionPane.showOptionDialog(null, "Select entree: \n We suggest: " + rec.recToCustomer("E", rec.priority, conn), "Menu", JOptionPane.DEFAULT_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, entree_items, entree_items[0]);
            Statement avail = conn.createStatement();
            sqlStatement = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'E"+(e_choice+1)+"'";
            ResultSet avail_check = avail.executeQuery(sqlStatement);

            int stock = 0; 
            while (avail_check.next()){
              stock = avail_check.getInt("Stock");
            }

            if (stock > 0){
              entree.add(e_choice+1);  // store the customer's entree choice
              orderedItems.add(entree_items[e_choice]);

              Statement stmt1 = conn.createStatement();
              sqlStatement = "SELECT \"Price\" FROM \"Menu\" WHERE \"Item_ID\" = 'E"+(e_choice+1)+"'";
              ResultSet e_price = stmt1.executeQuery(sqlStatement);

              while (e_price.next())
              { price += e_price.getDouble("Price"); }

              String sqlStatement1 = "UPDATE \"Menu\" SET \"Stock\" = '"+ (stock-1) + "'WHERE \"Item_ID\" = 'E"+(e_choice+1)+"'";
              stmt1.executeUpdate(sqlStatement1);
            }
            else {
              String[] moreDone = { "Yes", "No" };
              int md = JOptionPane.showOptionDialog(null, "We're sorry, the item you ordered is out of stock. \nWould you like to order something else?", 
              null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
            }

            String entreeAltText = JOptionPane.showInputDialog("Do you have any topping alterations or special request?");
            entreeAlt.add(entreeAltText);  // store the customer's entree Alteration choice
          }
          else if (item_type == 1)  // order side
          {
            // create a statement object
            Statement stmt = conn.createStatement();
            // create an SQL statement
            sqlStatement = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Side\' ORDER BY \"Item_ID\"";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);

            // retrieve all menu items that are sides
            ArrayList<String> arr2 = new ArrayList<String>();
            while (result.next())
            {
              menu_item = result.getString("Name");
              arr2.add(menu_item);
            }

            // store side menu items in array list to display as buttons
            String[] side_choice = new String[arr2.size()];
            for (int i = 0; i < arr2.size(); i++)
            {
              side_choice[i] = arr2.get(i);
            }

            // display side item buttons
            int s_choice = JOptionPane.showOptionDialog(null, "Select side: \n We suggest: " + rec.recToCustomer("S", rec.priority, conn), "Menu", JOptionPane.DEFAULT_OPTION,
                          JOptionPane.QUESTION_MESSAGE, null, side_choice, side_choice[0]);
            Statement avail = conn.createStatement();
            sqlStatement = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'S"+(s_choice+1)+"'";
            ResultSet avail_check = avail.executeQuery(sqlStatement);

            int stock = 0; 
            while (avail_check.next()){
              stock = avail_check.getInt("Stock");
            }

            if (stock > 0){
              side.add(s_choice+1);  // store the customer's side choice
              orderedItems.add(side_choice[s_choice]);

              Statement stmt1 = conn.createStatement();
              sqlStatement = "SELECT \"Price\" FROM \"Menu\" WHERE \"Item_ID\" = 'S"+(s_choice+1)+"'";
              ResultSet result1 = stmt1.executeQuery(sqlStatement);

              while (result1.next())
              { price += result1.getDouble("Price"); }
              String sqlStatement1 = "UPDATE \"Menu\" SET \"Stock\" = '"+ (stock-1) + "'WHERE \"Item_ID\" = 'S"+(s_choice+1)+"'";
              stmt1.executeUpdate(sqlStatement1);
            }
            else {
              String[] moreDone = { "Yes", "No" };
              int md = JOptionPane.showOptionDialog(null, "We're sorry, the item you ordered is out of stock. \nWould you like to order something else?", 
              null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
            }

            String sideAltText = JOptionPane.showInputDialog("Do you have any topping alterations or special request?");
            sideAlt.add(sideAltText);  // store the customer's entree Alteration choice
          }
          else if (item_type == 2)  // order drink
          {
            // create a statement object
            Statement stmt = conn.createStatement();
            // create an SQL statement
            sqlStatement = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Drink\' ORDER BY \"Item_ID\"";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);

            // retrieve all side items from menu table
            ArrayList<String> arr3 = new ArrayList<String>();
            while (result.next())
            {
              menu_item = result.getString("Name");
              arr3.add(menu_item);
            }

            // store side menu items in array list to display as buttons
            String[] drink_choice = new String[arr3.size()];
            for (int i = 0; i < arr3.size(); i++)
            {
              drink_choice[i] = arr3.get(i);
            }

            int b_choice = JOptionPane.showOptionDialog(null, "Select a drink: \n We suggest: " + rec.recToCustomer("B", rec.priority, conn), "Menu", JOptionPane.DEFAULT_OPTION,
                          JOptionPane.QUESTION_MESSAGE, null, drink_choice, drink_choice[0]);

            Statement avail = conn.createStatement();
            sqlStatement = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'B"+(b_choice+1)+"'";
            ResultSet avail_check = avail.executeQuery(sqlStatement);

            int stock = 0; 
            while (avail_check.next()){
              stock = avail_check.getInt("Stock");
            }

            if (stock > 0){
              drink.add(b_choice+1);  // store customer's drink choice
              orderedItems.add(drink_choice[b_choice]);

              Statement stmt1 = conn.createStatement();
              sqlStatement = "SELECT \"Price\" FROM \"Menu\" WHERE \"Item_ID\" = 'B"+(b_choice+1)+"'";
              ResultSet result1 = stmt1.executeQuery(sqlStatement);

              while (result1.next())
              { price += result1.getDouble("Price"); }

              String sqlStatement1 = "UPDATE \"Menu\" SET \"Stock\" = '"+ (stock-1) + "'WHERE \"Item_ID\" = 'B"+(b_choice+1)+"'";
              stmt1.executeUpdate(sqlStatement1);
            }
            else {
              String[] moreDone = { "Yes", "No" };
              int md = JOptionPane.showOptionDialog(null, "We're sorry, the item you ordered is out of stock. \nWould you like to order something else?", 
              null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
  
            }

            String drinkAltText = JOptionPane.showInputDialog("Do you have any topping alterations or special request?");
            drinkAlt.add(drinkAltText);  // store the customer's entree Alteration choice
          }
          else if (item_type == 3)  // order dessert
          {
            // create a statement object
            Statement stmt = conn.createStatement();
            // create an SQL statement
            sqlStatement = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Dessert\' ORDER BY \"Item_ID\"";
            // send statement to DBMS
            ResultSet result = stmt.executeQuery(sqlStatement);

            // retrieve all side items from menu table
            ArrayList<String> arr4 = new ArrayList<String>();
            while (result.next())
            {
              menu_item = result.getString("Name");
              arr4.add(menu_item);
            }

            // store side menu items in array list to display as buttons
            String[] dessert_choice = new String[arr4.size()];
            for (int i = 0; i < arr4.size(); i++)
            {
              dessert_choice[i] = arr4.get(i);
            }

            int d_choice = JOptionPane.showOptionDialog(null, "Select dessert: \n We suggest: " + rec.recToCustomer("D", rec.priority, conn), "Menu", JOptionPane.DEFAULT_OPTION,
              JOptionPane.QUESTION_MESSAGE, null, dessert_choice, dessert_choice[0]);

            Statement avail = conn.createStatement();
            sqlStatement = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'D"+(d_choice+1)+"'";
            ResultSet avail_check = avail.executeQuery(sqlStatement);

            int stock = 0; 
            while (avail_check.next()){
              stock = avail_check.getInt("Stock");
            }

            if (stock > 0){
              dessert.add(d_choice+1);    // store customer's dessert choice
              orderedItems.add(dessert_choice[d_choice]);

              Statement stmt1 = conn.createStatement();
              sqlStatement = "SELECT \"Price\" FROM \"Menu\" WHERE \"Item_ID\" = 'D"+(d_choice+1)+"'";
              ResultSet result1 = stmt1.executeQuery(sqlStatement);

              while (result1.next())
              { price += result1.getDouble("Price"); }
              String sqlStatement1 = "UPDATE \"Menu\" SET \"Stock\" = '"+ (stock-1) + "'WHERE \"Item_ID\" = 'D"+(d_choice+1)+"'";
              stmt1.executeUpdate(sqlStatement1);
            }
            else {
              String[] moreDone = { "Yes", "No" };
              int md = JOptionPane.showOptionDialog(null, "We're sorry, the item you ordered is out of stock. \nWould you like to order something else?", 
              null, JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
            }

            String dessertAltText = JOptionPane.showInputDialog("Do you have any topping alterations or special request?");
            dessertAlt.add(dessertAltText);  // store the customer's entree Alteration choice
          }

          // Continue the order
          String[] moreDone = { "Yes", "No" };
          int md = JOptionPane.showOptionDialog(null, "Total Price: "+price+"\nWould you like to order more?", null, JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);

          if (md == 1)  // done ordering
          {
        	  do
        	  {
        		  // display order and price here
            	  for (int i = 0; i < orderedItems.size(); i++)
            	  {
            		  System.out.println(orderedItems.get(i));
            	  }
            	  
                  String [] addRemove = {"Add", "Remove", "Complete Order"};
                  md = JOptionPane.showOptionDialog(null, "Would you like to add or remove any items?", null, JOptionPane.DEFAULT_OPTION,
                  JOptionPane.QUESTION_MESSAGE, null, addRemove, addRemove[0]);
                  
                  if (md == 1)  // remove items
                  {
                	  String ordered[] = new String[orderedItems.size()];              
                	  for(int j =0;j<orderedItems.size();j++){
                		  ordered[j] = orderedItems.get(j);
            		  }

                      int remove_choice = JOptionPane.showOptionDialog(null, "Which items would you like to remove?", "Menu", JOptionPane.DEFAULT_OPTION,
                              JOptionPane.QUESTION_MESSAGE, null, ordered, ordered[0]);

                      String item_removed = ordered[remove_choice];
                      System.out.println("removing: " + item_removed);
                      int stockInt = 0;
                      Statement stock = conn.createStatement();
                      String stockCheck = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Name\" = \'"+ item_removed +"\'";
                      ResultSet resultStock = stock.executeQuery(stockCheck);
                      
                      while (resultStock.next())
                      { stockInt = resultStock.getInt("Stock"); }

                      stockInt += 1;
                      String sqlStatement2 = "UPDATE \"Menu\" SET \"Stock\" = '"+ stockInt + "'WHERE \"Name\" = '"+ item_removed +"'";
                      stock.executeUpdate(sqlStatement2);
                      System.out.println("stock: " + stockInt);
                    
                  }
                  else if (md == 2)  // complete order
                  {
                	  cont = false;
                  }
                  
        	  } while (md == 1);
          }

          // Continue the order
          String[] moreDone1 = {"No", "Yes"};
          md = JOptionPane.showOptionDialog(null, "Are you ready to pay?", null, JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE, null, moreDone1, moreDone1[0]);
          
          if (md == 1)  // complete order
          { cont = false; }

        } // end while loop

        Array e_arr = conn.createArrayOf("INTEGER", entree.toArray());
        Array eAlt_arr = conn.createArrayOf("INTEGER", entreeAlt.toArray());
        Array s_arr = conn.createArrayOf("INTEGER", side.toArray());
        Array sAlt_arr = conn.createArrayOf("INTEGER", sideAlt.toArray());
        Array b_arr = conn.createArrayOf("INTEGER", drink.toArray());
        Array bAlt_arr = conn.createArrayOf("INTEGER", drinkAlt.toArray());
        Array d_arr = conn.createArrayOf("INTEGER", dessert.toArray());
        Array dAlt_arr = conn.createArrayOf("INTEGER", dessertAlt.toArray());

        // GET NEXT ORDER ID
        int  orderID = 0;
        Statement stmt2 = conn.createStatement();
        sqlStatement = "SELECT \"Order_ID\" FROM \"Orders\" ORDER BY \"Order_ID\" DESC LIMIT 1";
        ResultSet result2 = stmt2.executeQuery(sqlStatement);
        while (result2.next())
        {
        	orderID = result2.getInt("Order_ID") + 1;
        }

        // PLACE ORDER
        Statement stmt1 = conn.createStatement();
        sqlStatement = "INSERT INTO \"Orders\" (\"Order_ID\", \"Name\", \"Date\", \"Price_Total\", \"Entree\", \"Entree_Alterations\", \"Sides\", \"Sides_Alterations\", \"Dessert\", \"Dessert_Alterations\", \"Beverage\", \"Beverage_Alterations\") VALUES ('"+orderID+"', '"+c_name+"', '"+java.time.LocalDate.now()+"', '"+price+"', '"+e_arr+"', '"+eAlt_arr+"', '"+s_arr+"', '"+sAlt_arr+"', '"+d_arr+"', '"+dAlt_arr+"', '"+b_arr+"', '"+bAlt_arr+"')";
        stmt1.executeUpdate(sqlStatement);

        // PRINT ORDER
        String summarize = "";
        
        Statement stm = conn.createStatement();
        sqlStatement = "SELECT * FROM \"Orders\" ORDER BY \"Order_ID\" DESC LIMIT 1";
        ResultSet r = stm.executeQuery(sqlStatement);

        r.next();
        
        summarize += "Customer's name: ";
        summarize += r.getString("Name") + "\n";
        
        
        //print out entree
        for(int s: entree){
          //create statement object
          Statement stmt3 = conn.createStatement();
          // create sql statement
          String sqlStatement1 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Item_ID\" = \'E"+s+"\'";
          //create send statement to DBMS
          ResultSet result = stmt3.executeQuery(sqlStatement1);
          String value = "";
          while(result.next()){
            value = result.getString(1);
            System.out.println(value);
          }
          summarize += value + "\n";
        }
        //print out side items
        
        for(int s : side){
          
          //create statement object
          Statement stmt3 = conn.createStatement();
          // create sql statement
          String sqlStatement1 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Item_ID\" = \'S"+s+"\'";
          //create send statement to DBMS
          ResultSet result = stmt3.executeQuery(sqlStatement1);
          String value = "";
          while(result.next()){
            value = result.getString(1);
            System.out.println(value);
          }
          summarize += value + "\n"; 
        }
        //print out drink
        for(int s : drink){
          
          //create statement object
          Statement stmt3 = conn.createStatement();
          // create sql statement
          String sqlStatement1 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Item_ID\" = \'B"+s+"\'";
          //create send statement to DBMS
          ResultSet result = stmt3.executeQuery(sqlStatement1);
          String value = "";
          while(result.next()){
            value = result.getString(1);
            System.out.println(value);
          }
          summarize += value + "\n"; 
        }
        //print out dessert
        for(int s : dessert){
          //create statement object
          Statement stmt3 = conn.createStatement();
          // create sql statement
          String sqlStatement1 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Item_ID\" = \'D"+s+"\'";
          //create send statement to DBMS
          ResultSet result = stmt3.executeQuery(sqlStatement1);
          String value = "";
          while(result.next()){
            value = result.getString(1);
            System.out.println(value);
          }
          summarize += value + "\n"; 
        }
        
        summarize += "The total price is: " + r.getDouble("Price_Total") + "\n";
        //print out the whole order
        JOptionPane.showMessageDialog(null, summarize);


        JOptionPane.showMessageDialog(null, "Thank you for choosing our service! Hope to see you soon.");

      } // end try
      catch (Exception e)
      {
        JOptionPane.showMessageDialog(null, "Error accessing Database.");
      }
    } // end customer

    else if (answer == 0)  // Admin
    {
      Double newprice = -0.01;
      JOptionPane.showMessageDialog(null, "Admin");
      String c_name = JOptionPane.showInputDialog("Admin Info \nName: ");
      
      String[] sumOrPrice = {"Summary", "Change Prices"};
      int action = JOptionPane.showOptionDialog(null, "Do you want to view a summary or change prices?", "Menu",
              JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, sumOrPrice, sumOrPrice[0]);
      if(action == 0)
      {
     	 String start_d = JOptionPane.showInputDialog("Give a start day ");
     	 String start_m = JOptionPane.showInputDialog("Give a start month ");
     	 String start_y = JOptionPane.showInputDialog("Give a start year ");
     	 String end_d = JOptionPane.showInputDialog("Give an end day ");
     	 String end_m = JOptionPane.showInputDialog("Give an end month ");
     	 String end_y = JOptionPane.showInputDialog("Give an end year ");
     	 
     	 dateStruct startDate = new dateStruct(start_d,start_m,start_y);
     	 dateStruct endDate = new dateStruct(end_d,end_m,end_y);
     	 
     	 String sum = rec.Summary(startDate, endDate, conn);
     	 JOptionPane.showMessageDialog(null, sum);
     	 
     	 
     	 
      }
      else if(action == 1)
      {

	      try {
	        while (cont == true)  // continue making changes
	        {
	          String[] food_choice = { "Entree", "Side", "Drink", "Dessert" };
	          int choice = JOptionPane.showOptionDialog(null, "Which item do you want to change price?", "Menu",
	            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, food_choice, food_choice[0]);
	          String menu_item = "";
	
	          if (choice == 0) // order entree
	          {
	            // create a statement object
	            Statement stmt = conn.createStatement();
	            // create an SQL statement
	            String sqlStatement2 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Entree\' ORDER BY \"Item_ID\"";
	            // send statement to DBMS
	            ResultSet result = stmt.executeQuery(sqlStatement2);
	
	            // retrieve all entree items from menu table
	            ArrayList<String> arr1 = new ArrayList<String>();
	            while (result.next())
	            {
	              menu_item = result.getString("Name");
	              arr1.add(menu_item);
	            }
	
	            // store entree menu items in array list to display as buttons
	            String[] entree_choice = new String[arr1.size()];
	            for (int i = 0; i < arr1.size(); i++)
	            {
	              entree_choice[i] = arr1.get(i);
	            }
	
	            int e_choice = JOptionPane.showOptionDialog(null, "Select entree: ", "Menu", JOptionPane.DEFAULT_OPTION,
	              JOptionPane.QUESTION_MESSAGE, null, entree_choice, entree_choice[0]);
	
	            Statement stmt1 = conn.createStatement();
	
	            // Select item and check stock
	            int stockInt = 0;
	            Statement stock = conn.createStatement();
	            String stockCheck = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'E"+(e_choice+1)+"'";
	            ResultSet resultStock = stock.executeQuery(stockCheck);
	            while (resultStock.next())
	            { stockInt = resultStock.getInt("Stock"); }
	
	            if (stockInt > 0) {
	              // do while for error checking price
	              do {
	                String priceAdmin = JOptionPane.showInputDialog("New price: ");
	                // If does error checking on null, empty, and letters.
	                if (!(priceAdmin == null) && !priceAdmin.isEmpty() && !priceAdmin.matches("[a-zA-Z]+"))
	                  newprice = Double.parseDouble(priceAdmin);
	              } while (!(newprice >= 0.00));
	              String sqlStatement1 = "UPDATE \"Menu\" SET \"Price\" = '"+ newprice +"' WHERE \"Item_ID\" = 'E"+(e_choice+1)+"'";
	              stmt1.executeUpdate(sqlStatement1);
	            } else {
	              JOptionPane.showMessageDialog(null,"Stock count is 0");
	            }
	
	            String[] moreDone = { "Yes", "No" };
	            int md = JOptionPane.showOptionDialog(null, "Do you make more changes?", null, JOptionPane.DEFAULT_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
	
	            if (md == 1)
	            { cont = false; }
	          }
	          if (choice == 1) // order Side
	          {
	            // create a statement object
	            Statement stmt = conn.createStatement();
	            // create an SQL statement
	            String sqlStatement2 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Side\' ORDER BY \"Item_ID\"";
	            // send statement to DBMS
	            ResultSet result = stmt.executeQuery(sqlStatement2);
	
	            // retrieve all entree items from menu table
	            ArrayList<String> arr1 = new ArrayList<String>();
	            while (result.next())
	            {
	              menu_item = result.getString("Name");
	              arr1.add(menu_item);
	            }
	
	            // store entree menu items in array list to display as buttons
	            String[] entree_choice = new String[arr1.size()];
	            for (int i = 0; i < arr1.size(); i++)
	            {
	              entree_choice[i] = arr1.get(i);
	            }
	
	            int e_choice = JOptionPane.showOptionDialog(null, "Select side: ", "Menu", JOptionPane.DEFAULT_OPTION,
	              JOptionPane.QUESTION_MESSAGE, null, entree_choice, entree_choice[0]);
	
	            Statement stmt1 = conn.createStatement();
	
	            // Select item and check stock
	            int stockInt = 0;
	            Statement stock = conn.createStatement();
	            String stockCheck = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'S"+(e_choice+1)+"'";
	            ResultSet resultStock = stock.executeQuery(stockCheck);
	            while (resultStock.next())
	            { stockInt = resultStock.getInt("Stock"); }
	
	            if (stockInt > 0) {
	              // do while for error checking price
	              do {
	                String priceAdmin = JOptionPane.showInputDialog("New price: ");
	                // If does error checking on null, empty, and letters.
	                if (!(priceAdmin == null) && !priceAdmin.isEmpty() && !priceAdmin.matches("[a-zA-Z]+"))
	                  newprice = Double.parseDouble(priceAdmin);
	              } while (!(newprice >= 0.00));
	              String sqlStatement1 = "UPDATE \"Menu\" SET \"Price\" = '"+ newprice +"' WHERE \"Item_ID\" = 'S"+(e_choice+1)+"'";
	              stmt1.executeUpdate(sqlStatement1);
	            } else {
	              JOptionPane.showMessageDialog(null,"Stock count is 0");
	            }
	
	            String[] moreDone = { "Yes", "No" };
	            int md = JOptionPane.showOptionDialog(null, "Do you make more changes?", null, JOptionPane.DEFAULT_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
	
	            if (md == 1)
	            { cont = false; }
	          }
	          if (choice == 2) // order Drink
	          {
	            // create a statement object
	            Statement stmt = conn.createStatement();
	            // create an SQL statement
	            String sqlStatement2 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Drink\' ORDER BY \"Item_ID\"";
	            // send statement to DBMS
	            ResultSet result = stmt.executeQuery(sqlStatement2);
	
	            // retrieve all entree items from menu table
	            ArrayList<String> arr1 = new ArrayList<String>();
	            while (result.next())
	            {
	              menu_item = result.getString("Name");
	              arr1.add(menu_item);
	            }
	
	            // store entree menu items in array list to display as buttons
	            String[] entree_choice = new String[arr1.size()];
	            for (int i = 0; i < arr1.size(); i++)
	            {
	              entree_choice[i] = arr1.get(i);
	            }
	
	            int e_choice = JOptionPane.showOptionDialog(null, "Select drink: ", "Menu", JOptionPane.DEFAULT_OPTION,
	              JOptionPane.QUESTION_MESSAGE, null, entree_choice, entree_choice[0]);
	
	            Statement stmt1 = conn.createStatement();
	
	            // Select item and check stock
	            int stockInt = 0;
	            Statement stock = conn.createStatement();
	            String stockCheck = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'B"+(e_choice+1)+"'";
	            ResultSet resultStock = stock.executeQuery(stockCheck);
	            while (resultStock.next())
	            { stockInt = resultStock.getInt("Stock"); }
	
	            if (stockInt > 0) {
	              // do while for error checking price
	              do {
	                String priceAdmin = JOptionPane.showInputDialog("New price: ");
	                // If does error checking on null, empty, and letters.
	                if (!(priceAdmin == null) && !priceAdmin.isEmpty() && !priceAdmin.matches("[a-zA-Z]+"))
	                  newprice = Double.parseDouble(priceAdmin);
	              } while (!(newprice >= 0.00));
	              String sqlStatement1 = "UPDATE \"Menu\" SET \"Price\" = '"+ newprice +"' WHERE \"Item_ID\" = 'B"+(e_choice+1)+"'";
	              stmt1.executeUpdate(sqlStatement1);
	            } else {
	              JOptionPane.showMessageDialog(null,"Stock count is 0");
	            }
	
	            String[] moreDone = { "Yes", "No" };
	            int md = JOptionPane.showOptionDialog(null, "Do you make more changes?", null, JOptionPane.DEFAULT_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
	
	            if (md == 1)
	            { cont = false; }
	          }
	          if (choice == 3) // order dessert
	          {
	            // create a statement object
	            Statement stmt = conn.createStatement();
	            // create an SQL statement
	            String sqlStatement2 = "SELECT \"Name\" FROM \"Menu\" WHERE \"Type\" =\'Dessert\' ORDER BY \"Item_ID\"";
	            // send statement to DBMS
	            ResultSet result = stmt.executeQuery(sqlStatement2);
	
	            // retrieve all entree items from menu table
	            ArrayList<String> arr1 = new ArrayList<String>();
	            while (result.next())
	            {
	              menu_item = result.getString("Name");
	              arr1.add(menu_item);
	            }
	
	            // store entree menu items in array list to display as buttons
	            String[] entree_choice = new String[arr1.size()];
	            for (int i = 0; i < arr1.size(); i++)
	            {
	              entree_choice[i] = arr1.get(i);
	            }
	
	            int e_choice = JOptionPane.showOptionDialog(null, "Select dessert: ", "Menu", JOptionPane.DEFAULT_OPTION,
	              JOptionPane.QUESTION_MESSAGE, null, entree_choice, entree_choice[0]);
	
	            Statement stmt1 = conn.createStatement();
	
	            // Select item and check stock
	            int stockInt = 0;
	            Statement stock = conn.createStatement();
	            String stockCheck = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Item_ID\" = 'D"+(e_choice+1)+"'";
	            ResultSet resultStock = stock.executeQuery(stockCheck);
	            while (resultStock.next())
	            { stockInt = resultStock.getInt("Stock"); }
	
	            if (stockInt > 0) {
	              // do while for error checking price
	              do {
	                String priceAdmin = JOptionPane.showInputDialog("New price: ");
	                // If does error checking on null, empty, and letters.
	                if (!(priceAdmin == null) && !priceAdmin.isEmpty() && !priceAdmin.matches("[a-zA-Z]+"))
	                  newprice = Double.parseDouble(priceAdmin);
	              } while (!(newprice >= 0.00));
	              String sqlStatement1 = "UPDATE \"Menu\" SET \"Price\" = '"+ newprice +"' WHERE \"Item_ID\" = 'D"+(e_choice+1)+"'";
	              stmt1.executeUpdate(sqlStatement1);
	            } else {
	              JOptionPane.showMessageDialog(null,"Stock count is 0");
	            }
	
	            String[] moreDone = { "Yes", "No" };
	            int md = JOptionPane.showOptionDialog(null, "Do you make more changes?", null, JOptionPane.DEFAULT_OPTION,
	                JOptionPane.QUESTION_MESSAGE, null, moreDone, moreDone[0]);
	
	            if (md == 1)
	            { cont = false; }
	          }
	
	        }
	      }//end try
	      catch (Exception e) {
	        JOptionPane.showMessageDialog(null, "Error accessing Database.");
	      }
      }
    } // end admin
  }// end main
}// end Class