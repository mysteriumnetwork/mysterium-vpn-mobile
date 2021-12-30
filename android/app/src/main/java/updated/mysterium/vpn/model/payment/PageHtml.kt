package updated.mysterium.vpn.model.payment

import com.google.gson.annotations.SerializedName

data class PageHtml(
    @SerializedName("secure_form")
    val htmlSecureData: Any
)
