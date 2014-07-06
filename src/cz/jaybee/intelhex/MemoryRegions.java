/**
 * @license Copyright (c) 2014, Jan Breuer All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Jan Breuer
 * @license BSD 2-Clause
 */
public class MemoryRegions {

    List<Region> regions;

    public MemoryRegions() {
        regions = new ArrayList<Region>();
    }

    private class Region implements Comparable<Region>{

        private long start;
        private long length;

        public Region(long start, long length) {
            this.start = start;
            this.length = length;
        }

        public long getStart() {
            return start;
        }

        public long getLength() {
            return length;
        }

        @Override
        public String toString() {
            return String.format("0x%08x:0x%08x (%dB 0x%08X)", start, start + length - 1, length, length);
        }

        @Override
        public int compareTo(Region o) {
            if(this.start == o.start) {
                return Long.compare(this.length, o.length);
            } else {
                return Long.compare(this.start, o.start);
            }
        }
    }

    public void add(long start, long length) {
        Region prevRegion;
        if (regions.size() > 0) {
            prevRegion = regions.get(regions.size() - 1);
            long nextAddress = prevRegion.start + prevRegion.length;
            if (nextAddress == start) {
                prevRegion.length += length;
                return;
            }
        }        
        regions.add(new Region(start, length));
    }

    public void compact() {
        Collections.sort(regions);
        
        Iterator<Region> iter = regions.iterator();
        while(iter.hasNext()) {
            Region r = iter.next();
            
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Region r : regions) {
            sb.append(r).append("\r\n");
        }

        return sb.toString();
    }
}
