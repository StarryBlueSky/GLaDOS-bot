/*
 * The MIT License (MIT)
 *
 *     Copyright (c) 2017-2019 Nephy Project Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

@file:Suppress("UNUSED")

package jp.nephy.glados.clients.discord.listener.websocket

import jp.nephy.glados.api.EventModel
import jp.nephy.glados.clients.discord.listener.websocket.events.category.DiscordCategoryCreateEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.category.DiscordCategoryDeleteEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.category.update.DiscordCategoryUpdateNameEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.category.update.DiscordCategoryUpdatePermissionsEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.category.update.DiscordCategoryUpdatePositionEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.private.DiscordPrivateChannelCreateEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.private.DiscordPrivateChannelDeleteEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.text.DiscordTextChannelCreateEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.text.DiscordTextChannelDeleteEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.text.update.*
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.voice.DiscordVoiceChannelCreateEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.voice.DiscordVoiceChannelDeleteEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.channel.voice.update.*
import jp.nephy.glados.clients.discord.listener.websocket.events.emote.DiscordEmoteAddedEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.emote.DiscordEmoteRemovedEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.emote.update.DiscordEmoteUpdateNameEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.emote.update.DiscordEmoteUpdateRolesEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.general.*
import jp.nephy.glados.clients.discord.listener.websocket.events.guild.*
import jp.nephy.glados.clients.discord.listener.websocket.events.guild.member.*
import jp.nephy.glados.clients.discord.listener.websocket.events.guild.update.*
import jp.nephy.glados.clients.discord.listener.websocket.events.guild.voice.*
import jp.nephy.glados.clients.discord.listener.websocket.events.message.*
import jp.nephy.glados.clients.discord.listener.websocket.events.message.guild.*
import jp.nephy.glados.clients.discord.listener.websocket.events.message.private.*
import jp.nephy.glados.clients.discord.listener.websocket.events.message.reaction.DiscordMessageReactionAddEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.message.reaction.DiscordMessageReactionRemoveAllEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.message.reaction.DiscordMessageReactionRemoveEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.role.DiscordRoleCreateEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.role.DiscordRoleDeleteEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.role.update.*
import jp.nephy.glados.clients.discord.listener.websocket.events.user.DiscordUserActivityEndEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.DiscordUserActivityStartEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.DiscordUserTypingEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.self.*
import jp.nephy.glados.clients.discord.listener.websocket.events.user.update.*

interface DiscordWebsocketEventModel: EventModel {
    suspend fun onDiscordReady(event: DiscordReadyEvent) {}
    suspend fun onDiscordResume(event: DiscordResumedEvent) {}
    suspend fun onDiscordReconnect(event: DiscordReconnectedEvent) {}
    suspend fun onDiscordDisconnect(event: DiscordDisconnectEvent) {}
    suspend fun onDiscordShutdown(event: DiscordShutdownEvent) {}
    suspend fun onDiscordStatusChange(event: DiscordStatusChangeEvent) {}
    suspend fun onDiscordException(event: DiscordExceptionEvent) {}
    suspend fun onDiscordHttpRequest(event: DiscordHttpRequestEvent) {}
    
    suspend fun onDiscordUserUpdateName(event: DiscordUserUpdateNameEvent) {}
    suspend fun onDiscordUserUpdateDiscriminator(event: DiscordUserUpdateDiscriminatorEvent) {}
    suspend fun onDiscordUserUpdateAvatar(event: DiscordUserUpdateAvatarEvent) {}
    suspend fun onDiscordUserUpdateOnlineStatus(event: DiscordUserUpdateOnlineStatusEvent) {}
    suspend fun onDiscordUserUpdateActivityOrder(event: DiscordUserUpdateActivityOrderEvent) {}
    suspend fun onDiscordUserTyping(event: DiscordUserTypingEvent) {}
    suspend fun onDiscordUserActivityStart(event: DiscordUserActivityStartEvent) {}
    suspend fun onDiscordUserActivityEnd(event: DiscordUserActivityEndEvent) {}

    suspend fun onDiscordSelfUpdateAvatar(event: DiscordSelfUpdateAvatarEvent) {}
    suspend fun onDiscordSelfUpdateEmail(event: DiscordSelfUpdateEmailEvent) {}
    suspend fun onDiscordSelfUpdateMFA(event: DiscordSelfUpdateMFAEvent) {}
    suspend fun onDiscordSelfUpdateName(event: DiscordSelfUpdateNameEvent) {}
    suspend fun onDiscordSelfUpdateVerified(event: DiscordSelfUpdateVerifiedEvent) {}
    
    suspend fun onDiscordGuildMessageReceived(event: DiscordGuildMessageReceivedEvent) {}
    suspend fun onDiscordGuildMessageUpdate(event: DiscordGuildMessageUpdateEvent) {}
    suspend fun onDiscordGuildMessageDelete(event: DiscordGuildMessageDeleteEvent) {}
    suspend fun onDiscordGuildMessageEmbed(event: DiscordGuildMessageEmbedEvent) {}
    suspend fun onDiscordGuildMessageReactionAdd(event: DiscordGuildMessageReactionAddEvent) {}
    suspend fun onDiscordGuildMessageReactionRemove(event: DiscordGuildMessageReactionRemoveEvent) {}
    suspend fun onDiscordGuildMessageReactionRemoveAll(event: DiscordGuildMessageReactionRemoveAllEvent) {}
    
    suspend fun onDiscordPrivateMessageReceived(event: DiscordPrivateMessageReceivedEvent) {}
    suspend fun onDiscordPrivateMessageUpdate(event: DiscordPrivateMessageUpdateEvent) {}
    suspend fun onDiscordPrivateMessageDelete(event: DiscordPrivateMessageDeleteEvent) {}
    suspend fun onDiscordPrivateMessageEmbed(event: DiscordPrivateMessageEmbedEvent) {}
    suspend fun onDiscordPrivateMessageReactionAdd(event: DiscordPrivateMessageReactionAddEvent) {}
    suspend fun onDiscordPrivateMessageReactionRemove(event: DiscordPrivateMessageReactionRemoveEvent) {}
    
    suspend fun onDiscordMessageReceived(event: DiscordMessageReceivedEvent) {}
    suspend fun onDiscordMessageUpdate(event: DiscordMessageUpdateEvent) {}
    suspend fun onDiscordMessageDelete(event: DiscordMessageDeleteEvent) {}
    suspend fun onDiscordMessageBulkDelete(event: DiscordMessageBulkDeleteEvent) {}
    suspend fun onDiscordMessageEmbed(event: DiscordMessageEmbedEvent) {}
    suspend fun onDiscordMessageReactionAdd(event: DiscordMessageReactionAddEvent) {}
    suspend fun onDiscordMessageReactionRemove(event: DiscordMessageReactionRemoveEvent) {}
    suspend fun onDiscordMessageReactionRemoveAll(event: DiscordMessageReactionRemoveAllEvent) {}
    
    suspend fun onDiscordTextChannelDelete(event: DiscordTextChannelDeleteEvent) {}
    suspend fun onDiscordTextChannelUpdateName(event: DiscordTextChannelUpdateNameEvent) {}
    suspend fun onDiscordTextChannelUpdateTopic(event: DiscordTextChannelUpdateTopicEvent) {}
    suspend fun onDiscordTextChannelUpdatePosition(event: DiscordTextChannelUpdatePositionEvent) {}
    suspend fun onDiscordTextChannelUpdatePermissions(event: DiscordTextChannelUpdatePermissionsEvent) {}
    suspend fun onDiscordTextChannelUpdateNSFW(event: DiscordTextChannelUpdateNSFWEvent) {}
    suspend fun onDiscordTextChannelUpdateParent(event: DiscordTextChannelUpdateParentEvent) {}
    suspend fun onDiscordTextChannelUpdateSlowmode(event: DiscordTextChannelUpdateSlowmodeEvent) {}
    suspend fun onDiscordTextChannelCreate(event: DiscordTextChannelCreateEvent) {}
    
    suspend fun onDiscordVoiceChannelDelete(event: DiscordVoiceChannelDeleteEvent) {}
    suspend fun onDiscordVoiceChannelUpdateName(event: DiscordVoiceChannelUpdateNameEvent) {}
    suspend fun onDiscordVoiceChannelUpdatePosition(event: DiscordVoiceChannelUpdatePositionEvent) {}
    suspend fun onDiscordVoiceChannelUpdateUserLimit(event: DiscordVoiceChannelUpdateUserLimitEvent) {}
    suspend fun onDiscordVoiceChannelUpdateBitrate(event: DiscordVoiceChannelUpdateBitrateEvent) {}
    suspend fun onDiscordVoiceChannelUpdatePermissions(event: DiscordVoiceChannelUpdatePermissionsEvent) {}
    suspend fun onDiscordVoiceChannelUpdateParent(event: DiscordVoiceChannelUpdateParentEvent) {}
    suspend fun onDiscordVoiceChannelCreate(event: DiscordVoiceChannelCreateEvent) {}
    
    suspend fun onDiscordCategoryDelete(event: DiscordCategoryDeleteEvent) {}
    suspend fun onDiscordCategoryUpdateName(event: DiscordCategoryUpdateNameEvent) {}
    suspend fun onDiscordCategoryUpdatePosition(event: DiscordCategoryUpdatePositionEvent) {}
    suspend fun onDiscordCategoryUpdatePermissions(event: DiscordCategoryUpdatePermissionsEvent) {}
    suspend fun onDiscordCategoryCreate(event: DiscordCategoryCreateEvent) {}
    
    suspend fun onDiscordPrivateChannelCreate(event: DiscordPrivateChannelCreateEvent) {}
    suspend fun onDiscordPrivateChannelDelete(event: DiscordPrivateChannelDeleteEvent) {}
    
    suspend fun onDiscordGuildReady(event: DiscordGuildReadyEvent) {}
    suspend fun onDiscordGuildJoin(event: DiscordGuildJoinEvent) {}
    suspend fun onDiscordGuildLeave(event: DiscordGuildLeaveEvent) {}
    suspend fun onDiscordGuildAvailable(event: DiscordGuildAvailableEvent) {}
    suspend fun onDiscordGuildUnavailable(event: DiscordGuildUnavailableEvent) {}
    suspend fun onDiscordUnavailableGuildJoined(event: DiscordUnavailableGuildJoinedEvent) {}
    suspend fun onDiscordGuildBan(event: DiscordGuildBanEvent) {}
    suspend fun onDiscordGuildUnban(event: DiscordGuildUnbanEvent) {}
    suspend fun onDiscordGuildUpdateAfkChannel(event: DiscordGuildUpdateAfkChannelEvent) {}
    suspend fun onDiscordGuildUpdateSystemChannel(event: DiscordGuildUpdateSystemChannelEvent) {}
    suspend fun onDiscordGuildUpdateAfkTimeout(event: DiscordGuildUpdateAfkTimeoutEvent) {}
    suspend fun onDiscordGuildUpdateExplicitContentLevel(event: DiscordGuildUpdateExplicitContentLevelEvent) {}
    suspend fun onDiscordGuildUpdateIcon(event: DiscordGuildUpdateIconEvent) {}
    suspend fun onDiscordGuildUpdateMFALevel(event: DiscordGuildUpdateMFALevelEvent) {}
    suspend fun onDiscordGuildUpdateName(event: DiscordGuildUpdateNameEvent) {}
    suspend fun onDiscordGuildUpdateNotificationLevel(event: DiscordGuildUpdateNotificationLevelEvent) {}
    suspend fun onDiscordGuildUpdateOwner(event: DiscordGuildUpdateOwnerEvent) {}
    suspend fun onDiscordGuildUpdateRegion(event: DiscordGuildUpdateRegionEvent) {}
    suspend fun onDiscordGuildUpdateSplash(event: DiscordGuildUpdateSplashEvent) {}
    suspend fun onDiscordGuildUpdateVerificationLevel(event: DiscordGuildUpdateVerificationLevelEvent) {}
    suspend fun onDiscordGuildUpdateFeatures(event: DiscordGuildUpdateFeaturesEvent) {}
    
    suspend fun onDiscordGuildMemberJoin(event: DiscordGuildMemberJoinEvent) {}
    suspend fun onDiscordGuildMemberLeave(event: DiscordGuildMemberLeaveEvent) {}
    suspend fun onDiscordGuildMemberRoleAdd(event: DiscordGuildMemberRoleAddEvent) {}
    suspend fun onDiscordGuildMemberRoleRemove(event: DiscordGuildMemberRoleRemoveEvent) {}
    suspend fun onDiscordGuildMemberNickChange(event: DiscordGuildMemberNickChangeEvent) {}
    
    suspend fun onDiscordGuildVoiceUpdate(event: DiscordGuildVoiceUpdateEvent) {}
    suspend fun onDiscordGuildVoiceJoin(event: DiscordGuildVoiceJoinEvent) {}
    suspend fun onDiscordGuildVoiceMove(event: DiscordGuildVoiceMoveEvent) {}
    suspend fun onDiscordGuildVoiceLeave(event: DiscordGuildVoiceLeaveEvent) {}
    suspend fun onDiscordGuildVoiceMute(event: DiscordGuildVoiceMuteEvent) {}
    suspend fun onDiscordGuildVoiceDeafen(event: DiscordGuildVoiceDeafenEvent) {}
    suspend fun onDiscordGuildVoiceGuildMute(event: DiscordGuildVoiceGuildMuteEvent) {}
    suspend fun onDiscordGuildVoiceGuildDeafen(event: DiscordGuildVoiceGuildDeafenEvent) {}
    suspend fun onDiscordGuildVoiceSelfMute(event: DiscordGuildVoiceSelfMuteEvent) {}
    suspend fun onDiscordGuildVoiceSelfDeafen(event: DiscordGuildVoiceSelfDeafenEvent) {}
    suspend fun onDiscordGuildVoiceSuppress(event: DiscordGuildVoiceSuppressEvent) {}
    
    suspend fun onDiscordRoleCreate(event: DiscordRoleCreateEvent) {}
    suspend fun onDiscordRoleDelete(event: DiscordRoleDeleteEvent) {}
    suspend fun onDiscordRoleUpdateColor(event: DiscordRoleUpdateColorEvent) {}
    suspend fun onDiscordRoleUpdateHoisted(event: DiscordRoleUpdateHoistedEvent) {}
    suspend fun onDiscordRoleUpdateMentionable(event: DiscordRoleUpdateMentionableEvent) {}
    suspend fun onDiscordRoleUpdateName(event: DiscordRoleUpdateNameEvent) {}
    suspend fun onDiscordRoleUpdatePermissions(event: DiscordRoleUpdatePermissionsEvent) {}
    suspend fun onDiscordRoleUpdatePosition(event: DiscordRoleUpdatePositionEvent) {}
    
    suspend fun onDiscordEmoteAdded(event: DiscordEmoteAddedEvent) {}
    suspend fun onDiscordEmoteRemoved(event: DiscordEmoteRemovedEvent) {}
    suspend fun onDiscordEmoteUpdateName(event: DiscordEmoteUpdateNameEvent) {}
    suspend fun onDiscordEmoteUpdateRoles(event: DiscordEmoteUpdateRolesEvent) {}
}
