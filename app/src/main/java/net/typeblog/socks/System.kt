package net.typeblog.socks

object System {
    val abi: String
        external get

    init {
        java.lang.System.loadLibrary("system")
    }

    external fun exec(cmd: String)
    external fun sendfd(fd: Int): Int
    external fun jniclose(fd: Int)
}
