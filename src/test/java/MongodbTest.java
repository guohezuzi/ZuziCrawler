import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;

import java.util.Collections;

/**
 * \* Created with IntelliJ IDEA.
 * \* @author: guohezuzi
 * \* Date: 2019-02-11
 * \* Time: 下午1:38
 * \* Description:mogodb连接数据库测试
 * \
 */

public class MongodbTest {
    public static void main(String args[]) {
        final String user = "crawler"; // the user name
        final String dbName = "crawler"; // the name of the database in which the user is defined
        final char[] password = "crawler".toCharArray(); // the password as a character array
        try {
            MongoCredential credential = MongoCredential.createCredential(user, dbName, password);

            MongoClient mongoClient = new MongoClient(new ServerAddress("localhost", 27017),
                    Collections.singletonList(credential));
            MongoDatabase database = mongoClient.getDatabase(dbName);

            database.createCollection("cappedCollection",
                    new CreateCollectionOptions().capped(true).sizeInBytes(0x100000));

            for (String name : database.listCollectionNames()) {
                System.out.println(name);
            }

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
