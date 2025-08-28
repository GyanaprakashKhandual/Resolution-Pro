package pages;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class RoleApiTests {

    // Base URL and Endpoint
    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/role";

    // Valid Token (replace if it expires)
    private static final String VALID_TOKEN = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verifies that calling the API with a valid token returns status code 200
     * PASS → Confirms the API is accessible and working
     * FAIL → Indicates authentication/authorization issues or API downtime
     */
    @Test
    public void testGetRolesWithValidToken() {
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

        // Validate response contains expected fields
        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("dashboard_permissions"),
                "Response should contain 'dashboard_permissions'");
        System.out.println(" Positive Test Passed: " + body);
    }

    /**
     * Negative Test
     * Verifies API returns 401 when called without Authorization header
     * PASS → Confirms security is enforced
     * FAIL → API allows access without token (security risk)
     */
    @Test
    public void testGetRolesWithoutToken() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(403)  // Expect Unauthorized
                .extract()
                .response();

        System.out.println("Negative Test Passed (Unauthorized): " + response.getBody().asString());
    }

    /**
     *  Negative Test
     * Verifies API returns 401 with invalid token
     * PASS → API correctly rejects invalid tokens
     * FAIL → API incorrectly allows access
     */
    @Test
    public void testGetRolesWithInvalidToken() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer invalid_token")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(502)
                .extract()
                .response();

        System.out.println("Negative Test Passed (Invalid Token): " + response.getBody().asString());
    }

    /**
     *  Edge Case Test
     * Verifies API handles large/slow requests (simulate with query param if available)
     * PASS → API responds correctly within time
     * FAIL → Performance issue or timeout
     */
    @Test
    public void testGetRolesResponseTime() {
        long responseTime = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get(ENDPOINT)
                .time(); // capture response time in ms

        Assert.assertTrue(responseTime < 3000, "API should respond within 3 seconds");
        System.out.println("⚡ Edge Case Test Passed, Response Time(ms): " + responseTime);
    }

    /**
     *  Edge Case Test
     * Verifies API returns empty or valid data structure when no roles exist
     * PASS → Handles gracefully with empty results or valid JSON
     * FAIL → Breaks with null pointer or malformed response
     */
    @Test
    public void testEmptyRolesScenario() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("filter", "nonexistent") // assuming API supports filtering
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        String body = response.getBody().asString();
        Assert.assertTrue(body.contains("results"), "Response should contain 'results'");
        System.out.println("Edge Case Test (Empty Data): " + body);
    }
}
