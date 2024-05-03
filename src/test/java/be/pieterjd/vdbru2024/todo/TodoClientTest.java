package be.pieterjd.vdbru2024.todo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.maciejwalkowiak.wiremock.spring.ConfigureWireMock;
import com.maciejwalkowiak.wiremock.spring.EnableWireMock;
import com.maciejwalkowiak.wiremock.spring.InjectWireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@EnableWireMock(
        @ConfigureWireMock(
                name = "jsonplaceholder", //name of api to mock, also used in test resources folder
                property = "todoapi.url"  //the property to inject the wiremock host and portname into

        )
)
class TodoClientTest {
    @Autowired
    private TodoClient client;
    @InjectWireMock("jsonplaceholder")
    private WireMockServer wiremock;

    @Autowired
    private ObjectMapper mapper;
    @Test
    void shouldGet200Todos_whenApiOnline() throws URISyntaxException, IOException, InterruptedException {
        // this test uses the mapping defined in resources/wiremock/jsonplaceholder/mappings
        List<Todo> todos = client.getTodos();
        assertNotNull(todos);
        assertFalse(todos.isEmpty());
        assertEquals(200, todos.size());
    }

    @Test
    void shouldThrowException_whenApiIsNotWorking(){
        // this stubbing Overrides the mapping defined in resources/wiremock/jsonplaceholder/mappings
        wiremock.stubFor(
                WireMock.get("/todos")
                        .willReturn(
                                ResponseDefinitionBuilder.responseDefinition()
                                        .withStatus(500)
                        )
        );
        assertThrowsExactly(TodoClientException.class, ()->client.getTodos());
    }

    @Test
    void shouldHaveId_whenSavingValidNewTodo() throws IOException, URISyntaxException, InterruptedException {
        Todo toSave = new Todo(10, null,"New Todo", false);
        Todo expectedAfterPost = new Todo(10, 201,"New Todo", false);
        wiremock.stubFor(
                WireMock.post("/todos")
                        .withRequestBody(WireMock.equalToJson(mapper.writeValueAsString(toSave)))
                        .willReturn(
                                ResponseDefinitionBuilder.responseDefinition()
                                        .withStatus(201)
                                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                        .withBody(mapper.writeValueAsString(expectedAfterPost))

                        )
        );
        assertNotNull(toSave);
        assertNull(toSave.getId());

        Todo actual = client.saveTodo(toSave);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertEquals(toSave.getCompleted(), actual.getCompleted());
        assertEquals(toSave.getTitle(), actual.getTitle());
        assertEquals(toSave.getUserId(), actual.getUserId());
    }

    @Test
    void shouldNotSaveAndThrowException_whenTitleIsEmpty(){
        Todo toSave = new Todo(10, null,"", false);
        // next comment block is equivalent to the mapping in save_todo_with_empty_title.json
        /*
        wiremock.stubFor(
                WireMock.post("/todos")
                        .withRequestBody(WireMock.matchingJsonPath("$.title", WireMock.absent()))
                        .willReturn(
                                ResponseDefinitionBuilder.responseDefinition()
                                        .withStatus(500)
                        )
        );

         */
        assertThrowsExactly(TodoClientException.class, ()->client.saveTodo(toSave));
    }

    @Test
    void shouldUpdateTodo_whenSavingExistingTodo() throws URISyntaxException, IOException, InterruptedException {
        Todo todoById = client.getTodoById(1);
        assertNotNull(todoById);
        assertNotNull(todoById.getId());

        String newTitle = todoById.getTitle() + String.format("Updated at %s", LocalDate.now());
        todoById.setTitle(newTitle);
        // next comment block is equivalent to the mapping in update_todo.json
        /*
        wiremock.stubFor(
                WireMock.put(WireMock.urlMatching("/todos/(\\d+)"))
                        .willReturn(
                                ResponseDefinitionBuilder.responseDefinition()
                                        .withTransformers("response-template")
                                        .withBody("{{request.body}}")
                                        .withStatus(201)
                        )
        );
        */


        Todo updatedTodo = client.updateTodo(todoById);
        assertEquals(todoById.getId(), updatedTodo.getId());
        assertEquals(newTitle, updatedTodo.getTitle());
    }
}