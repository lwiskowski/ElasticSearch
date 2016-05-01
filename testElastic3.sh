#!/bin/sh

curl -XDELETE localhost:9200/test

curl -XGET 'localhost:9200/idx/type/5359b8d10cf2f2ff4ec71ed7/_percolate' -d '{
  "filter": {
    "or": {
      "filters": [
        {}
      ]
    }
  }
}'
