test: test_python test_java test_js

test_python:
	cd py; \
	nosetests .

test_java:
	cd java; \
	./gradlew cleanTest test

test_js:
	cd js; \
	yarn test
