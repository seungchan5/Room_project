package project.study.component;

import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.net.URISyntaxException;

public class CustomMvcResult {

    private final MockMvc mockMvc;

    public CustomMvcResult(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @NotNull
    public MvcResult getMvcResult(HttpMethodType methodType, String url, HttpStatus httpStatus) throws Exception {
        return mockMvc.perform(getRequest(methodType, url))
            .andExpect(MockMvcResultMatchers.status().is(httpStatus.value()))
            .andReturn();
    }

    @NotNull
    private static MockHttpServletRequestBuilder getRequest(HttpMethodType methodType, String url) throws URISyntaxException {

        return switch (methodType) {
            case GET -> MockMvcRequestBuilders.get(new URI(url));
            case POST -> MockMvcRequestBuilders.post(new URI(url));
            case DELETE -> MockMvcRequestBuilders.delete(new URI(url));
        };
    }

    public enum HttpMethodType {
        GET,
        POST,
        DELETE;
    }

}
