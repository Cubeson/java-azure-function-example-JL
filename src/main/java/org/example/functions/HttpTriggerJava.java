package org.example.functions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import org.example.functions.db.Person;

/**
 * Azure Functions with HTTP Trigger.
 */
public class HttpTriggerJava {
    /**
     * This function listens at endpoint "/api/HttpTriggerJava". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpTriggerJava
     * 2. curl {your host}/api/HttpTriggerJava?name=HTTP%20Query
     */
    @FunctionName("HttpTriggerJava")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET, HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        String query = request.getQueryParameters().get("name");
        String name = request.getBody().orElse(query);

        if (name == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
        } else {
            return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
        }
    }
    @FunctionName("GetPersons")
    public HttpResponseMessage GetPersons(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws SQLException {
        String dbUrl = System.getenv("DB_CONNECTION_STRING");
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            String sql = "SELECT * FROM Person";
            try (java.sql.Statement statement = connection.createStatement();
                 java.sql.ResultSet resultSet = statement.executeQuery(sql)) {
                List<Person> persons = new ArrayList<>();
                while (resultSet.next()) {
                    persons.add(new Person(resultSet.getString("FirstName"), resultSet.getString("LastName")));
                }
                return request.createResponseBuilder(HttpStatus.OK).body(persons).build();
                 }

        }catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getStackTrace()).build();
        }
    }
    @FunctionName("tmp")
    public HttpResponseMessage tmp(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws JsonProcessingException {
        String json = "{\n" +
                "    \"FirstName\": \"Jan1\",\n" +
                "    \"LastName\": \"Kowalski\"\n" +
                "}";
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(json, Person.class);

        return request.createResponseBuilder(HttpStatus.OK).body(person).build();
    }

    @FunctionName("AddPerson")
    public HttpResponseMessage AddPerson(
            @HttpTrigger(name = "req", methods = {HttpMethod.POST}, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) throws SQLException, JsonProcessingException {
        String body = request.getBody().orElse(null);
        if(body == null || body.isEmpty()) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Body is empty").build();
        }
        Person person = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            person = mapper.readValue(body, Person.class);
        }catch (JsonProcessingException e){
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getStackTrace()).build();
        }

        String dbUrl = System.getenv("DB_CONNECTION_STRING");
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            String sql = "INSERT INTO Person (FirstName, LastName) VALUES (?,?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, person.getFirstName());
            statement.setString(2, person.getLastName());
            statement.executeUpdate();
            return request.createResponseBuilder(HttpStatus.OK).body("Person added").build();

        }catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body(e.getStackTrace()).build();
        }
    }

}
/* Nie działa mi :(
        context.getLogger().info("Starting execution of GetPersons.");
        SessionFactory sf = HibernateUtil.getSessionFactory();
        context.getLogger().info("Created SessionFactory.");
        Session session = sf.openSession();
        context.getLogger().info("Opened Session.");
        MultiIdentifierLoadAccess<Person> multi = session.byMultipleIds(Person.class);
        context.getLogger().info("Obtained persons from DB.");
        List<Person> persons = multi.multiLoad();
        context.getLogger().info("Multiloaded persons.");

 */
