import java.sql.*;
import java.util.*;
import java.util.regex.*;

import protocol.*;

public class GetController extends Controller
{
	@Override
	public void start()
	{
		System.out.println("[GetController] Starting...");
		super.start();
	}

	@Override
	public void stop()
	{
		System.out.println("[GetController] Stopping...");
		super.stop();
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		try
		{
			String[] parts = request.getUri().split(Pattern.quote("/"));
			int id = Integer.parseInt(parts[parts.length - 1]);
			
			String query = "SELECT * FROM animations.animations WHERE id = ?;";
			PreparedStatement p = this.db.prepareStatement(query);
			p.setInt(1, id);
			ResultSet r = p.executeQuery();
			List<Animation> a = this.getAnimations(r);
			
			Animation animation = a.get(0);
			String json = this.gson.toJson(animation);
			
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