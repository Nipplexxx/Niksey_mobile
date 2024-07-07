package com.example.niksey.models

/* Общая модель для всех сущностей приложения*/

data class CommonModel(
    val id: String = "",
    var username: String = "",
    var bio: String = "",
    var fullname: String = "",
    var state: String = "",
    var phone: String = "",
    var photoUrl: String = "empty",
    var text: String = "",
    var type: String = "",
    var from: String = "",
    var timeStamp: Any = "",
    var fileUrl: String = "empty",
    var lastMessage: String = "",
    var choice: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return (other as CommonModel).id == id
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + bio.hashCode()
        result = 31 * result + fullname.hashCode()
        result = 31 * result + state.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + photoUrl.hashCode()
        result = 31 * result + text.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + fileUrl.hashCode()
        result = 31 * result + lastMessage.hashCode()
        result = 31 * result + choice.hashCode()
        return result
    }
}