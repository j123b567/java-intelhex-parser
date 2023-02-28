/**
 * Copyright (c) 2015, Jan Breuer All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p>
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
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
package cz.jaybee.intelhex.listeners;

import cz.jaybee.intelhex.DataListener;
import cz.jaybee.intelhex.MemoryRegions;
import cz.jaybee.intelhex.Region;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Binary file writer
 *
 * @author Jan Breuer
 */
public class BinWriter extends Writer {

    final boolean minimize;

    public BinWriter(Region outputRegion, OutputStream destination, boolean minimize) {
        super(outputRegion, destination);
        this.minimize = minimize;
    }

    @Override
    public void write() throws IOException {
        if (!minimize) {
            maxAddress = outputRegion.getAddressEnd();
        }
        destination.write(buffer, 0, (int) (maxAddress - outputRegion.getAddressStart() + 1));
    }

    @Override
    public void eof() {
        try {
            write();
        } catch (IOException ex) {
            Logger.getLogger(BinWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
