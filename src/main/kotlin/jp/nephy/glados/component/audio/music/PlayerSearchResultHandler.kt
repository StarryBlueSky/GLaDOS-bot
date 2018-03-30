package jp.nephy.glados.component.audio.music

import jp.nephy.glados.component.api.niconico.model.SearchResult

interface PlayerSearchResultHandler {
    fun onFoundNiconicoResult(result: SearchResult) {}
    fun onFoundYouTubeResult(result: List<com.google.api.services.youtube.model.SearchResult>) {}
    fun onNoResult() {}
}
