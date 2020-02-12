test: src/portable_text/*.clj test/portable_text/*.clj
	./bin/kaocha

autotest: src/portable_text/*.clj test/portable_text/*.clj
	./bin/kaocha --watch

target:
	mkdir target

target/portable-text.jar: src/portable_text/*.* deps.edn target
	clojure -A:jar

jar: target/portable-text.jar

clean:
	rm -fr target

deploy: target/portable-text.jar
	mvn deploy:deploy-file -Dfile=portable-text.jar -DrepositoryId=clojars -Durl=https://clojars.org/repo -DpomFile=pom.xml

.PHONY: test autotest deploy clean jar
