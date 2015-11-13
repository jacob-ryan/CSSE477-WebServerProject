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
			PreparedStatement p1 = this.db.prepareStatement(query);
			p1.setString(1, animation.name);
			p1.setString(2, animation.dateCreated);
			p1.setString(3, animation.author);
			p1.setString(4, animation.description);
			p1.setString(5, animation.animations);
			System.out.println("Inserted " + p1.executeUpdate() + " records!");
			
			query = "SELECT LAST_INSERT_ID();";
			PreparedStatement p2 = this.db.prepareStatement(query);
			ResultSet r = p2.executeQuery();
			
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