# Java Project
## _Java project for Computer Science_
Hi! This is a game that can either be plaeyd by two people online or two people playing on one computer. The goal of the game is to hit the other person off the tiles more than the other player does to you. You can charge up a shot to do more knockback. Tiles will sometimes disappear, but are reset once one player dies. Sometimes a power up appears, which shoots 3 bullets at once.
Visit at: https://augustkoskinen.github.io/Java-Project.

##_HTML Commands_
Command to run html debug:
```
./gradlew html:superDev
```

Command to build html:
```
./gradlew html:dist
```

Command to run Node JS server within Node-JS directory:
```
node .
```

##_Desktop Commands_
Command to run desktop debug (does not work currently):
```
./gradlew desktop:run
```
Command to build from a jar to dmg
```
jpackage --input . --main-jar desktop/build/libs/desktop-1.0.jar --main-class com.javagame.game.DesktopLauncher --name Java-Project --icon gradle/icons/icon.icns
```

Command to build into a jar
```
./gradlew desktop:dist
```

Command to test
```
./gradlew desktop:run
```
