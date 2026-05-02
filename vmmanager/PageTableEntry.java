package vmmanager;

final class PageTableEntry {
    boolean loaded;
    int frameNumber = -1;
    boolean dirty;
    long lastAccessTime = -1L;
}
