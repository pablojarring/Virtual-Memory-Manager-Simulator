package vmmanager;

import vmsimulation.BackingStore;
import vmsimulation.BitwiseToolbox;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

import java.math.BigInteger;
import java.util.ArrayList;

public class VirtualMemoryManagerV1 {


    MainMemory memory;    // The main memory
    BackingStore disk;    // The disk
    Integer pageSize;    // Page size

    int numBitsToAddress;
    // MORE INSTANCE VARIABLE NEEDED, MOST LIKELY

    // log2(): Convenient function to compute the log2 of an integer;
    private int log2(int x) {
        return (int) (Math.log(x) / Math.log(2));
    }

    // Constructor
    public VirtualMemoryManagerV1(MainMemory memory,
                                  BackingStore disk,
                                  Integer pageSize) throws MemoryException {
        this.memory = memory;
        this.disk = disk;
        this.pageSize = pageSize;



        // TO AUGMENT, MOST LIKELY
    }

    // Method to write a byte to memory given a virtual address
    public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {


    }


    // Method to read a byte to memory given a virtual address
    public Byte readByte(Integer fourByteBinaryString) throws MemoryException {

        int address = BitwiseToolbox.extractBits(fourByteBinaryString, 0, numBitsToAddress - 1);
        byte valInAddr = memory.readByte(address);


        return valInAddr; // MUST RETURN THE VALUE THAT WAS READ INSTEAD OF JUST ZERO
    }

    private MemoryState managePageTable(int pageNumber) {

        return null;

    }

    // Method to print all memory content
    public void printMemoryContent() throws MemoryException {

    }

    // Method to print all disk content
    public void printDiskContent() throws MemoryException {


    }

    // Method to write back all pages to disk
    public void writeBackAllPagesToDisk() throws MemoryException {

    }

    // Method to retrieve the page fault count
    public int getPageFaultCount() {

        // TO IMPLEMENT
        return 0; // MUST RETURN THE NUMBER OF PAGE FAULTS INSTEAD OF JUST ZERO
    }

    // Method to retrieve the number of bytes transfered between RAM and disk
    public int getTransferedByteCount() {

        // TO IMPLEMENT
        return 0; // MUST RETURN THE NUMBER OF BYTES TRANSFERRED INSTEAD OF JUST ZERO
    }


}

class MemoryState {
    private boolean pageLoaded = true;
    private Integer frameNum;
    private int displacement;

    public boolean getLoadedState() {
        return pageLoaded;
    }

    public void setLoadedState(boolean loaded) {
        pageLoaded = loaded;
    }

    public void setFrameNum(Integer frameNum) {
        this.frameNum = frameNum;
    }

    public Integer getFrameNum() {
        return frameNum;
    }

    public void clearState() {
        pageLoaded = true;
    }

    public void setDisplacement(int disp) {
        displacement = disp;
    }

    public int getDisplacement() {
        return displacement;
    }

}
