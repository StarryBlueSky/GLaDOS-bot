package jp.nephy.glados.feature

import jp.nephy.glados.GLaDOS
import jp.nephy.glados.component.helper.FeatureHelper

interface IFeature {
    val bot: GLaDOS
    val helper: FeatureHelper
}
