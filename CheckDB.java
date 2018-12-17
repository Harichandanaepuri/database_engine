import java.io.File;

public class CheckDB {

	public static boolean checkDB(String database)
	{
		File catalog = new File("data\\"+database);
		
		if(catalog.exists())
		{
			DavisBase.currentDatabase=database;						
			return true;
		}
		return false;
	}

	
	public static boolean tableExist(String table) 
	{
		boolean table_check = false;
		try 
		{
			File user_tables = new File("data\\"+DavisBase.currentDatabase);
			if (user_tables.mkdir()) 
			{
				System.out.println("System directory 'data\\user_data' doesn't exit, Initializing user_data!");
			}
			String[] tableList;
			tableList = user_tables.list();
			for (int i = 0; i < tableList.length; i++) 
			{
				if (tableList[i].equals(table))
					return true;
			}
		} 
		catch (SecurityException se) 
		{
			System.out.println("Unable to create data container directory" + se);
		}
		return table_check;
	}
	
	
	
}
