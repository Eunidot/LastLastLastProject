package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class APIMovie {
    // 네이버 API 키값
    String clientId = "";
    String clientSecret = "";

    URL url;
    String apiURL;
    String title;
    String inputLine;


    public String getinfo(String title, String type) {
        StringBuffer response = new StringBuffer();

        try {
            String text = URLEncoder.encode(title, "UTF-8");
            apiURL = "https://openapi.naver.com/v1/search/movie?query=" + text + "&yearfrom=1950&yearto=2017";
            url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            while ((inputLine = br.readLine()) != null) {
                if (inputLine.contains(type)) {
                    String arr[] = new String[5];
                    arr = inputLine.split("\"", 5);
                    response.append(arr[3] + "\n");
                    break;
                }
            }
            br.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        return response.toString();
    }
}
