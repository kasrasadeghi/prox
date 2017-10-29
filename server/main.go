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

		client.Write([]byte(msg.UserID + " says hello"))
	}
	client.Close()
}
