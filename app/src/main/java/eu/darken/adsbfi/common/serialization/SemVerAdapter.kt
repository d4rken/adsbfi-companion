package eu.darken.adsbfi.common.serialization

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import net.swiftzer.semver.SemVer

class SemVerAdapter {
    @ToJson
    fun toJson(semVer: SemVer) = semVer.toString()

    @FromJson
    fun fromJson(s: String) = SemVer.parse(s)
}
