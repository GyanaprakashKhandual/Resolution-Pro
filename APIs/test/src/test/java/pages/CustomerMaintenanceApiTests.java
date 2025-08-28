package pages;  // package requested

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class CustomerMaintenanceApiTests {

    // Base URL and endpoint
    private static final String BASE_URL = "https://cdfwasmk2m.us-east-1.awsapprunner.com";
    private static final String ENDPOINT = "/v1/customer-maintenance";

    // Valid token (update if expired)
    private static final String VALID_TOKEN =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI2ODBmMGI5Y2MzODYyMjAwMjcxNmI0YzIiLCJpYXQiOjE3NTYzNTYyNzQsInR5cGUiOiJyZWZyZXNoIn0.VWGnmEeK_m2XSpehquoHfZcqHCgE25988uAutwFzGvI";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    /**
     * Positive Test
     * Verify API responds with 200 when valid token is provided.
     * Pass → Confirms customer-maintenance endpoint is accessible.
     * Fail → Authentication or server issue.
     */
    @Test
    public void testGetCustomerMaintenanceWithValidToken() {
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

        // Check that docs array and company name exist
        Assert.assertTrue(body.contains("docs"), "Response must contain 'docs'");
        Assert.assertTrue(body.contains("company_name"), "Response must contain company_name field");

        // Extract company_name field and validate
        String companyName = response.jsonPath().getString("docs[0].company_name");
        Assert.assertNotNull(companyName, "company_name should not be null");
        System.out.println("Positive Test Passed, Company Name: " + companyName);
    }

    /**
     * Negative Test
     * Verify API returns 401 Unauthorized when no token is sent.
     * Pass → Confirms security enforcement.
     * Fail → API is not secured properly.
     */
    @Test
    public void testGetCustomerMaintenanceWithoutToken() {
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
     * Verify API returns 401 Unauthorized when invalid token is used.
     * Pass → Confirms invalid tokens are rejected.
     * Fail → Security issue if invalid tokens are accepted.
     */
    @Test
    public void testGetCustomerMaintenanceWithInvalidToken() {
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
     * Verify API response time is under 3 seconds.
     * Pass → API performs well.
     * Fail → Performance issue.
     */
    @Test
    public void testGetCustomerMaintenanceResponseTime() {
        long responseTime = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", VALID_TOKEN)
                .when()
                .get(ENDPOINT)
                .time();

        Assert.assertTrue(responseTime < 3000, "Response time should be < 6000s");
        System.out.println("Edge Case Test Passed, Response Time: " + responseTime + " ms");
    }

    /**
     * Edge Case Test
     * Verify director details are included and contain expected fields.
     * Pass → API returns nested structure correctly.
     * Fail → Missing nested fields indicates data issue.
     */
    @Test
    public void testGetCustomerMaintenanceDirectorDetails() {
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

        // Validate directorDataDetails array
        String directorName = response.jsonPath().getString("docs[0].directorDataDetails[0].name");
        Assert.assertNotNull(directorName, "Director name should not be null");

        String designation = response.jsonPath().getString("docs[0].directorDataDetails[0].designation");
        Assert.assertNotNull(designation, "Director designation should not be null");

        System.out.println("Edge Case Director Test Passed, Director: " + directorName + ", Designation: " + designation);
    }
}
