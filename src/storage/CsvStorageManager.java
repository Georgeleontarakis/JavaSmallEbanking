package storage;

import java.io.*;

public class CsvStorageManager implements StorageManager {

    private static CsvStorageManager instance;
    private String storagePath;

    private CsvStorageManager() {
        this.storagePath = "./";
    }

    public static CsvStorageManager getInstance() {
        if (instance == null) {
            instance = new CsvStorageManager();
        }
        return instance;
    }

    @Override
    public void load(Storable storable, String fileName) throws UnMarshalingException, IOException{
        String filePath = storagePath + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File " + filePath + " does not exist");
        }
        if (!file.canRead() || !file.isFile()) {
            throw new IOException("File " + filePath + " cannot be read");
        }
        BufferedReader fileBufferReader = null;
        try {
            fileBufferReader = new BufferedReader(new FileReader(file));
            String line;
            StringBuffer sb = new StringBuffer("");
            while ((line = fileBufferReader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            storable.unmarshal(sb.toString());
        } finally {
            // FIXED: Better error handling when closing reader
            if (fileBufferReader != null) {
                try {
                    fileBufferReader.close();
                } catch (IOException e) {
                    System.err.println("Warning: Error closing file reader: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void save(Storable storable, String fileName, boolean append) throws IOException {
        String filePath = storagePath + fileName;
        File file = new File(filePath);
        
        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        if (!file.exists()) {
            file.createNewFile();
        }

        try (FileWriter writer = new FileWriter(file, append)) {
            writer.write(storable.marshal());
        }
    }
}