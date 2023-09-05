# How To start emulating

## Port Forwarding

We have to handle all incoming tcp connections to our host and pass them through the emulated system by portforwarding

1. Start emulator via Pepper SDK and remember the Number in the new emulator window.
   - It will look like this:  
     - Android Emulator - \<build\>:**5575**
   - This will be the port of the emulator 
2. Open terminal on pc
3. ``telnet localhost <emulator build number>``
4. Enter the needed authentication token wit ``auth <auth_token>``
   - Your terminal will tell you where to find it
   - Should be in: ``/home/<user>/.emulator_console_auth_token``
   - Type ``cat /home/<user>/.emulator_console_auth_token`` in another terminal to show your key
5. ``redir add tcp:8080:8080``  
   - The first 8080 is the port we will visit from our host  
   - The second 8080 is the port we are emulating in the emulator
6. Now you can run the app in Android Studio 
   - Be sure that you have selected the right Emulator to run in and have selected **app** to be run
7. Visit http://localhost:8080

## Extra info

- use ```redir list``` to find all your port forwardings
- use ```redir del tcp:<host-port>``` to delete a specific port forwarding