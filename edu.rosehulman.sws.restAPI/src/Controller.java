import java.sql.*;
import java.util.*;

import com.google.gson.*;

import plugins.*;

public abstract class Controller implements IServlet
{
	protected Connection db = null;
	protected Gson gson = null;
	
	@Override
	public void start()
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://localhost/animations?user=sqluser&password=password";
			this.db = DriverManager.getConnection(url);
			this.gson = new GsonBuilder().create();
		}
		catch (ClassNotFoundException | SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop()
	{
		try
		{
			this.db.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	protected List<Animation> getAnimations(ResultSet results) throws SQLException
	{
		List<Animation> animations = new ArrayList<Animation>();
		while (results.next())
		{
			Animation a = new Animation();
			a.id = results.getInt("id");
			a.name = results.getString("name");
			a.dateCreated = results.getString("dateCreated");
			a.author = results.getString("author");
			a.description = results.getString("description");
			a.animations = results.getString("animations");
			animations.add(a);
		}
		return animations;
	}
	
	protected int getResultId(ResultSet results) throws SQLException
	{
		results.next();
		return results.getInt(0);
	}
}