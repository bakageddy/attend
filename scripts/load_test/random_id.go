package main

import (
	"context"
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
	"net/url"
	"os"
	"os/signal"
	"sync/atomic"
)

const BASE string = "http://localhost:8080/app/api"

var url_templates []string = []string{
	"/batch/search?batchid=%d",
	"/batch/search?teacherid=%d",
	"/teacher/search?teacherid=%d",
	"/student/search?rollno=%d",
	"/subject/search?id=%d",
}

var counter atomic.Uint64

func worker(ctx context.Context) {
	for {
		select {
		case <- ctx.Done(): {
			return
		}
		default: {
			url := fmt.Sprintf(BASE + url_templates[rand.Intn(len(url_templates))], rand.Intn(100_000))
			response, err := http.Get(url)
			if err != nil {
				log.Fatalln(err.Error())
				continue
			}

			if bytes, err := io.ReadAll(response.Body); err != nil {
				log.Fatalln(err.Error())
			} else {
				log.Printf("REQUEST SUCESSFUL, RECEIVED #%d bytes\n", len(bytes))
				counter.Add(1)
			}

		}
		}
	}
}

func main() {
	ctx := context.Background()
	os_handler_ctx, _ := signal.NotifyContext(ctx, os.Interrupt)

	for i := 0; i < 4; i++ {
		go worker(os_handler_ctx)
	}

	<-os_handler_ctx.Done()
	fmt.Printf("Completed %d requests\n", counter.Load())
}
