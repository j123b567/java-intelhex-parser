Java IntelHex Parser Library
====================

* IntelHex file format parsing library written in Java.
* Licensed under Simplified BSD license
* Including demo code: intelhex to binary converter

Simples usage:
~~~~~
	// create input stream of some IntelHex data
	InputStream is = new FileInputStream("Application.hex");
	
	// create IntelHexParserObject
	Parser ihp = new Parser(is);
	
	// register parser listener
	ihp.setDataListener(new DataListener() {
	
		@Override
		public void data(long address, byte[] data) {
			// process data
		}
		
		@Override
		public void eof() {
			// do some action
		}
	});
	ihp.parse();
	
~~~~~

### Build:

`mvn clean install`

### Run demo:
after build go to the target directory and run:

`java -jar intelhex-1.0.jar`