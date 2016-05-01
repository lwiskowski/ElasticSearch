#!/bin/sh

curl localhost:9200/_cat/indices
curl http://localhost:9200/_cat/indices/\*_results-\*
