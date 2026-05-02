package vmmanager;

import vmsimulation.BitwiseToolbox;
import vmsimulation.MainMemory;
import vmsimulation.MemoryException;

public class VirtualMemoryManagerV0 {

	MainMemory memory;

  	// log2(): Convenient function to compute the log2 of an integer;
	private int log2(int x) {
		return (int)(Math.log(x)/Math.log(2));
	}

	// Constructor
	public VirtualMemoryManagerV0(MainMemory memory) throws MemoryException {
		this.memory = memory;
	}

  	// Method to write a byte to memory given a physical address
  	public void writeByte(Integer fourByteBinaryString, Byte value) throws MemoryException {
  		// TO IMPLEMENT
    }

  	// Method to write a byte to memory given a physical address
  	public Byte readByte(Integer fourByteBinaryString) throws MemoryException {
  		// TO IMPLEMENT
        return 0; // MUST RETURN THE VALUE THAT WAS READ INSTEAD OF JUST ZERO
  	}

  	// Method to print all memory content
  	public void printMemoryContent() throws MemoryException {
  		// TO IMPLEMENT
  	}

}
