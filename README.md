# Henry

Henry is a chatbot built as a greenfield Java project. Given below are instructions on how to use it.

## Setting up in Intellij

Prerequisites: JDK 25, update Intellij to the most recent version.

1. Open Intellij (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into Intellij as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 25** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate `src/main/java/henry/gui/Launcher.java`, right-click it, and choose
   `Run Launcher.main()`. Henry's chat window should open. If the code editor is showing compile
   errors, try restarting the IDE.

You can also start the GUI from the project root with:

```shell
./gradlew run
```

On Windows, use `gradlew.bat run` instead.

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Creating and running a fat JAR

A fat JAR contains Henry's compiled classes together with its runtime dependencies, so it can be run without separately adding those dependencies to the classpath.

From the project root, create the fat JAR with:

```shell
./gradlew shadowJar
```

On Windows, use `gradlew.bat shadowJar` instead. The generated JAR is located at `build/libs/henry.jar`.

Run it from the project root with Java 25:

```shell
java -jar build/libs/henry.jar
```
