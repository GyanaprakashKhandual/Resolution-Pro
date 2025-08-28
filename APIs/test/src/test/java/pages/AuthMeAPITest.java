package pages;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class AuthMeAPITest {

    private static final String BASE_URL = "http://localhost:5000";
    private static final String ENDPOINT = "/api/v1/auth/me";
    private static final String TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjY4YWFlNGU1NmZmOWFkYzUxMTQ2Y2MxNiIsImlhdCI6MTc1NjA0NDE5NiwiZXhwIjoxNzU4NjM2MTk2fQ.m2qOd0r2Xr_RftzWQ1UpQwkELAAWU2jP7tGzkLxU1K8";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = BASE_URL;
    }

    @Test
    public void testAuthMeAPI() {
        Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + TOKEN)
                .when()
                .get(ENDPOINT)
                .then()
                .statusCode(200)   // assert directly
                .extract()
                .response();

        // Optionally verify the response body is not empty
        String responseBody = response.getBody().asString();
        Assert.assertNotNull(responseBody, "Response body should not be null");

        System.out.println("Response: " + responseBody);
    }
}
