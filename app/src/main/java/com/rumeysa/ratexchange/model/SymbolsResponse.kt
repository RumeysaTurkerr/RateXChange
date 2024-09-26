package com.rumeysa.ratexchange.model

data class SymbolsResponse(
    val success: Boolean,
    val symbols: Map<String, String>
)