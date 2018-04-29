package jp.nephy.glados.feature

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.FeatureHelper

interface Feature {
    val bot: GLaDOS
    val helper: FeatureHelper
}
