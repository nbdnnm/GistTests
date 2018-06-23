package io.fake

import io.fake.objects.Gist
import io.qameta.allure.Issue
import io.restassured.RestAssured.*
import org.assertj.core.api.Assertions.assertThat
import org.testng.annotations.AfterClass
import org.testng.annotations.Test
import java.lang.System.getProperty
import java.util.*

class GistCRUDTests {

    @Test()
    fun `Create a gist`() {
        val gist = getUniqueGist()
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
        val gist = getUniqueGist()
        val existingGist = createGist(gist)

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
        val oldGist = getUniqueGist()
        val oldGistId = createGist(oldGist)

        //modify old gist with new content and description
        val newContent = mapOf("content" to UUID.randomUUID().toString())
        val newGist = Gist(UUID.randomUUID().toString(), true, mapOf(oldGist.files.keys.first() to newContent))
        
        val updatedGist = given()
                .body(newGist)
                .`when`()
                .patch("/gists/$oldGistId")
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
        val gist = createGist(getUniqueGist())

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
        val gistId = createGist(getUniqueGist())
        get("/gists/$gistId/star")
                .then()
                .statusCode(404)
    }

    @Issue("DEFECT, delete command on non exist resource (star) returns 204 No content, but should return 404")
    @Test()
    fun `Unstar not starred gist`() {
        val gist = getUniqueGist()
        val gistId = createGist(gist)
        given()
                .body(gist)
                .delete("/gists/$gistId/star")
                .then()
                .statusCode(404)
    }

    //enable if you want to clean up gists
    @AfterClass(enabled = false)
    fun `Remove all gists`() {
        do {
            val result = get("/users/" + getProperty("user") + "/gists")
                    .then()
                    .extract()
                    .body()

            val truncated = result.jsonPath().getBoolean("truncated")

            val allGists = result.jsonPath()
                    .getList("id", String::class.java)

            allGists.forEach {
                delete("/gists/$it")
            }
        } while (truncated)
    }

    //prerequisite for tests, create a gist and returns its ID
    private fun createGist(gist: Gist): String? {
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
    private fun getUniqueGist(): Gist {
        val gistContent = mapOf("content" to UUID.randomUUID().toString())
        return Gist(UUID.randomUUID().toString(), true, mapOf(UUID.randomUUID().toString() + ".txt" to gistContent))
    }

}