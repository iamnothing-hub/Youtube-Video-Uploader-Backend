package com.youtube.video.uploader.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class YoutubeVideoUploadService {

//    search on google like "youtube video upload url api"
    private static final String  UPLOAD_URL = "https://www.googleapis.com/upload/youtube/v3/videos?uploadType=resumable&part=snippet,status";

    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static  final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    private ChatClient chatClient;

    public YoutubeVideoUploadService(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    // Method to Upload Video
    public String uploadVideo(String title, String description, String visibility, MultipartFile videoFile, String accessToken, String[] tags) throws IOException {

        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();

        // Correctly format metadata with variables
        String metaData = String.format("""
            {
              "snippet": {
                "title": "%s",
                "description": "%s",
                "tags": [%s],
                "categoryId": 22
              },
              "status": {
                "privacyStatus": "%s",
                "embeddable": true,
                "license": "youtube"
              }
            }
            """, title, description,
                Arrays.stream(tags).map(tag -> "\"" + tag + "\"").collect(Collectors.joining(",")),
                visibility);

        try {
            // Create POST request for metadata
            HttpRequest request = requestFactory.buildPostRequest(
                    new GenericUrl(UPLOAD_URL),
                    ByteArrayContent.fromString("application/json", metaData)
            );

            // Set headers
            request.getHeaders().setAuthorization("Bearer " + accessToken);
            request.getHeaders().setContentType("application/json");

            // Execute POST request
            HttpResponse response = request.execute();
            System.out.println("Response for POST: " + response);

            // Get video upload URL from response headers
            String videoUploadUrl = response.getHeaders().getLocation();
            if (videoUploadUrl == null) {
                throw new IOException("Failed to retrieve video upload URL.");
            }

            // Create PUT request for video file
            HttpRequest request1 = requestFactory.buildPutRequest(
                    new GenericUrl(videoUploadUrl),
                    new InputStreamContent("video/*", videoFile.getInputStream())
            );

            // Execute PUT request
            HttpResponse response1 = request1.execute();
            System.out.println("Response for PUT: " + response1);

            return "Uploaded Successfully";
        } catch (HttpResponseException e) {
            System.out.println("Exception is: => " + e.getStatusCode() + " " + e.getStatusMessage());
            if (e.getStatusCode() == 401) {
                return "Unauthorized: Invalid or expired token.";
            }
            throw e;
        } catch (Exception e) {
            System.out.println("Exception is: => " + e);
            return null;
        }
    }


    public Map<String, Object> generateMetaData(String title) throws JsonProcessingException {

        StringBuilder tempPrompt = new StringBuilder();
        tempPrompt.append("As you are a video seo expert for this video of title");
        tempPrompt.append("\"");
        tempPrompt.append(title);
        tempPrompt.append("\"");

        tempPrompt.append("Provide good rephrase title, and proper video description as well as video tags");
        tempPrompt.append("Description should be equivalent to title and description should be little bit large");

        tempPrompt.append("The content should be in json format as given: ");
        tempPrompt.append("\"{\\\"title\\\":\\\"newvideotitle\\\",\\\"description\\\":\\\"completevideodescription\\\",\\\"videotags\\\":[\\\"allvideotagsinthisarray\\\"]}\"");
        tempPrompt.append("Provide only json, no extra words or line be there in response");
        String prompt = tempPrompt.toString();
        String content = chatClient.prompt(prompt).call().content();

        content = content.replace("```json","");
        content = content.replace("```","");
        System.out.println(content);

        ObjectMapper objectMapper = new ObjectMapper();

        Map map = objectMapper.readValue(content, Map.class);
        return map;
    }
}
