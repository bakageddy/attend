all:
	gradle clean build war
	./migrate.sh
