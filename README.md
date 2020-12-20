# Intent Sender plugin

<!-- Plugin description -->
Android Intent Sender is a plugin which allows you to send intents with specified data and extras to android devices or emulators with adb command
"broadcast", "startactivity", "startservice".  
Full readme: <a href="https://github.com/WeezLabs/idea-intent-sender-plugin">here</a>
<!-- Plugin description end -->

Typical plugin use-cases:
1. Broadcast receivers testing (including intent filters testing for custom data schemes, mime-types and so on)
2. Starting activities which are deep withing an app flow (even not exported ones)
3. Launching services from the IDE
4. Testing receivers, activities and services with different intent extras
5. ...

!! Note:
- starting not exported activities may not work on some devices (basically it does not work on some Samsung devices)
- it is not possible to test broadcast receivers registered using LocalBroadcastManager. Use Context's register receiver instead

# Installation
Install the plugin automatically from the JetBrains repository or download the latest zip (https://plugins.jetbrains.com/plugin/7945?pr=) and follow next steps:
- Open the plugin manager (File -> Settings -> Plugins (on the left pane) or by typing "plugin" in find action field (Help -> Find Action))
- Click "Install plugin from disk...", select downloaded file and click the install button
- Restart the IDE (will be prompted automatically)

# Usage
To open the plugin window click on the "Intent sender" button on the right side of the IDE or open it from the tool windows menu (View -> Tool Windows or from the icon on the bottom left corner of the IDE).

Specify intent parameters, select a component if needed, add extras and flags, and then just click on one of the send intent buttons. That's it.

To restore previously sent commands you can use the button next to the "Intent parameters" section header.

If you are adding any of the array extras, type items with comma as a separator and do not place space symbols between items

# Typical use-cases samples
* <b>Broadcast receiver testing.</b>

Just set up the Action and add extras you need and click the “Send Broadcast” button. Pretty easy, isn’t it?  
For example the Receiver has a code inside onReceive as following:
```java
@Override
public void onReceive(Context context, Intent intent) {
	String action = intent.getAction();
	if (action.equals("simple_intent_action")){
		Log.d("SimpleReceiver", "Got intent with action: " + action + " and string extra: " + intent.getStringExtra("string_extra"));
	}
}
```
The Receiver is registered with an IntentFilter like:  
`IntentFilter simpleFilter = new IntentFilter("simple_intent_action");`  
So to send a test broadcast message to a device we need to put `“simple_intent_action”` as the Action, add extra with `“string_extra”` as a Key and any text you like as a value and press the “Send Broadcast” button. After that logs will confirm an Intent receiving:  
`Got intent with action: simple_intent_action and string extra: Awesome string`

* <b>Activity or Service starting.</b>

Imagine that the MessageActivity should be launched and the MessageActivity picks up a message id from an intent inside its onCreate (id type is long and extra Key is `“messge_id”`).  
The component picker can be used to get the MessageActivity’s full component name and fill the User field. The Component name should be like `“com.some.package/.activities.MessageActivity”` and the User field is `“com.some.package”`. After that required extra data should be added and it is only left to press the “Start Activity” button and checkout launched MessageActivity on a device with the defined message opened.  
Of course it can be easily checked with logging like:  
`Log.d(LOG_TAG, "Message id: " + getIntent().getLongExtra("messageId", -1));`

* <b>Custom data scheme testing.</b>

Let’s say that a custom scheme like `http://myhost.com/...` under development and should be tested.
The scheme can be registered in the manifest with following lines of code (note: everything will work with the scheme registered programmatically too):
```java
<intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data android:scheme="http" android:host="myhost.com"/>
</intent-filter>
```
Inside an activity for this scheme we can put logging like:  
`Log.d("TAG", "Intent data: " + getIntent().getDataString());`  
So for testing we just put `“android.intent.action.VIEW”` inside the Action field and set `http://myhost.com/some_data` as the Data and click on the “Start Activity” button.  
The app chooser will be shown on a device and after clicking on the app we can checkout logs:  
`Intent data: http://myhost.com/some_data`  
The same result will be achieved if we send a test sms message with text `“Testing scheme: http://myhost.com/some_data”` and click on a link there.

* <b>Google Cloud Messaging (GCM) service testing</b>

Imagine that a class GcmBroadcastReceiver is created for GCM messages handling and a json stored in a String with a Key `“message”` expected.  
The GcmBroadcastReceiver will be registered in the manifest with the permission `android:permission="com.google.android.c2dm.permission.SEND"`. Unfortunately this permission will block the ADB command and we have to remove it during tests.  
Intent filter for the GcmBroadcastReceiver:
```xml
<intent-filter>
                <action android:name="com.google.android.c2dm.intent.RECEIVE" />
                <category android:name="com.your.package" />
</intent-filter>
```
To send a test GCM we need to set the Action as `“com.google.android.c2dm.intent.RECEIVE”` and Category as `“com.your.package”`, add a String extra with the key `“message_type”` and the value `“gcm”`, add one more String extra `“message”` with the expected json as the value and click on the “Send Broadcast” button. The app will handle broadcast message we sent as it will handle a real GCM message.

# Limitations

+ Starting not exported activities may not work on some devices (basically it does not work on some Samsung devices)
+ It is not possible to test broadcast receivers registered using LocalBroadcastManager. Use Context's register receiver instead
+ For now it is not possible to attach parcelable objects as extras but I am working on it...

# Contribution

Contributions are highly welcome :)

# License

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
