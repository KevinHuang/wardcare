package org.lds.wardcare;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.gson.Gson;

public class Util {

	public static String MemberToJson(Entity ent) throws JSONException {

		JSONObject obj = MemberToJsonObject(ent);
		return obj.toString();
	}
	
	public static JSONObject MemberToJsonObject(Entity ent) throws JSONException {
		JSONObject json = new JSONObject();
		if (ent != null) {
			json.put("rec_no", ent.getProperty("rec_no").toString());
			json.put("name", ent.getProperty("name").toString());
			json.put("gender", ent.getProperty("gender").toString());
			json.put("is_active", Boolean.parseBoolean(ent.getProperty("gender").toString()));
			json.put("age", Integer.parseInt(ent.getProperty("age").toString()));
			json.put("pristhood", (ent.getProperty("pristhood") != null) ? ent.getProperty("pristhood").toString() : "");
			json.put("tel_h", (ent.getProperty("tel_h") != null) ? ent.getProperty("tel_h").toString() : "");
			json.put("address", (ent.getProperty("address") != null) ? ent.getProperty("address").toString() : "");
			json.put("birthday", (ent.getProperty("birthday") != null) ? ent.getProperty("birthday").toString() : "");
			json.put("confirm_date", (ent.getProperty("confirm_date") != null) ? ent.getProperty("confirm_date").toString() : "");
		}
		return json;
	}
	
	public static JSONArray MembersToJsonArray(List<Entity> entities) throws JSONException {
		JSONArray ary = new JSONArray();
		for(Entity ent : entities) {
			ary.put(MemberToJsonObject(ent));
		}
		return ary;
	}
	
	public static String AttendanceToJson(List<Entity> entities) throws JSONException {
		JSONArray ary = AttendanceToJsonArray(entities);
		return ary.toString();
	}
	
	public static JSONArray AttendanceToJsonArray(List<Entity> entities) throws JSONException {
		JSONArray ary = new JSONArray();
		for(Entity ent : entities) {
			ary.put(AttendanceToJsonObject(ent));
		}
		return ary;
	}
	
	public static JSONObject AttendanceToJsonObject(Entity ent) throws JSONException {
		JSONObject json = new JSONObject();
		Gson g = new Gson();
		if (ent != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String att_date = sdf.format((Date)ent.getProperty("att_date"));
			json.put("att_date", att_date);
			
			json.put("created", (ent.getProperty("created") == null ? "" : sdf.format((Date)ent.getProperty("created"))));
			json.put("meeting", Integer.parseInt(ent.getProperty("meeting").toString()));
			Key keyMember = (Key)ent.getProperty("member");
			JSONObject obj = new JSONObject(g.toJson(keyMember));
			json.put("member", obj);
		}
		return json;
	}
	
	public static String MembersToJson(List<Entity> entities) throws JSONException {
		JSONArray ary = MembersToJsonArray(entities);
		return ary.toString();
	}
	
	public static void sendUTF8JSON(String jsonString, HttpServletResponse resp) throws IOException {
		sendUTF8JSON(jsonString, resp, true);
	}
	
	public static void sendUTF8JSON(String jsonString, HttpServletResponse resp, boolean needAuth) throws IOException {
		resp.setCharacterEncoding("utf8");
		resp.setContentType("application/json");
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Request-Method", "*");
		
		if (needAuth) {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			if (user == null) {
				resp.getWriter().print("{ error : 'please signin first'}");
			}
			else {
				resp.getWriter().print(jsonString);
			}
		}
		else {
			resp.getWriter().print(jsonString);
		}
	}
	
	public static void sendMsg(String msg, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("utf8");
		resp.setContentType("application/json");
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Request-Method", "*");
		resp.getWriter().print(msg);
	}
	
	public static void sendErrorJson(String errMsg, HttpServletResponse resp) throws IOException {
		String errorJson = String.format("{ error : '%s' }", errMsg);
		sendMsg(errorJson, resp);
	}
	
	public static boolean checkSignIn(HttpServletResponse resp) throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		boolean result = true ;
		if (user == null) {
			sendErrorJson("Please Sign In first", resp);
			result = false ;
		}
		return result ;
	}
}
