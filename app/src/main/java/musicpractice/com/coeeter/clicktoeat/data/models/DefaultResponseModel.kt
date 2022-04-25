package musicpractice.com.coeeter.clicktoeat.data.models

data class DefaultResponseModel(
    val result: String?,
    val affectedRows: Int?,
    val accepted: List<String>?,
    val insertId: Int?
)
