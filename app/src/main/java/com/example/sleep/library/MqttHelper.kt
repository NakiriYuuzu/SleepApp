package com.example.sleep.library

import android.content.Context
import android.util.Log
import com.example.sleep.util.Constant
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class MqttHelper(val context: Context) {
    private var mqttClient: MqttAndroidClient = MqttAndroidClient(context, Constant.MQTT_HOST, MqttClient.generateClientId())
    private var shareHelper: ShareHelper = ShareHelper(context)
    private lateinit var mqttToken: IMqttToken

    init {
        mqttClient.setCallback(object : MqttCallbackExtended {
            override fun connectComplete(reconnect: Boolean, serverURI: String?) {
                Log.e("connectComplete: ", "$reconnect, $serverURI")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e("connectionLost: ", "$cause")
            }

            override fun messageArrived(topic: String?, message: MqttMessage?) {
                val grant = shareHelper.get<String>(Constant.GRANT)
                if (grant != "admin") {
                    val msg = message?.toString()?.split("|")
                    Log.e("TAG", "messageArrived: ${msg?.get(0)}")
                    if (msg?.get(0).toString() != "小花") {
                        val list = listOf(
                            MqttModel(
                                msg?.get(1).toString(),
                                msg?.get(0).toString(),
                                msg?.get(2).toString()
                            )
                        )
                        Log.e("messageArrived: ", "$list")
                        val shareList = shareHelper.get<List<MqttModel>>(Constant.USER_DATA)

                        if (shareList.isNullOrEmpty()) {
                            shareHelper.put(list, Constant.USER_DATA)
                            Log.e(
                                "messageArrived: ",
                                shareHelper.get<List<MqttModel>>(Constant.USER_DATA).toString()
                            )
                        } else {
                            val newList = shareList + list
                            Log.e("TAG", "messageArrived: $newList")
                            shareHelper.put(newList, Constant.USER_DATA)
                            Log.e(
                                "messageArrived: ",
                                shareHelper.get<List<MqttModel>>(Constant.USER_DATA).toString()
                            )
                        }
                    }
                }
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.e("TAG", "deliveryComplete: ${token?.message}")
                val grant = shareHelper.get<String>(Constant.GRANT)
                Log.e("TAG", "deliveryComplete: $grant")
                if (grant == "admin") {
                    val msg = token?.message?.toString()?.split("|")
                    val list = listOf(MqttModel(msg?.get(1).toString(), msg?.get(0).toString(), msg?.get(2).toString()))
                    Log.e("list: ", "$list")
                    val shareList = shareHelper.get<List<MqttModel>>(Constant.ADMIN_DATA)

                    if (shareList.isNullOrEmpty()) {
                        shareHelper.put(list, Constant.ADMIN_DATA)
                        Log.e("deliveryComplete: ", shareHelper.get<List<MqttModel>>(Constant.ADMIN_DATA).toString())
                    } else {
                        val newList = shareList + list
                        Log.e("TAG", "deliveryComplete: $newList")
                        shareHelper.put(newList, Constant.ADMIN_DATA)
                        Log.e("deliveryComplete: ", shareHelper.get<List<MqttModel>>(Constant.ADMIN_DATA).toString())
                    }
                }
            }
        })
    }

    fun mqttCallback(callback: MqttCallbackExtended) {
        mqttClient.setCallback(callback)
    }

    fun connect() {
        val options = MqttConnectOptions()
        options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
        options.isAutomaticReconnect = Constant.ADAFRUIT_CONNECTION_RECONNECT
        options.isCleanSession = Constant.ADAFRUIT_CONNECTION_CLEAN_SESSION
        options.connectionTimeout = Constant.ADAFRUIT_CONNECTION_TIMEOUT
        options.keepAliveInterval = Constant.ADAFRUIT_CONNECTION_KEEP_ALIVE_INTERVAL
        options.userName = Constant.MQTT_USER_NAME
        options.password = Constant.MQTT_USER_PASSWORD.toCharArray()

        try {
            mqttToken = mqttClient.connect(options)
            mqttToken.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.e("mqtt", "connect success $asyncActionToken")
                    subscribe(Constant.MQTT_TOPIC)
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("mqtt", "connect fail $asyncActionToken")
                }
            }
        } catch (e: MqttException) {
            Log.e("mqtt", "connect exception $e")
        }
    }

    fun subscribe(topic: String) {
        try {
            val sub = mqttClient.subscribe(topic, Constant.QOS)
            sub.actionCallback = object: IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.e("mqtt", "subscribe success $asyncActionToken")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    Log.e("mqtt", "subscribe fail $asyncActionToken")
                }
            }
        } catch (e: MqttException) {
            Log.e("mqtt", "subscribe exception $e")
        }
    }

    fun publish(payLoad: String) {
        val encodedPayload: ByteArray
        try {
            encodedPayload = payLoad.toByteArray(charset("UTF-8"))
            val message = MqttMessage(encodedPayload)
            Log.e("publish: ", message.toString())
            mqttClient.publish(Constant.MQTT_TOPIC, message)
        } catch (e: MqttException) {
            Log.e("mqtt", "publish exception $e")
        } catch (e: Exception) {
            Log.e("mqtt", "publish exception $e")
        }
    }

    fun disconnect() {
        try {
            mqttClient.unregisterResources()
            mqttClient.disconnect()
        } catch (e: MqttException) {
            Log.e("mqtt", "disconnect exception $e")
        }
    }
}
