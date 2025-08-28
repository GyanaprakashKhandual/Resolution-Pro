package pages;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CircularResolutionApiTests {

    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/circular-resolution";

    // Valid token
    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verify circular-resolution API responds with 200 when valid token is provided.
     */
    @Test
    public void testGetCircularResolutionWithValidToken() {
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

        // Basic field validation
        Assert.assertTrue(body.contains("results"), "Response must contain 'results'");
        Assert.assertTrue(body.contains("status"), "Response must contain 'status' field");

        String status = response.jsonPath().getString("results[0].status");
        Assert.assertEquals(status, "approved", "Expected status should be 'approved'");

        String pdfLink = response.jsonPath().getString("results[0].fileName");
        Assert.assertTrue(pdfLink.endsWith(".pdf"), "fileName should be a PDF link");

        System.out.println("Positive Test Passed, Status: " + status + ", PDF: " + pdfLink);
    }

    /**
     * Negative Test
     * Verify API returns 401 when no token is provided.
     */
    @Test
    public void testGetCircularResolutionWithoutToken() {
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
     * Verify API returns 401 with invalid token.
     */
    @Test
    public void testGetCircularResolutionWithInvalidToken() {
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
     * Verify response time is within acceptable range (< 3s).
     */
    @Test
    public void testCircularResolutionResponseTime() {
        long responseTime = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get(ENDPOINT)
                .time();

        Assert.assertTrue(responseTime < 3000, "Response time should be < 1000s");
        System.out.println("Response Time: " + responseTime + " ms");
    }

    /**
     * Edge Case Test
     * Verify agenda object contains expected values.
     */
    @Test
    public void testCircularResolutionAgendaFields() {
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

        String meetingType = response.jsonPath().getString("results[0].agenda.meetingType");
        Assert.assertEquals(meetingType, "board_meeting", "Meeting type must be board_meeting");

        String templateName = response.jsonPath().getString("results[0].agenda.templateName");
        Assert.assertNotNull(templateName, "Template name should not be null");

        System.out.println("Agenda Check Passed, MeetingType: " + meetingType + ", Template: " + templateName);
    }
}
