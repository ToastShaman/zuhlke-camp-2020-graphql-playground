package com.github.toastshaman.graphqldemo.graphql;

import graphql.Scalars;
import graphql.schema.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfiguration {

    @Bean
    public GraphQLSchema schema(GraphQLCodeRegistry registry) {
        return GraphQLSchema.newSchema()
                .codeRegistry(registry)
                .query(GraphQLObjectType.newObject()
                        .name("foobar")
                        .field(field -> field
                                .name("test")
                                .type(Scalars.GraphQLString)
                        )
                        .field(field -> field
                                .name("name")
                                .type(Scalars.GraphQLString)
                        )
                        .build())
                .build();
    }

    @Bean
    public GraphQLCodeRegistry codeRegistry() {
        return GraphQLCodeRegistry.newCodeRegistry()
                .dataFetcher(FieldCoordinates.coordinates("foobar", "test"), new AsyncDataFetcher<String>(new StaticDataFetcher("response")))
                .dataFetcher(FieldCoordinates.coordinates("foobar", "name"), new AsyncDataFetcher<String>(new StaticDataFetcher("response")))
                .build();
    }
}
