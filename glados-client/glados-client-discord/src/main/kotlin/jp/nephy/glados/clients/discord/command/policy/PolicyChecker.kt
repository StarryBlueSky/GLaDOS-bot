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

package jp.nephy.glados.clients.discord.command.policy

import jp.nephy.glados.api.GLaDOS
import jp.nephy.glados.api.Logger
import jp.nephy.glados.api.of
import jp.nephy.glados.clients.discord.command.arguments
import jp.nephy.glados.clients.discord.command.events.DiscordCommandEvent
import jp.nephy.glados.clients.discord.command.events.argumentString
import jp.nephy.glados.clients.discord.command.primaryCommandSyntax
import jp.nephy.glados.clients.discord.config.config
import jp.nephy.glados.clients.discord.config.textChannel
import jp.nephy.glados.clients.discord.extensions.*
import jp.nephy.glados.clients.discord.extensions.ColorPresets
import jp.nephy.glados.clients.discord.extensions.messages.reply
import jp.nephy.glados.clients.discord.jda
import jp.nephy.jsonkt.delegation.*
import net.dv8tion.jda.api.entities.TextChannel
import java.util.concurrent.TimeUnit

private val logger = Logger.of("GLaDOS.Discord.PolicyChecker")

internal fun DiscordCommandEvent.checkAllPolicies(): Boolean {
    when {
        !satisfyAllowFromSelfUserPolicy() -> {
            rejectAllowFromSelfUserPolicy()
        }
        !satisfyAllowFromBotAccountPolicy() -> {
            rejectAllowFromBotAccountPolicy()
        }
        !satisfyChannelPolicy() -> {
            rejectChannelPolicy()
        }
        !satisfyBotChannelPolicy() -> {
            rejectBotChannelPolicy()
        }
        !satisfyCommandAvailabilityPolicy() -> {
            rejectCommandAvailabilityPolicy()
        }
        !satisfyArgumentsSizePolicy() -> {
            rejectArgumentsSizePolicy()
        }
        !satisfyWhileInAnyVoiceChannelConditionPolicy() -> {
            rejectWhileInAnyVoiceChannelConditionPolicy()
        }
        !satisfyWhileInSameVoiceChannelConditionPolicy() -> {
            rejectWhileInSameVoiceChannelConditionPolicy()
        }
        !satisfyAdminOnlyPermissionPolicy() -> {
            rejectAdminOnlyPermissionPolicy()
        }
        !satisfyMainGuildAdminOnlyPermissionPolicy() -> {
            rejectMainGuildAdminOnlyPermissionPolicy()
        }
        !satisfyOwnerOnlyPermissionPolicy() -> {
            rejectOwnerOnlyPermissionPolicy()
        }
        else -> {
            return true
        }
    }
    
    return false
}

private fun DiscordCommandEvent.satisfyAllowFromSelfUserPolicy(): Boolean {
    return subscription.annotation.allowFromSelfUser || !author.isSelfUser
}

private fun DiscordCommandEvent.rejectAllowFromSelfUserPolicy() {
    logger.warn { "\"$argumentString\": 自身のコマンド実行は許可されていないため, 実行されませんでした。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyAllowFromBotAccountPolicy(): Boolean {
    return subscription.annotation.allowFromBotAccount || !author.isBot
}

private fun DiscordCommandEvent.rejectAllowFromBotAccountPolicy() {
    logger.warn { "\"$argumentString\": Bot からのコマンド実行は許可されていないため, 実行されませんでした。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyChannelPolicy(): Boolean {
    return subscription.annotation.channelType.classes.any { it.isInstance(channel) }
}

private fun DiscordCommandEvent.rejectChannelPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "このコマンドは ${subscription.annotation.channelType.classes.joinToString(", ") { it.simpleName.orEmpty() }} チャンネルでのみ実行可能です。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(15, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": チャンネルタイプが対象外のため, 実行されませんでした。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyBotChannelPolicy(): Boolean {
    return subscription.annotation.channelType != ChannelTypePolicy.BotChannel || guild.config?.textChannel("bot") == channel
}

private fun DiscordCommandEvent.rejectBotChannelPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "このコマンドは ${guild.config?.textChannel("bot")?.asMention ?: "#bot"} チャンネルでのみ実行可能です。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(15, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": Bot チャンネルではないため実行されませんでした。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyCommandAvailabilityPolicy(): Boolean {
    return guild == null || guild.config?.options?.booleanValueOrNull("enable_command") == true
}

private fun DiscordCommandEvent.rejectCommandAvailabilityPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "`${guild?.name}` では ${GLaDOS.jda.selfUser.asMention} のコマンド機能は利用できません。Admin または GLaDOS 開発者にご連絡ください。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(30, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": サーバ ${guild?.name} ではコマンド機能が無効なので実行されませんでした。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyArgumentsSizePolicy(): Boolean {
    return !subscription.annotation.checkArgumentsSize || subscription.arguments.size == arguments.size
}

private fun DiscordCommandEvent.rejectArgumentsSizePolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            descriptionBuilder {
                appendln("コマンドの引数の数が一致しません。`!help` コマンドも必要に応じてご確認ください。")
                append("実行例: `${subscription.primaryCommandSyntax} ${subscription.arguments.joinToString(" ") { "<$it>" }}`")
            }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(30, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": コマンドの引数が足りません。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyWhileInAnyVoiceChannelConditionPolicy(): Boolean {
    return subscription.annotation.condition != VoiceChannelConditionPolicy.WhileInAnyVoiceChannel || member?.voiceState?.inVoiceChannel() == true
}

private fun DiscordCommandEvent.rejectWhileInAnyVoiceChannelConditionPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "このコマンドはボイスチャンネルに参加中のみ実行できます。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(30, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": コマンド実行の要件(WhileInAnyVoiceChannel)が足りません。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyWhileInSameVoiceChannelConditionPolicy(): Boolean {
    return subscription.annotation.condition != VoiceChannelConditionPolicy.WhileInSameVoiceChannel || guild?.currentVoiceChannel == member?.voiceState?.channel
}

private fun DiscordCommandEvent.rejectWhileInSameVoiceChannelConditionPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "このコマンドは ${GLaDOS.jda.selfUser.asMention} と同じボイスチャンネルに参加中のみ実行できます。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(30, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": コマンド実行の要件(WhileInSameVoiceChannel)が足りません。 (${message.fullName})" }
}

private fun DiscordCommandEvent.satisfyAdminOnlyPermissionPolicy(): Boolean {
    return subscription.annotation.permission != PermissionPolicy.AdminOnly || (member?.hasAdminPermission ?: author.hasAdminPermission || author.isGLaDOSOwner)
}

private fun DiscordCommandEvent.rejectAdminOnlyPermissionPolicy() {
    if (channel is TextChannel) {
        reply {
            embed {
                title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
                description { "このコマンドは `${guild?.name}` の管理者ロールが付与されているメンバーのみが実行できます。判定に問題がある場合はサーバのオーナーにご連絡ください。" }
                color(ColorPresets.Bad)
                timestamp()
            }
        }.launchAndDelete(30, TimeUnit.SECONDS)

        logger.warn { "\"$argumentString\": 管理者ロールがないため実行されませんでした。 (${message.fullName})" }
    } else {
        reply {
            embed {
                title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
                description { "このコマンドは管理者ロールが必要であるため, DMでは実行できません。" }
                color(ColorPresets.Bad)
                timestamp()
            }
        }.launchAndDelete(30, TimeUnit.SECONDS)

        logger.warn { "\"$argumentString\": 管理者ロールがないため(DM)実行されませんでした。 (${message.fullName})" }
    }
}

private fun DiscordCommandEvent.satisfyMainGuildAdminOnlyPermissionPolicy(): Boolean {
    return subscription.annotation.permission != PermissionPolicy.MainGuildAdminOnly || ((guild.config?.isMain == true && member?.hasAdminPermission ?: author.hasAdminPermission) || author.isGLaDOSOwner)
}

private fun DiscordCommandEvent.rejectMainGuildAdminOnlyPermissionPolicy() {
    if (channel is TextChannel) {
        reply {
            embed {
                title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
                description { "このコマンドはメインサーバの管理者ロールが付与されているメンバーのみが実行できます。" }
                color(ColorPresets.Bad)
                timestamp()
            }
        }.launchAndDelete(30, TimeUnit.SECONDS)

        logger.warn { "\"$argumentString\": メインサーバの管理者ロールがないため実行されませんでした。 (${message.fullName})" }
    } else {
        reply {
            embed {
                title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
                description { "このコマンドはメインサーバの管理者ロールが必要であるため, DMでは実行できません。" }
                color(ColorPresets.Bad)
                timestamp()
            }
        }.launchAndDelete(30, TimeUnit.SECONDS)

        logger.warn { "\"$argumentString\": メインサーバの管理者ロールがないため(DM)実行されませんでした。 (${message.fullName})" }
    }
}

private fun DiscordCommandEvent.satisfyOwnerOnlyPermissionPolicy(): Boolean {
    return subscription.annotation.permission != PermissionPolicy.OwnerOnly || author.isGLaDOSOwner
}

private fun DiscordCommandEvent.rejectOwnerOnlyPermissionPolicy() {
    reply {
        embed {
            title("コマンドエラー: `${subscription.primaryCommandSyntax}`")
            description { "このコマンドは ${GLaDOS.jda.selfUser.asMention} のオーナーのみが実行できます。" }
            color(ColorPresets.Bad)
            timestamp()
        }
    }.launchAndDelete(30, TimeUnit.SECONDS)

    logger.warn { "\"$argumentString\": オーナーではないため実行されませんでした。 (${message.fullName})" }
}
