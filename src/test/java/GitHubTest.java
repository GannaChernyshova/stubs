import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBodyExtractionOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


public class GitHubTest {
    private static WireMockServer wireMockServer =
            new WireMockServer(WireMockConfiguration.options()
                    .extensions(new ResponseTemplateTransformer(false))
                    .port(8097));

    @BeforeAll
    public static void beforeAll() {
        wireMockServer.start();
        configureFor("localhost", 8097);
        stubFor(get(urlMatching("/search/repositories\\?q=.*"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withTransformers("response-template")
                        .withBody("{\n" +
                                "  \"total_count\": 39,\n" +
                                "  \"incomplete_results\": false,\n" +
                                "  \"items\": [\n" +
                                "    {\n" +
                                "      \"id\": 271826090,\n" +
                                "      \"node_id\": \"MDEwOlJlcG9zaXRvcnkyNzE4MjYwOTA=\",\n" +
                                "      \"name\": \"jira-clone-angular\",\n" +
                                "      \"full_name\": \"trungk18/jira-clone-angular\",\n" +
                                "      \"html_url\": \"https://github.com/trungk18/jira-clone-angular\",\n" +
                                "      \"description\": \"{{request.query.q}}\" }]}")));

    }


    @Test
    @DisplayName("Verify that /search/repositories response contains query in description")
    void testRepoSearch() {
        RestAssured.baseURI = "http://localhost:8097";
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

    @AfterAll
    public static void afterAll() {
        wireMockServer.stop();
    }
}
