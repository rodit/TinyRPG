package net.site40.rodit.tinyrpg.mp.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class API {
	
	public static final int STATUS_UNKNOWN_ERROR = 1;
	public static final int STATUS_OK = 2;
	public static final int STATUS_BAD_REQUEST = 20;
	public static final int STATUS_BAD_CREDENTAILS = 21;
	public static final int STATUS_BAD_SESSION = 22;
	public static final int STATUS_BAD_REGISTER = 23;
	public static final int STATUS_NOT_CONFIRMED = 24;
	
	public static final int LENGTH_SESSION_ID = 64;

	public static final String API_HOST = "http://localhost/tinyrpg/api/1.0/";
	public static final String API_MATCHMAKING = "matchmaking/";
	public static final String API_BROADCAST = "server/broadcast/";
	public static final String API_AUTH = "client/";
	public static final String API_LOGIN = "login/";
	public static final String API_SESSION = "session/";
	
	public static class ApiRequest{

		private String url;
		private HashMap<String, String> postData;
		
		public ApiRequest(String url){
			this.url = url;
			this.postData = new HashMap<String, String>();
		}

		public String getUrl(){
			return url;
		}

		public void put(String key, String value){
			postData.put(key,  value);
		}

		protected String makeXml(){
			String xml = "<request>\n";
			for(String key : postData.keySet())
				xml += "<" + key + ">" + postData.get(key) + "</" + key + ">\n";
			return xml + "</request>";
		}
		
		public ApiResponse requestSync()throws Exception{
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection)obj.openConnection();

			con.setRequestMethod("POST");

			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			String xml = makeXml();
			wr.writeBytes(xml);
			wr.flush();
			wr.close();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null)
				response.append(inputLine);
			
			in.close();
			
			return new ApiResponse(response.toString());
		}

		public void request(final ApiCallback callback){
			new Thread(){
				public void run(){
					try{
						ApiResponse response = requestSync();
						
						if(callback != null)
							callback.response(response);
					}catch(Exception e){
						e.printStackTrace();
						if(callback != null)
							callback.response(new ApiResponse("ERROR"));
					}
				}
			}.start();
		}

		public static class Builder{

			private ApiRequest request;

			public Builder(){
				this.request = new ApiRequest("");
			}

			public static Builder create(){
				return new Builder();
			}

			public Builder setUrl(String url){
				request.url = url;
				return this;
			}

			public Builder put(String key, String value){
				request.put(key, value);
				return this;
			}

			public ApiRequest build(){
				return request;
			}
		}
	}

	public static class ApiResponse{

		private String raw;
		private Document document;
		private Element responseElement;
		
		public ApiResponse(String raw){
			this.raw = raw;

			document = Xml.parse(raw);
			responseElement = (Element)document.getElementsByTagName("response").item(0);
		}
		
		public String getRaw(){
			return raw;
		}
		
		public Document getDocument(){
			return document;
		}

		public String get(String key){
			return responseElement.getElementsByTagName(key).item(0).getTextContent();
		}
		
		public Element getElement(String key){
			return (Element)responseElement.getElementsByTagName(key).item(0);
		}
	}

	public static interface ApiCallback{

		public void response(ApiResponse response);
	}
}
