import org.javalite.activejdbc.Model;

public class Article extends Model {
    static {
        validatePresenceOf("title", "summary", "author");
        validatePresenceOf("author").message("Author must be provided");
    }
}