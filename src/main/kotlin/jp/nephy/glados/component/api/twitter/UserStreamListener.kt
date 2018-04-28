package jp.nephy.glados.component.api.twitter

import com.google.gson.JsonObject
import jp.nephy.penicillin.model.*
import jp.nephy.penicillin.request.streaming.UserStream


class UserStreamListener: UserStream.Listener {
    override fun onStatus(status: Status) {}
    override fun onDirectMessage(message: DirectMessage) {}

    override fun onAnyEvent(event: UserStreamEvent) {}
    /* Status event */
    override fun onAnyStatusEvent(event: UserStreamStatusEvent) {}
    override fun onFavorite(event: UserStreamStatusEvent) {}
    override fun onUnfavorite(event: UserStreamStatusEvent) {}
    override fun onFavoritedRetweet(event: UserStreamStatusEvent) {}
    override fun onRetweetedRetweet(event: UserStreamStatusEvent) {}
    override fun onQuotedTweet(event: UserStreamStatusEvent) {}
    /* List event */
    override fun onAnyListEvent(event: UserStreamListEvent) {}
    override fun onListCreated(event: UserStreamListEvent) {}
    override fun onListDestroyed(event: UserStreamListEvent) {}
    override fun onListUpdated(event: UserStreamListEvent) {}
    override fun onListMemberAdded(event: UserStreamListEvent) {}
    override fun onListMemberRemoved(event: UserStreamListEvent) {}
    override fun onListUserSubscribed(event: UserStreamListEvent) {}
    override fun onListUserUnsubscribed(event: UserStreamListEvent) {}
    /* User event */
    override fun onAnyUserEvent(event: UserStreamUserEvent) {}
    override fun onFollow(event: UserStreamUserEvent) {}
    override fun onUnfollow(event: UserStreamUserEvent) {}
    override fun onBlock(event: UserStreamUserEvent) {}
    override fun onUnblock(event: UserStreamUserEvent) {}
    override fun onMute(event: UserStreamUserEvent) {}
    override fun onUnmute(event: UserStreamUserEvent) {}
    override fun onUserUpdate(event: UserStreamUserEvent) {}

    /* Misc */
    override fun onFriends(friends: UserStreamFriends) {}
    override fun onDelete(delete: StreamDelete) {}
    override fun onScrubGeo(scrubGeo: UserStreamScrubGeo) {}
    override fun onStatusWithheld(withheld: UserStreamStatusWithheld) {}
    override fun onLimit(limit: UserStreamLimit) {}

    override fun onUnknownData(data: JsonObject) {}
}
