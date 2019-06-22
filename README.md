# Home control

* author: d-    kozak
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

Dependencies between modules can be seen on the following diagram.
![alt text](./imgs/main-deps.png)

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
This module simulates sensor data. It communicates with the server through a socket at localhost:3000.
In the beginning it sends information about available sensors to the server and then it regularly generates random new sensor data.
Also it keeps track of current sensor values and updates it when it receives notifications from the server.
It's class diagram is the following.
![alt text](./imgs/gateway-classes.png)

### Server
The main functions of this component are the following.
 
* handle data coming from the gateway and persist them in the firebase database
* trigger user rules if necessary
* propagate requests for sensor changes from the client to the gateway
* call your mum when you will be late for dinner

When started, it waits for the Gateway to connect to it. Then it retrieves information about current sensors
and saves then in the the firebase database. 

Then it listens for sensor updates from the gateway and propagates them to the database.
It also setups listeners for sensor change requests from the client 
and propagates them to the backend.

The complete flow of data can be seen on the following diagram
![alt text](./imgs/dataflow.png)

### Sensors
This module contains domain classes used in the backend and also shared processing logic such as parsing
and serializing sensor data.
![alt text](./imgs/sensors-classes.png)

### Client 

