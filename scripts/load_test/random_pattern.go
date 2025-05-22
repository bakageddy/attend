package main

import (
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
)

const API_BASE string = "http://localhost:8080/app/api"
const LOWER string = "abcdefghijklmnopqrstuvwxyz"
const UPPER string = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

var TEMPLATES []string = []string{
	"/batch/search?pattern=%s",
	"/teacher/search?pattern=%s",
	"/student/search?pattern=%s",
	"/subject/search?pattern=%s",
	"/subject/search?code=%s",
}

func request_work(input chan(string)) {
	for request := range input {
		url := fmt.Sprintf(API_BASE + TEMPLATES[rand.Intn(len(TEMPLATES))], request)
		resp, err := http.Get(url)
		if err != nil {
			log.Println(err.Error())
		}

		if (resp.StatusCode == http.StatusNoContent) {
			continue
		}

		if (resp.StatusCode == http.StatusOK) {
			log.Println("Request Successful")
		}

		if body, err := io.ReadAll(resp.Body); err != nil {
			log.Println(err.Error())
		} else {
			fmt.Printf("REQUEST: %s RESPONSE: %s\n", request, string(body))
		}


	}
}

func rand_string(length int) string {
	buffer := make([]byte, length)
	buffer[0] = UPPER[rand.Intn(len(UPPER))]
	for i := 1; i < length; i++ {
		buffer[i] = LOWER[rand.Intn(len(LOWER))]
	}
	return string(buffer)
}

func main() {
	input := make(chan(string), 20)
	for i := 0; i < 2; i++ {
		go request_work(input)
	}

	// Random Strings of length, 1 to 3
	for i := 1; i < 100; i++ {
		input <- rand_string(rand.Intn(3) + 1)
	}

	close(input)
}
