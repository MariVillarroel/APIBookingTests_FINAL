// src/test/java/utils/Request.java
package utils;

import constants.EndPoints;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class Request {

    static {
        RestAssured.baseURI = EndPoints.BASE_URL;
    }

    // GET
    public static Response get(String endpoint) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .when()
                .get(endpoint);
    }

    // GET by ID
    public static Response getById(String endpoint, String id) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .get(endpoint);
    }

    // POST
    public static Response post(String endpoint, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(endpoint);
    }

    // PUT
    public static Response put(String endpoint, String id, String body) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .body(body)
                .when()
                .put(endpoint);
    }

    // DELETE
    public static Response delete(String endpoint, String id) {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .pathParam("id", id)
                .when()
                .delete(endpoint);
    }
}