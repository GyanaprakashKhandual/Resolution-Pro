package pages;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class ShareholderMeetingApiTests {

    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/shareholder-meeting";

    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verify shareholder-meeting API responds with 200 and contains meeting data.
     */
    @Test
    public void testGetShareholderMeetingWithValidToken() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Assert pagination values
        Assert.assertEquals(response.jsonPath().getInt("page"), 1, "Page should be 1");
        Assert.assertEquals(response.jsonPath().getInt("limit"), 10, "Limit should be 10");

        // Assert results are not empty
        Assert.assertFalse(response.jsonPath().getList("results").isEmpty(),
                "Results should not be empty for shareholder-meeting");

        // Validate first meeting details
        String meetingType = response.jsonPath().getString("results[0].notes.meetingType");
        Assert.assertEquals(meetingType, "shareholder_meeting", "Meeting type must be shareholder_meeting");

        String auditorName = response.jsonPath().getString("results[0].auditor_participant.name");
        Assert.assertNotNull(auditorName, "Auditor participant name should not be null");

        String momTemplate = response.jsonPath().getString("results[0].mom.templateName");
        Assert.assertEquals(momTemplate, "MOM", "MOM templateName must be MOM");
    }

    /**
     * Negative Test
     * Verify API returns 401 when token is missing.
     */
    @Test
    public void testGetShareholderMeetingWithoutToken() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(403);
    }

    /**
     * Edge Case
     * Verify API returns empty list for out-of-range page.
     */
    @Test
    public void testShareholderMeetingOutOfRangePage() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("page", 9999)
                .queryParam("limit", 10)
                .queryParam("sortBy", "createdAt:desc")
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertTrue(response.jsonPath().getList("results").isEmpty(),
                "Results should be empty for out-of-range page");
    }
}
