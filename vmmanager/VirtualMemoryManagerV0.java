package vmmanager;

import vmsimulation.BitwiseToolbox;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

public class VirtualMemoryManagerV0 {

    private final MainMemory memory;
    private final int addressBits;

    public VirtualMemoryManagerV0(MainMemory memory) throws MemoryException {
        this.memory = memory;
        this.addressBits = log2(memory.size());
    }

    private int log2(int value) {
        return (int) (Math.log(value) / Math.log(2));
    }

    public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {
        int physicalAddress = BitwiseToolbox.extractBits(fourByteBinaryString, 0, addressBits - 1);
        memory.writeByte(physicalAddress, value);
        System.out.println("RAM write: @" + BitwiseToolbox.getBitString(physicalAddress, addressBits - 1) + " <-- " + value);
    }

    public Byte readByte(Integer fourByteBinaryString) throws MemoryException {
        int physicalAddress = BitwiseToolbox.extractBits(fourByteBinaryString, 0, addressBits - 1);
        byte value = memory.readByte(physicalAddress);
        System.out.println("RAM read: @" + BitwiseToolbox.getBitString(physicalAddress, addressBits - 1) + " --> " + value);
        return value;
    }

    public void printMemoryContent() throws MemoryException {
        for (int address = 0; address < memory.size(); address++) {
            System.out.println(BitwiseToolbox.getBitString(address, addressBits - 1) + ": " + memory.readByte(address));
        }
    }
}
