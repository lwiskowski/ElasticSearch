#!/bin/sh

curl -XDELETE localhost:9200/test

curl -XPUT 'http://localhost:9200/test/doc/1' -d '{
  "message" : "apples oranges"
}'

curl -XPUT 'http://localhost:9200/test/doc/2' -d '{
  "message" : "apples"
}'

curl -XPUT 'http://localhost:9200/test/doc/3' -d '{
  "message" : "oranges"
}'

curl -XPOST 'http://localhost:9200/test/_refresh'

curl -XGET 'localhost:9200/test/doc/_search?pretty=true' -d '{
  "query": {
    "query_string": {
      "query": "apples-oranges*",
      "default_operator": "and",
      "analyze_wildcard": true
    }
  }
}'
