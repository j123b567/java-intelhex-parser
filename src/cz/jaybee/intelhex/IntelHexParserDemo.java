/**
 * @license
 * Copyright (c) 2012, Jan Breuer
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this 
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation 
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE 
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 */
package cz.jaybee.intelhex;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Jan Breuer
 * @license BSD 2-Clause 
 */
public class IntelHexParserDemo implements IntelHexDataListener {

    private long addressStart;
    private long addressStop;
    private byte[] buffer;
    private OutputStream destination;
    private MemoryRegions regions;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
        String fileIn = "Application.hex";
        String fileOut = "Application.bin";
        String dataFrom = "0x1D000000";
        String dataTo = "0x1D07FFEF";
                
        if (args.length >= 1) {
            fileIn = args[0];
        } 
        
        if (args.length >= 2) {
            fileOut = args[1];
        }
        
        if (args.length >= 3) {
            dataFrom = args[2];
        }
        
        if (args.length >= 4) {
            dataTo = args[3];
        }
        
        Long dataFromInt = Long.parseLong(dataFrom.substring(2), 16);
        Long dataToInt = Long.parseLong(dataTo.substring(2), 16);
        
        InputStream is = new FileInputStream(fileIn);
        OutputStream os = new FileOutputStream(fileOut);
        IntelHexParser ihp = new IntelHexParser(is);
        IntelHexParserDemo ihpd = new IntelHexParserDemo(dataFromInt, dataToInt, os);
        ihp.setDataListener(ihpd);
        ihp.parse();
        
        is.close();
        
        System.out.printf("Program start address 0x%08X\r\n", ihp.getStartAddress());
        
        System.out.println("Memory regions: ");
        System.out.println(ihpd.regions);
    }

    public IntelHexParserDemo(long addressStart, long addressStop, OutputStream destination) {
        this.addressStart = addressStart;
        this.addressStop = addressStop;
        this.destination = destination;
        this.buffer = new byte[(int) (addressStop - addressStart + 1)];
        Arrays.fill(buffer, (byte) 0xFF);
        regions = new MemoryRegions();
    }

    @Override
    public void data(long address, byte[] data) {
        regions.add(address, data.length);
        
        if ((address >= addressStart) && (address <= addressStop)) {
            int length = data.length;
            if ((address + length) > addressStop) {
                length = (int) (addressStop - address + 1);
            }
            System.arraycopy(data, 0, buffer, (int) (address - addressStart), length);
        }
    }

    @Override
    public void eof() {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(destination);
            dos.write(buffer);
        } catch (Exception ex) {
            Logger.getLogger(IntelHexParserDemo.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                dos.close();
            } catch (IOException ex) {
                Logger.getLogger(IntelHexParserDemo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
