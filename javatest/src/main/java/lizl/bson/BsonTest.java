package lizl.bson;

import lizl.BaseTest;
import org.bson.*;

import java.io.*;

/**
 * Created by cussyou on 17-1-9.
 */
public class BsonTest extends BaseTest{
    public void test() {
        String path = createTestFile();
        bsonDump(path);
    }
    // If using BsonDocumentWriter and BsonDocumentReader, you must override some methods.
    // See the source codes for more details.
    // Or you got nothing.
    public static String createTestFile(){
        String result = "test.bson";
        try {
            FileOutputStream fout = new FileOutputStream(result);
            BSONEncoder encoder = new BasicBSONEncoder();
            // start set contents
            for(int i=0;i<100;i++){
                BSONObject obj = new BasicBSONObject();
                obj.put("value", Math.random());
                byte[] data = encoder.encode(obj);
                fout.write(data);
                fout.flush();
            }
            // end
            fout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void bsonDump(String filename) {
        File file = new File(filename);
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        BSONDecoder decoder = new BasicBSONDecoder();
        int count = 0;
        try {
            while (inputStream.available() > 0) {

                BSONObject obj = decoder.readObject(inputStream);
                if(obj == null){
                    break;
                }
                System.out.println(obj);
                count++;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
            }
        }
        System.err.println(String.format("%s objects read", count));
    }
}
