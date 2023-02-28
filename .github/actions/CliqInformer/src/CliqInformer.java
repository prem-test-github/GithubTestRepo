import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.WRITE;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;
public class CliqInformer {
	public static void main(String args[]) {
		System.out.println("Calling Cliq...");
		HttpURLConnection connection;
		Integer MAX_MESSAGE_LENGTH = 4096;
		String MESSAGE_BREAK = "\\n";
		Integer status = 400;
		boolean MESSAGE_SEND_FAILURE_ERROR = true;
		boolean INVALID_ENDPOINT_ERROR = true;
		boolean GITHUB_ERROR = true;
		String ERROR_MESSAGE = new String("Multiple Errors Occured");
		StringBuffer responseContent = new StringBuffer();
		try {
			String message;
			String CustomMessage;
			String CliqChannelLink = args[0];		  
			if(CliqChannelLink.contains("message") && CliqChannelLink.contains("https://cliq.zoho") && CliqChannelLink.contains("/api/v2/") && CliqChannelLink.contains("?zapikey="))
			  INVALID_ENDPOINT_ERROR = false;
			CustomMessage = args[1];
			String[] AddedInfo = args[2].split("_\\+_");
			for(String s : AddedInfo)
				System.out.println(s);
			String Repository = AddedInfo[0];
			String Event = AddedInfo[1];
			String[] EventWords = Event.split("_");
			Event = new String();
			for(String s: EventWords)
			  Event += s.substring(0,1).toUpperCase() + s.substring(1) + " ";
			Event = Event.trim();
			String Action = AddedInfo[2];
			if(!Action.equals(""))
			{
			  String[] ActionWords = Action.split("_");
			  Action = new String();
			  for(String s: ActionWords)
			    Action += s + " ";
			  Action = Action.trim();
			}
			if(Action.equals(""))
        		Action = "made";
			String CliqInformerURL = "https://workdrive.zohoexternal.com/external/047d96f793983933bbdb59deb9c44f5443b83a7188e278736405d4d733923181/download?directDownload=true";
			//message = CustomMessage;
			message = new String();
			if(Event == "Push")
			{
				String Pusher = AddedInfo[3];
				String Branch_Name = AddedInfo[4];
				String Commit_URL = AddedInfo[5];
				String Compare_URL = AddedInfo[6];
				message ="[" + Pusher + "](https://github.com/" + Pusher + ") has pushed a new [code](" + Commit_URL + ") in the branch [" + Branch_Name + "](https://github.com/" + Repository + "/tree/" + Branch_Name + ")\n[View Comparison](" + Compare_URL + ")";
			}
			ArrayList<String> messages = new ArrayList<String>();
			for(int i = 0 ; i < message.length() ;)
			{
			  String split_message;
			  if(i+MAX_MESSAGE_LENGTH < message.length())
			  {
			    split_message = message.substring(i,i+MAX_MESSAGE_LENGTH);
			    int displaced_length = MAX_MESSAGE_LENGTH;
			    if(split_message.contains(MESSAGE_BREAK))
			    {
			      displaced_length = split_message.lastIndexOf(MESSAGE_BREAK) + 2;
			      split_message = message.substring(i,i+displaced_length);
			      split_message = split_message.replaceAll("\\\\n","");
			    }
			    else if(split_message.contains("\n"))
			    {
			      displaced_length = split_message.lastIndexOf("\n") + 1;
			      split_message = message.substring(i,i+displaced_length);
			    }
			    else if(split_message.contains("."))
			    {
			      displaced_length = split_message.lastIndexOf(".") + 1;
			      split_message = message.substring(i,i+displaced_length);
			    }
			    i += displaced_length;
			  }
			  else
			  {
			    split_message = message.substring(i,message.length());
			    i+= MAX_MESSAGE_LENGTH;
			  }
			  messages.add(split_message);
			}
			for(String msg : messages)
			{
			  msg = msg.replace("\"","'");
			  String TextParams = "{\n\"text\":\"" + msg + "\",\n\"bot\":\n{\n\"name\":\"CliqInformer\",\n\"image\":\"" + CliqInformerURL + "\"\n}}";
			  connection = (HttpURLConnection) new URL(CliqChannelLink).openConnection();
			  connection.setRequestMethod("POST");
			  connection.setRequestProperty("Content-Type","application/json");
			  connection.setDoOutput(true);
			  OutputStream os = connection.getOutputStream();
			  os.write(TextParams.getBytes());
			  os.flush();
			  os.close();
			  status = connection.getResponseCode();
			  if(status > 299) {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
				  String line;
				  while((line = reader.readLine()) != null) {
					  responseContent.append(line);
				  }
			    reader.close();
			  }
			  else
			  {
				  BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				  String line;
				  while((line = reader.readLine()) != null) {
					  responseContent.append(line);
				  }
				  reader.close();
			  }
			  if(status != 204)
			    ERROR_MESSAGE = responseContent.toString();
			}
			/*String ServerURL = args[3];
			String Workflow = args[5];
			String Actor = args[6];
			String RunId = args[7];
			String Ref = args[8];
			String RefType = args[9];
			String ActorURL = ServerURL + "/" + Actor;
			String RepositoryURL = ServerURL + "/" + Repository;
			String WorkflowURL = RepositoryURL + "/actions/runs/" + RunId;
			String RefURL = RepositoryURL;
			if(Ref.contains("pull"))
			  RefURL = RefURL + "/pull/";
			else
			  RefURL = RefURL + "/tree/";
			if(Ref.split("/").length > 2)
			  Ref = Ref.split("/")[2];
			RefURL = RefURL + Ref;
			CustomMessage = args[10];
			message = message.replace("(me)","[" + Actor + "](" + ActorURL + ")");
			message = message.replace("(workflow)","[" + Workflow + "](" + WorkflowURL + ")" );
			message = message.replace("(repo)","[" + Repository + "](" + RepositoryURL + ")" );
			message = message.replace("(event)","*" + Event + "*");
			message = message.replace("(action)",Action);
			message = message.replace("(ref)",RefType + " [" + Ref + "](" + RefURL + ")" );
			if(message.length() > MAX_MESSAGE_LENGTH)
			{
			  message = CustomMessage;
			  message = message.replace("(me)","*" + Actor + "*");
			  message = message.replace("(workflow)","*" + Workflow + "*" );
			  message = message.replace("(repo)","*" + Repository + "*" );
			  message = message.replace("(event)","*" + Event + "*");
			  message = message.replace("(action)",Action);
			  message = message.replace("(ref)",RefType + " *" + Ref + "*" );
			}*/
			var githubOutput = System.getenv("GITHUB_OUTPUT");
			if(Objects.nonNull(githubOutput))
			    GITHUB_ERROR = false;
			if(status == 204)
			  MESSAGE_SEND_FAILURE_ERROR = false;
			if(INVALID_ENDPOINT_ERROR)
			  ERROR_MESSAGE = "Invalid Endpoint. Endpoint must be of format : <Zoho Cliq Channel API Endpoint>?zapikey=<Zoho Cliq Webhook Token>";
			else if(GITHUB_ERROR)
			  ERROR_MESSAGE = "Environmental Variable GITHUB_OUTPUT missing";
			else if(MESSAGE_SEND_FAILURE_ERROR)
			  ERROR_MESSAGE = splitMessage(ERROR_MESSAGE);
			else if(status == 204)
			  ERROR_MESSAGE = "CliqInformer executed Successfully";
			writeGithubOutput(status,ERROR_MESSAGE);
		}  catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally
		{
		  try
		  {
		    var githubOutput = System.getenv("GITHUB_OUTPUT");
		    var file = Path.of(githubOutput);
		    if(file.getParent() != null) Files.createDirectories(file.getParent());
		    if(MESSAGE_SEND_FAILURE_ERROR)
		    {
		      ERROR_MESSAGE = "Unknown Error Occured : " + ERROR_MESSAGE;
		    }
		    writeGithubOutput(status,ERROR_MESSAGE);
		  }
		  catch(Exception e)
		  {
		    ERROR_MESSAGE = "Sorry we couldn't process your request due to a technical error. Please Try again later.";
		    System.err.println("Unknown Error Occured : " + ERROR_MESSAGE);
		    System.exit(1);
		  }
		}
	}
	
	// To Split and Seperate the Message from the JSON
	public static String splitMessage(String JSON)
	{
	  JSON = JSON.substring(JSON.indexOf("{"), JSON.indexOf("}"));
	  String[] JSONArray = JSON.split(",");
	  for(String s : JSONArray)
	    if(s.contains("\"message\":"))
	      return s.substring(s.indexOf(":")+1,s.length());
	  return "Error Description not Provided";
	}
	
	// used to write a Github Output so that the Shell Runner can Read
	public static void writeGithubOutput(Integer Status , String ErrorMessage) throws IOException
	{
	  var githubOutput = System.getenv("GITHUB_OUTPUT");
    var file = Path.of(githubOutput);
	  var lines = ("message-status=" + Status).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		lines = ("error-message=" + ErrorMessage).lines().toList();
		Files.write(file, lines, UTF_8 , CREATE , APPEND , WRITE);
		System.out.println("Message - Status : " + Status);
	}
}
