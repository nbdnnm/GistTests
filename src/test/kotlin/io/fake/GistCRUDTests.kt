package io.fake

import io.fake.objects.Gist
import io.restassured.RestAssured.given
import org.testng.annotations.Test

class GistCRUDTests {

    @Test
    fun `Create a gist`() {
        val gistContent = mapOf("content" to "content")
        val gist = Gist("descr", true, mapOf("file.txt" to gistContent))
        given()
                .body(gist)
                .`when`()
                .post("/gists")
                .then()
                .assertThat()
                .statusCode(201)
    }

    @Test
    fun `Read a gist`() {

    }

    @Test
    fun `Update a gist`() {

    }

    @Test
    fun `Delete a gist`() {

    }

}