all:
	@gradle clean build war
	@./migrate.sh

unit:
	@gradle clean test

load_id_test:
	go run ./scripts/load_test/random_id.go


load_pattern_test:
	go run ./scripts/load_test/random_pattern.go
