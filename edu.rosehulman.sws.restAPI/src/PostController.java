import java.sql.*;
import protocol.*;

public class PostController extends Controller
{
	@Override
	public void start()
	{
		System.out.println("[PostController] Starting...");
		super.start();
	}

	@Override
	public void stop()
	{
		System.out.println("[PostController] Stopping...");
		super.stop();
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		try
		{
			String json = new String(request.getBody());
			Animation animation = this.gson.fromJson(json, Animation.class);
			
			String query = "INSERT INTO animations VALUES (0, ?, ?, ?, ?, ?);";
			query += "SELECT LAST_INSERT_ID();";
			PreparedStatement p = this.db.prepareStatement(query);
			p.setString(2, animation.name);
			p.setString(3, animation.dateCreated);
			p.setString(4, animation.author);
			p.setString(5, animation.description);
			p.setString(6, animation.animations);
			ResultSet r = p.executeQuery();
			
			int id = this.getResultId(r);
			String body = "" + id;
			response.setBody(body.getBytes());
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
		}
	}
}