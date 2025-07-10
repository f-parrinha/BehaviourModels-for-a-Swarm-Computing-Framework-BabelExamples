#!/bin/bash

jar_name="BitVote-1.0-SNAPSHOT-jar-with-dependencies.jar"
port=9000
peers="localhost:${port}"
peers_count=3

populate_peers() {
  i=1
  while [ $i -lt $peers_count ]; do
      peers="${peers},localhost:$((port + $i))"
      i=$(($i + 1))
  done
}


# Starts and verifies the execution of one node
start_node() {
  local port=$1
  local jar_path="target/$jar_name"
  if [[ ! -f "$jar_path" ]]; then
      echo "Error: JAR file '$jar_path' not found!"
      return 1  # Exit the function with an error
    fi

  # Start node
  java -jar $jar_path port="$port" peers="$peers" &
  local pid=$!
  sleep 1

  # Validate if it started
  if kill -0 $pid 2>/dev/null; then
    echo "Started node with port $port (PID: $pid)"
    echo
  else
    echo "Failed to start node with port $port"
    echo
  fi
}

kill_nodes() {
  for pid in $(ps aux | grep "$jar_name" | grep -v "grep" | awk '{print $2}'); do
    kill "$pid"
  done
  echo "Done!"
}


# Trap SIGINT to call kill_nodes before exiting
trap 'kill_nodes; exit' SIGINT

# Compile and run
echo "Compiling.."
echo
if mvn clean package; then
  echo "Compilation successful"
  echo

  populate_peers
  echo "Using peers list: $peers"

  i=0
  while [ $i -lt $peers_count ]; do
    start_node $((port + i))
    i=$((i + 1))
  done

  read -r
  kill_nodes
else
  echo "Maven compilation failed"
  exit 1
fi