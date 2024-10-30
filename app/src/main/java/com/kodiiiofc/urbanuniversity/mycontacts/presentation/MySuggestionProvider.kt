package com.kodiiiofc.urbanuniversity.mycontacts.presentation

import android.content.SearchRecentSuggestionsProvider

class MySuggestionProvider : SearchRecentSuggestionsProvider() {
    companion object {
        val AUTHORITY = "ru.phpnick.MySuggegstionProvider"
        val MODE = DATABASE_MODE_QUERIES
    }

    init {
        setupSuggestions(AUTHORITY, MODE)
    }
}