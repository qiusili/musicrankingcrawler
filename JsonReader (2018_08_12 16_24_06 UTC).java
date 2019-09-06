package main;

import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonReader {

  private static String readAll(Reader rd) throws IOException {
    StringBuilder sb = new StringBuilder();
    int cp;
    while ((cp = rd.read()) != -1) {
      sb.append((char) cp);
    }
    return sb.toString();
  }

  public static JSONObject readJsonFromUrl(String url) throws JSONException {
	  JSONObject json = null;
    InputStream is;
    try {
	is	= new URL(url).openStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
      String jsonText = readAll(rd);
      // System.out.println(jsonText);
      // trim to JSON format
      String jsonTextTrimmed = jsonText.substring(jsonText.indexOf("(")+1);
      jsonTextTrimmed = jsonTextTrimmed.substring(0, jsonTextTrimmed.length()-1);
      // System.out.println(jsonTextTrimmed);
       json = new JSONObject(jsonTextTrimmed);
      
    } catch(IOException e){
		
	}
	finally {
	  return json;
    }
  }
  
  private static String getPrevWeek(String currWeek) {
	  int year = Integer.parseInt(currWeek.substring(0, 4));
	  int week;
	  if(currWeek.charAt(5) == '0') {
		  week = Integer.parseInt(currWeek.substring(6));
	  }
	  else {
		  week = Integer.parseInt(currWeek.substring(5));
	  }
	  if(week == 0) {
		  year--;
		  week = 52;
		  return year+"_"+week;
	  }
	  else {
		  week--;
		  if(week < 10) return year+"_0"+week;
		  else return year+"_"+week;
	  }
	  
  }
  
  private static String getNewestWeek() {
	  String newWeek = null;
	  try {
		  File weekRecord = new File("main/week_record");
		  BufferedReader br = new BufferedReader(new FileReader(weekRecord));
		  int first = Integer.parseInt(br.readLine());
		  int second = Integer.parseInt(br.readLine());
		  String oldWeek;
		  if(second < 10) {
			  oldWeek = first + "_0" + second;
		  }
		  else {
			  oldWeek = first + "_" + second;
		  }
		  
		  if(second == 52) {
			  first++;
			  second = 1;
			  newWeek = first + "_0" + second;
		  }
		  else {
			  second++;
			  if(second < 10) {
				  newWeek = first + "_0" + second;
			  }
			  else {
				  newWeek = first + "_" + second;
			  }  
		  }
		  int code = readJsonFromUrl("https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+newWeek+ 
				  "&topid=26&type=top&song_begin=0&song_num=300&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0")
				  .getInt("code");
		  if(code==-1) {
			  newWeek = oldWeek;
		  }
		  else if(code==0){
			  PrintWriter pw = new PrintWriter("week_record");
			  pw.println(first);
			  pw.println(second);
			  pw.close();
		  }
		  else {
			  System.out.println("JSON callback code: " + code);
		  }
		  br.close();
	  }catch(JSONException | IOException e) {
		  e.printStackTrace();
	  }finally {
		  return newWeek;
	  }
  }
  
  private static Date getNewestDate() {
	  Calendar cal = Calendar.getInstance();
	  Date date = new Date();
	  String currDate;
	  while(true) { 
		  currDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
		  int cod;
		try {
			cod = readJsonFromUrl("https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+currDate
				  		+ "&topid=27&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0")
					  .getInt("code");
			if(cod == -1) {
				  cal.setTime(date);
				  cal.add(Calendar.DATE, -1);
				  date = cal.getTime();
			  }
			  else if(cod == 0) {
				  return date;
			  }
			  else {
				  System.out.println("JSON callback code: " + cod);
			  }
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	  }
  }
  
  private static int searchQQ(int topid, String[] singersinput, String songinput, String week, String date) throws IOException, JSONException {
	  JSONObject json;
	  while(true){
		String url = "";
	  switch (topid)
	  {
	  case 26: url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=26&type=top&song_begin=0&song_num=300&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
	  		break;
	  case 27: url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+date
	  		+ "&topid=27&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
	  		break;
	  case 28 : url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=28&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
	  		break;
	  case 5 : url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=5&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0";
	  		break;
	  case 6 : url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=6&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0\r\n" + 
	  		"";
	  		break;
	  case 52: url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=52&type=top&song_begin=0&song_num=100&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0\r\n" + 
	  		"";
	  		break;
	  case 36: url = "https://c.y.qq.com/v8/fcg-bin/fcg_v8_toplist_cp.fcg?tpl=3&page=detail&date="+week
	  		+ "&topid=36&type=top&song_begin=0&song_num=56&g_tk=5381&jsonpCallback=MusicJsonCallbacktoplist&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0\r\n" + 
	  		"";
	  		break;
	  case 100: url = "https://c.y.qq.com/mv/fcgi-bin/fcg_musicshow_mvtoplist.fcg?format=jsonp&g_tk=5381&jsonpCallback=MusicJsonCallback0815870957337812&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&listtype=all&listid=all_musicshow_mvtoplist_current_new\r\n" + 
	  		"";
	  }
	  	json = readJsonFromUrl(url);
		if(json != null) break;
	  }
	  
		
	  	int code = json.getInt("code");
	  	if(code != 0) {
	  		System.out.println("无结果");
	  		return 0;
	  	}
//	    System.out.println(json.toString());
	  	if(topid != 100)
	  	{
	  		JSONArray songlist = json.getJSONArray("songlist");
	  		Boolean foundInList = false;
	        for(int i = 0; i < songlist.length(); i++)
	        {
//		      	System.out.println("排名: " + i);
		      	JSONObject data = songlist.getJSONObject(i).getJSONObject("data");
		      	String songname = data.getString("songname");
		      	// System.out.println(songname);
		      	if(songname.equals(songinput)) 
		      	{
		      		JSONArray singers = data.getJSONArray("singer");
//			      	System.out.println("专辑： " + data.getString("albumname"));
		      		Boolean skipRest = true;
		      		for(int j = 0; j < singersinput.length; j++) {
		      			String curr = singersinput[j];
		      			Boolean found = false;
		      			ArrayList<String> singerArray = new ArrayList<String>();
		      			for(int k = 0; k < singers.length(); k++) {
		      				String target = singers.getJSONObject(k).getString("name");
		      				target = target.trim();
		      				singerArray.add(target);
		      				if(curr.equals(target)) {
		      					found = true;
		      				}
		      			}
		      			if(found == true) {
		      				foundInList = true;
		      				int rank = i+1;
		      				System.out.println("歌名：" + songname + " 歌手：" + Arrays.toString(singerArray.toArray()) + " 专辑：" + 
		      			data.getString("albumname") + " " + rank + "名");	
		      				break;
		      			}
		      			if(found == false) {
		      				skipRest = false;
		      			}
		      		}
		      		if(skipRest && singersinput.length == singers.length()) break;
		      		
		      	}
		      	
		      }
		      if(foundInList == false) {
		    	  System.out.println("无结果");
		      }
	  	}
	  	else {
	  		JSONArray mvlist = json.getJSONObject("data").getJSONArray("list");
	  		Boolean foundInList = false;
	  		for(int i = 0; i < mvlist.length(); i++)
	  		{
	  			JSONObject currmv = mvlist.getJSONObject(i);
	  			String mvname = currmv.getJSONObject("info").getString("Fmv_title");
	  			if(mvname.equals(songinput)) {
	  				JSONArray singerslist = currmv.getJSONArray("singers");
	  				ArrayList<String> singerArray = new ArrayList<String>();
	  				Boolean skipRest = true;
		  			for(int j = 0; j < singersinput.length; j++)
		  			{
		  				Boolean found = false;
		  				for(int k = 0; k < singerslist.length(); k++) {
		  					String currsinger = singerslist.getJSONObject(j).getString("name");
		  					currsinger = currsinger.trim();
			  				singerArray.add(currsinger);
		  					if(currsinger.equals(singersinput[k])) {
			  					found = true;
			  				}
		  				}
		  				if(found == true) {
		  					foundInList = true;
		  					int rank = i + 1;
		  					System.out.println("MV名称：" + mvname + " 歌手：" + Arrays.toString(singerArray.toArray()) + " " +
		  					rank + "名");
		  					break;
		  				}
		  				if(found == false) {
		  					skipRest = false;
		  				}
		  			}
		  			if(skipRest && singersinput.length == singerslist.length()) break;
	  			}
	  		}
	  		if(foundInList == false) {
		    	  System.out.println("无结果");
		      }
	  	}
	      return 0;
	   // System.out.println(json.get("id"));
  }
  
  private static void searchKuwo(int topid, String[] singersinput, String songinput) {
	  try {
		String url;
		if(topid == 93) url = "http://www.kuwo.cn/bang/content?name=%E9%85%B7%E6%88%91%E9%A3%99%E5%8D%87%E6%A6%9C&bangId=93";
		else url = "http://www.kuwo.cn/bang/content?name=%E9%85%B7%E6%88%91%E5%8D%8E%E8%AF%AD%E6%A6%9C&bangId=62";
		Document doc = Jsoup.connect(url).get();
		Elements items = doc.select(".tools");
		int size = items.size();
		Boolean foundInList = false;
		for(int i = 0; i < size; i++)
		{
			String info = items.get(i).attr("data-music");
			JSONObject json = new JSONObject(info);
			String songname = json.getString("name");
			if(songname.equals(songinput)) {
				String singers = json.getString("artist");
				String[] singer = singers.split("&");
				for(int j = 0; j < singer.length; j++) {
					singer[j] = singer[j].trim();
				}
				Boolean skipRest = true;
				for(int j = 0; j < singersinput.length; j++) {
					Boolean found = false;
					for(int k = 0; k < singer.length; k++){
						if(singer[k].equals(singersinput[j])) {
							found = true;
						}
					}
					if(found == true) {
						foundInList = true;
						int rank = i + 1;
						System.out.println("歌名：" + songname + " 歌手：" + Arrays.toString(singer) + " 专辑：" + 
								json.getString("album") + " " + rank + "名");	
				      	break;
					}
					else {
						skipRest = false;
					}
				}
				if(skipRest && singersinput.length == singer.length) break;
			}
		}
		if(foundInList == false) {
			System.out.println("无结果");
		}
		
	  } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (JSONException e) {
		e.printStackTrace();
	}
	  
  }
  
  private static void searchKugou(int topid, String[] singersname, String songname) {
	  try {
		  if(topid == 1) {
			  Boolean skipAll = false;
			  Boolean foundInList = false;
			  for(int j = 1; j <= 23; j++)
			  {
				  Document doc = Jsoup.connect("http://www.kugou.com/yy/rank/home/" + j + "-8888.html?from=rank").get();
				Elements items = doc.select(".pc_temp_songname");
				int size = items.size();
				for(int i = 0; i < size; i++)
				{
					String info = items.get(i).html();
//					System.out.println(info);
					String[] names = info.split(" - ");
//					System.out.println(Arrays.toString(names));
					if(names[1].equals(songname)) {
						String[] singers = names[0].split("、");
						for(int k = 0; k < singers.length; k++)
						{
							singers[k] = singers[k].trim();
							if(singers[k].contains("&amp;"))
							{
								singers[k] = singers[k].replaceAll("&amp;", "&");
							}
						}
						Boolean skipRest = true;
						for(int k = 0; k < singersname.length; k++) {
							Boolean found = false;
							for(int l = 0; l < singers.length; l++) {
			  					if(singers[l].equals(singersname[k])) {
				  					found = true;
				  				}
							}
							if(found == true) {
			  					foundInList = true;
			  					int rank = (j-1)*22 + i+1;
			  					System.out.println("歌名：" + names[1] + " 歌手：" + Arrays.toString(singers) + " " +
			  					rank + "名");
			  					break;
			  				}
			  				if(found == false) {
			  					skipRest = false;
			  				}
						}
						if(skipRest && singersname.length == singers.length) {
							skipAll = true;
							break;
						}
						
					}
				}
				if(skipAll == true) break;
			  }
			  if(foundInList == false) {
				  System.out.println("无结果");
			  }
		  }
		  else if(topid == 2) {
			  Boolean skipAll = false;
			  Boolean foundInList = false;
			  for(int i = 1; i <= 5; i++) {
				  Document doc = Jsoup.connect("http://www.kugou.com/yy/rank/home/"+i+"-6666.html?from=rank").get();
					Elements items = doc.select(".pc_temp_songname");
					for(int j = 0; j < items.size(); j++) {
						String info = items.get(i).html();
						String[] names = info.split(" - ");
						if(names[1].equals(songname)) {
							String[] singers = names[0].split("、");
							for(int k = 0; k < singers.length; k++)
							{
								singers[k] = singers[k].trim();
								if(singers[k].contains("&amp;"))
								{
									singers[k] = singers[k].replaceAll("&amp;", "&");
								}
							}
							Boolean skipRest = true;
							for(int k = 0; k < singersname.length; k++) {
								Boolean found = false;
								for(int l = 0; l < singers.length; l++) {
				  					if(singers[l].equals(singersname[k])) {
					  					found = true;
					  				}
								}
								if(found == true) {
				  					foundInList = true;
				  					int rank = (i-1)*22 + j+1;
				  					System.out.println("歌名：" + names[1] + " 歌手：" + Arrays.toString(singers) + " " +
				  					rank + "名");
				  					break;
				  				}
				  				if(found == false) {
				  					skipRest = false;
				  				}
							}
							if(skipRest && singersname.length == singers.length) {
								skipAll = true;
								break;
							}
						}
					}
					if(skipAll == true) break;
			  }
			  if(foundInList == false) {
				  System.out.println("无结果");
			  }
		  }
		  else if(topid == 3) {
			  Boolean skipAll = false;
			  Boolean foundInList = false;
			  for(int j = 1; j <= 23; j++)
			  {
				  Document doc = Jsoup.connect("http://www.kugou.com/yy/rank/home/"+j+"-23784.html?from=rank").get();
				Elements items = doc.select(".pc_temp_songname");
				int size = items.size();
				for(int i = 0; i < size; i++)
				{
					String info = items.get(i).html();
//					System.out.println(info);
					String[] names = info.split(" - ");
//					System.out.println(Arrays.toString(names));
					if(names[1].equals(songname)) {
						String[] singers = names[0].split("、");
						for(int k = 0; k < singers.length; k++)
						{
							singers[k] = singers[k].trim();
							if(singers[k].contains("&amp;"))
							{
								singers[k] = singers[k].replaceAll("&amp;", "&");
							}
						}
						Boolean skipRest = true;
						for(int k = 0; k < singersname.length; k++) {
							Boolean found = false;
							for(int l = 0; l < singers.length; l++) {
			  					if(singers[l].equals(singersname[k])) {
				  					found = true;
				  				}
							}
							if(found == true) {
			  					foundInList = true;
			  					int rank = (j-1)*22 + i+1;
			  					System.out.println("歌名：" + names[1] + " 歌手：" + Arrays.toString(singers) + " " +
			  					rank + "名");
			  					break;
			  				}
			  				if(found == false) {
			  					skipRest = false;
			  				}
						}
						if(skipRest && singersname.length == singers.length) {
							skipAll = true;
							break;
						}
						
					}
				}
				if(skipAll == true) break;
			  }
			  if(foundInList == false) {
				  System.out.println("无结果");
			  }
		  }
			
		  } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
  }
  
  private static void searchSingerRank(String singerinput) {
	  ArrayList<String> u = new ArrayList<String>();
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI846639206457525&g_tk=5381&jsonpCallback=getUCGI846639206457525&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A0%2C%22cur_page%22%3A1%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI44656369609384483&g_tk=5381&jsonpCallback=getUCGI44656369609384483&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A80%2C%22cur_page%22%3A2%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5033016559769725&g_tk=5381&jsonpCallback=getUCGI5033016559769725&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A160%2C%22cur_page%22%3A3%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6474576058728416&g_tk=5381&jsonpCallback=getUCGI6474576058728416&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A240%2C%22cur_page%22%3A4%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI841017181376972&g_tk=5381&jsonpCallback=getUCGI841017181376972&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A320%2C%22cur_page%22%3A5%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI13034566398913672&g_tk=5381&jsonpCallback=getUCGI13034566398913672&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A400%2C%22cur_page%22%3A6%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8759118951530527&g_tk=5381&jsonpCallback=getUCGI8759118951530527&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A480%2C%22cur_page%22%3A7%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4486075717797806&g_tk=5381&jsonpCallback=getUCGI4486075717797806&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A560%2C%22cur_page%22%3A8%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI22670576793485342&g_tk=5381&jsonpCallback=getUCGI22670576793485342&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A640%2C%22cur_page%22%3A9%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI45125861669605394&g_tk=5381&jsonpCallback=getUCGI45125861669605394&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A720%2C%22cur_page%22%3A10%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI286870785892007&g_tk=5381&jsonpCallback=getUCGI286870785892007&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A800%2C%22cur_page%22%3A11%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI0981755009013634&g_tk=5381&jsonpCallback=getUCGI0981755009013634&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A880%2C%22cur_page%22%3A12%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9794701509093211&g_tk=5381&jsonpCallback=getUCGI9794701509093211&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A960%2C%22cur_page%22%3A13%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI08321878404885408&g_tk=5381&jsonpCallback=getUCGI08321878404885408&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1040%2C%22cur_page%22%3A14%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6762113709842479&g_tk=5381&jsonpCallback=getUCGI6762113709842479&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1120%2C%22cur_page%22%3A15%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9699116296215611&g_tk=5381&jsonpCallback=getUCGI9699116296215611&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1200%2C%22cur_page%22%3A16%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI15240227398452566&g_tk=5381&jsonpCallback=getUCGI15240227398452566&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1280%2C%22cur_page%22%3A17%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9860329887040968&g_tk=5381&jsonpCallback=getUCGI9860329887040968&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1360%2C%22cur_page%22%3A18%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI41748723172985014&g_tk=5381&jsonpCallback=getUCGI41748723172985014&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1440%2C%22cur_page%22%3A19%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9305064003715526&g_tk=5381&jsonpCallback=getUCGI9305064003715526&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1520%2C%22cur_page%22%3A20%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI11542363190918192&g_tk=5381&jsonpCallback=getUCGI11542363190918192&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1600%2C%22cur_page%22%3A21%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8868126963593828&g_tk=5381&jsonpCallback=getUCGI8868126963593828&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1680%2C%22cur_page%22%3A22%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI10245875132991422&g_tk=5381&jsonpCallback=getUCGI10245875132991422&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1760%2C%22cur_page%22%3A23%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI17193035091227982&g_tk=5381&jsonpCallback=getUCGI17193035091227982&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1840%2C%22cur_page%22%3A24%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI47947796066904647&g_tk=5381&jsonpCallback=getUCGI47947796066904647&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A1920%2C%22cur_page%22%3A25%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9731096455421189&g_tk=5381&jsonpCallback=getUCGI9731096455421189&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2000%2C%22cur_page%22%3A26%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI39640627928559913&g_tk=5381&jsonpCallback=getUCGI39640627928559913&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2080%2C%22cur_page%22%3A27%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI17570406020692597&g_tk=5381&jsonpCallback=getUCGI17570406020692597&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2160%2C%22cur_page%22%3A28%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI40379368087606227&g_tk=5381&jsonpCallback=getUCGI40379368087606227&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2240%2C%22cur_page%22%3A29%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2713279929524546&g_tk=5381&jsonpCallback=getUCGI2713279929524546&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2320%2C%22cur_page%22%3A30%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9917885595182068&g_tk=5381&jsonpCallback=getUCGI9917885595182068&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2400%2C%22cur_page%22%3A31%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4923641927397433&g_tk=5381&jsonpCallback=getUCGI4923641927397433&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2480%2C%22cur_page%22%3A32%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI0931390500075222&g_tk=5381&jsonpCallback=getUCGI0931390500075222&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2560%2C%22cur_page%22%3A33%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7791569856307587&g_tk=5381&jsonpCallback=getUCGI7791569856307587&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2640%2C%22cur_page%22%3A34%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2548366694606623&g_tk=5381&jsonpCallback=getUCGI2548366694606623&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2720%2C%22cur_page%22%3A35%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8087961996728916&g_tk=5381&jsonpCallback=getUCGI8087961996728916&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2800%2C%22cur_page%22%3A36%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9327790361949801&g_tk=5381&jsonpCallback=getUCGI9327790361949801&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2880%2C%22cur_page%22%3A37%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2993099156268668&g_tk=5381&jsonpCallback=getUCGI2993099156268668&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A2960%2C%22cur_page%22%3A38%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9626582950473921&g_tk=5381&jsonpCallback=getUCGI9626582950473921&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3040%2C%22cur_page%22%3A39%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI484942034671729&g_tk=5381&jsonpCallback=getUCGI484942034671729&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3120%2C%22cur_page%22%3A40%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI1983351593079845&g_tk=5381&jsonpCallback=getUCGI1983351593079845&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3200%2C%22cur_page%22%3A41%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI11089221668192994&g_tk=5381&jsonpCallback=getUCGI11089221668192994&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3280%2C%22cur_page%22%3A42%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5605579461207382&g_tk=5381&jsonpCallback=getUCGI5605579461207382&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3360%2C%22cur_page%22%3A43%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI07478102923077468&g_tk=5381&jsonpCallback=getUCGI07478102923077468&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3440%2C%22cur_page%22%3A44%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI1676946727939126&g_tk=5381&jsonpCallback=getUCGI1676946727939126&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3520%2C%22cur_page%22%3A45%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI47105585788124826&g_tk=5381&jsonpCallback=getUCGI47105585788124826&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3600%2C%22cur_page%22%3A46%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI48275927768675464&g_tk=5381&jsonpCallback=getUCGI48275927768675464&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3680%2C%22cur_page%22%3A47%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI690632411811118&g_tk=5381&jsonpCallback=getUCGI690632411811118&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3760%2C%22cur_page%22%3A48%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI47481230355565374&g_tk=5381&jsonpCallback=getUCGI47481230355565374&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3840%2C%22cur_page%22%3A49%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7345219712056985&g_tk=5381&jsonpCallback=getUCGI7345219712056985&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A3920%2C%22cur_page%22%3A50%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8279936388238254&g_tk=5381&jsonpCallback=getUCGI8279936388238254&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4000%2C%22cur_page%22%3A51%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7492260931098635&g_tk=5381&jsonpCallback=getUCGI7492260931098635&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4080%2C%22cur_page%22%3A52%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI16159058409187477&g_tk=5381&jsonpCallback=getUCGI16159058409187477&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4160%2C%22cur_page%22%3A53%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI029380218655977952&g_tk=5381&jsonpCallback=getUCGI029380218655977952&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4240%2C%22cur_page%22%3A54%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6997684843796506&g_tk=5381&jsonpCallback=getUCGI6997684843796506&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4320%2C%22cur_page%22%3A55%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI05445811980818105&g_tk=5381&jsonpCallback=getUCGI05445811980818105&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4400%2C%22cur_page%22%3A56%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7965572183370049&g_tk=5381&jsonpCallback=getUCGI7965572183370049&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4480%2C%22cur_page%22%3A57%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI04183060139478645&g_tk=5381&jsonpCallback=getUCGI04183060139478645&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4560%2C%22cur_page%22%3A58%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI34136992066187033&g_tk=5381&jsonpCallback=getUCGI34136992066187033&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4640%2C%22cur_page%22%3A59%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9786910958781443&g_tk=5381&jsonpCallback=getUCGI9786910958781443&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4720%2C%22cur_page%22%3A60%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI23142724977591933&g_tk=5381&jsonpCallback=getUCGI23142724977591933&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4800%2C%22cur_page%22%3A61%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI19480400872036552&g_tk=5381&jsonpCallback=getUCGI19480400872036552&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4880%2C%22cur_page%22%3A62%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30855993014065985&g_tk=5381&jsonpCallback=getUCGI30855993014065985&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A4960%2C%22cur_page%22%3A63%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7606193191753654&g_tk=5381&jsonpCallback=getUCGI7606193191753654&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5040%2C%22cur_page%22%3A64%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI06589119498875684&g_tk=5381&jsonpCallback=getUCGI06589119498875684&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5120%2C%22cur_page%22%3A65%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9225359528440453&g_tk=5381&jsonpCallback=getUCGI9225359528440453&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5200%2C%22cur_page%22%3A66%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6095740677465984&g_tk=5381&jsonpCallback=getUCGI6095740677465984&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5280%2C%22cur_page%22%3A67%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7992951653979214&g_tk=5381&jsonpCallback=getUCGI7992951653979214&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5360%2C%22cur_page%22%3A68%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5601205123024346&g_tk=5381&jsonpCallback=getUCGI5601205123024346&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5440%2C%22cur_page%22%3A69%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4706920871427154&g_tk=5381&jsonpCallback=getUCGI4706920871427154&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5520%2C%22cur_page%22%3A70%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7154048075719746&g_tk=5381&jsonpCallback=getUCGI7154048075719746&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5600%2C%22cur_page%22%3A71%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4196126400986473&g_tk=5381&jsonpCallback=getUCGI4196126400986473&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5680%2C%22cur_page%22%3A72%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI49701972286186513&g_tk=5381&jsonpCallback=getUCGI49701972286186513&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5760%2C%22cur_page%22%3A73%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6683170844401907&g_tk=5381&jsonpCallback=getUCGI6683170844401907&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5840%2C%22cur_page%22%3A74%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI11524013400493627&g_tk=5381&jsonpCallback=getUCGI11524013400493627&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A5920%2C%22cur_page%22%3A75%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI13253052192887482&g_tk=5381&jsonpCallback=getUCGI13253052192887482&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6000%2C%22cur_page%22%3A76%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6325493873393406&g_tk=5381&jsonpCallback=getUCGI6325493873393406&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6080%2C%22cur_page%22%3A77%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6619129426146559&g_tk=5381&jsonpCallback=getUCGI6619129426146559&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6160%2C%22cur_page%22%3A78%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5905158039864795&g_tk=5381&jsonpCallback=getUCGI5905158039864795&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6240%2C%22cur_page%22%3A79%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6900010433956667&g_tk=5381&jsonpCallback=getUCGI6900010433956667&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6320%2C%22cur_page%22%3A80%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5429260753401781&g_tk=5381&jsonpCallback=getUCGI5429260753401781&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6400%2C%22cur_page%22%3A81%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7162503718532194&g_tk=5381&jsonpCallback=getUCGI7162503718532194&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6480%2C%22cur_page%22%3A82%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2081670839104195&g_tk=5381&jsonpCallback=getUCGI2081670839104195&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6560%2C%22cur_page%22%3A83%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4370526743249039&g_tk=5381&jsonpCallback=getUCGI4370526743249039&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6640%2C%22cur_page%22%3A84%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4168113337061601&g_tk=5381&jsonpCallback=getUCGI4168113337061601&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6720%2C%22cur_page%22%3A85%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9268605560420009&g_tk=5381&jsonpCallback=getUCGI9268605560420009&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6800%2C%22cur_page%22%3A86%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7558834240214565&g_tk=5381&jsonpCallback=getUCGI7558834240214565&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6880%2C%22cur_page%22%3A87%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI23217839816972186&g_tk=5381&jsonpCallback=getUCGI23217839816972186&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A6960%2C%22cur_page%22%3A88%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3718779815614355&g_tk=5381&jsonpCallback=getUCGI3718779815614355&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7040%2C%22cur_page%22%3A89%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI07481258796404067&g_tk=5381&jsonpCallback=getUCGI07481258796404067&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7120%2C%22cur_page%22%3A90%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6999403858611724&g_tk=5381&jsonpCallback=getUCGI6999403858611724&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7200%2C%22cur_page%22%3A91%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7664472847826074&g_tk=5381&jsonpCallback=getUCGI7664472847826074&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7280%2C%22cur_page%22%3A92%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8835636922111649&g_tk=5381&jsonpCallback=getUCGI8835636922111649&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7360%2C%22cur_page%22%3A93%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6229962819779826&g_tk=5381&jsonpCallback=getUCGI6229962819779826&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7440%2C%22cur_page%22%3A94%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4331621980471674&g_tk=5381&jsonpCallback=getUCGI4331621980471674&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7520%2C%22cur_page%22%3A95%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6931845624030306&g_tk=5381&jsonpCallback=getUCGI6931845624030306&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7600%2C%22cur_page%22%3A96%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI04485533845395806&g_tk=5381&jsonpCallback=getUCGI04485533845395806&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7680%2C%22cur_page%22%3A97%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6255728004662111&g_tk=5381&jsonpCallback=getUCGI6255728004662111&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7760%2C%22cur_page%22%3A98%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI262056242773284&g_tk=5381&jsonpCallback=getUCGI262056242773284&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7840%2C%22cur_page%22%3A99%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI28646875531307625&g_tk=5381&jsonpCallback=getUCGI28646875531307625&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A7920%2C%22cur_page%22%3A100%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI980710896301477&g_tk=5381&jsonpCallback=getUCGI980710896301477&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8000%2C%22cur_page%22%3A101%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI333697328175274&g_tk=5381&jsonpCallback=getUCGI333697328175274&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8080%2C%22cur_page%22%3A102%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI49134768061967216&g_tk=5381&jsonpCallback=getUCGI49134768061967216&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8160%2C%22cur_page%22%3A103%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6868250380234973&g_tk=5381&jsonpCallback=getUCGI6868250380234973&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8240%2C%22cur_page%22%3A104%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI12086233664034274&g_tk=5381&jsonpCallback=getUCGI12086233664034274&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8320%2C%22cur_page%22%3A105%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6657851472925036&g_tk=5381&jsonpCallback=getUCGI6657851472925036&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8400%2C%22cur_page%22%3A106%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3310771496670277&g_tk=5381&jsonpCallback=getUCGI3310771496670277&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8480%2C%22cur_page%22%3A107%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI701730027995495&g_tk=5381&jsonpCallback=getUCGI701730027995495&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8560%2C%22cur_page%22%3A108%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI04632367294952067&g_tk=5381&jsonpCallback=getUCGI04632367294952067&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8640%2C%22cur_page%22%3A109%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI1458449380264819&g_tk=5381&jsonpCallback=getUCGI1458449380264819&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8720%2C%22cur_page%22%3A110%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6577674891091225&g_tk=5381&jsonpCallback=getUCGI6577674891091225&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8800%2C%22cur_page%22%3A111%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI20142044290460248&g_tk=5381&jsonpCallback=getUCGI20142044290460248&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8880%2C%22cur_page%22%3A112%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8400396510413528&g_tk=5381&jsonpCallback=getUCGI8400396510413528&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A8960%2C%22cur_page%22%3A113%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI32804025258698344&g_tk=5381&jsonpCallback=getUCGI32804025258698344&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9040%2C%22cur_page%22%3A114%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI02981013622314732&g_tk=5381&jsonpCallback=getUCGI02981013622314732&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9120%2C%22cur_page%22%3A115%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI45657808954881784&g_tk=5381&jsonpCallback=getUCGI45657808954881784&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9200%2C%22cur_page%22%3A116%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI771942103925825&g_tk=5381&jsonpCallback=getUCGI771942103925825&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9280%2C%22cur_page%22%3A117%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7034653360838896&g_tk=5381&jsonpCallback=getUCGI7034653360838896&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9360%2C%22cur_page%22%3A118%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7175427849891896&g_tk=5381&jsonpCallback=getUCGI7175427849891896&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9440%2C%22cur_page%22%3A119%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9605288214811993&g_tk=5381&jsonpCallback=getUCGI9605288214811993&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9520%2C%22cur_page%22%3A120%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI219851927367253&g_tk=5381&jsonpCallback=getUCGI219851927367253&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9600%2C%22cur_page%22%3A121%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6530132751126936&g_tk=5381&jsonpCallback=getUCGI6530132751126936&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9680%2C%22cur_page%22%3A122%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6301103992203203&g_tk=5381&jsonpCallback=getUCGI6301103992203203&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9760%2C%22cur_page%22%3A123%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8961555210788281&g_tk=5381&jsonpCallback=getUCGI8961555210788281&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9840%2C%22cur_page%22%3A124%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7042293610159491&g_tk=5381&jsonpCallback=getUCGI7042293610159491&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A9920%2C%22cur_page%22%3A125%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3283189138769653&g_tk=5381&jsonpCallback=getUCGI3283189138769653&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10000%2C%22cur_page%22%3A126%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8327702857062629&g_tk=5381&jsonpCallback=getUCGI8327702857062629&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10080%2C%22cur_page%22%3A127%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI18953595440261295&g_tk=5381&jsonpCallback=getUCGI18953595440261295&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10160%2C%22cur_page%22%3A128%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI920522355421878&g_tk=5381&jsonpCallback=getUCGI920522355421878&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10240%2C%22cur_page%22%3A129%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5318967407292907&g_tk=5381&jsonpCallback=getUCGI5318967407292907&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10320%2C%22cur_page%22%3A130%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6284856191470853&g_tk=5381&jsonpCallback=getUCGI6284856191470853&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10400%2C%22cur_page%22%3A131%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI475498678346546&g_tk=5381&jsonpCallback=getUCGI475498678346546&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10480%2C%22cur_page%22%3A132%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7190409378369993&g_tk=5381&jsonpCallback=getUCGI7190409378369993&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10560%2C%22cur_page%22%3A133%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30721665216685&g_tk=5381&jsonpCallback=getUCGI30721665216685&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10640%2C%22cur_page%22%3A134%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5681609794303184&g_tk=5381&jsonpCallback=getUCGI5681609794303184&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10720%2C%22cur_page%22%3A135%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9563934749998251&g_tk=5381&jsonpCallback=getUCGI9563934749998251&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10800%2C%22cur_page%22%3A136%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8567678143274153&g_tk=5381&jsonpCallback=getUCGI8567678143274153&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10880%2C%22cur_page%22%3A137%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8674001498643893&g_tk=5381&jsonpCallback=getUCGI8674001498643893&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A10960%2C%22cur_page%22%3A138%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7459433968016171&g_tk=5381&jsonpCallback=getUCGI7459433968016171&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11040%2C%22cur_page%22%3A139%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4233431240837837&g_tk=5381&jsonpCallback=getUCGI4233431240837837&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11120%2C%22cur_page%22%3A140%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8642919092300188&g_tk=5381&jsonpCallback=getUCGI8642919092300188&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11200%2C%22cur_page%22%3A141%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3691593033029088&g_tk=5381&jsonpCallback=getUCGI3691593033029088&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11280%2C%22cur_page%22%3A142%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI14462433048302215&g_tk=5381&jsonpCallback=getUCGI14462433048302215&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11360%2C%22cur_page%22%3A143%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8240414855732539&g_tk=5381&jsonpCallback=getUCGI8240414855732539&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11440%2C%22cur_page%22%3A144%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8626264664542003&g_tk=5381&jsonpCallback=getUCGI8626264664542003&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11520%2C%22cur_page%22%3A145%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI29165760836700283&g_tk=5381&jsonpCallback=getUCGI29165760836700283&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11600%2C%22cur_page%22%3A146%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI11586915299648148&g_tk=5381&jsonpCallback=getUCGI11586915299648148&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11680%2C%22cur_page%22%3A147%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI06323434156480268&g_tk=5381&jsonpCallback=getUCGI06323434156480268&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11760%2C%22cur_page%22%3A148%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI053073310728853684&g_tk=5381&jsonpCallback=getUCGI053073310728853684&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11840%2C%22cur_page%22%3A149%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6518669334659335&g_tk=5381&jsonpCallback=getUCGI6518669334659335&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A11920%2C%22cur_page%22%3A150%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI43147529100834636&g_tk=5381&jsonpCallback=getUCGI43147529100834636&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12000%2C%22cur_page%22%3A151%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4084639779430319&g_tk=5381&jsonpCallback=getUCGI4084639779430319&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12080%2C%22cur_page%22%3A152%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI85341666595498&g_tk=5381&jsonpCallback=getUCGI85341666595498&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12160%2C%22cur_page%22%3A153%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI27487316550845864&g_tk=5381&jsonpCallback=getUCGI27487316550845864&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12240%2C%22cur_page%22%3A154%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI645956859346253&g_tk=5381&jsonpCallback=getUCGI645956859346253&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12320%2C%22cur_page%22%3A155%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI48507175431545213&g_tk=5381&jsonpCallback=getUCGI48507175431545213&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12400%2C%22cur_page%22%3A156%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6113796769771933&g_tk=5381&jsonpCallback=getUCGI6113796769771933&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12480%2C%22cur_page%22%3A157%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8009122437432448&g_tk=5381&jsonpCallback=getUCGI8009122437432448&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12560%2C%22cur_page%22%3A158%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3953260275941817&g_tk=5381&jsonpCallback=getUCGI3953260275941817&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12640%2C%22cur_page%22%3A159%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6904170080584098&g_tk=5381&jsonpCallback=getUCGI6904170080584098&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12720%2C%22cur_page%22%3A160%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8584075498830914&g_tk=5381&jsonpCallback=getUCGI8584075498830914&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12800%2C%22cur_page%22%3A161%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI32514168661549947&g_tk=5381&jsonpCallback=getUCGI32514168661549947&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12880%2C%22cur_page%22%3A162%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI419469946679075&g_tk=5381&jsonpCallback=getUCGI419469946679075&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A12960%2C%22cur_page%22%3A163%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI25104237290724885&g_tk=5381&jsonpCallback=getUCGI25104237290724885&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13040%2C%22cur_page%22%3A164%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI25998283459658444&g_tk=5381&jsonpCallback=getUCGI25998283459658444&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13120%2C%22cur_page%22%3A165%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8983999703110597&g_tk=5381&jsonpCallback=getUCGI8983999703110597&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13200%2C%22cur_page%22%3A166%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6373363413733353&g_tk=5381&jsonpCallback=getUCGI6373363413733353&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13280%2C%22cur_page%22%3A167%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI37615449232154585&g_tk=5381&jsonpCallback=getUCGI37615449232154585&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13360%2C%22cur_page%22%3A168%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI02677609822852678&g_tk=5381&jsonpCallback=getUCGI02677609822852678&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13440%2C%22cur_page%22%3A169%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI26368756768104173&g_tk=5381&jsonpCallback=getUCGI26368756768104173&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13520%2C%22cur_page%22%3A170%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5957145283887302&g_tk=5381&jsonpCallback=getUCGI5957145283887302&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13600%2C%22cur_page%22%3A171%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8622605065540028&g_tk=5381&jsonpCallback=getUCGI8622605065540028&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13680%2C%22cur_page%22%3A172%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7736549313309633&g_tk=5381&jsonpCallback=getUCGI7736549313309633&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13760%2C%22cur_page%22%3A173%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7117434618506617&g_tk=5381&jsonpCallback=getUCGI7117434618506617&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13840%2C%22cur_page%22%3A174%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3707706483903277&g_tk=5381&jsonpCallback=getUCGI3707706483903277&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A13920%2C%22cur_page%22%3A175%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9833676021603623&g_tk=5381&jsonpCallback=getUCGI9833676021603623&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14000%2C%22cur_page%22%3A176%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6056883912275295&g_tk=5381&jsonpCallback=getUCGI6056883912275295&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14080%2C%22cur_page%22%3A177%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8534916063499209&g_tk=5381&jsonpCallback=getUCGI8534916063499209&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14160%2C%22cur_page%22%3A178%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30974855362768894&g_tk=5381&jsonpCallback=getUCGI30974855362768894&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14240%2C%22cur_page%22%3A179%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9526007809719241&g_tk=5381&jsonpCallback=getUCGI9526007809719241&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14320%2C%22cur_page%22%3A180%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI018554634001799686&g_tk=5381&jsonpCallback=getUCGI018554634001799686&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14400%2C%22cur_page%22%3A181%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI23159291585005182&g_tk=5381&jsonpCallback=getUCGI23159291585005182&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14480%2C%22cur_page%22%3A182%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9083962227016977&g_tk=5381&jsonpCallback=getUCGI9083962227016977&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14560%2C%22cur_page%22%3A183%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7378746468625972&g_tk=5381&jsonpCallback=getUCGI7378746468625972&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14640%2C%22cur_page%22%3A184%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8317370609925463&g_tk=5381&jsonpCallback=getUCGI8317370609925463&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14720%2C%22cur_page%22%3A185%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9925195626525363&g_tk=5381&jsonpCallback=getUCGI9925195626525363&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14800%2C%22cur_page%22%3A186%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6962097225060095&g_tk=5381&jsonpCallback=getUCGI6962097225060095&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14880%2C%22cur_page%22%3A187%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI96478072580644&g_tk=5381&jsonpCallback=getUCGI96478072580644&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A14960%2C%22cur_page%22%3A188%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI34988488390228967&g_tk=5381&jsonpCallback=getUCGI34988488390228967&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15040%2C%22cur_page%22%3A189%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8470416511396777&g_tk=5381&jsonpCallback=getUCGI8470416511396777&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15120%2C%22cur_page%22%3A190%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI16635406602273917&g_tk=5381&jsonpCallback=getUCGI16635406602273917&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15200%2C%22cur_page%22%3A191%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8232844000567183&g_tk=5381&jsonpCallback=getUCGI8232844000567183&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15280%2C%22cur_page%22%3A192%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6085244476832221&g_tk=5381&jsonpCallback=getUCGI6085244476832221&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15360%2C%22cur_page%22%3A193%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8539859919293926&g_tk=5381&jsonpCallback=getUCGI8539859919293926&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15440%2C%22cur_page%22%3A194%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6009658772425315&g_tk=5381&jsonpCallback=getUCGI6009658772425315&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15520%2C%22cur_page%22%3A195%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI04010550400371815&g_tk=5381&jsonpCallback=getUCGI04010550400371815&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15600%2C%22cur_page%22%3A196%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5370940423169741&g_tk=5381&jsonpCallback=getUCGI5370940423169741&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15680%2C%22cur_page%22%3A197%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5299249776291348&g_tk=5381&jsonpCallback=getUCGI5299249776291348&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15760%2C%22cur_page%22%3A198%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI692263070880694&g_tk=5381&jsonpCallback=getUCGI692263070880694&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15840%2C%22cur_page%22%3A199%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4886225054056794&g_tk=5381&jsonpCallback=getUCGI4886225054056794&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A15920%2C%22cur_page%22%3A200%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI17581980918350681&g_tk=5381&jsonpCallback=getUCGI17581980918350681&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16000%2C%22cur_page%22%3A201%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5264715766966579&g_tk=5381&jsonpCallback=getUCGI5264715766966579&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16080%2C%22cur_page%22%3A202%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3054035075911705&g_tk=5381&jsonpCallback=getUCGI3054035075911705&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16160%2C%22cur_page%22%3A203%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI670560293803464&g_tk=5381&jsonpCallback=getUCGI670560293803464&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16240%2C%22cur_page%22%3A204%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9954561483348074&g_tk=5381&jsonpCallback=getUCGI9954561483348074&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16320%2C%22cur_page%22%3A205%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI70230207222712&g_tk=5381&jsonpCallback=getUCGI70230207222712&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16400%2C%22cur_page%22%3A206%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30896423337901946&g_tk=5381&jsonpCallback=getUCGI30896423337901946&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16480%2C%22cur_page%22%3A207%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI08405633857367945&g_tk=5381&jsonpCallback=getUCGI08405633857367945&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16560%2C%22cur_page%22%3A208%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9991758273563707&g_tk=5381&jsonpCallback=getUCGI9991758273563707&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16640%2C%22cur_page%22%3A209%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI931712967439561&g_tk=5381&jsonpCallback=getUCGI931712967439561&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16720%2C%22cur_page%22%3A210%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI783037558129362&g_tk=5381&jsonpCallback=getUCGI783037558129362&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16800%2C%22cur_page%22%3A211%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7029762380651359&g_tk=5381&jsonpCallback=getUCGI7029762380651359&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16880%2C%22cur_page%22%3A212%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8778032834890939&g_tk=5381&jsonpCallback=getUCGI8778032834890939&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A16960%2C%22cur_page%22%3A213%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI0691812271501564&g_tk=5381&jsonpCallback=getUCGI0691812271501564&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17040%2C%22cur_page%22%3A214%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI38241475831836813&g_tk=5381&jsonpCallback=getUCGI38241475831836813&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17120%2C%22cur_page%22%3A215%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5046030248159081&g_tk=5381&jsonpCallback=getUCGI5046030248159081&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17200%2C%22cur_page%22%3A216%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4060085354626257&g_tk=5381&jsonpCallback=getUCGI4060085354626257&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17280%2C%22cur_page%22%3A217%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9266842025946991&g_tk=5381&jsonpCallback=getUCGI9266842025946991&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17360%2C%22cur_page%22%3A218%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI210869976013907&g_tk=5381&jsonpCallback=getUCGI210869976013907&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17440%2C%22cur_page%22%3A219%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI006784119797766275&g_tk=5381&jsonpCallback=getUCGI006784119797766275&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17520%2C%22cur_page%22%3A220%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7890731849873982&g_tk=5381&jsonpCallback=getUCGI7890731849873982&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17600%2C%22cur_page%22%3A221%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7646005509082339&g_tk=5381&jsonpCallback=getUCGI7646005509082339&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17680%2C%22cur_page%22%3A222%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI979450488271419&g_tk=5381&jsonpCallback=getUCGI979450488271419&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17760%2C%22cur_page%22%3A223%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6664931483748853&g_tk=5381&jsonpCallback=getUCGI6664931483748853&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17840%2C%22cur_page%22%3A224%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9458080955024257&g_tk=5381&jsonpCallback=getUCGI9458080955024257&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A17920%2C%22cur_page%22%3A225%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6836107616738922&g_tk=5381&jsonpCallback=getUCGI6836107616738922&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18000%2C%22cur_page%22%3A226%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI762475890359886&g_tk=5381&jsonpCallback=getUCGI762475890359886&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18080%2C%22cur_page%22%3A227%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5011558231759943&g_tk=5381&jsonpCallback=getUCGI5011558231759943&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18160%2C%22cur_page%22%3A228%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30208510065381033&g_tk=5381&jsonpCallback=getUCGI30208510065381033&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18240%2C%22cur_page%22%3A229%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI18171742781388223&g_tk=5381&jsonpCallback=getUCGI18171742781388223&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18320%2C%22cur_page%22%3A230%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI40960466916690486&g_tk=5381&jsonpCallback=getUCGI40960466916690486&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18400%2C%22cur_page%22%3A231%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI18525226375973203&g_tk=5381&jsonpCallback=getUCGI18525226375973203&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18480%2C%22cur_page%22%3A232%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4707557959752604&g_tk=5381&jsonpCallback=getUCGI4707557959752604&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18560%2C%22cur_page%22%3A233%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8126047303163331&g_tk=5381&jsonpCallback=getUCGI8126047303163331&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18640%2C%22cur_page%22%3A234%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI17606941970162038&g_tk=5381&jsonpCallback=getUCGI17606941970162038&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18720%2C%22cur_page%22%3A235%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI29550650542078327&g_tk=5381&jsonpCallback=getUCGI29550650542078327&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18800%2C%22cur_page%22%3A236%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5444837959394178&g_tk=5381&jsonpCallback=getUCGI5444837959394178&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18880%2C%22cur_page%22%3A237%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI1316839220193955&g_tk=5381&jsonpCallback=getUCGI1316839220193955&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A18960%2C%22cur_page%22%3A238%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4408830295152373&g_tk=5381&jsonpCallback=getUCGI4408830295152373&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19040%2C%22cur_page%22%3A239%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6802689151782475&g_tk=5381&jsonpCallback=getUCGI6802689151782475&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19120%2C%22cur_page%22%3A240%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8586284838462332&g_tk=5381&jsonpCallback=getUCGI8586284838462332&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19200%2C%22cur_page%22%3A241%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8675399626176996&g_tk=5381&jsonpCallback=getUCGI8675399626176996&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19280%2C%22cur_page%22%3A242%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2537273756316534&g_tk=5381&jsonpCallback=getUCGI2537273756316534&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19360%2C%22cur_page%22%3A243%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8805803045992344&g_tk=5381&jsonpCallback=getUCGI8805803045992344&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19440%2C%22cur_page%22%3A244%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI06938500955137061&g_tk=5381&jsonpCallback=getUCGI06938500955137061&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19520%2C%22cur_page%22%3A245%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5260319751544662&g_tk=5381&jsonpCallback=getUCGI5260319751544662&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19600%2C%22cur_page%22%3A246%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8794912910617259&g_tk=5381&jsonpCallback=getUCGI8794912910617259&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19680%2C%22cur_page%22%3A247%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9743432089436523&g_tk=5381&jsonpCallback=getUCGI9743432089436523&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19760%2C%22cur_page%22%3A248%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4663948155253679&g_tk=5381&jsonpCallback=getUCGI4663948155253679&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19840%2C%22cur_page%22%3A249%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI20492142156970194&g_tk=5381&jsonpCallback=getUCGI20492142156970194&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A19920%2C%22cur_page%22%3A250%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI07355640822480858&g_tk=5381&jsonpCallback=getUCGI07355640822480858&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20000%2C%22cur_page%22%3A251%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5155874390920903&g_tk=5381&jsonpCallback=getUCGI5155874390920903&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20080%2C%22cur_page%22%3A252%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7023009971773353&g_tk=5381&jsonpCallback=getUCGI7023009971773353&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20160%2C%22cur_page%22%3A253%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9974061603764794&g_tk=5381&jsonpCallback=getUCGI9974061603764794&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20240%2C%22cur_page%22%3A254%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI017388810558033096&g_tk=5381&jsonpCallback=getUCGI017388810558033096&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20320%2C%22cur_page%22%3A255%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI624392254612226&g_tk=5381&jsonpCallback=getUCGI624392254612226&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20400%2C%22cur_page%22%3A256%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI27504718788131255&g_tk=5381&jsonpCallback=getUCGI27504718788131255&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20480%2C%22cur_page%22%3A257%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8549578949484182&g_tk=5381&jsonpCallback=getUCGI8549578949484182&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20560%2C%22cur_page%22%3A258%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5515729861650289&g_tk=5381&jsonpCallback=getUCGI5515729861650289&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20640%2C%22cur_page%22%3A259%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5730834217451333&g_tk=5381&jsonpCallback=getUCGI5730834217451333&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20720%2C%22cur_page%22%3A260%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8022375221067268&g_tk=5381&jsonpCallback=getUCGI8022375221067268&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20800%2C%22cur_page%22%3A261%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI40214003856863845&g_tk=5381&jsonpCallback=getUCGI40214003856863845&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20880%2C%22cur_page%22%3A262%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9389099870772117&g_tk=5381&jsonpCallback=getUCGI9389099870772117&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A20960%2C%22cur_page%22%3A263%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI44306896456461886&g_tk=5381&jsonpCallback=getUCGI44306896456461886&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21040%2C%22cur_page%22%3A264%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2669875249914859&g_tk=5381&jsonpCallback=getUCGI2669875249914859&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21120%2C%22cur_page%22%3A265%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI30315573510163896&g_tk=5381&jsonpCallback=getUCGI30315573510163896&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21200%2C%22cur_page%22%3A266%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI1874237664506726&g_tk=5381&jsonpCallback=getUCGI1874237664506726&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21280%2C%22cur_page%22%3A267%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI563747295007462&g_tk=5381&jsonpCallback=getUCGI563747295007462&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21360%2C%22cur_page%22%3A268%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI3755535419363165&g_tk=5381&jsonpCallback=getUCGI3755535419363165&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21440%2C%22cur_page%22%3A269%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6110248511492944&g_tk=5381&jsonpCallback=getUCGI6110248511492944&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21520%2C%22cur_page%22%3A270%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9957164695980032&g_tk=5381&jsonpCallback=getUCGI9957164695980032&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21600%2C%22cur_page%22%3A271%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI26565980667041234&g_tk=5381&jsonpCallback=getUCGI26565980667041234&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21680%2C%22cur_page%22%3A272%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9323518211083066&g_tk=5381&jsonpCallback=getUCGI9323518211083066&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21760%2C%22cur_page%22%3A273%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9905814876651162&g_tk=5381&jsonpCallback=getUCGI9905814876651162&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21840%2C%22cur_page%22%3A274%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI11649796881596397&g_tk=5381&jsonpCallback=getUCGI11649796881596397&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A21920%2C%22cur_page%22%3A275%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9740257388589244&g_tk=5381&jsonpCallback=getUCGI9740257388589244&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22000%2C%22cur_page%22%3A276%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI46341136988191356&g_tk=5381&jsonpCallback=getUCGI46341136988191356&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22080%2C%22cur_page%22%3A277%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI18101850695572752&g_tk=5381&jsonpCallback=getUCGI18101850695572752&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22160%2C%22cur_page%22%3A278%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8040580476638022&g_tk=5381&jsonpCallback=getUCGI8040580476638022&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22240%2C%22cur_page%22%3A279%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7703759020172409&g_tk=5381&jsonpCallback=getUCGI7703759020172409&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22320%2C%22cur_page%22%3A280%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI4122860998824198&g_tk=5381&jsonpCallback=getUCGI4122860998824198&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22400%2C%22cur_page%22%3A281%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI524734669328639&g_tk=5381&jsonpCallback=getUCGI524734669328639&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22480%2C%22cur_page%22%3A282%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6238735809122222&g_tk=5381&jsonpCallback=getUCGI6238735809122222&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22560%2C%22cur_page%22%3A283%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9781098317421004&g_tk=5381&jsonpCallback=getUCGI9781098317421004&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22640%2C%22cur_page%22%3A284%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI2768815491937753&g_tk=5381&jsonpCallback=getUCGI2768815491937753&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22720%2C%22cur_page%22%3A285%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI8687448662458015&g_tk=5381&jsonpCallback=getUCGI8687448662458015&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22800%2C%22cur_page%22%3A286%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6442055625372587&g_tk=5381&jsonpCallback=getUCGI6442055625372587&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22880%2C%22cur_page%22%3A287%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI5908187231442739&g_tk=5381&jsonpCallback=getUCGI5908187231442739&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A22960%2C%22cur_page%22%3A288%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI17364736040136908&g_tk=5381&jsonpCallback=getUCGI17364736040136908&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23040%2C%22cur_page%22%3A289%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI12648634590093888&g_tk=5381&jsonpCallback=getUCGI12648634590093888&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23120%2C%22cur_page%22%3A290%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI08960370861993572&g_tk=5381&jsonpCallback=getUCGI08960370861993572&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23200%2C%22cur_page%22%3A291%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI9613817939082494&g_tk=5381&jsonpCallback=getUCGI9613817939082494&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23280%2C%22cur_page%22%3A292%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6675365075380006&g_tk=5381&jsonpCallback=getUCGI6675365075380006&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23360%2C%22cur_page%22%3A293%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI6855400291450167&g_tk=5381&jsonpCallback=getUCGI6855400291450167&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23440%2C%22cur_page%22%3A294%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI19487142121452128&g_tk=5381&jsonpCallback=getUCGI19487142121452128&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23520%2C%22cur_page%22%3A295%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI14208075074278903&g_tk=5381&jsonpCallback=getUCGI14208075074278903&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23600%2C%22cur_page%22%3A296%7D%7D%7D");
	  u.add("https://u.y.qq.com/cgi-bin/musicu.fcg?callback=getUCGI7310643448653187&g_tk=5381&jsonpCallback=getUCGI7310643448653187&loginUin=0&hostUin=0&format=jsonp&inCharset=utf8&outCharset=utf-8&notice=0&platform=yqq&needNewCode=0&data=%7B%22comm%22%3A%7B%22ct%22%3A24%2C%22cv%22%3A10000%7D%2C%22singerList%22%3A%7B%22module%22%3A%22Music.SingerListServer%22%2C%22method%22%3A%22get_singer_list%22%2C%22param%22%3A%7B%22area%22%3A-100%2C%22sex%22%3A-100%2C%22genre%22%3A-100%2C%22index%22%3A-100%2C%22sin%22%3A23680%2C%22cur_page%22%3A297%7D%7D%7D");
	 
	  int cnt = 0;
	  Boolean flag = false;
	  for(int i = 0; i < u.size(); i++) {
		  try {
			JSONObject json = readJsonFromUrl(u.get(i));
			JSONArray singerList = json.getJSONObject("singerList").getJSONObject("data").getJSONArray("singerlist");
			int size = singerList.length();
			for(int j = 0; j < size; j++) {
				String singerName = singerList.getJSONObject(j).getString("singer_name");
				if(singerName.contains(singerinput)) {
					int rank = cnt + j + 1;
					System.out.println(singerName + " 第" + rank + "名");
					System.out.println("https://y.qq.com/n/yqq/singer/" +singerList.getJSONObject(j).getString("singer_mid")+".html");
					if(singerName.equals(singerinput.trim())) {
						flag = true;
						break;
					}
					System.out.println("搜索其他匹配歌手中...");
				}
			}
			if(flag) break;
			cnt += size;
		} 
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
	  System.out.println("搜索完毕");
  }

  public static void main(String[] args) throws IOException, JSONException, ParseException  {
	  File file = new File("input.txt");
	  BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
	  String singername = br.readLine();
	  singername = singername.replace("\uFEFF", "");
	  String[] singers = singername.split("/");
	  for(int i = 0; i < singers.length; i++) {
		  singers[i] = singers[i].trim();
	  }
	  String songname = br.readLine();
	  songname = songname.trim();
	  System.out.println("-------------------------------------");
	  System.out.println("歌名：" + songname + "    歌手：" + Arrays.toString(singers));
	  int cnt = 0;
	  Scanner scan = new Scanner(System.in);
	  while(true) {
		  System.out.println("-------------------------------------");
		  if(cnt == 0) {
			  System.out.println("请输入搜索榜单代码，可以任意顺序输入\n");
			  cnt++;
		  }
		  else System.out.println("需要搜索在其他榜单排名？");
		  System.out.println("0 QQ音乐歌手排名");
		  System.out.println("1 QQ音乐巅峰榜·热歌 ");
		  System.out.println("2 QQ音乐巅峰榜·新歌");
		  System.out.println("3 QQ音乐巅峰榜·网络歌曲");
		  System.out.println("4 QQ音乐巅峰榜·内地");
		  System.out.println("5 QQ音乐巅峰榜·港台");
		  System.out.println("6 QQ音乐巅峰榜·腾讯音乐人原创榜");
		  System.out.println("7 QQ音乐巅峰榜·K歌金曲");
		  System.out.println("8 QQ音乐巅峰榜·MV");
		  System.out.println("a 酷狗TOP500");
		  System.out.println("b 酷狗飙升榜");
		  System.out.println("c 酷狗网络红歌榜");
		  System.out.println("d 酷我飙升榜");
		  System.out.println("e 酷我华语榜");
		  System.out.println("Ctrl C直接结束程序");
		  System.out.println("-------------------------------------");
		  System.out.print("请输入： ");
		  String line = "";
		  if(scan.hasNextLine()) {
			  line = scan.nextLine();
			  if(line.isEmpty()) break;
		  }
		  if(line.contains("0")) {
			  System.out.println("QQ音乐歌手排名： ");
			  for(String singer : singers) {
				  searchSingerRank(singer);
			  }
		  }
		  if(line.contains("1")) {
			  System.out.println("QQ音乐巅峰榜·热歌: ");
			  if(line.contains("11")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(26, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(26, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("2")) {
			  System.out.println("QQ音乐巅峰榜·新歌： ");
			  if(line.contains("22")) {
				  Calendar cal = Calendar.getInstance();
				  Date currDate = getNewestDate();
				  for(int i = 0; i < 730; i++) {
					  String currDateStr = new SimpleDateFormat("yyyy-MM-dd").format(currDate);
					  System.out.print(currDateStr+": ");
					  searchQQ(27, singers, songname, "", currDateStr);
					  cal.setTime(currDate);
					  cal.add(Calendar.DATE, -1);
					  currDate = cal.getTime();
				  }
			  }
			  else {
				  searchQQ(27, singers, songname, "", new SimpleDateFormat("yyyy-MM-dd").format(getNewestDate()));
			  }
		  }
		  if(line.contains("3")) {
			  System.out.println("QQ音乐巅峰榜·网络歌曲: ");
			  if(line.contains("33")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(28, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(28, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("4")) {
			  System.out.println("QQ音乐巅峰榜·内地: ");
			  if(line.contains("44")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(5, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(5, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("5")) {
			  System.out.println("QQ音乐巅峰榜·港台: ");
			  if(line.contains("55")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(6, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(6, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("6")) {
			  System.out.println("QQ音乐巅峰榜·腾讯音乐人原创榜: ");
			  if(line.contains("66")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(52, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(52, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("7")) {
			  System.out.println("QQ音乐巅峰榜·K歌金曲: ");
			  if(line.contains("77")) {
				  String currWeek = getNewestWeek();
				  for(int i = 0; i < 104; i++) {
					  System.out.print(currWeek.substring(0, 4)+"年"+currWeek.substring(5)+"周： ");
					  searchQQ(36, singers, songname, currWeek, "");
					  currWeek = getPrevWeek(currWeek);
				  }
			  }
			  else {
				  searchQQ(36, singers, songname, getNewestWeek(), "");
			  }
		  }
		  if(line.contains("8")) {
			  System.out.println("QQ音乐巅峰榜·MV: ");
			  searchQQ(100, singers, songname, "", "");
		  }
		  if(line.contains("a")) {
			  System.out.println("酷狗TOP500：  ");
			  searchKugou(1, singers, songname);
		  }
		  if(line.contains("b")) {
			  System.out.println("酷狗飙升榜: ");
			  searchKugou(2, singers, songname);
		  }
		  if(line.contains("c")) {
			  System.out.println("酷狗网络红歌榜: ");
			  searchKugou(3, singers, songname);
		  }
		  if(line.contains("d")) {
			  System.out.println("酷我飙升榜: ");
			  searchKuwo(93, singers, songname);
		  }
		  if(line.contains("e")) {
			  System.out.println("酷我华语榜: ");
			  searchKuwo(62, singers, songname);
		  }
	  }
	  
    
  }
}