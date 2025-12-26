package storage;

import java.io.FileNotFoundException;
import java.io.IOException;

public interface StorageManager  {

    void load(Storable s, String filePath)throws UnMarshalingException, IOException;
    void save(Storable s, String filePath, boolean append) throws Exception;

}
