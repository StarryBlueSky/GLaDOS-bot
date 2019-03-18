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
import net.dv8tion.jda.api.events.guild.member.*
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
        initializeJDA()
    }

    override fun stop() {
        disposeJDA()
    }

    internal object Listener: ListenerAdapter() {
        override fun onReady(event: ReadyEvent) {
            event.jda.presence.status = OnlineStatus.ONLINE
        }
        
        override fun onGenericEvent(event: GenericEvent) {
            when (event) {
                is ReadyEvent -> runEvent {
                    DiscordReadyEvent(it, event)
                }
                is ResumedEvent -> runEvent {
                    DiscordResumedEvent(it, event)
                }
                is ReconnectedEvent -> runEvent {
                    DiscordReconnectedEvent(it, event)
                }
                is DisconnectEvent -> runEvent {
                    DiscordDisconnectEvent(it, event)
                }
                is ShutdownEvent -> runEvent {
                    DiscordShutdownEvent(it, event)
                }
                is StatusChangeEvent -> runEvent {
                    DiscordStatusChangeEvent(it, event)
                }
                is ExceptionEvent -> runEvent {
                    DiscordExceptionEvent(it, event)
                }
                is HttpRequestEvent -> runEvent {
                    DiscordHttpRequestEvent(it, event)
                }

                is UserUpdateNameEvent -> runEvent {
                    DiscordUserUpdateNameEvent(it, event)
                }
                is UserUpdateDiscriminatorEvent -> runEvent {
                    DiscordUserUpdateDiscriminatorEvent(it, event)
                }
                is UserUpdateAvatarEvent -> runEvent {
                    DiscordUserUpdateAvatarEvent(it, event)
                }
                is UserUpdateOnlineStatusEvent -> runEvent {
                    DiscordUserUpdateOnlineStatusEvent(it, event)
                }
                is UserUpdateActivityOrderEvent -> runEvent {
                    DiscordUserUpdateActivityOrderEvent(it, event)
                }
                is UserTypingEvent -> runEvent {
                    DiscordUserTypingEvent(it, event)
                }
                is UserActivityStartEvent -> runEvent {
                    DiscordUserActivityStartEvent(it, event)
                }
                is UserActivityEndEvent -> runEvent {
                    DiscordUserActivityEndEvent(it, event)
                }

                is SelfUpdateAvatarEvent -> runEvent {
                    DiscordSelfUpdateAvatarEvent(it, event)
                }
                is SelfUpdateEmailEvent -> runEvent {
                    DiscordSelfUpdateEmailEvent(it, event)
                }
                is SelfUpdateMFAEvent -> runEvent {
                    DiscordSelfUpdateMFAEvent(it, event)
                }
                is SelfUpdateNameEvent -> runEvent {
                    DiscordSelfUpdateNameEvent(it, event)
                }
                is SelfUpdateVerifiedEvent -> runEvent {
                    DiscordSelfUpdateVerifiedEvent(it, event)
                }

                is GuildMessageReceivedEvent -> runEvent {
                    DiscordGuildMessageReceivedEvent(it, event)
                }
                is GuildMessageUpdateEvent -> runEvent {
                    DiscordGuildMessageUpdateEvent(it, event)
                }
                is GuildMessageDeleteEvent -> runEvent {
                    DiscordGuildMessageDeleteEvent(it, event)
                }
                is GuildMessageEmbedEvent -> runEvent {
                    DiscordGuildMessageEmbedEvent(it, event)
                }
                is GuildMessageReactionAddEvent -> runEvent {
                    DiscordGuildMessageReactionAddEvent(it, event)
                }
                is GuildMessageReactionRemoveEvent -> runEvent {
                    DiscordGuildMessageReactionRemoveEvent(it, event)
                }
                is GuildMessageReactionRemoveAllEvent -> runEvent {
                    DiscordGuildMessageReactionRemoveAllEvent(it, event)
                }

                is PrivateMessageReceivedEvent -> runEvent {
                    DiscordPrivateMessageReceivedEvent(it, event)
                }
                is PrivateMessageUpdateEvent -> runEvent {
                    DiscordPrivateMessageUpdateEvent(it, event)
                }
                is PrivateMessageDeleteEvent -> runEvent {
                    DiscordPrivateMessageDeleteEvent(it, event)
                }
                is PrivateMessageEmbedEvent -> runEvent {
                    DiscordPrivateMessageEmbedEvent(it, event)
                }
                is PrivateMessageReactionAddEvent -> runEvent {
                    DiscordPrivateMessageReactionAddEvent(it, event)
                }
                is PrivateMessageReactionRemoveEvent -> runEvent {
                    DiscordPrivateMessageReactionRemoveEvent(it, event)
                }

                is MessageReceivedEvent -> runEvent {
                    DiscordMessageReceivedEvent(it, event)
                }
                is MessageUpdateEvent -> runEvent {
                    DiscordMessageUpdateEvent(it, event)
                }
                is MessageDeleteEvent -> runEvent {
                    DiscordMessageDeleteEvent(it, event)
                }
                is MessageBulkDeleteEvent -> runEvent {
                    DiscordMessageBulkDeleteEvent(it, event)
                }
                is MessageEmbedEvent -> runEvent {
                    DiscordMessageEmbedEvent(it, event)
                }
                is MessageReactionAddEvent -> runEvent {
                    DiscordMessageReactionAddEvent(it, event)
                }
                is MessageReactionRemoveEvent -> runEvent {
                    DiscordMessageReactionRemoveEvent(it, event)
                }
                is MessageReactionRemoveAllEvent -> runEvent {
                    DiscordMessageReactionRemoveAllEvent(it, event)
                }

                is TextChannelDeleteEvent -> runEvent {
                    DiscordTextChannelDeleteEvent(it, event)
                }
                is TextChannelUpdateNameEvent -> runEvent {
                    DiscordTextChannelUpdateNameEvent(it, event)
                }
                is TextChannelUpdateTopicEvent -> runEvent {
                    DiscordTextChannelUpdateTopicEvent(it, event)
                }
                is TextChannelUpdatePositionEvent -> runEvent {
                    DiscordTextChannelUpdatePositionEvent(it, event)
                }
                is TextChannelUpdatePermissionsEvent -> runEvent {
                    DiscordTextChannelUpdatePermissionsEvent(it, event)
                }
                is TextChannelUpdateNSFWEvent -> runEvent {
                    DiscordTextChannelUpdateNSFWEvent(it, event)
                }
                is TextChannelUpdateParentEvent -> runEvent {
                    DiscordTextChannelUpdateParentEvent(it, event)
                }
                is TextChannelUpdateSlowmodeEvent -> runEvent {
                    DiscordTextChannelUpdateSlowmodeEvent(it, event)
                }
                is TextChannelCreateEvent -> runEvent {
                    DiscordTextChannelCreateEvent(it, event)
                }

                is VoiceChannelDeleteEvent -> runEvent {
                    DiscordVoiceChannelDeleteEvent(it, event)
                }
                is VoiceChannelUpdateNameEvent -> runEvent {
                    DiscordVoiceChannelUpdateNameEvent(it, event)
                }
                is VoiceChannelUpdatePositionEvent -> runEvent {
                    DiscordVoiceChannelUpdatePositionEvent(it, event)
                }
                is VoiceChannelUpdateUserLimitEvent -> runEvent {
                    DiscordVoiceChannelUpdateUserLimitEvent(it, event)
                }
                is VoiceChannelUpdateBitrateEvent -> runEvent {
                    DiscordVoiceChannelUpdateBitrateEvent(it, event)
                }
                is VoiceChannelUpdatePermissionsEvent -> runEvent {
                    DiscordVoiceChannelUpdatePermissionsEvent(it, event)
                }
                is VoiceChannelUpdateParentEvent -> runEvent {
                    DiscordVoiceChannelUpdateParentEvent(it, event)
                }
                is VoiceChannelCreateEvent -> runEvent {
                    DiscordVoiceChannelCreateEvent(it, event)
                }

                is CategoryDeleteEvent -> runEvent {
                    DiscordCategoryDeleteEvent(it, event)
                }
                is CategoryUpdateNameEvent -> runEvent {
                    DiscordCategoryUpdateNameEvent(it, event)
                }
                is CategoryUpdatePositionEvent -> runEvent {
                    DiscordCategoryUpdatePositionEvent(it, event)
                }
                is CategoryUpdatePermissionsEvent -> runEvent {
                    DiscordCategoryUpdatePermissionsEvent(it, event)
                }
                is CategoryCreateEvent -> runEvent {
                    DiscordCategoryCreateEvent(it, event)
                }

                is PrivateChannelCreateEvent -> runEvent {
                    DiscordPrivateChannelCreateEvent(it, event)
                }
                is PrivateChannelDeleteEvent -> runEvent {
                    DiscordPrivateChannelDeleteEvent(it, event)
                }

                is GuildReadyEvent -> runEvent {
                    DiscordGuildReadyEvent(it, event)
                }
                is GuildJoinEvent -> runEvent {
                    DiscordGuildJoinEvent(it, event)
                }
                is GuildLeaveEvent -> runEvent {
                    DiscordGuildLeaveEvent(it, event)
                }
                is GuildAvailableEvent -> runEvent {
                    DiscordGuildAvailableEvent(it, event)
                }
                is GuildUnavailableEvent -> runEvent {
                    DiscordGuildUnavailableEvent(it, event)
                }
                is UnavailableGuildJoinedEvent -> runEvent {
                    DiscordUnavailableGuildJoinedEvent(it, event)
                }
                is GuildBanEvent -> runEvent {
                    DiscordGuildBanEvent(it, event)
                }
                is GuildUnbanEvent -> runEvent {
                    DiscordGuildUnbanEvent(it, event)
                }
                is GuildUpdateAfkChannelEvent -> runEvent {
                    DiscordGuildUpdateAfkChannelEvent(it, event)
                }
                is GuildUpdateSystemChannelEvent -> runEvent {
                    DiscordGuildUpdateSystemChannelEvent(it, event)
                }
                is GuildUpdateAfkTimeoutEvent -> runEvent {
                    DiscordGuildUpdateAfkTimeoutEvent(it, event)
                }
                is GuildUpdateExplicitContentLevelEvent -> runEvent {
                    DiscordGuildUpdateExplicitContentLevelEvent(it, event)
                }
                is GuildUpdateIconEvent -> runEvent {
                    DiscordGuildUpdateIconEvent(it, event)
                }
                is GuildUpdateMFALevelEvent -> runEvent {
                    DiscordGuildUpdateMFALevelEvent(it, event)
                }
                is GuildUpdateNameEvent -> runEvent {
                    DiscordGuildUpdateNameEvent(it, event)
                }
                is GuildUpdateNotificationLevelEvent -> runEvent {
                    DiscordGuildUpdateNotificationLevelEvent(it, event)
                }
                is GuildUpdateOwnerEvent -> runEvent {
                    DiscordGuildUpdateOwnerEvent(it, event)
                }
                is GuildUpdateRegionEvent -> runEvent {
                    DiscordGuildUpdateRegionEvent(it, event)
                }
                is GuildUpdateSplashEvent -> runEvent {
                    DiscordGuildUpdateSplashEvent(it, event)
                }
                is GuildUpdateVerificationLevelEvent -> runEvent {
                    DiscordGuildUpdateVerificationLevelEvent(it, event)
                }
                is GuildUpdateFeaturesEvent -> runEvent {
                    DiscordGuildUpdateFeaturesEvent(it, event)
                }

                is GuildMemberJoinEvent -> runEvent {
                    DiscordGuildMemberJoinEvent(it, event)
                }
                is GuildMemberLeaveEvent -> runEvent {
                    DiscordGuildMemberLeaveEvent(it, event)
                }
                is GuildMemberRoleAddEvent -> runEvent {
                    DiscordGuildMemberRoleAddEvent(it, event)
                }
                is GuildMemberRoleRemoveEvent -> runEvent {
                    DiscordGuildMemberRoleRemoveEvent(it, event)
                }
                is GuildMemberNickChangeEvent -> runEvent {
                    DiscordGuildMemberNickChangeEvent(it, event)
                }

                is GuildVoiceUpdateEvent -> runEvent {
                    DiscordGuildVoiceUpdateEvent(it, event)
                }
                is GuildVoiceJoinEvent -> runEvent {
                    DiscordGuildVoiceJoinEvent(it, event)
                }
                is GuildVoiceMoveEvent -> runEvent {
                    DiscordGuildVoiceMoveEvent(it, event)
                }
                is GuildVoiceLeaveEvent -> runEvent {
                    DiscordGuildVoiceLeaveEvent(it, event)
                }
                is GuildVoiceMuteEvent -> runEvent {
                    DiscordGuildVoiceMuteEvent(it, event)
                }
                is GuildVoiceDeafenEvent -> runEvent {
                    DiscordGuildVoiceDeafenEvent(it, event)
                }
                is GuildVoiceGuildMuteEvent -> runEvent {
                    DiscordGuildVoiceGuildMuteEvent(it, event)
                }
                is GuildVoiceGuildDeafenEvent -> runEvent {
                    DiscordGuildVoiceGuildDeafenEvent(it, event)
                }
                is GuildVoiceSelfMuteEvent -> runEvent {
                    DiscordGuildVoiceSelfMuteEvent(it, event)
                }
                is GuildVoiceSelfDeafenEvent -> runEvent {
                    DiscordGuildVoiceSelfDeafenEvent(it, event)
                }
                is GuildVoiceSuppressEvent -> runEvent {
                    DiscordGuildVoiceSuppressEvent(it, event)
                }

                is RoleCreateEvent -> runEvent {
                    DiscordRoleCreateEvent(it, event)
                }
                is RoleDeleteEvent -> runEvent {
                    DiscordRoleDeleteEvent(it, event)
                }
                is RoleUpdateColorEvent -> runEvent {
                    DiscordRoleUpdateColorEvent(it, event)
                }
                is RoleUpdateHoistedEvent -> runEvent {
                    DiscordRoleUpdateHoistedEvent(it, event)
                }
                is RoleUpdateMentionableEvent -> runEvent {
                    DiscordRoleUpdateMentionableEvent(it, event)
                }
                is RoleUpdateNameEvent -> runEvent {
                    DiscordRoleUpdateNameEvent(it, event)
                }
                is RoleUpdatePermissionsEvent -> runEvent {
                    DiscordRoleUpdatePermissionsEvent(it, event)
                }
                is RoleUpdatePositionEvent -> runEvent {
                    DiscordRoleUpdatePositionEvent(it, event)
                }

                is EmoteAddedEvent -> runEvent {
                    DiscordEmoteAddedEvent(it, event)
                }
                is EmoteRemovedEvent -> runEvent {
                    DiscordEmoteRemovedEvent(it, event)
                }
                is EmoteUpdateNameEvent -> runEvent {
                    DiscordEmoteUpdateNameEvent(it, event)
                }
                is EmoteUpdateRolesEvent -> runEvent {
                    DiscordEmoteUpdateRolesEvent(it, event)
                }

                else -> {
                    logger.trace { "未対応のイベントです。(${event::class.qualifiedName})" }
                }
            }
        }
    }
}
