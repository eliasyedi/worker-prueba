package main

import (
	"log"

	"github.com/confluentinc/confluent-kafka-go/v2/kafka"
)

func NewKafkaProducer() *kafka.Producer {

	//this should go up
	brokers := "localhost:9092" // Replace with your Kafka brokers
	//topic := "example-topic"    // The topic to which we will send messages
	// Set up the Kafka producer configuration
	config := &kafka.ConfigMap{
		"bootstrap.servers": brokers,
	}
	producer, err := kafka.NewProducer(config)
	if err != nil {
		log.Fatalf("failed to create producer: %v", err)
	}
	defer producer.Close()
    return producer

}
