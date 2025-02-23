package com.example.demo;

import okhttp3.*;
import org.json.*;
 
import org.springframework.stereotype.Service;

import java.io.IOException;
 
@Service
public class NameMatcherService {
      
    private static final  String API_KEY="Pd7SK4vqcnyyJ6wsE8lNUKwxc9j2UDgEgXUADsJy";
    		 
    
    private static final String API_URL = "http://127.0.0.1:8000/embed";

    
    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) dp[i][j] = j;
                else if (j == 0) dp[i][j] = i;
                else {
                    int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                    dp[i][j] = Math.min(dp[i - 1][j] + 1, Math.min(dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

     
    private double nameSimilarity(String s1, String s2) {
        int maxLen = Math.max(s1.length(), s2.length());
        int distance = levenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        return 1.0 - ((double) distance / maxLen);
    }

     
    private double[] getEmbedding(String text) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("sentences", new JSONArray().put(text));   
       

        RequestBody body = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build();

        Response response = client.newCall(request).execute();
        String responseData = response.body().string();
        System.out.println("API Response: " + responseData);
        
        JSONObject jsonResponse = new JSONObject(responseData);
        JSONArray embeddingArray = jsonResponse.getJSONArray("embeddings").getJSONArray(0);

        double[] embedding = new double[embeddingArray.length()];
        for (int i = 0; i < embeddingArray.length(); i++) {
            embedding[i] = embeddingArray.getDouble(i);
        }
        return embedding;
    }

    
    private double cosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0.0, norm1 = 0.0, norm2 = 0.0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            norm1 += Math.pow(vec1[i], 2);
            norm2 += Math.pow(vec2[i], 2);
        }
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    
    public int matchNames(String correctName, String givenName) throws IOException {
        String[] correctParts = correctName.toLowerCase().split(" ");
        String firstNameCorrect = correctParts[0];
        String lastNameCorrect = correctParts[correctParts.length - 1];

        String givenLower = givenName.toLowerCase();
        String[] givenParts = givenLower.split(" ");
        String firstNameGiven = givenParts.length > 0 ? givenParts[0] : "";
        double[] emb1 = getEmbedding(correctName);
        double[] emb2 = getEmbedding(givenName);

        
        double similarityScore = cosineSimilarity(emb1, emb2);

        return (int) (similarityScore * 100);
    }
}
