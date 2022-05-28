# alien-hunter
A simple Java based game.

Execute the application from an IDE or from the command line after compilation.
If executing the application from a Linux operating system you may need to execute
via **sudo** from the command line. For example:

> sudo java com.pasquasoft.games.hunter.AlienHunter

This is necessary as the application will attempt to persist application preferences in a file under the root preference node for the system. Under a Linux OS this file will be located at **/etc/.java/.systemPrefs/prefs.xml**. Typically files located under the **/etc** folder hierarchy in Linux are not  _world_  writable.

# How to Play

Choose the **Game->Start** menu item to start the game. There are menu items under the **Game** menu to **Pause**, **Stop**, and **Resume** the game. The **Game->Game Options...** menu item will display the below dialog which allows the user to set game options for the number of aliens and time limit. The maximum number of aliens allowed is 99 and the time limit max is 59:59 (59 minutes 59 seconds).

![Game Options](https://user-images.githubusercontent.com/32653184/137032127-fe2ec26e-506e-4e06-9454-d5ec7c16a9e5.png)

Minimizing the main window will pause a started game. Deiconifying the main window will resume a paused game.

Use the mouse pointer to click on the flying aliens to destroy them. You win the game when all aliens are destroyed before the time limit expires.

The application's status bar displays the remaining time and aliens during game play.

# Screenshot

![Alien Hunter](https://user-images.githubusercontent.com/32653184/137034536-8c6270bf-0364-4063-aec3-3d3541a33ce3.png)

