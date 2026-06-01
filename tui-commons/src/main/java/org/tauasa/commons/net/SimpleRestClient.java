/*
 * Copyright 2026 Tauasa Timoteo
 * 
 * Permission is hereby granted, free of charge, to any person 
 * obtaining a copy of this software and associated documentation 
 * files (the “Software”), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, 
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, 
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be 
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, 
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES 
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-
 * INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS 
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN 
 * AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF 
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS 
 * IN THE SOFTWARE.
 */
package org.tauasa.commons.net;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * SimpleRestClient is a basic HTTP client for making RESTful API calls. It supports GET, POST, PUT, and DELETE methods.
 * It uses Java's built-in HttpClient for making HTTP requests and handling responses.
 * <code>
 * SimpleRestClient client = new SimpleRestClient("https://api.example.com");
 *  try {
 *      String getResponse = client.get("/users");
 *      System.out.println("GET response: " + getResponse);
 * 
 *      String postResponse = client.post("/users", "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
 *      System.out.println("POST response: " + postResponse);
 *  } catch (Exception e) {
 *      e.printStackTrace();
 *  }
 * </code>
 * 
 * @author Tauasa Timoteo
 * @version 1.0
 * @since 2026-06-01 
*/
public class SimpleRestClient {

    private final HttpClient httpClient;
    private final String baseUrl;

    public SimpleRestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    public String post(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    public String put(String endpoint, String jsonBody) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .uri(URI.create(baseUrl + endpoint))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    public String delete(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create(baseUrl + endpoint))
                .header("Accept", "application/json")
                .build();
        
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return handleResponse(response);
    }

    private String handleResponse(HttpResponse<String> response) {
        int statusCode = response.statusCode();
        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        } else {
            throw new RuntimeException("HTTP request failed with status code: " + statusCode);
        }
    }

    /*
    // Usage example
    public static void main(String[] args) {
        SimpleRestClient client = new SimpleRestClient("https://api.example.com");
        try {
            String getResponse = client.get("/users");
            System.out.println("GET response: " + getResponse);

            String postResponse = client.post("/users", "{\"name\":\"John Doe\",\"email\":\"john@example.com\"}");
            System.out.println("POST response: " + postResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//*/
}
