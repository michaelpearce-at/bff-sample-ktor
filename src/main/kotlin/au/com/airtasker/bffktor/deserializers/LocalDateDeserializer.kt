package au.com.airtasker.bffktor.deserializers

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class LocalDateDeserializer : JsonDeserializer<LocalDate>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): LocalDate {
        return p?.valueAsString?.let {
            LocalDate.parse(it, DateTimeFormatter.ISO_DATE_TIME)
        } ?: throw IOException("value not available")
    }
}
