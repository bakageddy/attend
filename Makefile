all:
	gradle build war
	./migrate.sh
