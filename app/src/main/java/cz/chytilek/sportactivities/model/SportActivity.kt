package cz.chytilek.sportactivities.model

import com.google.firebase.database.Exclude
import java.io.Serializable

data class SportActivity(var ID: Long,
                         var name: String,
                         var location: String,
                         var lengthOfActivity: Long,
                         var DBType: Int) : Serializable {
    @Exclude
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "location" to location,
            "lengthOfActivity" to lengthOfActivity,
            "DBType" to DBType
        )
    }
}