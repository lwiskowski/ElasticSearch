public void testToQueryPrefixQuery() throws Exception {
        assumeTrue("test runs only when at least a type is registered", getCurrentTypes().length > 0);
        for (Operator op : Operator.values()) {
            Query query = queryStringQuery("foo-bar-foobar*")
                .defaultField(STRING_FIELD_NAME)
                .analyzeWildcard(true)
                .analyzer("standard")
                .defaultOperator(op)
                .toQuery(createShardContext());
            assertThat(query, instanceOf(BooleanQuery.class));
            BooleanQuery bq = (BooleanQuery) query;
            assertThat(bq.clauses().size(), equalTo(3));
            String[] expectedTerms = new String[]{"foo", "bar", "foobar"};
            for (int i = 0; i < bq.clauses().size(); i++) {
                BooleanClause clause = bq.clauses().get(i);
                if (i != bq.clauses().size() - 1) {
                    assertTermQuery(clause.getQuery(), STRING_FIELD_NAME, expectedTerms[i]);
                } else {
                    assertPrefixQuery(clause.getQuery(), STRING_FIELD_NAME, expectedTerms[i]);
                }
                if (op == Operator.AND) {
                    assertThat(clause.getOccur(), equalTo(BooleanClause.Occur.MUST));
                } else {
                    assertThat(clause.getOccur(), equalTo(BooleanClause.Occur.SHOULD));
                }
            }
        }
    }
