package com.example.demo;

import okhttp3.*;
import org.json.*;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class NameMatcherService {

    private static final String API_URL = "http://127.0.0.1:8000/similarity";

    public double matchNames(String name1, String name2) throws IOException {
        OkHttpClient client = new OkHttpClient();

        // Create JSON payload
        JSONObject json = new JSONObject();
        json.put("name1", name1);
        json.put("name2", name2);

        // Build the request body
        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        // Create request
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute the request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            // Parse the response
            String responseData = response.body().string();
            System.out.println("API Response: " + responseData);

            // Extract similarity score
            JSONObject jsonResponse = new JSONObject(responseData);
            return jsonResponse.getDouble("similarity_percentage");  // Returns the similarity score
        }
    }
}
