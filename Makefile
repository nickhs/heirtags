test: test_python test_java

test_python:
	cd py; \
	nosetests .

test_java:
	cd java; \
	./gradlew test
