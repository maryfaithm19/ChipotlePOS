public class dateStruct 
{
	//Data
	String day;
	String month;
	String year;
	String date;
	dateStruct(String day_, String month_, String year_)
	{
		if(day_.length()==1)
			day = "0" + day_.toString();
		else if(day_.length()==2)
			day = day_.toString();
		else
			System.err.println("Incorrect date format");
		
		if(month_.length()==1)
			month = "0" + month_.toString();
		else if(month_.length()==2)
			month = month_.toString();
		else
			System.err.println("Incorrect date format");
		
		if(year_.length()==4)
			year = year_.toString();
		else
			System.err.println("Incorrect date format");
		
		date = year + "-" + month + "-" + day;
	}
	void printDate()
	{
		System.out.println(date);
	}
	
}