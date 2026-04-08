package com.optiflow.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.config.ConfigLoader;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class AIService
{

    private static final String API_KEY = ConfigLoader.getProperty("OPEN_ROUTER_API_KEY");
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public String generateTasks(String projectTitle, String projectDetails)
    {
        return generateTasks(projectTitle, projectDetails, Collections.emptyList());
    }

    public String generateTasks(String projectTitle, String projectDetails, List<String> availableSkills)
    {
        try
        {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("HTTP-Referer", "http://localhost");
            conn.setRequestProperty("X-Title", "OptiFlow");

            conn.setDoOutput(true);

            String prompt = buildPrompt(projectTitle, projectDetails, availableSkills);

            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> bodyMap = new HashMap<>();
            bodyMap.put("model", "mistralai/mixtral-8x7b-instruct");

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            messages.add(message);
            bodyMap.put("messages", messages);

            String requestBody = mapper.writeValueAsString(bodyMap);

            System.out.println("Request Body: " + requestBody);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();

            int statusCode = conn.getResponseCode();

            InputStream inputStream = (statusCode >= 200 && statusCode < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null)
            {
                response.append(line);
            }

            conn.disconnect();

            System.out.println("Status Code: " + statusCode);
            System.out.println("Response: " + response);

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private String buildPrompt(String title, String details, List<String> availableSkills)
    {
        String allowedSkills = (availableSkills == null || availableSkills.isEmpty())
                ? "No predefined skills provided."
                : String.join(", ", availableSkills);

        return "You are an expert project manager of your company.\n\n" +
                "Your task is to analyze the given project and break it down into clear, actionable tasks for your employees.\n\n" +

                "Instructions:\n" +
                "- Return ONLY a valid JSON array.\n" +
                "- Do NOT include explanations, headings, or extra text.\n" +
                "- Each task must be practical and implementation-focused.\n" +
                "- Divide the project into each task such that a specific person can do it with the necessary background.\n" +
                "- Cover each domain like frontend, creatives, construction etc. where applicable to fit project needs.\n\n" +

                "Each task object must follow this exact format:\n" +
                "{\n" +
                "  \"title\": \"string\",\n" +
                "  \"description\": \"string\",\n" +
                "  \"skills\": [\"string\"],\n" +
                "  \"estimated_hours\": number,\n" +
                "  \"priority\": \"HIGH | MEDIUM | LOW\"\n" +
                "}\n\n" +

                "Use only skills from this company skill catalog when possible:\n" + allowedSkills + "\n\n" +

                "Return an array of such task objects.\n\n" +

                "Project Title:\n" + title + "\n\n" +
                "Project Description:\n" + details;
    }
}