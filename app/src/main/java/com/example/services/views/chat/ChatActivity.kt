package com.example.services.views.chat

import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.services.R
import com.example.services.adapters.chat.MessageListAdapter
import com.example.services.application.MyApplication
import com.example.services.chatSocket.ConnectionListener
import com.example.services.chatSocket.SocketConnectionManager
import com.example.services.constants.GlobalConstants
import com.example.services.databinding.ActivityChatBinding
import com.example.services.model.chat.ChatListModel
import com.example.services.sharedpreference.SharedPrefClass
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity: com.example.services.utils.BaseActivity(), ConnectionListener {
    private val sharedPrefClass = SharedPrefClass()
    private var chatList: ArrayList<ChatListModel>?=null
    private val gson = GsonBuilder().serializeNulls().create()
    lateinit var chatBinding: ActivityChatBinding
    private var mMessageAdapter: MessageListAdapter ?=null

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun initViews() {
        chatBinding = viewDataBinding as ActivityChatBinding
        chatBinding.commonToolBar.imgRight.visibility = View.GONE
        chatBinding.commonToolBar.imgToolbarText.text =
            resources.getString(R.string.chat)
        chatList = ArrayList()
        mMessageAdapter = MessageListAdapter(this, chatList, sharedPrefClass)
        chatBinding.reyclerviewMessageList.setLayoutManager(LinearLayoutManager(this))
        chatBinding.reyclerviewMessageList.setAdapter(mMessageAdapter)

        try {
            val socketConnectionManager: SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        val objectCreateRoom = JSONObject()
        objectCreateRoom.put("authToken", sharedPrefClass.getPrefValue(
            MyApplication.instance,
            GlobalConstants.ACCESS_TOKEN
        ).toString())
        objectCreateRoom.put("receiverId", "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13")
        SocketConnectionManager.getInstance().socket.emit("joinRoom", objectCreateRoom)

        //do heavy work on a background thread
        SocketConnectionManager.getInstance()
            .addEventListener("joinRoom") { args ->
                val data = args[0] as JSONObject
                try {
                    val roomId = data.getString("groupId")
                    sharedPrefClass.putObject(this, GlobalConstants.ROOM_ID, roomId)

                    val objectChatHistory = JSONObject()
                    objectChatHistory.put("authToken", sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ACCESS_TOKEN
                    ).toString())
                    objectChatHistory.put("groupId", sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ROOM_ID
                    ).toString())
                    SocketConnectionManager.getInstance().socket.emit("chatHistory", objectChatHistory)

                } catch (e: JSONException) {

                }
                runOnUiThread {
                    Toast.makeText(this, "roomJoined", Toast.LENGTH_SHORT).show()
                }
            }

        SocketConnectionManager.getInstance()
            .addEventListener("chatHistory") { args ->
                val data = args[0] as JSONArray
              chatList =  (gson.fromJson(""+data, Array<ChatListModel>::class.java)).toCollection(ArrayList())
                runOnUiThread {
                    mMessageAdapter!!.setData(chatList)
                    mMessageAdapter!!.notifyDataSetChanged()
                }
            }

        chatBinding.buttonChatboxSend.setOnClickListener {
            chatBinding.edittextChatbox.setText("")
            val objectChatHistory = JSONObject()
            objectChatHistory.put("authToken", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ACCESS_TOKEN
            ).toString())
            objectChatHistory.put("groupId", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ROOM_ID
            ).toString())
            objectChatHistory.put("type", 1)
            objectChatHistory.put("message", chatBinding.edittextChatbox.text.toString())
            objectChatHistory.put("usertype", "user")
            SocketConnectionManager.getInstance().socket.emit("sendMessage", objectChatHistory)
        }

        SocketConnectionManager.getInstance()
            .addEventListener("newMessage") { args ->
                val data = args[0] as JSONObject
                runOnUiThread {
                    chatList!!.add(gson.fromJson<ChatListModel>(""+data, ChatListModel::class.java))
                    mMessageAdapter!!.setData(chatList)
                    mMessageAdapter!!.notifyDataSetChanged()
                }
            }
    }

    override fun onConnectError() {
        runOnUiThread {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnected() {
        runOnUiThread {
            Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDisconnected() {
        runOnUiThread {
            Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val socketConnectionManager = SocketConnectionManager.getInstance()
        socketConnectionManager.closeConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
        val socketConnectionManager = SocketConnectionManager.getInstance()
        socketConnectionManager.closeConnection()
    }
}