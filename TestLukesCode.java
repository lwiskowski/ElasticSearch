import com.fasterxml.jackson.core.JsonParser;
import org.elasticsearch.common.bytes.BytesArray;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.test.ESTestCase;


public class TestLukesCode {

 
  public static void testValueType() {
    public final JsonParser parser = new JsonParser();
    assertThat(parser.longValue(false), equalTo(parser.doFloatValue()));
    assertThat(parser.intValue(false), equalTo(parser.doIntValue()));
    assertThat(parser.doubleValue(false), equalTo(parser.doDoubleValue()));
    assertThat(parser.shortValue(false), equalTo(parser.doShortValue()));
  }

}
