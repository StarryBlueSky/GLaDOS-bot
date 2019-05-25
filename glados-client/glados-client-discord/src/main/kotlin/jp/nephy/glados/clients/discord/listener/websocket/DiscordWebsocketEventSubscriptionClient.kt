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

package jp.nephy.glados.clients.discord.listener.websocket

import jp.nephy.glados.GLaDOSSubscriptionClient
import jp.nephy.glados.api.Plugin
import jp.nephy.glados.clients.discord.disposeJDA
import jp.nephy.glados.clients.discord.initializeJDA
import jp.nephy.glados.clients.discord.listener.DiscordEvent
import jp.nephy.glados.clients.discord.listener.defaultDiscordEventAnnotation
import jp.nephy.glados.clients.discord.listener.websocket.events.DiscordWebsocketEventBase
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
import jp.nephy.glados.clients.discord.listener.websocket.events.user.DiscordUserTypingEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.presence.DiscordUserActivityEndEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.presence.DiscordUserActivityStartEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.presence.DiscordUserUpdateActivityOrderEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.presence.DiscordUserUpdateOnlineStatusEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.self.*
import jp.nephy.glados.clients.discord.listener.websocket.events.user.update.DiscordUserUpdateAvatarEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.update.DiscordUserUpdateDiscriminatorEvent
import jp.nephy.glados.clients.discord.listener.websocket.events.user.update.DiscordUserUpdateNameEvent
import jp.nephy.glados.clients.runEvent
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.events.*
import net.dv8tion.jda.api.events.channel.category.CategoryCreateEvent
import net.dv8tion.jda.api.events.channel.category.CategoryDeleteEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdateNameEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePermissionsEvent
import net.dv8tion.jda.api.events.channel.category.update.CategoryUpdatePositionEvent
import net.dv8tion.jda.api.events.channel.priv.PrivateChannelCreateEvent
import net.dv8tion.jda.api.events.channel.priv.PrivateChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.text.update.*
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent
import net.dv8tion.jda.api.events.channel.voice.update.*
import net.dv8tion.jda.api.events.emote.EmoteAddedEvent
import net.dv8tion.jda.api.events.emote.EmoteRemovedEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateNameEvent
import net.dv8tion.jda.api.events.emote.update.EmoteUpdateRolesEvent
import net.dv8tion.jda.api.events.guild.*
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberLeaveEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleRemoveEvent
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateNicknameEvent
import net.dv8tion.jda.api.events.guild.update.*
import net.dv8tion.jda.api.events.guild.voice.*
import net.dv8tion.jda.api.events.http.HttpRequestEvent
import net.dv8tion.jda.api.events.message.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageDeleteEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageEmbedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageDeleteEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageEmbedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionRemoveEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent
import net.dv8tion.jda.api.events.role.RoleCreateEvent
import net.dv8tion.jda.api.events.role.RoleDeleteEvent
import net.dv8tion.jda.api.events.role.update.*
import net.dv8tion.jda.api.events.self.*
import net.dv8tion.jda.api.events.user.UserActivityEndEvent
import net.dv8tion.jda.api.events.user.UserActivityStartEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent
import net.dv8tion.jda.api.events.user.update.*
import net.dv8tion.jda.api.hooks.ListenerAdapter
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf

/**
 * DiscordWebsocketEventSubscriptionClient.
 */
object DiscordWebsocketEventSubscriptionClient: GLaDOSSubscriptionClient<DiscordEvent, DiscordWebsocketEventBase<*>, DiscordWebsocketEventSubscription>() {
    override fun create(plugin: Plugin, function: KFunction<*>, eventClass: KClass<*>): DiscordWebsocketEventSubscription? {
        if (!eventClass.isSubclassOf(DiscordWebsocketEventBase::class)) {
            return null
        }

        val annotation = function.findAnnotation() ?: defaultDiscordEventAnnotation
        return DiscordWebsocketEventSubscription(plugin, function, annotation)
    }

    override fun start() {
        runBlocking {
            initializeJDA()
        }
    }

    override fun stop() {
        runBlocking {
            disposeJDA()
        }
    }

    internal object Listener: ListenerAdapter() {
        private var initialized = false
        
        override fun onReady(event: ReadyEvent) {
            if (!initialized) {
                event.jda.presence.setStatus(OnlineStatus.ONLINE)
                initialized = true
            }

            runEvent {
                DiscordReadyEvent(it, event)
            }
        }

        override fun onResume(event: ResumedEvent) {
            runEvent {
                DiscordResumedEvent(it, event)
            }
        }

        override fun onReconnect(event: ReconnectedEvent) {
            runEvent {
                DiscordReconnectedEvent(it, event)
            }
        }

        override fun onDisconnect(event: DisconnectEvent) {
            runEvent {
                DiscordDisconnectEvent(it, event)
            }
        }

        override fun onShutdown(event: ShutdownEvent) {
            runEvent {
                DiscordShutdownEvent(it, event)
            }
        }

        override fun onStatusChange(event: StatusChangeEvent) {
            runEvent {
                DiscordStatusChangeEvent(it, event)
            }
        }

        override fun onException(event: ExceptionEvent) {
            runEvent {
                DiscordExceptionEvent(it, event)
            }
        }

        override fun onHttpRequest(event: HttpRequestEvent) {
            runEvent {
                DiscordHttpRequestEvent(it, event)
            }
        }

        override fun onUserUpdateName(event: UserUpdateNameEvent) {
            runEvent {
                DiscordUserUpdateNameEvent(it, event)
            }
        }

        override fun onUserUpdateDiscriminator(event: UserUpdateDiscriminatorEvent) {
            runEvent {
                DiscordUserUpdateDiscriminatorEvent(it, event)
            }
        }

        override fun onUserUpdateAvatar(event: UserUpdateAvatarEvent) {
            runEvent {
                DiscordUserUpdateAvatarEvent(it, event)
            }
        }

        override fun onUserUpdateOnlineStatus(event: UserUpdateOnlineStatusEvent) {
            runEvent {
                DiscordUserUpdateOnlineStatusEvent(it, event)
            }
        }

        override fun onUserUpdateActivityOrder(event: UserUpdateActivityOrderEvent) {
            runEvent {
                DiscordUserUpdateActivityOrderEvent(it, event)
            }
        }

        override fun onUserTyping(event: UserTypingEvent) {
            runEvent {
                DiscordUserTypingEvent(it, event)
            }
        }

        override fun onUserActivityStart(event: UserActivityStartEvent) {
            runEvent {
                DiscordUserActivityStartEvent(it, event)
            }
        }

        override fun onUserActivityEnd(event: UserActivityEndEvent) {
            runEvent {
                DiscordUserActivityEndEvent(it, event)
            }
        }

        override fun onSelfUpdateAvatar(event: SelfUpdateAvatarEvent) {
            runEvent {
                DiscordSelfUpdateAvatarEvent(it, event)
            }
        }

        override fun onSelfUpdateEmail(event: SelfUpdateEmailEvent) {
            runEvent {
                DiscordSelfUpdateEmailEvent(it, event)
            }
        }

        override fun onSelfUpdateMFA(event: SelfUpdateMFAEvent) {
            runEvent {
                DiscordSelfUpdateMFAEvent(it, event)
            }
        }

        override fun onSelfUpdateName(event: SelfUpdateNameEvent) {
            runEvent {
                DiscordSelfUpdateNameEvent(it, event)
            }
        }

        override fun onSelfUpdateVerified(event: SelfUpdateVerifiedEvent) {
            runEvent {
                DiscordSelfUpdateVerifiedEvent(it, event)
            }
        }

        override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
            runEvent {
                DiscordGuildMessageReceivedEvent(it, event)
            }
        }

        override fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
            runEvent {
                DiscordGuildMessageUpdateEvent(it, event)
            }
        }

        override fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
            runEvent {
                DiscordGuildMessageDeleteEvent(it, event)
            }
        }

        override fun onGuildMessageEmbed(event: GuildMessageEmbedEvent) {
            runEvent {
                DiscordGuildMessageEmbedEvent(it, event)
            }
        }

        override fun onGuildMessageReactionAdd(event: GuildMessageReactionAddEvent) {
            runEvent {
                DiscordGuildMessageReactionAddEvent(it, event)
            }
        }

        override fun onGuildMessageReactionRemove(event: GuildMessageReactionRemoveEvent) {
            runEvent {
                DiscordGuildMessageReactionRemoveEvent(it, event)
            }
        }

        override fun onGuildMessageReactionRemoveAll(event: GuildMessageReactionRemoveAllEvent) {
            runEvent {
                DiscordGuildMessageReactionRemoveAllEvent(it, event)
            }
        }

        override fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
            runEvent {
                DiscordPrivateMessageReceivedEvent(it, event)
            }
        }

        override fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {
            runEvent {
                DiscordPrivateMessageUpdateEvent(it, event)
            }
        }

        override fun onPrivateMessageDelete(event: PrivateMessageDeleteEvent) {
            runEvent {
                DiscordPrivateMessageDeleteEvent(it, event)
            }
        }

        override fun onPrivateMessageEmbed(event: PrivateMessageEmbedEvent) {
            runEvent {
                DiscordPrivateMessageEmbedEvent(it, event)
            }
        }

        override fun onPrivateMessageReactionAdd(event: PrivateMessageReactionAddEvent) {
            runEvent {
                DiscordPrivateMessageReactionAddEvent(it, event)
            }
        }

        override fun onPrivateMessageReactionRemove(event: PrivateMessageReactionRemoveEvent) {
            runEvent {
                DiscordPrivateMessageReactionRemoveEvent(it, event)
            }
        }

        override fun onMessageReceived(event: MessageReceivedEvent) {
            runEvent {
                DiscordMessageReceivedEvent(it, event)
            }
        }

        override fun onMessageUpdate(event: MessageUpdateEvent) {
            runEvent {
                DiscordMessageUpdateEvent(it, event)
            }
        }

        override fun onMessageDelete(event: MessageDeleteEvent) {
            runEvent {
                DiscordMessageDeleteEvent(it, event)
            }
        }

        override fun onMessageBulkDelete(event: MessageBulkDeleteEvent) {
            runEvent {
                DiscordMessageBulkDeleteEvent(it, event)
            }
        }

        override fun onMessageEmbed(event: MessageEmbedEvent) {
            runEvent {
                DiscordMessageEmbedEvent(it, event)
            }
        }

        override fun onMessageReactionAdd(event: MessageReactionAddEvent) {
            runEvent {
                DiscordMessageReactionAddEvent(it, event)
            }
        }

        override fun onMessageReactionRemove(event: MessageReactionRemoveEvent) {
            runEvent {
                DiscordMessageReactionRemoveEvent(it, event)
            }
        }

        override fun onMessageReactionRemoveAll(event: MessageReactionRemoveAllEvent) {
            runEvent {
                DiscordMessageReactionRemoveAllEvent(it, event)
            }
        }

        override fun onTextChannelCreate(event: TextChannelCreateEvent) {
            runEvent {
                DiscordTextChannelCreateEvent(it, event)
            }
        }

        override fun onTextChannelDelete(event: TextChannelDeleteEvent) {
            runEvent {
                DiscordTextChannelDeleteEvent(it, event)
            }
        }

        override fun onTextChannelUpdateName(event: TextChannelUpdateNameEvent) {
            runEvent {
                DiscordTextChannelUpdateNameEvent(it, event)
            }
        }

        override fun onTextChannelUpdateTopic(event: TextChannelUpdateTopicEvent) {
            runEvent {
                DiscordTextChannelUpdateTopicEvent(it, event)
            }
        }

        override fun onTextChannelUpdatePosition(event: TextChannelUpdatePositionEvent) {
            runEvent {
                DiscordTextChannelUpdatePositionEvent(it, event)
            }
        }

        override fun onTextChannelUpdatePermissions(event: TextChannelUpdatePermissionsEvent) {
            runEvent {
                DiscordTextChannelUpdatePermissionsEvent(it, event)
            }
        }

        override fun onTextChannelUpdateNSFW(event: TextChannelUpdateNSFWEvent) {
            runEvent {
                DiscordTextChannelUpdateNSFWEvent(it, event)
            }
        }

        override fun onTextChannelUpdateParent(event: TextChannelUpdateParentEvent) {
            runEvent {
                DiscordTextChannelUpdateParentEvent(it, event)
            }
        }

        override fun onTextChannelUpdateSlowmode(event: TextChannelUpdateSlowmodeEvent) {
            runEvent {
                DiscordTextChannelUpdateSlowmodeEvent(it, event)
            }
        }

        override fun onVoiceChannelCreate(event: VoiceChannelCreateEvent) {
            runEvent {
                DiscordVoiceChannelCreateEvent(it, event)
            }
        }

        override fun onVoiceChannelDelete(event: VoiceChannelDeleteEvent) {
            runEvent {
                DiscordVoiceChannelDeleteEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdateName(event: VoiceChannelUpdateNameEvent) {
            runEvent {
                DiscordVoiceChannelUpdateNameEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdatePosition(event: VoiceChannelUpdatePositionEvent) {
            runEvent {
                DiscordVoiceChannelUpdatePositionEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdateUserLimit(event: VoiceChannelUpdateUserLimitEvent) {
            runEvent {
                DiscordVoiceChannelUpdateUserLimitEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdateBitrate(event: VoiceChannelUpdateBitrateEvent) {
            runEvent {
                DiscordVoiceChannelUpdateBitrateEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdatePermissions(event: VoiceChannelUpdatePermissionsEvent) {
            runEvent {
                DiscordVoiceChannelUpdatePermissionsEvent(it, event)
            }
        }

        override fun onVoiceChannelUpdateParent(event: VoiceChannelUpdateParentEvent) {
            runEvent {
                DiscordVoiceChannelUpdateParentEvent(it, event)
            }
        }

        override fun onCategoryCreate(event: CategoryCreateEvent) {
            runEvent {
                DiscordCategoryCreateEvent(it, event)
            }
        }

        override fun onCategoryDelete(event: CategoryDeleteEvent) {
            runEvent {
                DiscordCategoryDeleteEvent(it, event)
            }
        }

        override fun onCategoryUpdateName(event: CategoryUpdateNameEvent) {
            runEvent {
                DiscordCategoryUpdateNameEvent(it, event)
            }
        }

        override fun onCategoryUpdatePosition(event: CategoryUpdatePositionEvent) {
            runEvent {
                DiscordCategoryUpdatePositionEvent(it, event)
            }
        }

        override fun onCategoryUpdatePermissions(event: CategoryUpdatePermissionsEvent) {
            runEvent {
                DiscordCategoryUpdatePermissionsEvent(it, event)
            }
        }

        override fun onPrivateChannelCreate(event: PrivateChannelCreateEvent) {
            runEvent {
                DiscordPrivateChannelCreateEvent(it, event)
            }
        }

        override fun onPrivateChannelDelete(event: PrivateChannelDeleteEvent) {
            runEvent {
                DiscordPrivateChannelDeleteEvent(it, event)
            }
        }

        override fun onGuildReady(event: GuildReadyEvent) {
            runEvent {
                DiscordGuildReadyEvent(it, event)
            }
        }

        override fun onGuildJoin(event: GuildJoinEvent) {
            runEvent {
                DiscordGuildJoinEvent(it, event)
            }
        }

        override fun onGuildLeave(event: GuildLeaveEvent) {
            runEvent {
                DiscordGuildLeaveEvent(it, event)
            }
        }

        override fun onGuildAvailable(event: GuildAvailableEvent) {
            runEvent {
                DiscordGuildAvailableEvent(it, event)
            }
        }

        override fun onGuildUnavailable(event: GuildUnavailableEvent) {
            runEvent {
                DiscordGuildUnavailableEvent(it, event)
            }
        }

        override fun onUnavailableGuildJoined(event: UnavailableGuildJoinedEvent) {
            runEvent {
                DiscordUnavailableGuildJoinedEvent(it, event)
            }
        }

        override fun onGuildBan(event: GuildBanEvent) {
            runEvent {
                DiscordGuildBanEvent(it, event)
            }
        }

        override fun onGuildUnban(event: GuildUnbanEvent) {
            runEvent {
                DiscordGuildUnbanEvent(it, event)
            }
        }

        override fun onGuildUpdateAfkChannel(event: GuildUpdateAfkChannelEvent) {
            runEvent {
                DiscordGuildUpdateAfkChannelEvent(it, event)
            }
        }

        override fun onGuildUpdateSystemChannel(event: GuildUpdateSystemChannelEvent) {
            runEvent {
                DiscordGuildUpdateSystemChannelEvent(it, event)
            }
        }

        override fun onGuildUpdateAfkTimeout(event: GuildUpdateAfkTimeoutEvent) {
            runEvent {
                DiscordGuildUpdateAfkTimeoutEvent(it, event)
            }
        }

        override fun onGuildUpdateExplicitContentLevel(event: GuildUpdateExplicitContentLevelEvent) {
            runEvent {
                DiscordGuildUpdateExplicitContentLevelEvent(it, event)
            }
        }

        override fun onGuildUpdateIcon(event: GuildUpdateIconEvent) {
            runEvent {
                DiscordGuildUpdateIconEvent(it, event)
            }
        }

        override fun onGuildUpdateMFALevel(event: GuildUpdateMFALevelEvent) {
            runEvent {
                DiscordGuildUpdateMFALevelEvent(it, event)
            }
        }

        override fun onGuildUpdateName(event: GuildUpdateNameEvent) {
            runEvent {
                DiscordGuildUpdateNameEvent(it, event)
            }
        }

        override fun onGuildUpdateNotificationLevel(event: GuildUpdateNotificationLevelEvent) {
            runEvent {
                DiscordGuildUpdateNotificationLevelEvent(it, event)
            }
        }

        override fun onGuildUpdateOwner(event: GuildUpdateOwnerEvent) {
            runEvent {
                DiscordGuildUpdateOwnerEvent(it, event)
            }
        }

        override fun onGuildUpdateRegion(event: GuildUpdateRegionEvent) {
            runEvent {
                DiscordGuildUpdateRegionEvent(it, event)
            }
        }

        override fun onGuildUpdateSplash(event: GuildUpdateSplashEvent) {
            runEvent {
                DiscordGuildUpdateSplashEvent(it, event)
            }
        }

        override fun onGuildUpdateVerificationLevel(event: GuildUpdateVerificationLevelEvent) {
            runEvent {
                DiscordGuildUpdateVerificationLevelEvent(it, event)
            }
        }

        override fun onGuildUpdateFeatures(event: GuildUpdateFeaturesEvent) {
            runEvent {
                DiscordGuildUpdateFeaturesEvent(it, event)
            }
        }

        override fun onGuildMemberJoin(event: GuildMemberJoinEvent) {
            runEvent {
                DiscordGuildMemberJoinEvent(it, event)
            }
        }

        override fun onGuildMemberLeave(event: GuildMemberLeaveEvent) {
            runEvent {
                DiscordGuildMemberLeaveEvent(it, event)
            }
        }

        override fun onGuildMemberRoleAdd(event: GuildMemberRoleAddEvent) {
            runEvent {
                DiscordGuildMemberRoleAddEvent(it, event)
            }
        }

        override fun onGuildMemberRoleRemove(event: GuildMemberRoleRemoveEvent) {
            runEvent {
                DiscordGuildMemberRoleRemoveEvent(it, event)
            }
        }

        override fun onGuildMemberUpdateNickname(event: GuildMemberUpdateNicknameEvent) {
            runEvent {
                DiscordGuildMemberUpdateNicknameEvent(it, event)
            }
        }

        override fun onGuildVoiceUpdate(event: GuildVoiceUpdateEvent) {
            runEvent {
                DiscordGuildVoiceUpdateEvent(it, event)
            }
        }

        override fun onGuildVoiceJoin(event: GuildVoiceJoinEvent) {
            runEvent {
                DiscordGuildVoiceJoinEvent(it, event)
            }
        }

        override fun onGuildVoiceMove(event: GuildVoiceMoveEvent) {
            runEvent {
                DiscordGuildVoiceMoveEvent(it, event)
            }
        }

        override fun onGuildVoiceLeave(event: GuildVoiceLeaveEvent) {
            runEvent {
                DiscordGuildVoiceLeaveEvent(it, event)
            }
        }

        override fun onGuildVoiceMute(event: GuildVoiceMuteEvent) {
            runEvent {
                DiscordGuildVoiceMuteEvent(it, event)
            }
        }

        override fun onGuildVoiceDeafen(event: GuildVoiceDeafenEvent) {
            runEvent {
                DiscordGuildVoiceDeafenEvent(it, event)
            }
        }

        override fun onGuildVoiceGuildMute(event: GuildVoiceGuildMuteEvent) {
            runEvent {
                DiscordGuildVoiceGuildMuteEvent(it, event)
            }
        }

        override fun onGuildVoiceGuildDeafen(event: GuildVoiceGuildDeafenEvent) {
            runEvent {
                DiscordGuildVoiceGuildDeafenEvent(it, event)
            }
        }

        override fun onGuildVoiceSelfMute(event: GuildVoiceSelfMuteEvent) {
            runEvent {
                DiscordGuildVoiceSelfMuteEvent(it, event)
            }
        }

        override fun onGuildVoiceSelfDeafen(event: GuildVoiceSelfDeafenEvent) {
            runEvent {
                DiscordGuildVoiceSelfDeafenEvent(it, event)
            }
        }

        override fun onGuildVoiceSuppress(event: GuildVoiceSuppressEvent) {
            runEvent {
                DiscordGuildVoiceSuppressEvent(it, event)
            }
        }

        override fun onRoleCreate(event: RoleCreateEvent) {
            runEvent {
                DiscordRoleCreateEvent(it, event)
            }
        }

        override fun onRoleDelete(event: RoleDeleteEvent) {
            runEvent {
                DiscordRoleDeleteEvent(it, event)
            }
        }

        override fun onRoleUpdateColor(event: RoleUpdateColorEvent) {
            runEvent {
                DiscordRoleUpdateColorEvent(it, event)
            }
        }

        override fun onRoleUpdateHoisted(event: RoleUpdateHoistedEvent) {
            runEvent {
                DiscordRoleUpdateHoistedEvent(it, event)
            }
        }

        override fun onRoleUpdateMentionable(event: RoleUpdateMentionableEvent) {
            runEvent {
                DiscordRoleUpdateMentionableEvent(it, event)
            }
        }

        override fun onRoleUpdateName(event: RoleUpdateNameEvent) {
            runEvent {
                DiscordRoleUpdateNameEvent(it, event)
            }
        }

        override fun onRoleUpdatePermissions(event: RoleUpdatePermissionsEvent) {
            runEvent {
                DiscordRoleUpdatePermissionsEvent(it, event)
            }
        }

        override fun onRoleUpdatePosition(event: RoleUpdatePositionEvent) {
            runEvent {
                DiscordRoleUpdatePositionEvent(it, event)
            }
        }

        override fun onEmoteAdded(event: EmoteAddedEvent) {
            runEvent {
                DiscordEmoteAddedEvent(it, event)
            }
        }

        override fun onEmoteRemoved(event: EmoteRemovedEvent) {
            runEvent {
                DiscordEmoteRemovedEvent(it, event)
            }
        }

        override fun onEmoteUpdateName(event: EmoteUpdateNameEvent) {
            runEvent {
                DiscordEmoteUpdateNameEvent(it, event)
            }
        }

        override fun onEmoteUpdateRoles(event: EmoteUpdateRolesEvent) {
            runEvent {
                DiscordEmoteUpdateRolesEvent(it, event)
            }
        }
    }
}
