package jp.nephy.glados.feature

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.FeatureHelper
import net.dv8tion.jda.core.hooks.ListenerAdapter

abstract class ListenerFeature(final override val bot: GLaDOS): ListenerAdapter(), IFeature {
    override val helper = FeatureHelper(bot)
}
