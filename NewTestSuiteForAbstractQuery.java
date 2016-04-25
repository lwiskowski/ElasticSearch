import org.apache.lucene.search.PrefixQuery;

protected static void assertPrefixQuery(Query query, String field, String value) {
    assertThat(query, instanceOf(PrefixQuery.class));
    PrefixQuery prefixQuery = (PrefixQuery) query;
    assertThat(prefixQuery.getPrefix().text(), equalTo(value));
    assertThat(prefixQuery.getPrefix().field(), equalTo(field));    
}
