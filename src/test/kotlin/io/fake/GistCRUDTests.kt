package io.fake

import io.restassured.RestAssured.get
import org.testng.annotations.Test

class GistCRUDTests {

    @Test
    fun `Get all gists`() {
        get("/gists")
    }
}