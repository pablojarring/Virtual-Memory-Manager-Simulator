package vmmanager;

import vmsimulation.BackingStore;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

public class VirtualMemoryManagerV2 {

    private final PagedVirtualMemoryManagerSupport support;

    public VirtualMemoryManagerV2(MainMemory memory,
                                  BackingStore disk,
                                  Integer pageSize) throws MemoryException {
        this.support = new PagedVirtualMemoryManagerSupport(memory, disk, pageSize, ReplacementPolicy.FIFO, false);
    }

    public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {
        support.writeByte(fourByteBinaryString, value);
    }

    public Byte readByte(Integer fourByteBinaryString) throws MemoryException {
        return support.readByte(fourByteBinaryString);
    }

    public void printMemoryContent() throws MemoryException {
        support.printMemoryContent();
    }

    public void printDiskContent() throws MemoryException {
        support.printDiskContent();
    }

    public void writeBackAllPagesToDisk() throws MemoryException {
        support.writeBackAllPagesToDisk();
    }

    public int getPageFaultCount() {
        return support.getPageFaultCount();
    }

    public int getTransferedByteCount() {
        return support.getTransferedByteCount();
    }
}
