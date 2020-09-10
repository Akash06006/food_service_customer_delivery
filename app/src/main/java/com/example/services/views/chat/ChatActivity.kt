package com.example.services.views.chat

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ReturnMode
import com.example.services.R
import com.example.services.adapters.chat.MessageListAdapter
import com.example.services.application.MyApplication
import com.example.services.chatSocket.ConnectionListener
import com.example.services.chatSocket.SocketConnectionManager
import com.example.services.constants.GlobalConstants
import com.example.services.constants.GlobalConstants.SOCKET_CHAT_URL
import com.example.services.databinding.ActivityChatBinding
import com.example.services.model.chat.ChatListModel
import com.example.services.sharedpreference.SharedPrefClass
import com.example.services.utils.ConvertBase64
import com.google.gson.GsonBuilder
import io.socket.emitter.Emitter
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URISyntaxException
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity: com.example.services.utils.BaseActivity(), ConnectionListener {
    private val sharedPrefClass = SharedPrefClass()
    private var chatList: ArrayList<ChatListModel>?=null
    private val gson = GsonBuilder().serializeNulls().create()
    lateinit var chatBinding: ActivityChatBinding
    private var mMessageAdapter: MessageListAdapter ?=null
    val RC_CODE_PICKER = 2000
    var images: MutableList<com.esafirm.imagepicker.model.Image> = mutableListOf()
    var licenseImageFile: File? = null
    var selectedImage = ""

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

        /*if (sharedPrefClass.getPrefValue(MyApplication.instance, GlobalConstants.ROOM_ID).toString().isNullOrEmpty()) {
            val objectCreateRoom = JSONObject()
            objectCreateRoom.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objectCreateRoom.put("receiverId", "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13")
            SocketConnectionManager.getInstance().socket.emit("joinRoom", objectCreateRoom)
        }else{
            val objectCreateRoom = JSONObject()
            objectCreateRoom.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objectCreateRoom.put("groupId", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ROOM_ID
            ).toString())
            objectCreateRoom.put("receiverId", "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13")
            SocketConnectionManager.getInstance().socket.emit("joinRoom", objectCreateRoom)
        }*/

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
                   // Toast.makeText(this, "roomJoined", Toast.LENGTH_SHORT).show()
                }
            }

        SocketConnectionManager.getInstance()
            .addEventListener("leaveRoom") { args ->
                val data = args[0] as JSONObject
                try {
                    sharedPrefClass.removeParticularKey(this, GlobalConstants.ROOM_ID)
                }
                catch (e: JSONException) {

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
            if (! chatBinding.edittextChatbox.text.toString().isEmpty()) {
                val objectChatHistory = JSONObject()
                objectChatHistory.put(
                    "authToken", sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ACCESS_TOKEN
                    ).toString()
                )
                objectChatHistory.put(
                    "groupId", sharedPrefClass.getPrefValue(
                        MyApplication.instance,
                        GlobalConstants.ROOM_ID
                    ).toString()
                )
                objectChatHistory.put("type", 1)
                objectChatHistory.put("message", chatBinding.edittextChatbox.text.toString())
                objectChatHistory.put("usertype", "user")
                SocketConnectionManager.getInstance().socket.emit("sendMessage", objectChatHistory)
                chatBinding.edittextChatbox.setText("")
            }else{
                showToastError("Please enter message")
            }
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

        chatBinding.buttonSelectImage.setOnClickListener {
            pickImage()
        }
    }

    override fun onConnectError() {
        runOnUiThread {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onConnected() {
        runOnUiThread {
            val objectCreateRoom = JSONObject()
            objectCreateRoom.put(
                "authToken", sharedPrefClass.getPrefValue(
                    MyApplication.instance,
                    GlobalConstants.ACCESS_TOKEN
                ).toString()
            )
            objectCreateRoom.put("receiverId", "25cbf58b-46ba-4ba2-b25d-8f8f653e9f13")
            SocketConnectionManager.getInstance().socket.emit("joinRoom", objectCreateRoom)
           // Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDisconnected() {
        runOnUiThread {
            Toast.makeText(this, "disconnected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
       /* try {
            val socketConnectionManager: SocketConnectionManager =
                SocketConnectionManager.getInstance()
            socketConnectionManager.createConnection(
                this,
                HashMap<String, Emitter.Listener>()
            )
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }*/
    }

    override fun onBackPressed() {
        super.onBackPressed()

        val objectChatHistory = JSONObject()
        objectChatHistory.put("authToken", sharedPrefClass.getPrefValue(
            MyApplication.instance,
            GlobalConstants.ACCESS_TOKEN
        ).toString())
        objectChatHistory.put("groupId", sharedPrefClass.getPrefValue(
            MyApplication.instance,
            GlobalConstants.ROOM_ID
        ).toString())
        SocketConnectionManager.getInstance().socket.emit("leaveRoom", objectChatHistory)
        val socketConnectionManager = SocketConnectionManager.getInstance()
        socketConnectionManager.closeConnection()
    }

    override fun onDestroy() {
        super.onDestroy()
       /* val socketConnectionManager = SocketConnectionManager.getInstance()
        socketConnectionManager.closeConnection()*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_CODE_PICKER && resultCode == RESULT_OK && data != null) {
            images = ImagePicker.getImages(data)

            licenseImageFile = File(images.get(0).path)
            selectedImage = images.get(0).name
           showImageData(true, images.get(0).path)
        }
    }

    private fun pickImage() {
        ImagePicker.create(this)
            .returnMode(ReturnMode.ALL) // set whether pick action or camera action should return immediate result or not. Only works in single mode for image picker
            .folderMode(true) // set folder mode (false by default)
            .single()
            .limit(1)
            .toolbarFolderTitle(getString(R.string.folder)) // folder selection title
            .toolbarImageTitle(getString(R.string.gallery_select_title_msg))
            .start(RC_CODE_PICKER)
    }

     fun showImageData(isThrowSelection: Boolean, imagePathOrURL: String){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.show_image_dialog)
         dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent)
        // set the custom dialog components - text, image and button

        // set the custom dialog components - text, image and button
        val image: ImageView = dialog.findViewById(R.id.img) as ImageView
        val btnCancel: Button = dialog.findViewById(R.id.btn_cancel) as Button
        val btnSend: Button = dialog.findViewById(R.id.btn_send) as Button

         if (isThrowSelection){
             Glide.with(this)
                 .load(imagePathOrURL) //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                 .placeholder(R.drawable.ic_category)
                 .into(image)
             btnSend.visibility = View.VISIBLE
         }else{
             Glide.with(this)
                 .load(SOCKET_CHAT_URL + "" + imagePathOrURL) //.apply(RequestOptions.bitmapTransform(RoundedCorners(20)))
                 .placeholder(R.drawable.ic_category)
                 .into(image)
             btnSend.visibility = View.GONE
         }


        btnSend.setOnClickListener {
            var imageExtension = images.get(0).path.substring(images.get(0).path.lastIndexOf(".") + 1)
            val bm =
                BitmapFactory.decodeFile(/*"/path/to/image.jpg"*/images.get(0).path)
            val baos = ByteArrayOutputStream()
            bm.compress(
                Bitmap.CompressFormat.JPEG,
                100,
                baos
            ) // bm is the bitmap object
            var image = ConvertBase64.getStringImage(bm)
            val objectChatHistory = JSONObject()
            objectChatHistory.put("authToken", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ACCESS_TOKEN
            ).toString())
            objectChatHistory.put("groupId", sharedPrefClass.getPrefValue(
                MyApplication.instance,
                GlobalConstants.ROOM_ID
            ).toString())
            objectChatHistory.put("type", 2)
            objectChatHistory.put("media", image)
            objectChatHistory.put("extension", imageExtension)
            objectChatHistory.put("usertype", "user")
            SocketConnectionManager.getInstance().socket.emit("sendMessage", objectChatHistory)
            dialog.dismiss()
        }
        btnCancel.setOnClickListener {
                dialog.dismiss()
            }

        dialog.show()
    }
}