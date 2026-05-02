package vmmanager;

import vmsimulation.BitwiseToolbox;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

public class VirtualMemoryManagerV0 {

	MainMemory memory;
    int numBitsToAddress;

  	// log2(): Convenient function to compute the log2 of an integer;
	private int log2(int x) {
		return (int)(Math.log(x)/Math.log(2));
	}

	// Constructor
	public VirtualMemoryManagerV0(MainMemory memory) throws MemoryException {
		this.memory = memory;
        this.numBitsToAddress = log2(memory.size());
	}

  	// Method to write a byte to memory given a physical address
  	public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {
        int address = BitwiseToolbox.extractBits(fourByteBinaryString, 0, numBitsToAddress - 1);
        memory.writeByte(address, value);
        System.out.println("RAM write: @" + BitwiseToolbox.getBitString(address, numBitsToAddress - 1) + " <-- " + value);
    }

  	// Method to write a byte to memory given a physical address
  	public Byte readByte(Integer fourByteBinaryString) throws MemoryException {
        int address = BitwiseToolbox.extractBits(fourByteBinaryString, 0, numBitsToAddress - 1);
        byte value = memory.readByte(address);
        System.out.println("RAM read: @" + BitwiseToolbox.getBitString(address, numBitsToAddress - 1) + " --> " + value);
        return value;
  	}

  	// Method to print all memory content
  	public void printMemoryContent() throws MemoryException {
        for (int address = 0; address < memory.size(); address++) {
            System.out.println(BitwiseToolbox.getBitString(address, numBitsToAddress - 1) + ": " + memory.readByte(address));
        }
  	}

}
