package com.personalDrive.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import java.nio.file.Path;

@ConfigurationProperties(prefix = "app.storage")
public class StorageProps {
    private Path root;
    public Path getRoot() { return root; }
    public void setRoot(Path root) { this.root = root; }    
}
