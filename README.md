# IntentSender
========
Plugin allows you to send intents with specified data and extras to android devices or emulators with adb command "broadcast", "startactivity", "startservice".
Typical plugin use-cases:
 1. Broadcast receivers testing (including intent filters testing for custom data schemes, mime-types and so on)
 2. Starting activities which are deep withing apps flow (even not exported ones)
 3. Launching services from IDE
 4. Testing receivers, activities and services with different intent extras
 5. ...

!!Note: starting not exported activities may not work on some devices (basically it does not work on some Samsung devices)

Installation
========
- Download plugin *.zip file
- Open plugin manager (File -> Settings -> Plugins (on the left pane) or by typing "plugin" in find action field (Help -> Find Action)). Also to open settings you can use ctrl+alt+S (Win) and to open find action field use ctrl+shift+A.
- Click "Install plugin from disk...", select downloaded file and click install button
- Restart IDE (will be prompted automatically)

Usage
========
To open plugin window click on "Intent sender" button on the right side of IDE or open it from tool windows menu (View -> Tool Windows or from the icon on the bottom left corner of the IDE).
Specify intent parameters, select component if needed, add extras and flags, and then just click on one of the send intent buttons. That's it.
To restore previously sent commands you can use button right near "Intent parameters" section header

If you are adding any of the array extras, type items with comma as a separator and do not place space symbols between items

Limitations
========
For now it is not possible to attach parcelable objects as extras but I am working on it...

License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

