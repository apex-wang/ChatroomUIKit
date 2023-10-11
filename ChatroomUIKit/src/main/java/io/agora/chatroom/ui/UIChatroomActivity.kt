package io.agora.chatroom.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.view.WindowCompat
import io.agora.chat.ChatOptions
import io.agora.chatroom.UIChatRoomViewTest
import io.agora.chatroom.model.UIChatroomInfo
import io.agora.chatroom.model.UserInfoProtocol
import io.agora.chatroom.service.ChatClient
import io.agora.chatroom.uikit.R

class UIChatroomActivity : ComponentActivity(){

    private val roomView: UIChatRoomViewTest by lazy { findViewById(R.id.room_view) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_chatroom)

        val roomId = intent.getStringExtra(KEY_ROOM_ID) ?: return

        val chatOptions = ChatOptions()
        chatOptions.appKey = "easemob#easeim"
        chatOptions.autoLogin = false
        ChatClient.getInstance().init(applicationContext, chatOptions)

        roomView.bindService(UIChatroomService(UIChatroomInfo("193314355740675", UserInfoProtocol())))
    }


    companion object {
        private const val KEY_ROOM_ID = "roomId"

        fun createIntent(
            context: Context,
            roomId: String,
        ): Intent {
            return Intent(context, UIChatroomActivity::class.java).apply {
                putExtra(KEY_ROOM_ID, roomId)
            }
        }
    }
}