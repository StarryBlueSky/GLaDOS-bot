package jp.nephy.glados.component.api.soundcloud.param

import jp.nephy.glados.component.helper.prompt.IPromptEnum

enum class Genre(private val altName: String? = null, private val altInternalName: String? = null): IPromptEnum {
    AllMusic("All Music", "all-music"), AllAudio("All Audio", "all-audio"),

    AlternativeRock("Alternative Rock"), Ambient, Classical, Country,
    DanceEDM("Dance & EDM"), Dancehall, DeepHouse("Deep House"), Disco,
    DrumBass("Drum & Bass"), Dubstep, Electronic, FolkSingerSongwriter("Folk & Singer-Songwriter"),
    HiphopRap("Hip-hop & Rap"), House, Indie, JazzBlues("Jazz & Blues"), Latin, Metal, Piano,
    Pop, RBSoul("R&B & Soul"), Reggae, Reggaeton, Rock, Soundtrack, Techno, Trance, Trap, Triphop, World,

    Audiobooks, Business, Comedy, Entertainment, Learning, NewsPolitics("News & Politics"),
    ReligionSpirituality("Religion & Spirituality"), Science, Sports, Storytelling, Technology;

    override val friendlyName: String
        get() = altName ?: name
    val internalName: String
        get() = altInternalName ?: name.toLowerCase()
}
