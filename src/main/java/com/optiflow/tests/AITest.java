package com.optiflow.tests;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.optiflow.dto.TaskDTO;
import com.optiflow.models.Tasks;
import com.optiflow.services.AIService;

import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class AITest
{
    public static void main(String[] args) throws JsonProcessingException
    {
        Scanner sc = new Scanner(System.in);
        AIService aiService = new AIService();

        System.out.println("********    AI Test    ********");
        System.out.println("1. Generate Tasks for a Project");

        System.out.print("\nEnter Title: ");
        String title = sc.nextLine();
        System.out.print("Enter Description: ");
        String desc = sc.nextLine();
        String response = aiService.generateTasks(title, desc);

        List<Tasks> tasksList = new LinkedList<>();
        List<TaskDTO> tasks = null;
        try
        {
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(response);

            String content = root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            content = content.replace("```json", "")
                    .replace("```", "")
                    .trim();

            tasks = mapper.readValue(
                    content,
                    new TypeReference<List<TaskDTO>>() {
                    });

            System.out.println("Task: " + tasks);

            for (TaskDTO task : tasks) {
                Tasks t = new Tasks();
                t.setTitle(task.getTitle());
                t.setDescription(task.getDescription());
                t.setSkillsList(task.getSkills());
                t.setEstimated_hours(task.getEstimatedHours());
                t.setPriority(task.getPriority());

                tasksList.add(t);
            }

            for(Tasks task: tasksList)
            {
                System.out.println();
                System.out.println(task.getTitle());
                System.out.println(task.getDescription());
                System.out.println(task.getSkillsList());
                System.out.println(task.getEstimated_hours());
                System.out.println(task.getPriority());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
