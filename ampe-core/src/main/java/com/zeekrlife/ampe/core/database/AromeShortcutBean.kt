package com.zeekrlife.ampe.core.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "arome_shortcut")
data class AromeShortcutBean(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    var name: String? = null,
    var slogan: String? = null,
    var appletUrl: String? = null,
    var appletByteArray: ByteArray? = null,

    ) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AromeShortcutBean

        if (id != other.id) return false
        if (name != other.name) return false
        if (slogan != other.slogan) return false
        if (appletUrl != other.appletUrl) return false
        if (appletByteArray != null) {
            if (other.appletByteArray == null) return false
            if (!appletByteArray.contentEquals(other.appletByteArray)) return false
        } else if (other.appletByteArray != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + (slogan?.hashCode() ?: 0)
        result = 31 * result + (appletUrl?.hashCode() ?: 0)
        result = 31 * result + (appletByteArray?.contentHashCode() ?: 0)
        return result
    }
}
