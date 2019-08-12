# sample code - sign in
## Description
Register when using the app for the first time
## File
* Const
* DeviceInitResuktEvent
* DeviceManager
* DeviceNameActivity
* DevicePulseActivity
* DeviceServiceReadyEvent
* DeviceUserListResuktEvent
* User
* MainActivity


## Architecture
```sequence
MainActivity->DevicePulseActivity: new intent
Note right of DevicePulseActivity: check user Register or not
DevicePulseActivity-->DeviceNameActivity: no register
DeviceNameActivity->DevicePulseActivity: Register user name

```
## library 
* client.aar

## sign in screen
![](https://i.imgur.com/nljnKz5.png)
