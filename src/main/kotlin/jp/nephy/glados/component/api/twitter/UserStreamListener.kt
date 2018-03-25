package jp.nephy.glados.component.api.twitter

import jp.nephy.penicillin.model.*
import jp.nephy.penicillin.streaming.IUserStreamListener


class UserStreamListener: IUserStreamListener {
    /* Status */
    override fun onStatus(status: Status) {}
    override fun onDirectMessage(message: DirectMessage) {}

    /* Status event */
    override fun onFavorite(event: StatusEvent) {}
    override fun onUnfavorite(event: StatusEvent) {}
    override fun onFavoritedRetweet(event: StatusEvent) {}
    override fun onRetweetedRetweet(event: StatusEvent) {}
    override fun onQuotedTweet(event: StatusEvent) {}

    /* List event */
    override fun onListCreated(event: ListEvent) {}
    override fun onListDestroyed(event: ListEvent) {}
    override fun onListUpdated(event: ListEvent) {}
    override fun onListMemberAdded(event: ListEvent) {}
    override fun onListMemberRemoved(event: ListEvent) {}
    override fun onListUserSubscribed(event: ListEvent) {}
    override fun onListUserUnsubscribed(event: ListEvent) {}

    /* User event */
    override fun onFollow(event: UserEvent) {}
    override fun onUnfollow(event: UserEvent) {}
    override fun onBlock(event: UserEvent) {}
    override fun onUnblock(event: UserEvent) {}
    override fun onMute(event: UserEvent) {}
    override fun onUnmute(event: UserEvent) {}
    override fun onUserUpdate(event: UserEvent) {}

    /* Misc */
    override fun onFriends(friends: Friends) {}
    override fun onDelete(delete: Delete) {}
    override fun onScrubGeo(scrubGeo: ScrubGeo) {}
    override fun onStatusWithheld(withheld: StatusWithheld) {}
    override fun onLimit(limit: Limit) {}

    override fun onUnknownData(data: String) {}
}
