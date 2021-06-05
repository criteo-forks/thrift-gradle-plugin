class ThriftBinaryLocator {
   static def getRelativePath() {
        def os = System.getProperty("os.name").toLowerCase()
        def platform = "linux"
        def binaryName = "thrift"
        if (os.indexOf("win") >= 0) {
            platform = "windows"
            binaryName = "thrift.exe"
        }
        if (os.indexOf("nix") >= 0) platform = "linux"
        if (os.indexOf("mac") >= 0) platform = "macosx"

		return "${platform}/${binaryName}"
   }
}