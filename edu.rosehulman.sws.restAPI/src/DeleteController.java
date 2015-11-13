import java.sql.*;
import java.util.regex.*;

import protocol.*;

public class DeleteController extends Controller
{
	@Override
	public void start()
	{
		System.out.println("[DeleteController] Starting...");
		super.start();
	}

	@Override
	public void stop()
	{
		System.out.println("[DeleteController] Stopping...");
		super.stop();
	}

	@Override
	public void processRequest(HttpRequest request, HttpResponse response, String rootDirectory)
	{
		try
		{
			String[] parts = request.getUri().split(Pattern.quote("/"));
			int id = Integer.parseInt(parts[parts.length - 1]);
			
			String query = "DELETE FROM animations WHERE id = ?;";
			PreparedStatement p1 = this.db.prepareStatement(query);
			p1.setInt(1, id);
			System.out.println("Deleted " + p1.executeUpdate() + " records!");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			response.setStatus(Protocol.BAD_REQUEST_CODE);
			response.setPhrase(Protocol.BAD_REQUEST_TEXT);
		}
	}
}