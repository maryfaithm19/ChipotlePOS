import java.sql.*;
import java.util.*;

public class recommendation 
{
	//Initializers
	dbSetup my = new dbSetup();
	Connection conn = null;
	Integer priority = 1;
	recommendation()
	{
		
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
	}
	ArrayList<Integer> indexOfSmallest(ArrayList<Integer> arr) 
	{
		ArrayList<Integer> indecies = new ArrayList<Integer>();
		Integer min = Collections.min(arr);
		for(Integer i = 0; i < arr.size(); i++)
		{
			if(arr.get(i)-min == 0)
				indecies.add(i);
		}

	    return indecies;
	}
	ArrayList<Integer> indexOfLargest(ArrayList<Integer> arr) 
	{
		ArrayList<Integer> indecies = new ArrayList<Integer>();
		Integer max = Collections.max(arr);
		for(Integer i = 0; i < arr.size(); i++)
		{
			if(arr.get(i)-max == 0)
				indecies.add(i);
		}

	    return indecies;
	}
	String getName(String type, Integer num, Connection conn)
	{
		try 
		{
			String sqlStatement = "SELECT \"Name\" FROM \"Menu\" WHERE \"Item_ID\" = '"+type+(num+1)+"'";
			Statement stmt = conn.createStatement();
			ResultSet e_name = stmt.executeQuery(sqlStatement);
			e_name.next();
			String name = e_name.getString("Name");
			//System.out.println("Name of "+ type+(num+1) + "=" + name);
			return name;
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Failed to get Name of "+ type+(num+1));
			e.printStackTrace();
		}
		
		return null;
	}

	Integer getStock(String item, Connection conn) //Get stock of an item
	{
		try 
		{
			String sqlStatement = "SELECT \"Stock\" FROM \"Menu\" WHERE \"Name\" = '" + item + "'";
			Statement stmt = conn.createStatement();
			ResultSet e_stock = stmt.executeQuery(sqlStatement);
			e_stock.next();
			Integer stock = e_stock.getInt("Stock");
			//System.out.println("Stock of "+ item + "=" + stock);
			return stock;
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Failed to get Stock of "+ item);
			e.printStackTrace();
		}
		
		return 0;
	}
	
	Double getPrice(String item, Connection conn) //Get price of an item
	{
		try 
		{
			String sqlStatement = "SELECT \"Price\" FROM \"Menu\" WHERE \"Name\" = '" + item + "'";
			Statement stmt = conn.createStatement();
			ResultSet e_price = stmt.executeQuery(sqlStatement);
			e_price.next();
			Double price = e_price.getDouble("Price");
			//System.out.println("price of "+ item + "=" + price);
			return price;
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.out.println("Failed to get price of "+ item);
			e.printStackTrace();
		}
		
		return 0.0;
	}
	
	Integer getTypeSize(String type, Connection conn) //Get size of a type "D" for Drink or "E" for Entree etc.
	{
		if(type == "D" || type == "Desserts")
			type = "Dessert";
		else if(type == "E" || type == "Entrees")
			type = "Entree";
		else if(type == "B" || type == "Beverage")
			type = "Drink";
		else if(type == "S" || type == "Sides" )
			type = "Side";
		try 
		{
			String sqlStatement = "SELECT * FROM \"Menu\" WHERE \"Type\" = '" + type + "'";
			Statement stmt = conn.createStatement();
			ResultSet e_num = stmt.executeQuery(sqlStatement);
			Integer num = 0;
			while (e_num.next())
            {
              num++;
            }
			//System.out.println("Number of "+ type + "=" + num);
			return num;
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	ArrayList<String> getHighestStock(String type, Connection conn) //returns items with highest stock
	{
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Integer> stocks = new ArrayList<Integer>();
		Integer numItems = getTypeSize(type, conn);
		for(Integer i = 0; i < numItems; i++)
		{
			String name = getName(type, i, conn);
			//System.out.println(name);
			items.add(name);
			stocks.add(getStock(name,conn));
			
		}
		ArrayList<String> highest_stocks = new ArrayList<String>();
		Integer max = Collections.max(stocks);
		if (max == 0)
		{
			return null;
		}
		//System.out.println(max);
		for(Integer i = 0; i < getTypeSize(type, conn); i++)
		{
			String name = items.get(i);//(type, i, conn);
			Integer stock = stocks.get(i);//(name,conn);
			//System.out.println(stock);
			if(stock == max)
			{
				//System.out.println("here");
				highest_stocks.add(name);
			}
		}
		return highest_stocks;
	}
	
	ArrayList<String> getHighestPrice(String type, Connection conn) //Returns items with highest price
	{
		ArrayList<String> items = new ArrayList<String>();
		ArrayList<Double> prices = new ArrayList<Double>();
		Integer numItems = getTypeSize(type, conn);
		for(Integer i = 0; i < numItems; i++)
		{
			String name = getName(type, i, conn);
			//System.out.println(name);
			items.add(name);
			prices.add(getPrice(name,conn));
			
		}
		ArrayList<String> highest_prices = new ArrayList<String>();
		Double max = Collections.max(prices);
		for(Integer i = 0; i < getTypeSize(type, conn); i++)
		{
			String name = items.get(i);//(type, i, conn);
			Double price = prices.get(i);//(name,conn);
			//System.out.println(price);
			if(price - max==0)
			{
				//System.out.println("here");
				highest_prices.add(name);
			}
		}
		return highest_prices;
	}
	
	ArrayList<String> recToCustomer(String type, Integer priority, Connection conn) //Recommends items to customers depending on type
	{
		ArrayList<String> recommended = new ArrayList<String>();
		ArrayList<String> stocks = getHighestStock(type, conn); //list with highest stocks
		ArrayList<String> price = getHighestPrice(type, conn); // list with highest price
		
		if(stocks == null)
		{
			recommended.add("Out of Stock, Nothing to Recommend");
			return recommended;
		}
		ArrayList<String> common = new ArrayList<String>(stocks);
		common.retainAll(price);
		
		if(!common.isEmpty())
		{
			recommended = common; //Return it if item has highest price or stock
		}
		else if(priority == 0)
		{
			recommended = stocks;
		}
		else if(priority == 1)
		{
			recommended = price;
		}
		
		return recommended;
	}
	
	ResultSet getOrders(dateStruct startDate, dateStruct endDate, Connection conn) //Get all orders from start date to end date
	{
		
		try 
		{
			String sqlStatement = "SELECT * FROM \"Orders\" WHERE \"Date\" between \'" + startDate.date + 
					"\' and \'" + endDate.date +"\' ORDER BY \"Date\"";
			Statement stmt = conn.createStatement();
			ResultSet orders = stmt.executeQuery(sqlStatement);
			//System.out.println(sqlStatement);
			//System.out.println(orders.getFetchSize());
			return orders;
			
		} catch (SQLException e) 
		{
			// TODO Auto-generated catch block
			System.err.println("Failed to get orders from given dates");
			e.printStackTrace();
		}
		
		return null;
	}
	
	ArrayList<Integer> totalCountOrders(String type, ResultSet orders, Connection conn)//will count total number of items order in a specific type
	{
		if(type == "D")
			type = "Dessert";
		else if(type == "E")
			type = "Entree";
		else if(type == "B")
			type = "Beverage";
		else if(type == "S")
			type = "Sides";
		
		Integer totalItems = getTypeSize(type, conn); //each index will correspond to an item on menu
		//System.out.println(totalItems);
		
		ArrayList<Integer> counts = new ArrayList<Integer>();
		for(Integer i = 0; i < totalItems; i++) //Fill each index with 0
			counts.add(0);
		//return null;
		
		try 
		{
			while(orders.next())
			{
				Array orderItemsA = orders.getArray(type);
				Integer orderItems[] = (Integer[]) orderItemsA.getArray();
				//orderItems = (ArrayList<Integer>) orders.getArray(type);
				//System.out.println(orderItems.length);
				for(Integer j = 0; j < orderItems.length; j++)
				{
					Integer item = orderItems[j]-1;//jth Item orders
					Integer incNum = counts.get(item)+1;
					counts.set(item, incNum);
					//System.out.println(counts);
				}
				
			}
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		
		return counts;
	}
	
	String typeSummary(ArrayList<Integer> typeArray, String type, Connection conn)
	{
		String out = "";
		for(Integer i = 0; i < typeArray.size(); i++)
		{
			out += (getName(type,i,conn) + ":" + typeArray.get(i) + "\n");
		}
		return out;
	}
	String typeRecommendation(ArrayList<Integer> typeArray, String type, Connection conn)
	{
		String out = "";
		
		out += "The following had the lowest sales, consider stocking less or lowering the price: \n";
		ArrayList<Integer> lowestSales = indexOfSmallest(typeArray);
		for(Integer i = 0; i < lowestSales.size(); i++)
			out += getName(type,lowestSales.get(i),conn) + "\n";
		
		out += "The following had the highest sales, consider stocking more or increasing the price: \n";
		ArrayList<Integer> highestSales = indexOfLargest(typeArray);
		for(Integer i = 0; i < highestSales.size(); i++)
			out += getName(type,highestSales.get(i),conn) + "\n";
		
		return out;
	}
	
	String Summary(dateStruct startDate, dateStruct endDate, Connection conn)
	{
		//Get history of Orders
		ResultSet history = getOrders(startDate, endDate, conn);
		ArrayList<Integer> entreeSales = totalCountOrders("E", history, conn);
		history = getOrders(startDate, endDate, conn);
		ArrayList<Integer> sidesSales = totalCountOrders("S", history, conn);
		history = getOrders(startDate, endDate, conn);
		ArrayList<Integer> drinksSales = totalCountOrders("B", history, conn);
		history = getOrders(startDate, endDate, conn);
		ArrayList<Integer> dessertSales = totalCountOrders("D", history, conn);
		
		String out = "";
		
		//Prompt
		out+="Summary for " + startDate.date + "-" + endDate.date + "\n";
		out+="Sales: \n";
		
		out+="\n Entrees: \n";
		out+=typeSummary(entreeSales, "E", conn);
		
		out+="\n Sides: \n";
		out+=typeSummary(sidesSales, "S", conn);
		
		out+="\n Dessert: \n";
		out+=typeSummary(dessertSales, "D", conn);
		
		out+="\n Beverages: \n";
		out+=typeSummary(drinksSales, "B", conn);
		
		//Recommendation
		out+="\n Recommendations: \n";
		
		out+="\n Entrees: \n";
		out+=typeRecommendation(entreeSales, "E",conn);
		
		out+="\n Sides: \n";
		out+=typeRecommendation(sidesSales, "S", conn);
		
		out+="\n Dessert: \n";
		out+=typeRecommendation(dessertSales, "D", conn);
		
		out+="\n Beverages: \n";
		out+=typeRecommendation(drinksSales, "B", conn);
		
		return out;
	}
}