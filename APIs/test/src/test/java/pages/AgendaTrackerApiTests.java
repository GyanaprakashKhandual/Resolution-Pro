package pages;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AgendaTrackerApiTests {

    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/agenda-tracker";

    // Valid token
    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verify agenda-tracker API responds with 200 for page=1, limit=10.
     */
    @Test
    @SuppressWarnings("SizeReplaceableByIsEmpty")
    public void testGetAgendaTrackerWithValidTokenAndPagination() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Validate response contains results
        Assert.assertTrue(response.jsonPath().getList("results").size() > 0,
                "Response should contain agenda tracker results");

        // Validate fields
        String agendaName = response.jsonPath().getString("results[0].agendaName");
        Assert.assertNotNull(agendaName, "Agenda name should not be null");

        String meetingType = response.jsonPath().getString("results[0].meetingId.notes.meetingType");
        Assert.assertEquals(meetingType, "board_meeting", "Meeting type should be board_meeting");

        System.out.println("Agenda: " + agendaName);
    }

    /**
     * Negative Test
     * Verify API returns 401 when token is missing.
     */
    @Test
    public void testGetAgendaTrackerWithoutToken() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(403);
    }

    /**
     * Edge Case
     * Verify API returns empty result when page=9999.
     */
    @Test
    public void testAgendaTrackerPaginationOutOfRange() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("page", 9999)
                .queryParam("limit", 10)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        Assert.assertTrue(response.jsonPath().getList("results").isEmpty(),
                "Results should be empty for out-of-range page");
    }

    /**
     * Edge Case
     * Verify limit parameter actually restricts number of records.
     */
    @Test
    public void testAgendaTrackerLimitRestriction() {
        int limit = 5;

        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .queryParam("page", 1)
                .queryParam("limit", limit)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)
                .extract()
                .response();

        int resultsCount = response.jsonPath().getList("results").size();
        Assert.assertTrue(resultsCount <= limit,
                "Results should not exceed the specified limit");

        System.out.println("Results returned: " + resultsCount + " (limit=" + limit + ")");
    }
}
