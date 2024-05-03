package be.pieterjd.vdbru2024.todo;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.net.URISyntaxException;
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
        List<Todo> todos = client.getTodos();
        assertNotNull(todos);
        assertFalse(todos.isEmpty());
        assertEquals(200, todos.size());
    }

    @Test
    void shouldThrowException_whenApiIsNotWorking(){
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
        assertNull(toSave.id());

        Todo actual = client.saveTodo(toSave);
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertEquals(toSave.completed(), actual.completed());
        assertEquals(toSave.title(), actual.title());
        assertEquals(toSave.userId(), actual.userId());
    }

    @Test
    void shouldNotSaveAndThrowException_whenTitleIsEmpty(){
        Todo toSave = new Todo(10, null,"", false);
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
}