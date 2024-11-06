BINARY_NAME=api

build: 
	GOARCH=amd64 GOOS=linux go build -o ./target/$(BINARY_NAME)


run: build
	./bin/$(BINARY_NAME)

clean:
	rm ./bin/$(BINARY_NAME)
