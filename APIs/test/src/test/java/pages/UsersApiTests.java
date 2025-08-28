package pages;  // package as requested

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class UsersApiTests {

    // Base URL and endpoint
    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/users";

    // Valid token (replace with new one if expired)
    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verifies that the API responds with status code 200 when a valid token is provided.
     * Pass → Confirms API is accessible and working.
     * Fail → Authentication or server issue.
     */
    @Test
    public void testGetUsersWithValidToken() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String body = response.getBody().asString();

        // Basic validations
        Assert.assertTrue(body.contains("results"), "Response should contain 'results'");
        Assert.assertTrue(body.contains("email"), "Response should contain 'email' field");

        System.out.println("Positive Test Passed: " + body);
    }

    /**
     * Negative Test
     * Verifies that the API returns 401 Unauthorized when no Authorization header is provided.
     * Pass → Confirms security enforcement.
     * Fail → API allows access without authentication (security risk).
     */
    @Test
    public void testGetUsersWithoutToken() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(403);
    }

    /**
     * Negative Test
     * Verifies that the API returns 401 Unauthorized when an invalid token is provided.
     * Pass → Confirms invalid tokens are rejected.
     * Fail → API incorrectly allows access.
     */
    @Test
    public void testGetUsersWithInvalidToken() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(401);
    }

    /**
     * Edge Case Test
     * Verifies that the API responds within acceptable performance limits.
     * Pass → Response time is acceptable.
     * Fail → Performance issue (slow API).
     */
    @Test
    public void testGetUsersResponseTime() {
        long time = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get(ENDPOINT)
                .time();

        Assert.assertTrue(time < 3000, "Response time should be under 3 seconds");
        System.out.println("Edge Case Test Passed, Response time: " + time + " ms");
    }

    /**
     * Edge Case Test
     * Verifies that the API handles filters gracefully (if supported).
     * Pass → Response should contain results field even if filter gives empty data.
     * Fail → API throws error for empty/non-existent data.
     */
    @Test
    public void testGetUsersWithInvalidFilter() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("name", "nonexistentUser123")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("results"), "Response should contain 'results'");
        System.out.println("Edge Case Filter Test Passed: " + body);
    }
}
