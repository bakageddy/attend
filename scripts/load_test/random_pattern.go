package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
	"os"
	"os/signal"
	"sync/atomic"
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

var sucessful_request atomic.Int32
var normal_request atomic.Int32

func rand_string(length int) string {
	buffer := make([]byte, length)
	buffer[0] = UPPER[rand.Intn(len(UPPER))]
	for i := 1; i < length; i++ {
		buffer[i] = LOWER[rand.Intn(len(LOWER))]
	}
	return string(buffer)
}

func worker_pattern(ctx context.Context) {
	for {
		select {
		case <-ctx.Done(): {
			return
		}
		default: {
			param := rand_string(rand.Intn(4) + 2)

			url := fmt.Sprintf(
				API_BASE+TEMPLATES[rand.Intn(len(TEMPLATES))],
				param,
			)

			req, err := http.NewRequestWithContext(ctx, http.MethodGet, url, nil)
			if err != nil {
				log.Println(err.Error())
			}

			resp, err := http.DefaultClient.Do(req)
			if err != nil {
				log.Println(err.Error())
			}

			normal_request.Add(1)
			if resp.StatusCode == http.StatusNoContent {
				fmt.Printf("Pattern: %s not found\n", param)
				continue
			}

			if resp.StatusCode == http.StatusOK {
				sucessful_request.Add(1)
				log.Println("Request Successful")
			}

			if body, err := io.ReadAll(resp.Body); err != nil {
				log.Println(err.Error())
			} else {
				fmt.Printf("Got a result of length: %d\n", len(body))
			}

		}
		}
	}
}


func main() {
	interrupt_handler_ctx, _ := signal.NotifyContext(context.Background(), os.Interrupt)

	for i := 0; i < 4; i++ {
		go worker_pattern(interrupt_handler_ctx)
	}


	<-interrupt_handler_ctx.Done()

	fmt.Printf(
		"Completed %d valid requests with a total of %d requests\n",
		sucessful_request.Load(),
		normal_request.Load(),
	)

}
