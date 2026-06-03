package raio.upload.file;

import java.io.InputStream;

public interface FileStorage {
    String store(InputStream inputStream, String originalFileName, String contentType);
}
