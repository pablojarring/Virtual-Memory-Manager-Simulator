package vmmanager;

import vmsimulation.BackingStore;
import vmsimulation.BitwiseToolbox;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

import java.util.ArrayDeque;
import java.util.Deque;

final class PagedVirtualMemoryManagerSupport {

    private final MainMemory memory;
    private final BackingStore disk;
    private final int pageSize;
    private final int ramAddressBits;
    private final int diskAddressBits;
    private final int offsetBits;
    private final int numPages;
    private final int numFrames;
    private final ReplacementPolicy replacementPolicy;
    private final boolean useDirtyBit;
    private final PageTableEntry[] pageTable;
    private final Deque<Integer> fifoOrder;

    private int nextFreeFrame;
    private int pageFaultCount;
    private int transferedByteCount;
    private long accessClock;

    PagedVirtualMemoryManagerSupport(MainMemory memory,
                                     BackingStore disk,
                                     Integer pageSize,
                                     ReplacementPolicy replacementPolicy,
                                     boolean useDirtyBit) throws MemoryException {
        this.memory = memory;
        this.disk = disk;
        this.pageSize = pageSize;
        this.replacementPolicy = replacementPolicy;
        this.useDirtyBit = useDirtyBit;
        this.ramAddressBits = log2(memory.size());
        this.diskAddressBits = log2(disk.size());
        this.offsetBits = log2(pageSize);
        this.numPages = disk.size() / pageSize;
        this.numFrames = memory.size() / pageSize;
        this.pageTable = new PageTableEntry[numPages];
        this.fifoOrder = replacementPolicy == ReplacementPolicy.FIFO ? new ArrayDeque<>() : null;

        for (int page = 0; page < numPages; page++) {
            this.pageTable[page] = new PageTableEntry();
        }
    }

    private int log2(int value) {
        return (int) (Math.log(value) / Math.log(2));
    }

    public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {
        Translation translation = translate(fourByteBinaryString);
        memory.writeByte(translation.physicalAddress, value);
        if (useDirtyBit) {
            pageTable[translation.pageNumber].dirty = true;
        }
        System.out.println("RAM: @" + BitwiseToolbox.getBitString(translation.physicalAddress, ramAddressBits - 1) + " <-- " + value);
    }

    public Byte readByte(Integer fourByteBinaryString) throws MemoryException {
        Translation translation = translate(fourByteBinaryString);
        byte value = memory.readByte(translation.physicalAddress);
        System.out.println("RAM: @" + BitwiseToolbox.getBitString(translation.physicalAddress, ramAddressBits - 1) + " --> " + value);
        return value;
    }

    public void printMemoryContent() throws MemoryException {
        for (int address = 0; address < memory.size(); address++) {
            System.out.println(BitwiseToolbox.getBitString(address, ramAddressBits - 1) + ": " + memory.readByte(address));
        }
    }

    public void printDiskContent() throws MemoryException {
        for (int page = 0; page < numPages; page++) {
            byte[] diskPage = disk.readPage(page);
            StringBuilder builder = new StringBuilder();
            builder.append("PAGE ").append(page).append(": ");
            for (int offset = 0; offset < diskPage.length; offset++) {
                if (offset > 0) {
                    builder.append(",");
                }
                builder.append(diskPage[offset]);
            }
            System.out.println(builder);
        }
    }

    public void writeBackAllPagesToDisk() throws MemoryException {
        for (int page = 0; page < numPages; page++) {
            PageTableEntry entry = pageTable[page];
            if (!entry.loaded) {
                continue;
            }
            if (useDirtyBit && !entry.dirty) {
                continue;
            }
            writeFrameToDisk(page, entry.frameNumber);
            entry.dirty = false;
        }
    }

    public int getPageFaultCount() {
        return pageFaultCount;
    }

    public int getTransferedByteCount() {
        return transferedByteCount;
    }

    private Translation translate(int virtualAddress) throws MemoryException {
        int offset = BitwiseToolbox.extractBits(virtualAddress, 0, offsetBits - 1);
        int pageNumber = BitwiseToolbox.extractBits(virtualAddress, offsetBits, diskAddressBits - 1);
        PageTableEntry entry = pageTable[pageNumber];

        if (entry.loaded) {
            System.out.println("Page " + pageNumber + " is in memory");
        } else {
            handlePageFault(pageNumber);
            entry = pageTable[pageNumber];
        }

        accessClock++;
        entry.lastAccessTime = accessClock;
        return new Translation(pageNumber, entry.frameNumber * pageSize + offset);
    }

    private void handlePageFault(int pageNumber) throws MemoryException {
        pageFaultCount++;
        int frameNumber;

        if (nextFreeFrame < numFrames) {
            frameNumber = nextFreeFrame;
            nextFreeFrame++;
        } else {
            int victimPage = selectVictimPage();
            frameNumber = evictPage(victimPage);
        }

        loadPageIntoFrame(pageNumber, frameNumber);
    }

    private int selectVictimPage() {
        if (replacementPolicy == ReplacementPolicy.LRU) {
            long oldestAccess = Long.MAX_VALUE;
            int victimPage = -1;
            for (int page = 0; page < numPages; page++) {
                PageTableEntry entry = pageTable[page];
                if (entry.loaded && entry.lastAccessTime < oldestAccess) {
                    oldestAccess = entry.lastAccessTime;
                    victimPage = page;
                }
            }
            return victimPage;
        }

        if (replacementPolicy == ReplacementPolicy.FIFO) {
            return fifoOrder.removeFirst();
        }

        throw new IllegalStateException("No replacement policy is available for this version");
    }

    private int evictPage(int pageNumber) throws MemoryException {
        PageTableEntry entry = pageTable[pageNumber];
        if (useDirtyBit && !entry.dirty) {
            System.out.println("Evicting page " + pageNumber + " (NOT DIRTY)");
        } else {
            System.out.println("Evicting page " + pageNumber);
        }

        if (!useDirtyBit || entry.dirty) {
            writeFrameToDisk(pageNumber, entry.frameNumber);
        }

        int frameNumber = entry.frameNumber;
        entry.loaded = false;
        entry.frameNumber = -1;
        entry.dirty = false;
        entry.lastAccessTime = -1L;
        return frameNumber;
    }

    private void loadPageIntoFrame(int pageNumber, int frameNumber) throws MemoryException {
        System.out.println("Bringing page " + pageNumber + " into frame " + frameNumber);
        byte[] pageBytes = disk.readPage(pageNumber);
        int baseAddress = frameNumber * pageSize;
        for (int index = 0; index < pageBytes.length; index++) {
            memory.writeByte(baseAddress + index, pageBytes[index]);
        }
        transferedByteCount += pageSize;

        PageTableEntry entry = pageTable[pageNumber];
        entry.loaded = true;
        entry.frameNumber = frameNumber;
        entry.dirty = false;
        entry.lastAccessTime = -1L;

        if (replacementPolicy == ReplacementPolicy.FIFO) {
            fifoOrder.addLast(pageNumber);
        }
    }

    private void writeFrameToDisk(int pageNumber, int frameNumber) throws MemoryException {
        byte[] pageBytes = new byte[pageSize];
        int baseAddress = frameNumber * pageSize;
        for (int index = 0; index < pageSize; index++) {
            pageBytes[index] = memory.readByte(baseAddress + index);
        }
        disk.writePage(pageNumber, pageBytes);
        transferedByteCount += pageSize;
    }

    private static final class Translation {
        private final int pageNumber;
        private final int physicalAddress;

        private Translation(int pageNumber, int physicalAddress) {
            this.pageNumber = pageNumber;
            this.physicalAddress = physicalAddress;
        }
    }
}
