# Java-Project
Java work for Computer Science

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
