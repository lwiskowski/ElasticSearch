public void testParentChildHierarchy() throws Exception {
        assertAcked(prepareCreate("index1")
            .addMapping("level1")
            .addMapping("level2", "_parent", "type=level1")
            .addMapping("level3", "_parent", "type=level2")
            .addMapping("level4", "_parent", "type=level3")
        );

        client().prepareIndex("index1", "level1", "1").setSource("{}").get();
        client().prepareIndex("index1", "level2", "2").setParent("1").setRouting("1").setSource("{}").get();
        client().prepareIndex("index1", "level3", "3").setParent("2").setRouting("1").setSource("{}").get();
        client().prepareIndex("index1", "level4", "4").setParent("3").setRouting("1").setSource("{}").get();
        refresh();

        SearchResponse response = client().prepareSearch("index1")
            .setQuery(
                hasChildQuery("level2",
                    hasChildQuery("level3",
                        hasChildQuery("level4", matchAllQuery()).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                    ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
            )
            .get();
        assertHitCount(response, 1);
        assertThat(response.getHits().getAt(0).getId(), equalTo("1"));
        assertThat(response.getHits().getAt(0).getType(), equalTo("level1"));
        assertThat(response.getHits().getAt(0).getIndex(), equalTo("index1"));

        assertThat(response.getHits().getAt(0).getInnerHits().size(), equalTo(1));
        SearchHits innerHits = response.getHits().getAt(0).getInnerHits().get("level2");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getId(), equalTo("2"));

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level3");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level3"));
        assertThat(innerHits.getAt(0).getId(), equalTo("3"));

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level4");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level4"));
        assertThat(innerHits.getAt(0).getId(), equalTo("4"));
        assertThat(innerHits.getAt(0).getInnerHits(), nullValue());

        response = client().prepareSearch("index1")
            .setQuery(
                hasParentQuery("level3",
                    hasParentQuery("level2",
                        hasParentQuery("level1", matchAllQuery()).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                    ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
            )
            .get();
        assertHitCount(response, 1);
        assertThat(response.getHits().getAt(0).getId(), equalTo("4"));
        assertThat(response.getHits().getAt(0).getType(), equalTo("level4"));
        assertThat(response.getHits().getAt(0).getIndex(), equalTo("index1"));

        assertThat(response.getHits().getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = response.getHits().getAt(0).getInnerHits().get("level3");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level3"));
        assertThat(innerHits.getAt(0).getId(), equalTo("3"));

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level2");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getId(), equalTo("2"));

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level1");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("level1"));
        assertThat(innerHits.getAt(0).getId(), equalTo("1"));
    }

    public void testNestedHierarchy() throws Exception {
        XContentBuilder mapping  = jsonBuilder().startObject().startObject("type").startObject("properties")
                .startObject("level1")
                    .field("type", "nested")
                    .startObject("properties")
                        .startObject("level2")
                            .field("type", "nested")
                            .startObject("properties")
                                .startObject("level3")
                                    .field("type", "nested")
                                    .startObject("properties")
                                        .startObject("level4")
                                            .field("type", "nested")
                                        .endObject()
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject()
                .endObject()
            .endObject().endObject().endObject();
        assertAcked(prepareCreate("index")
            .addMapping("type", mapping)
        );

        XContentBuilder source = jsonBuilder().startObject()
            .startArray("level1")
                .startObject()
                    .field("field", "value1")
                    .startArray("level2")
                        .startObject()
                            .field("field", "value2")
                            .startArray("level3")
                                .startObject()
                                    .field("field", "value3")
                                    .startArray("level4")
                                        .startObject()
                                            .field("field", "value4")
                                        .endObject()
                                    .endArray()
                                .endObject()
                            .endArray()
                        .endObject()
                    .endArray()
                .endObject()
            .endArray()
            .endObject();
        client().prepareIndex("index", "type", "1").setSource(source).get();
        refresh();

        SearchResponse searchResponse = client().prepareSearch("index")
            .setQuery(
                nestedQuery("level1",
                    nestedQuery("level1.level2",
                        nestedQuery("level1.level2.level3",
                            nestedQuery("level1.level2.level3.level4", matchAllQuery()).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                        ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                    ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
            )
            .get();

        assertHitCount(searchResponse, 1);
        assertThat(searchResponse.getHits().getAt(0).getId(), equalTo("1"));
        assertThat(searchResponse.getHits().getAt(0).getType(), equalTo("type"));
        assertThat(searchResponse.getHits().getAt(0).getNestedIdentity(), nullValue());

        assertThat(searchResponse.getHits().getAt(0).getInnerHits().size(), equalTo(1));
        SearchHits innerHits = searchResponse.getHits().getAt(0).getInnerHits().get("level1");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("type"));
        assertThat(innerHits.getAt(0).getId(), equalTo("1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getField().string(), equalTo("level1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild(), nullValue());

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level1.level2");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("type"));
        assertThat(innerHits.getAt(0).getId(), equalTo("1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getField().string(), equalTo("level1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getField().string(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild(), nullValue());

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level1.level2.level3");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("type"));
        assertThat(innerHits.getAt(0).getId(), equalTo("1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getField().string(), equalTo("level1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getField().string(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getField().string(), equalTo("level3"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getChild(), nullValue());

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level1.level2.level3.level4");
        assertThat(innerHits, notNullValue());
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getType(), equalTo("type"));
        assertThat(innerHits.getAt(0).getId(), equalTo("1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getField().string(), equalTo("level1"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getField().string(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getField().string(), equalTo("level3"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getChild().getField().string(), equalTo("level4"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getChild().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild().getChild().getChild().getChild(), nullValue());
    }

    public void testParentChildAndNestedHierarchy() throws Exception {
        assertAcked(prepareCreate("index")
            .addMapping("level1")
            .addMapping("level2", "_parent", "type=level1", "level3", "type=nested")
        );

        client().prepareIndex("index", "level1", "1").setSource("{}").get();
        XContentBuilder source = jsonBuilder().startObject()
            .startArray("level3")
                .startObject()
                    .field("field", "value")
                .endObject()
            .endArray()
            .endObject();
        client().prepareIndex("index", "level2", "2").setParent("1").setSource(source).get();
        refresh();

        SearchResponse searchResponse = client().prepareSearch("index")
            .setQuery(
                hasChildQuery("level2",
                    nestedQuery("level3", matchAllQuery()).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
                ).innerHit(new QueryInnerHits(null, new InnerHitsBuilder.InnerHit()))
            )
            .get();
        assertHitCount(searchResponse, 1);
        assertThat(searchResponse.getHits().getAt(0).getId(), equalTo("1"));
        assertThat(searchResponse.getHits().getAt(0).getType(), equalTo("level1"));

        assertThat(searchResponse.getHits().getAt(0).getInnerHits().size(), equalTo(1));
        SearchHits innerHits = searchResponse.getHits().getAt(0).getInnerHits().get("level2");
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getId(), equalTo("2"));
        assertThat(innerHits.getAt(0).getType(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getNestedIdentity(), nullValue());

        assertThat(innerHits.getAt(0).getInnerHits().size(), equalTo(1));
        innerHits = innerHits.getAt(0).getInnerHits().get("level3");
        assertThat(innerHits.getTotalHits(), equalTo(1L));
        assertThat(innerHits.getAt(0).getId(), equalTo("2"));
        assertThat(innerHits.getAt(0).getType(), equalTo("level2"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getField().string(), equalTo("level3"));
        assertThat(innerHits.getAt(0).getNestedIdentity().getOffset(), equalTo(0));
        assertThat(innerHits.getAt(0).getNestedIdentity().getChild(), nullValue());
    }
