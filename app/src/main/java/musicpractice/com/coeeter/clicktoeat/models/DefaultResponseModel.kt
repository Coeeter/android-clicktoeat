package musicpractice.com.coeeter.clicktoeat.models

data class DefaultResponseModel(
    val result: String?,
    val affectedRows: Int?,
    val accepted: List<String>?,
    val insertId: Int?
)
