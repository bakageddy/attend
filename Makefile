all:
	@gradle clean build war
	@./migrate.sh

test:
	@gradle clean test
