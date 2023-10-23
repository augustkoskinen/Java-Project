# Java-Project
Java work for Computer Science

Command to build from a jar to dmg
```
jpackage --input ./ --name Java-Game --main-jar desktop/build/libs/desktop-1.0.jar --main-class com.javagame.game.DesktopLauncher --type dmg --icon "gradle/icons/icon.icns" --app-version "1.0.0" --mac-package-name "Java Game"
```

Command to build into a jar
```
./gradlew desktop:dist
```

Command to build jar
```
java -XstartOnFirstThread -jar desktop/build/libs/desktop-1.0.jar
```

Command to test
```
./gradlew desktop:run
```
