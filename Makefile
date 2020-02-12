test: src/portable_text/*.clj test/portable_text/*.clj
	./bin/kaocha

autotest: src/portable_text/*.clj test/portable_text/*.clj
	./bin/kaocha --watch

.PHONY: test
