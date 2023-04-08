
compile-test:
	docker-compose -f "docker-compose.test.yml" build --no-cache

test: compile-test
	docker-compose -f "docker-compose.test.yml" run --rm it-test-runner bash -c "mvn test-compile failsafe:integration-test failsafe:verify"
