package com.example.niksey.database

import com.example.niksey.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.StorageReference
//Связь с БД
    lateinit var AUTH: FirebaseAuth
    lateinit var CURRENT_UID: String
    lateinit var REF_DATABASE_ROOT: DatabaseReference
    lateinit var REF_STORAGE_ROOT: StorageReference
    lateinit var USER: UserModel
//Глобальные ноды в БД
    const val NODE_USERS = "users"
    const val NODE_MAIN_LIST = "main_list"
    const val NODE_PHONES_CONTACTS = "phone_book"
    const val NODE_PHONES = "phones_and_uid"
    const val NODE_MESSAGES = "private_messages"
    const val NODE_GROUPS = "groups_messages"
//Второстепенные ноды
    const val NODE_USERNAMES = "usernames"
    const val FOLDER_PROFILE_IMAGE = "profile_image"
    const val FOLDER_MESSAGE_IMAGE = "message_image"
    const val CHILD_ID = "id"
    const val CHILD_USERNAME = "username"
    const val CHILD_PHONE = "phone"
    const val CHILD_FULLNAME = "fullname"
    const val CHILD_BIO = "bio"
    const val CHILD_EMAIL = "email"
    const val CHILD_PASSWORD = "password"
    const val CHILD_PHOTO_URL = "photoUrl"
    const val FOLDER_FILES = "messages_files"
    const val CHILD_STATE = "state"
    const val USER_ADMIN ="admin"
    const val USER_MEMBER = "member"
    const val USER_CREATOR = "creator"
    const val CHILD_FILE_URL = "fileUrl"
    const val FOLDER_GROUPS_IMAGE = "groups_image"
    const val NODE_MEMBERS = "members"
//Типы данных в БД
    const val TYPE_TEXT = "text"
    const val CHILD_TEXT = "text"
    const val CHILD_TYPE = "type"
    const val CHILD_FROM = "from"
    const val CHILD_TIMESTAMP = "timeStamp"

