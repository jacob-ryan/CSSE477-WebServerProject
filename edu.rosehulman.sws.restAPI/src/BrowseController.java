import java.sql.*;
import java.util.*;
import protocol.*;

public class BrowseController extends Controller
{
	@Override
	public void start()
	{
		System.out.println("[BrowseController] Starting...");
		super.start();
	}

	@Override
	public void stop()
	{
		System.out.println("[BrowseController] Stopping...");
		super.stop();
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		try
		{
			String query = "SELECT * FROM animations.animations;";
			PreparedStatement p = this.db.prepareStatement(query);
			ResultSet r = p.executeQuery();
			List<Animation> a = this.getAnimations(r);
			
			String json = this.gson.toJson(a);
			
			response.setBody(json.getBytes());
			System.out.println("Set body to: " + json);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
		}
	}
}