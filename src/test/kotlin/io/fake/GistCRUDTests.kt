package io.fake

import io.fake.objects.Gist
import io.qameta.allure.Issue
import io.restassured.RestAssured.*
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.Test
import java.util.*

class GistCRUDTests {

    @Test()
    fun `Create a gist`() {
        val gist = newGist()
        val result = given()
                .body(gist)
                .`when`()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .extract()
                .response()

        assertThat(result.jsonPath().getString("description"))
                .contains(gist.description)

        assertThat(result.jsonPath().getString("files"))
                .contains(gist.files.keys.first(),
                        gist.files[gist.files.keys.first()]!!["content"])

    }

    @Test()
    fun `Read a gist`() {
        val gist = newGist()
        val existingGist = createNewGist(gist)

        val result = get("/gists/$existingGist")
                .then()
                .extract()
                .response()

        assertThat(result.jsonPath().getString("description"))
                .contains(gist.description)
        assertThat(result.jsonPath().getString("files"))
                .contains(gist.files.keys.first(),
                        gist.files[gist.files.keys.first()]!!["content"])
    }

    @Test()
    fun `Update a gist`() {
        val oldGist = createNewGist(newGist())
        val newGist = newGist()
        val updatedGist = given()
                .body(newGist)
                .`when`()
                .patch("/gists/$oldGist")
                .then()
                .extract()
                .response()

        assertThat(updatedGist.jsonPath().getString("description"))
                .contains(newGist.description)

        assertThat(updatedGist.jsonPath().getString("files"))
                .contains(newGist.files.keys.first(),
                        newGist.files[newGist.files.keys.first()]!!["content"])
    }

    @Test
    fun `Delete a gist`() {
        val gist = createNewGist(newGist())

        delete("/gists/$gist")
                .then()
                .assertThat()
                .statusCode(204)
        get("/gists/$gist")
                .then()
                .assertThat()
                .statusCode(404)
    }

    @Test()
    fun `Check not starred gist`() {
        val gistId = createNewGist(newGist())
        get("/gists/$gistId/star")
                .then()
                .statusCode(404)
    }

    @Issue("DEFECT, delete command on non exist resource (star) returns 204 No content, but should return 404")
    @Test()
    fun `Edge case unstar not starred gist`() {
        val gist = newGist()
        val gistId = createNewGist(gist)
        given()
                .body(gist)
                .delete("/gists/$gistId/star")
                .then()
                .statusCode(404)
    }

    //prerequisite for tests, create a gist and returns its ID
    private fun createNewGist(gist: Gist): String? {
        return given()
                .body(gist)
                .`when`()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
                .and()
                .extract().jsonPath().getString("id")
    }

    //create a new gist object
    //UUID is needed for making objects unique and keeping tests independent and parallel runnable
    private fun newGist(): Gist {
        val gistContent = mapOf("content" to UUID.randomUUID().toString())
        return Gist(UUID.randomUUID().toString(), true, mapOf(UUID.randomUUID().toString() + ".txt" to gistContent))
    }

}