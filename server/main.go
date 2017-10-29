package main

import (
	"fmt"
	"net"
	"strconv"
	"encoding/json"
)

const PORT = 5000

func main() {
	server, err := net.Listen("tcp", ":"+strconv.Itoa(PORT))
	if server == nil {
		panic("couldn't start listening: " + err.Error())
	}
	conns := clientConns(server)
	go requestHandler()
	for {
		go handleConn(<-conns)
	}
}

func clientConns(listener net.Listener) chan net.Conn {
	ch := make(chan net.Conn)
	i := 0
	go func() {
		for {
			client, err := listener.Accept()
			if client == nil {
				fmt.Printf("couldn't accept: " + err.Error())
				continue
			}
			i++
			fmt.Printf("%d: %v <-> %v\n", i, client.LocalAddr(), client.RemoteAddr())
			ch <- client
		}
	}()
	return ch
}

type UserRequest struct {
	UserID   string `json:"userID"`
	TargetID string `json:"targetID"`
	Data     string `json:"data"`
}

var requestChannel chan UserRequest
var responseChannel chan []string

//TODO: clearing
//TODO: the response channel might mix up two requests.
// maybe put a channel in the UserRequest struct and send back the response in that channel
//TODO: the UserID may not be in the map, so we might return nil or some error case. we need to check for that

func requestHandler() {
	messages := make(map[string][]string)

	for {
		req := <-requestChannel
		messages[req.TargetID] = append(messages[req.TargetID], req.Data)
		result := messages[req.UserID]
		responseChannel<-result
	}
}

/* JSON format

{
  "userID":   "..." ,
  "targetID": "..." ,
  "data":     "..." ,
}

 */
func handleConn(client net.Conn) {
	d := json.NewDecoder(client)

	for {
		var msg UserRequest
		err := d.Decode(&msg)
		if err != nil {
			fmt.Println(err.Error())
		}

		requestChannel<-msg

		response := <-responseChannel

		bytes, err := json.Marshal(response)
		if err != nil {
			client.Write([]byte("json marshalling failed\n"))
		} else {
			client.Write(bytes)
		}
	}
	client.Close()
}
