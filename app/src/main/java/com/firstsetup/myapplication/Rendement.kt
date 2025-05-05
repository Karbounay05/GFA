import android.os.Parcel
import android.os.Parcelable

data class Rendement(
    val id: Int,  // 👈 ajoute ceci en premier
    val categorie: String,
    val superficie: Double,
    val production: Double,
    val pertes: Double,
    val mois: String,
    val annee: String,
    val rendementParHa: Double,
    val pertesParHa: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),                      // id
        parcel.readString() ?: "",             // categorie
        parcel.readDouble(),                   // superficie
        parcel.readDouble(),                   // production
        parcel.readDouble(),                   // pertes
        parcel.readString() ?: "",             // mois
        parcel.readString() ?: "",             // annee
        parcel.readDouble(),                   // rendementParHa
        parcel.readDouble()                    // pertesParHa
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(categorie)
        parcel.writeDouble(superficie)
        parcel.writeDouble(production)
        parcel.writeDouble(pertes)
        parcel.writeString(mois)
        parcel.writeString(annee)
        parcel.writeDouble(rendementParHa)
        parcel.writeDouble(pertesParHa)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Rendement> {
        override fun createFromParcel(parcel: Parcel): Rendement = Rendement(parcel)
        override fun newArray(size: Int): Array<Rendement?> = arrayOfNulls(size)
    }
}
