package com.misakamikoto.urlparser.parser.common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestHttpGet {
    private String USER_AGENT = "Mozilla/5.0";

    public String call(String url) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);

        // add request header
        request.addHeader("User-Agent", USER_AGENT);
        request.addHeader("Content-Type", "application/json;charset=UTF-8");

        HttpResponse response = client.execute(request);

        System.out.println("Response Code : "
                + response.getStatusLine().getStatusCode());

        String result = "";
        HttpEntity resEntity = response.getEntity();
        if (resEntity != null) {
            result = EntityUtils.toString(resEntity,"UTF-8");
        }
        return result;
    }
}
