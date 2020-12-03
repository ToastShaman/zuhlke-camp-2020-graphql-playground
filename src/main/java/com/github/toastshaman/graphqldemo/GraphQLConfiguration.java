package com.github.toastshaman.graphqldemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import com.zuhlke.todo.client.TodoClient;
import com.zuhlke.todo.client.model.GetTodoRequest;
import com.zuhlke.todo.client.model.Todo;
import com.zuhlke.todo.client.model.UpdateTodoRequestBuilder;
import graphql.Scalars;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class GraphQLConfiguration {

    private final TodoClient client;

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module());

    public GraphQLConfiguration(TodoClient client) {
        this.client = client;
    }

    @Bean
    public GraphQLSchema schema(GraphQLCodeRegistry registry) {
        return GraphQLSchema.newSchema()
                .codeRegistry(registry)
                .query(GraphQLObjectType.newObject()
                        .name("Query")
                        .field(f -> f
                                .name("todo")
                                .argument(arg -> arg.name("id").type(Scalars.GraphQLString))
                                .type(todoObjectType("TodoObjectType"))
                        )
                )
                .mutation(GraphQLObjectType.newObject()
                        .name("UpdateTodo")
                        .field(f -> f
                                .name("todo")
                                .argument(arg -> arg.name("item").type(updateTodoInputObjectType()))
                                .type(todoObjectType("TodoUpdateObjectType")))
                )
                .build();
    }

    private GraphQLInputObjectType updateTodoInputObjectType() {
        return GraphQLInputObjectType.newInputObject()
                .name("UpdateTodoInputObjectType")
                .field(it -> it.name("id").type(Scalars.GraphQLString))
                .field(it -> it.name("rev").type(Scalars.GraphQLString))
                .field(it -> it.name("text").type(Scalars.GraphQLString))
                .field(it -> it.name("status").type(Scalars.GraphQLString))
                .field(it -> it.name("category").type(Scalars.GraphQLString))
                .field(it -> it.name("tags").type(GraphQLList.list(Scalars.GraphQLString)))
                .build();
    }

    private GraphQLObjectType todoObjectType(String name) {
        return GraphQLObjectType.newObject()
                .name(name)
                .field(it -> it.name("id").type(Scalars.GraphQLString))
                .field(it -> it.name("rev").type(Scalars.GraphQLString))
                .field(it -> it.name("text").type(Scalars.GraphQLString))
                .field(it -> it.name("status").type(Scalars.GraphQLString))
                .field(it -> it.name("category").type(Scalars.GraphQLString))
                .field(it -> it.name("tags").type(GraphQLList.list(Scalars.GraphQLString)))
                .build();
    }

    @Bean
    public GraphQLCodeRegistry codeRegistry() {
        return GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(FieldCoordinates.coordinates("Query", "todo"), new TodoFetcher(client))
                .dataFetcher(FieldCoordinates.coordinates("UpdateTodo", "todo"), new TodoUpdateMutationDataFetcher(client, mapper))
                .build();
    }

    private static class TodoFetcher implements DataFetcher<Todo> {
        private final TodoClient client;

        public TodoFetcher(TodoClient client) {
            this.client = client;
        }

        @Override
        public Todo get(DataFetchingEnvironment environment) {
            String id = environment.getArgument("id");
            return client.get(new GetTodoRequest(id)).getTodo();
        }
    }

    private static class TodoUpdateMutationDataFetcher implements DataFetcher<Todo> {
        private final TodoClient client;
        private final ObjectMapper mapper;

        public TodoUpdateMutationDataFetcher(TodoClient client, ObjectMapper mapper) {
            this.client = client;
            this.mapper = mapper;
        }

        @Override
        public Todo get(DataFetchingEnvironment environment) {
            Map<String, Object> item = environment.getArgument("item");
            Todo todo = mapper.convertValue(item, Todo.class);
            return client.update(UpdateTodoRequestBuilder.builder(todo).build()).getTodo();
        }
    }
}
