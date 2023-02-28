package cz.jaybee.intelhex.listeners;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.Region;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Writer implements DataListener {

    protected final Region outputRegion;
    protected final OutputStream destination;
    protected byte[] buffer;
    //private final MemoryRegions regions;
    protected long maxAddress;

    public Writer(Region outputRegion) {
        this(outputRegion, null);
    }
    public Writer(Region outputRegion, OutputStream destination) {
        this.outputRegion = outputRegion;
        this.destination = destination;
        this.buffer = new byte[(int) (outputRegion.getLength())];
        Arrays.fill(buffer, (byte) 0xFF);
        //regions = new MemoryRegions();
        maxAddress = outputRegion.getAddressStart();
    }

    @Override
    public void data(long address, byte[] data) {
        //regions.add(address, data.length);

        if ((address >= outputRegion.getAddressStart()) && (address <= outputRegion.getAddressEnd())) {
            int length = data.length;

            if ((address + length) > outputRegion.getAddressEnd()) {
                length = (int) (outputRegion.getAddressEnd() - address + 1);
            }
            System.arraycopy(data, 0, buffer, (int) (address - outputRegion.getAddressStart()), length);

            if (maxAddress < (address + data.length -1)) {
                maxAddress = address + data.length - 1;
            }
        }
    }

    public abstract void write() throws IOException;

}
