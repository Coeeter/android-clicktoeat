package musicpractice.com.coeeter.clicktoeat.data.repositories

import musicpractice.com.coeeter.clicktoeat.data.daos.UserDao
import musicpractice.com.coeeter.clicktoeat.utils.createMultipartFormData
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UserRepository private constructor(private val userDao: UserDao) {

    fun getUser(token: String) {
        userDao.getUser(token)
    }

    fun getAllUsers() {
        userDao.getAllUsers()
    }

    fun addUser(
        username: String,
        password: String,
        email: String,
        phoneNum: String,
        firstName: String,
        lastName: String,
        gender: String,
        address: String,
        image: MultipartBody.Part? = null
    ) {
        userDao.addUser(
            username.createMultipartFormData(),
            password.createMultipartFormData(),
            email.createMultipartFormData(),
            phoneNum.createMultipartFormData(),
            firstName.createMultipartFormData(),
            lastName.createMultipartFormData(),
            gender.createMultipartFormData(),
            address.createMultipartFormData(),
            image
        )
    }

    fun login(username: String, password: String) {
        userDao.login(username, password)
    }

    fun removeUser(token: String) {
        userDao.removeUser(token)
    }

    fun forgetPassword(email: String) {
        userDao.forgetPassword(email)
    }

    fun updateUser(
        token: String,
        fieldsToBeUpdated: ArrayList<ArrayList<String>>,
        image: MultipartBody.Part? = null
    ) {
        val map = HashMap<String, RequestBody>()
        for (i in fieldsToBeUpdated.indices) {
            map[fieldsToBeUpdated[i][0]] = fieldsToBeUpdated[i][1].createMultipartFormData()
        }
        userDao.updateUser(token, map, image)
    }

    fun getProfile() = userDao.getProfile()
    fun getUserList() = userDao.getUserList()
    fun getCreateUserResult() = userDao.getCreateUserResult()
    fun getLoginResult() = userDao.getLoginResult()
    fun getRemoveUserResult() = userDao.getRemoveUserResult()
    fun getForgetPasswordResult() = userDao.getForgetPasswordResult()
    fun getUpdateUserResult() = userDao.getUpdateUserResult()

    companion object {
        @Volatile
        private var instance: UserRepository? = null

        fun getInstance(userDao: UserDao) = instance ?: synchronized(this) {
            instance ?: UserRepository(userDao).also { instance = it }
        }
    }
}