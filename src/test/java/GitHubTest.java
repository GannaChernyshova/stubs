import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBodyExtractionOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class GitHubTest {

    @Test
    @DisplayName("Verify that /search/repositories response contains query in description")
    void testRepoSearch() {
        RestAssured.baseURI = "https://api.github.com";
        RestAssured.basePath = "/search/repositories";
        String searchQuery = "topic:akita";
        ResponseBodyExtractionOptions response = given().log().everything()
                .contentType(ContentType.JSON)
                .queryParam("q", searchQuery)
                .when().get()
                .then().log().everything()
                .extract().body();

        assertThat(response.jsonPath().getString("items[0].description").toLowerCase()).contains("akita");
    }

}
