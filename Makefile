all: clean
	mkdir bin
	cd src && javac -d ../bin c3/Main.java
	cp -r src/c3/images src/c3/text bin/c3/

run:
	cd bin &&	java c3.Main

clean:
	rm -rf bin