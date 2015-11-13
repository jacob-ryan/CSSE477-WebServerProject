import java.sql.*;
import protocol.*;

public class PutController extends Controller
{
	@Override
	public void start()
	{
		System.out.println("[PutController] Starting...");
		super.start();
	}

	@Override
	public void stop()
	{
		System.out.println("[PutController] Stopping...");
		super.stop();
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		try
		{
			String json = new String(request.getBody());
			Animation animation = this.gson.fromJson(json, Animation.class);
			
			String query = "UPDATE animations SET (?, ?, ?, ?, ?, ?) WHERE id = ?;";
			PreparedStatement p1 = this.db.prepareStatement(query);
			p1.setInt(1, animation.id);
			p1.setString(2, animation.name);
			p1.setString(3, animation.dateCreated);
			p1.setString(4, animation.author);
			p1.setString(5, animation.description);
			p1.setString(6, animation.animations);
			p1.setInt(7, animation.id);
			System.out.println("Updated " + p1.executeUpdate() + " records!");
			
			String body = "" + animation.id;
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