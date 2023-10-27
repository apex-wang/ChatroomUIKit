package io.agora.chatroom.viewmodel.member

import android.util.Log
import io.agora.chatroom.ChatroomUIKitClient
import io.agora.chatroom.model.toUser
import io.agora.chatroom.service.OnError
import io.agora.chatroom.service.OnValueSuccess
import io.agora.chatroom.service.UserEntity
import io.agora.chatroom.service.UserOperationType
import io.agora.chatroom.ui.UIChatroomService
import io.agora.chatroom.viewmodel.RequestListViewModel

open class MemberListViewModel(
    private val roomId: String,
    private val service: UIChatroomService,
    private val pageSize: Int = 10
): RequestListViewModel<UserEntity>() {
    private var cursor: String? = null
    private var hasMore: Boolean = true
    fun fetchRoomMembers(
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        cursor = null
        hasMore = true
        clear()
        // clear cache data
        ChatroomUIKitClient.getInstance().getCacheManager().clearRoomUserCache()
        fetchMoreRoomMembers(true, isLoadMore = false, onSuccess = { list ->
            add(list)
            onSuccess.invoke(list)
        }, onError = { code, message ->
            onError.invoke(code, message)
        })
    }

    fun fetchMoreRoomMembers(
        fetchUserInfo: Boolean = false,
        isLoadMore: Boolean = true,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ){
        loading()
        service.getChatService().fetchMembers(roomId, cursor, pageSize, {cursorResult ->
            hasMore = cursorResult.data.size == pageSize
            cursor = cursorResult.cursor
            Log.e("apex", "fetchMoreRoomMembers: ${cursorResult.data}")
            ChatroomUIKitClient.getInstance().getCacheManager().saveRoomMemberList(roomId, cursorResult.data)
            val propertyList = cursorResult.data.filter { userId ->
                !ChatroomUIKitClient.getInstance().getCacheManager().inCache(userId)
            }
            if (fetchUserInfo && propertyList.isNotEmpty()) {
                fetchUsersInfo(propertyList, { list ->
                    val result = cursorResult.data.map { userId ->
                        ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    }
                    if (isLoadMore) {
                        addMore(result)
                    }
                    onSuccess.invoke(result)
                }, { code, error ->
                    val result = cursorResult.data.map { userId ->
                        ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    }
                    if (isLoadMore) {
                        addMore(result)
                    }
                    onError.invoke(code, error)
                })
            } else {
                val result = cursorResult.data.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                if (isLoadMore) {
                    addMore(result)
                }
                onSuccess.invoke(result)
            }
        }, {code, error ->
            error(code, error)
        })
    }

    /**
     * Returns the cached chatroom members.
     */
    fun getCacheMemberList(): List<UserEntity> {
        return ChatroomUIKitClient.getInstance().getCacheManager().getRoomMemberList(roomId).map { userId ->
            ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
        }
    }

    /**
     * Fetches the user information of the chatroom members.
     */
    fun fetchUsersInfo(
        userIdList: List<String>,
        onSuccess: OnValueSuccess<List<UserEntity>> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getUserService().getUserInfoList(userIdList, { list ->
            val users = list.map {
                it.toUser()
            }
            users.forEach {
                ChatroomUIKitClient.getInstance().getCacheManager().saveUserInfo(it.userId, it)
            }
            refresh()
            onSuccess.invoke(users)
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Returns whether there are more members to fetch.
     */
    fun hasMore(): Boolean {
        return hasMore
    }

    /**
     * Fetches user information based on visible items on the page.
     */
    fun fetchUsersInfo(firstVisibleIndex: Int, lastVisibleIndex: Int) {
        Log.e("apex", "fetchUsersInfo: $firstVisibleIndex, $lastVisibleIndex")
        items.subList(firstVisibleIndex, lastVisibleIndex).filter { user ->
            !ChatroomUIKitClient.getInstance().getCacheManager().inCache(user.userId)
        }.let { list ->
            if (list.isNotEmpty()) {
                fetchUsersInfo(list.map { it.userId })
            }
        }
    }

    /**
     * Mutes a user.
     */
    fun muteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.MUTE, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Unmutes a user.
     */
    fun unmuteUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.UNMUTE, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    /**
     * Kicks a user.
     */
    fun removeUser(
        userId: String,
        onSuccess: OnValueSuccess<UserEntity> = {},
        onError: OnError = { _, _ ->}
    ) {
        service.getChatService().operateUser(roomId, userId, UserOperationType.KICK, { chatroom ->
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMember(roomId, userId)
            ChatroomUIKitClient.getInstance().getCacheManager().removeRoomMuteMember(roomId, userId)
            onSuccess.invoke(ChatroomUIKitClient.getInstance().getChatroomUser().getUserInfo(userId))
        }, { code, error ->
            onError.invoke(code, error)
        })
    }

    fun searchUsers(
        keyword: String,
        isMute: Boolean = false,
        onSuccess: OnValueSuccess<List<UserEntity>> = {}
    ) {
        clear()
        if (keyword.isEmpty()) {
            onSuccess.invoke(emptyList())
            return
        }
        if (isMute) {
            ChatroomUIKitClient.getInstance().getCacheManager().getRoomMuteList(roomId).let { list ->
                Log.e("apex", "searchUsers from mute: $list")
                val result = list.filter { userId ->
                    val user = ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    user.nickName?.contains(keyword) ?: false || user.userId.contains(keyword)
                }.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                add(result)
                onSuccess.invoke(result)
            }
        } else {
            ChatroomUIKitClient.getInstance().getCacheManager().getRoomMemberList(roomId).let { list ->
                Log.e("apex", "searchUsers from member: $list")
                val result = list.filter { userId ->
                    val user = ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                    user.nickName?.contains(keyword) ?: false || user.userId.contains(keyword)
                }.map { userId ->
                    ChatroomUIKitClient.getInstance().getCacheManager().getUserInfo(userId)
                }
                add(result)
                onSuccess.invoke(result)
            }
        }

    }
}
