package be.pieterjd.vdbru2024.todo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {
    private TodoClient client;

    public TodoController(TodoClient client) {
        this.client = client;
    }

    @GetMapping("/all")
    List<Todo> getAll() throws URISyntaxException, IOException, InterruptedException {
        return client.getTodos();
    }

}
