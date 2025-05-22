package main

import (
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
	"sync"
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

func main() {
	var i atomic.Uint64 = atomic.Uint64{}
	var checkpoint sync.WaitGroup
	i.Store(1)

	loop_var := i.Load()
	for loop_var < 10000 {
		go func(request_id int) {
			defer checkpoint.Done()
			id := rand.Intn(200)
			url := fmt.Sprintf(BASE+url_templates[id%len(url_templates)], id)
			resp, err := http.Get(url)
			if err != nil {
				log.Fatalf("FAILED TO SEND Request #%d: %s", request_id, err.Error())
				return
			}

			if resp.StatusCode == 200 {
				b, err := io.ReadAll(resp.Body)
				if err != nil {
					log.Fatalf("FAILED TO READ BODY: %s", err.Error())
					return
				}

				fmt.Println(string(b))
				log.Printf("Request #%d success!", request_id)
				return
			}
		}(int(i.Load()))
		checkpoint.Add(1)
		i.Add(1)
		loop_var = i.Load()
	}
	checkpoint.Wait()
}
