package jp.nephy.glados.feature.listener.audit.guild

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.Color
import jp.nephy.glados.component.helper.fullNameWithoutGuild
import jp.nephy.glados.feature.ListenerFeature
import net.dv8tion.jda.core.events.guild.update.*


class GuildSetting(bot: GLaDOS): ListenerFeature(bot) {
    override fun onGuildUpdateRegion(event: GuildUpdateRegionEvent) {
        helper.messageLog(event, "サーバリージョン変更", Color.Good) { "サーバリージョンが `${event.oldRegion.name} (${event.oldRegionRaw})` から `${event.newRegion.name} (${event.newRegionRaw})` に変更されました。" }
    }
    override fun onGuildUpdateName(event: GuildUpdateNameEvent) {
        helper.messageLog(event, "サーバ名変更", Color.Neutral) { "サーバ名が `${event.oldName}` から `${event.guild.name}` に変更されました。" }
    }

    override fun onGuildUpdateAfkChannel(event: GuildUpdateAfkChannelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のAFKチャンネルが `${event.oldAfkChannel.name}` から `${event.guild.afkChannel.name}` に変更されました." }
    }

    override fun onGuildUpdateSystemChannel(event: GuildUpdateSystemChannelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のシステムチャンネルが `${event.oldSystemChannel.name}` から `${event.guild.systemChannel.name}` に変更されました." }
    }

    override fun onGuildUpdateOwner(event: GuildUpdateOwnerEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のオーナーが `${event.oldOwner.fullNameWithoutGuild}` から `${event.guild.owner.fullNameWithoutGuild}` に変更されました." }
    }

    override fun onGuildUpdateVerificationLevel(event: GuildUpdateVerificationLevelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` の認証レベルが `${event.oldVerificationLevel.name}` から `${event.guild.verificationLevel.name}` に変更されました." }
    }

    override fun onGuildUpdateNotificationLevel(event: GuildUpdateNotificationLevelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のデフォルト通知レベルが `${event.oldNotificationLevel.name}` から `${event.guild.defaultNotificationLevel.name}` に変更されました." }
    }

    override fun onGuildUpdateExplicitContentLevel(event: GuildUpdateExplicitContentLevelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のコンテンツ規制レベルが `${event.oldLevel}` から `${event.guild.explicitContentLevel.name}` に変更されました." }
    }

    override fun onGuildUpdateMFALevel(event: GuildUpdateMFALevelEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` の要求セキュリティレベルが `${event.oldMFALevel.name}` から `${event.guild.requiredMFALevel.name}` に変更されました." }
    }

    override fun onGuildUpdateSplash(event: GuildUpdateSplashEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のスプラッシュが `${event.oldSplashUrl}` から `${event.guild.splashUrl}` に変更されました." }
    }

    override fun onGuildUpdateAfkTimeout(event: GuildUpdateAfkTimeoutEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のAFKタイムアウト時間が `${event.oldAfkTimeout.seconds}秒` から `${event.guild.afkTimeout.seconds}秒` に変更されました." }
    }

    override fun onGuildUpdateFeatures(event: GuildUpdateFeaturesEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` の特殊機能が `${event.oldFeatures.joinToString(", ") }` から `${event.guild.features.joinToString(", ")}` に変更されました." }
    }

    override fun onGuildUpdateIcon(event: GuildUpdateIconEvent) {
        bot.logger.info { "サーバ `${event.guild.name}` のアイコンが `${event.oldIconUrl}` から `${event.guild.iconUrl}` に変更されました." }
    }
}
