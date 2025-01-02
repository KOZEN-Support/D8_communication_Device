> In the D8 project, the main screen is D0861, the secondary screen is D08C1, and the SDK document for communication using USB.


## How to use SDK


The SDK is in the form of a 'jar' package.



### Reference the SDK.jar package


####  1. Put 'usbHost.jar' package under 'app/libs' as follows:


![](libs.png)

#### 2. Add a reference to the app/build.gradle file


```groovy

gradle.projectsEvaluated {

  tasks.withType(JavaCompile) {

    Set<File> fileSet = options.bootstrapClasspath.getFiles();

    List<File> newFileList = new ArrayList<>()

    newFileList.add(new File("app/libs/usbHost.jar")) // Absolute or relative path of the jar package


    newFileList.addAll(fileSet)

    options.bootstrapClasspath = files(newFileList.toArray())

  }

}

 dependencies {

    ...

     compileOnly files('libs/usbHost.jar')

    ...

 }

```

#### 3. In the main project under 'build.gradle' add:


```groovy

allprojects {

  gradle.projectsEvaluated {

    tasks.withType(JavaCompile) {

      Set<File> fileSet = options.bootstrapClasspath.getFiles()

      List<File> newFileList = new ArrayList<>();

      //"../usbHost.jar" For relative position, need to refer to the modification, or use absolute position

      newFileList.add(new File("app/libs/usbHost.jar"))

      newFileList.addAll(fileSet)

      options.bootstrapClasspath = files(

          newFileList.toArray()

      )

    }

  }

}

```

> **The above addition is the jar of the D0861 main screen, while the 'usbDevices.jar' addition of the secondary screen D08C1 is the same. As above **




### Main screen D0861 XcUsbManager 

#### Get the XcUsbManager instance


```java

XcUsbManager mXcUsbManager = (XcUsbManager) Context.getSystemService(Context.XC_USB_SERVICE);

```

The above 'Context.XC_USB_SERVICE' is an error, but if the above jar package is imported properly, the compilation will work.



### getConnectionState

##### interface description

`getConnectionState()` Gets the connection status.


##### interface method

```java 

public int getConnectionState() {

  try {

    return mService.getConnectionState();

  } catch (RemoteException e) {

    Log.e(TAG, "getConnectionState() failed", e);

    return -1;

  }

}

```

##### Return value

- 1: The connection is successful
-0: Connecting...
- -1: disconnects the connection


### sendData

##### interface description

`sendData(byte[] data)` Send data in byte[] format.

##### interface method

```java

public boolean sendData(byte[] data) {

  try {

    return mService.sendData(data);

  } catch (RemoteException e) {

    Log.e(TAG, "sendData() failed", e);

    return false;

  }

}

```

##### parameter

- data : Send data in byte[] format.




### reconnectSub

##### interface description

`reconnectSub()` Reconnect to the secondary screen. If the secondary screen is abnormally disconnected, you can try to reconnect.


##### interface method

```java

public void reconnectSub() {

  try {

    mService.reconnectSub();

  } catch (RemoteException e) {

    Log.e(TAG, "reconnectSub() failed", e);

  }

}

```

> ** The following is the interface for the secondary screen operation :**




### subPowerOff

##### interface description

`subPowerOff()` The secondary screen is off.


##### interface method

```java

public void subPowerOff() {

  try {

    mService.subPowerOff();

  } catch (RemoteException e) {

    Log.e(TAG, "subPowerOff() failed", e);

  }

}

```

### subPowerOn

##### interface description

`subPowerOn()` The operation secondary screen lights up.


##### interface method

```java

public void subPowerOn() {

  try {

    mService.subPowerOn();

  } catch (RemoteException e) {

    Log.e(TAG, "subPowerOn() failed", e);

  }

}

```

### getSubModel

##### interface description

`getSubModel()` get the model of the secondary screen.


##### interface method

```java

public String getSubModel() {

  try {

    return mService.getSubModel();

  } catch (RemoteException e) {

    Log.e(TAG, "getSubModel() failed", e);

    return null;

  }

}

```

##### Return value

- Return：Secondary screen model. The value is a String.



### getSubSn

##### interface description

`getSubSn()` get the Sn of the secondary screen.


##### interface method

```java

public String getSubSn() {

  try {

    return mService.getSubSn();

  } catch (RemoteException e) {

    Log.e(TAG, "getSubSn() failed", e);

    return null;

  }

}

```

##### Return value

- Return：Sn，String type



### getSubBuildNumber

##### interface description

`getSubBuildNumber()` Get the version number.


##### interface method

```java

public String getSubBuildNumber() {

  try {

    return mService.getSubBuildNumber();

  } catch (RemoteException e) {

    Log.e(TAG, "getSubBuildNumber() failed", e);

    return null;

  }

}

```

##### Return value

- Return：Software version. The value is a String.



### getSubFwVersion

##### interface description

`getSubFwVersion()` get the firmware version number.


##### interface method

```java

public String getSubFwVersion() {

  try {

    return mService.getSubFwVersion();

  } catch (RemoteException e) {

    Log.e(TAG, "getSubFwVersion() failed", e);

    return null;

  }

}

```

##### Return value

- Return：Firmware version. The value is a String.




### getSubSpFwVersion

##### interface description

`getSubSpFwVersion()` Get the financial firmware version number


##### interface method

```java

public String getSubSpFwVersion() {

  try {

    return mService.getSubSpFwVersion();

  } catch (RemoteException e) {

    Log.e(TAG, "getSubSpFwVersion() failed", e);

    return null;

  }

}

```

##### Return value

- Return： Financial firmware version. The value is a String.




### setSubDateTime

##### interface description

`setSubDateTime()` Set the secondary screen time to synchronize with the primary screen time.


##### interface method

```java

public void setSubDateTime() {

  try {

    mService.setSubDateTime();

  } catch (RemoteException e) {

    Log.e(TAG, "setSubDateTime() failed", e);

  }

}

```

### subReboot

##### interface description

`subReboot()` Operation secondary screen restart


##### interface method

```java

public void subReboot() {

  try {

    mService.subReboot();

  } catch (RemoteException e) {

    Log.e(TAG, "subReboot() failed", e);

  }

}

```

### setReceiveListener

##### interface description

`setReceiveListener()` Monitor the data sent by the secondary screen and receive the callback of the data


##### interface method

```java

public void setReceiveListener(IXcUsbListener listener) {

  try {

    mService.setReceiveListener(listener);

  } catch (RemoteException e) {

    Log.e(TAG, "setReceiveListener() failed", e);

  }

}

```

##### How to use the interface

```java

mXcUsbManager.setReceiveListener(new IXcUsbListener.Stub() {

  @Override

  public void onReceiveData(byte[] data) {

    //data ： Monitor the data sent by the secondary screen and receive the callback of the data


  }

});

```


### Secondary screen D08C1 XcUsbManager
Get the XcUsbManager instance

```java

XcUsbManager mXcUsbManager = (XcUsbManager) context.getSystemService(Context.XC_USB_SERVICE);

```
The above 'Context.XC_USB_SERVICE' is an error, but if the above jar package is imported properly, the compilation will work.




### sendData

##### interface description

`sendData(byte[] data)` Send data in byte[] format.

##### interface method

```java

public boolean sendData(byte[] data) {

  Log.d(TAG, "sendData()");

  try {

    return mService.sendData(data);

  } catch (RemoteException e) {

    Log.e(TAG, "sendData() failed", e);

    return false;

  }

}

```

##### parameter

- data : The data to be sent is of byte[] type.



### setReceiveListener

##### interface description

`setReceiveListener()` Listen to the data sent by the home screen and receive the callback of the data


##### interface method

```java

public void setReceiveListener(IXcUsbListener listener) {

  try {

    mService.setReceiveListener(listener);

  } catch (RemoteException e) {

    Log.e(TAG, "setReceiveListener() failed", e);

  }

}

```

##### How to use the interface

```java
mXcUsbManager.setReceiveListener(new IXcUsbListener.Stub() {

  @Override

  public void onReceiveData(byte[] data) {

    //data: indicates the received data. The value is byte[]


  }

});

```



## Notes 

- When calling the interface of 'main screen to secondary screen operation' such as: 'getSubFwVersion()' method, then the callback interface of receiving data 'onReceiveData(byte[] data)' Return data is' null ', both cannot be used at the same time.
- When the sendData() method is called in the transfer file, the interface of the primary screen to secondary screen operation cannot be called at the same time, such as the getSubFwVersion() method.

