all:			FileHandlingClient.class FileHandlingServer.class \
			FileHandlingInterface.class \
			FileHandlingClientThread.class

FileHandlingInterface.class:	FileHandlingInterface.java
			@javac FileHandlingInterface.java

FileHandlingClient.class:	FileHandlingClient.java
			@javac FileHandlingClient.java

FileHandlingServer.class:	FileHandlingServer.java
			@javac FileHandlingServer.java

FileHandlingClientThread.class:	FileHandlingClientThread.java
			@javac FileHandlingClientThread.java				

run:			all
			@java FileHandlingServer &
			@sleep 1
			@java FileHandlingClient

clean:
			@rm -f *.class *~

