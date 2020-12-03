package com.github.toastshaman.graphqldemo;

import com.zuhlke.todo.client.TodoClient;
import com.zuhlke.todo.client.http.HttpTodoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TodoClientConfiguration {

    @Bean
    public HttpTodoClient todoClient() {
        return TodoClient.builder()
                .setHost("http://localhost:8080")
                .build();
    }
}
