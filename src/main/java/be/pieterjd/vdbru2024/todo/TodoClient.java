package be.pieterjd.vdbru2024.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TodoClient {
    @Value("${todoapi.url}")
    private String endpoint;
    private ObjectMapper mapper;
    private HttpClient client;

    @Autowired
    public TodoClient(ObjectMapper mapper){
        this.mapper = mapper;
        client = HttpClient.newHttpClient();
    }

    public List<Todo> getTodos() throws URISyntaxException, IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(new URI(String.format("%s/todos", endpoint)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()/100 != 2){
            throw new TodoClientException("Something went wrong");
        }
        return Arrays.asList(mapper.readValue(response.body(), Todo[].class));
    }

    public Todo saveTodo(Todo todo) throws IOException, URISyntaxException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(todo)))
                .uri(new URI(String.format("%s/todos", endpoint)))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode()/100 != 2){
            throw new TodoClientException("Something went wrong");
        }
        return mapper.readValue(response.body(), Todo.class);
    }
}
