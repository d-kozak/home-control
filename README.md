# Home control

* author: d-dkozak
* mail: dkozak94@gmail.com

Home control is a IOT solution that allows users to monitor and change the values of sensors in their home. 
The whole system consists of four modules.

* Gateway
    * Simulates sensor data
* Server
    * Communicates with gateway and performs various data processing
* Client
    * Android app that can be used to monitor and change the values of the sensors
* Sensors
    * Contains common code shared between the server and the gateway. 

## Requirements
The client app requires Android version _7_ or higher.

The backend components require Java version 12 with preview features enabled. Gradle is used to build the backend components,
so running the following commands builds both of them.
```
gradle jar
```
For example, the command to start the server is.
```
java  --enable-preview  -jar server/build/libs/server.jar
```

For editing the client, [Android Studio](https://developer.android.com/studio/?gclid=Cj0KCQjw9JzoBRDjARIsAGcdIDVKioluWo98udXZtLYEUFwdkUPb_eNCMVdCwZJFjLHwzXrjwyaDGioaAibbEALw_wcB)
is recommended and for editing the backend, you are encouraged to use [IntelliJ IDEA](https://www.jetbrains.com/idea/specials/idea/ultimate.html?gclid=Cj0KCQjwo7foBRD8ARIsAHTy2wnI0oTQIOPsaqz6WNrpCz_LZDErrCRMYsIeAC3FQF1t699ipOkOpcwaAui9EALw_wcB),
even though other IDEs should work as well as long as they support Gradle projects. 

## Structure
In this section, the design and implementation of the four components will be discussed.

### Gateway

### Server

### Client 

### Sensors