#!/bin/sh

curl -XDELETE localhost:9200/test

curl -XPOST 'http://localhost:9200/grandchildren' -d '{
"mappings" : {
"parent" : {
"properties" : {
"parent-name" : {
"type" : "string",
"index" : "not_analyzed"
}
}
},
"child" : {
"_parent" : {
"type" : "parent"
},
"_routing" : {
"required" : true
},
"properties" : {
"parent-name" : {
"type" : "string",
"index" : "not_analyzed"
}
}
},
"grandchild" : {
"_parent" : {
"type" : "child"
},
"_routing" : {
"required" : true
},
"properties" : {
"grandchild-name" : {
"type" : "string",
"index" : "not_analyzed"
}
}
}
}
}'

curl -XPOST 'http://localhost:9200/grandchildren/parent/parent' -d '{ "parent-name" : "Parent" }'
curl -XPOST 'http://localhost:9200/grandchildren/child/child?parent=parent&routing=parent' -d '{ "child-name" : "Child" }'
curl -XPOST 'http://localhost:9200/grandchildren/grandchild/grandchild?parent=child&routing=parent' -d '{ "grandchild-name" : "Grandchild" }'

curl -XGET 'http://localhost:9200/grandchildren/_search?pretty' -d '{
"query" : {
"has_child" : {
"query" : {
"has_child" : {
"query" : {
"match_all" : {}
},
"child_type" : "grandchild",
"inner_hits" : {
"name" : "grandchild"
}
}
},
"child_type" : "child",
"inner_hits" : {
"name" : "child"
}
}
}
}'
