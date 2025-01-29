package com.example.superhero.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import java.util.Map;

@Service
public class SqsService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    public SqsService(SqsClient sqsClient, ObjectMapper objectMapper) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
    }


    public void sendJsonMessage(String queueUrl, Object data) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(data);

            SendMessageRequest request = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(jsonPayload)
                    .build();

            sqsClient.sendMessage(request);

            System.out.println("Sent JSON message to SQS: " + jsonPayload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing the object to JSON", e);
        }
    }

    public Map<String, Object> receiveAndDeleteMessage(String queueUrl) {
        ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
                .waitTimeSeconds(5)
                .build();

        ReceiveMessageResponse response = sqsClient.receiveMessage(receiveRequest);
        List<Message> messages = response.messages();

        if (messages.isEmpty()) {
            return null;
        }

        Message message = messages.get(0);
        System.out.println("Received message from SQS: " + message.body());

        try {
            // Deserialize JSON into a Map<String, Object>
            Map<String, Object> data = objectMapper.readValue(
                    message.body(),
                    new TypeReference<Map<String, Object>>() {}
            );

            // Delete the message
            deleteMessage(queueUrl, message.receiptHandle());

            return data;
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to Map<String, Object>", e);
        }
    }

    private void deleteMessage(String queueUrl, String receiptHandle) {
        DeleteMessageRequest deleteRequest = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();
        sqsClient.deleteMessage(deleteRequest);

        System.out.println("Deleted message: " + receiptHandle);
    }

    public Map<String, Object> convertMessageToMap(String message) {
        try {
            return objectMapper.readValue(
                    message,
                    new TypeReference<Map<String, Object>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to Map<String, Object>", e);
        }
    }
}