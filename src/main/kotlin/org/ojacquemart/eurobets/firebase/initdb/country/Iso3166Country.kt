package org.ojacquemart.eurobets.firebase.initdb.country

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Iso3166Country(
    val name: String,
    @JsonProperty("alpha-2") val alpha2: String) {
}