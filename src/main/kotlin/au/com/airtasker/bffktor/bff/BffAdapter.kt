package au.com.airtasker.bffktor.bff

import au.com.airtasker.bffktor.entities.Claimable
import au.com.airtasker.bffktor.entities.api.RawClaimableResponse
import au.com.airtasker.bffktor.entities.api.toClaimable
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.headers
import io.ktor.client.request.url

class BffAdapter(private val client: HttpClient) {
    suspend fun claimable(authToken: String): List<Claimable> {
        val claimableResponse = client.get<RawClaimableResponse> {
            url(urlString = "https://api.dev.airtasker.com/api/v2/removals/claimable")
            headers { header("X-Auth-Token", authToken) }
        }

        return claimableResponse.data.map { it.toClaimable() }
    }
}
