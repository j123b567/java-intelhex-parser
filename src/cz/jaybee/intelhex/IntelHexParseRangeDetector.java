package cz.jaybee.intelhex;

public class IntelHexParseRangeDetector implements IntelHexDataListener {
	private final MemoryRegions regions = new MemoryRegions();

	@Override
	public void data(long address, byte[] data) {
		regions.add(address, data.length);
	}

	@Override
	public void eof() {
		regions.compact();
	}

	public void reset() {
		regions.regions.clear();
	}

	public long getStart() {
		if (regions.regions.size() == 0) return 0;
		MemoryRegions.Region first = regions.regions.get(0);
		return first.getStart();
	}

	public long getLength() {
		if (regions.regions.size() == 0) return 0;
		MemoryRegions.Region last = regions.regions.get(regions.regions.size() - 1);
		return last.getStart() + last.getLength() - getStart();
	}
}
