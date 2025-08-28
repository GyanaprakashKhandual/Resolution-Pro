package pages;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class ComplianceTrackerApiTests {

    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/compliance-tracker";

    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verify compliance-tracker API responds with 200 and empty results when no data exists.
     */
    @Test
    public void testGetComplianceTrackerWithValidToken() {
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

        // Assert pagination values
        Assert.assertEquals(response.jsonPath().getInt("page"), 1, "Page should be 1");
        Assert.assertEquals(response.jsonPath().getInt("limit"), 10, "Limit should be 10");

        // Assert empty results
        Assert.assertTrue(response.jsonPath().getList("results").isEmpty(),
                "Results should be empty when no compliance records exist");

        // Assert total pages & results are 0
        Assert.assertEquals(response.jsonPath().getInt("totalPages"), 0, "Total pages should be 0");
        Assert.assertEquals(response.jsonPath().getInt("totalResults"), 0, "Total results should be 0");
    }

    /**
     * Negative Test
     * Verify API returns 401 when token is missing.
     */
    @Test
    public void testGetComplianceTrackerWithoutToken() {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .queryParam("page", 1)
                .queryParam("limit", 10)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(401);
    }

    /**
     * Edge Case
     * Verify API gracefully handles out-of-range page parameter.
     */
    @Test
    public void testComplianceTrackerOutOfRangePage() {
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
}
