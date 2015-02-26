import java.io.{PrintWriter, StringWriter}

package object common {
  def using[T <: { def close() }] (resource: T) (func: T => Unit) =
    try { func(resource) } finally { if (resource != null) resource.close() }

  def usage[A, B <: {def close(): Unit}] (closeable: B) (f: B => A): A =
    try { f(closeable) } finally { closeable.close() }

  def loadStringFromFile(path: String): String = {
    common.usage(scala.io.Source.fromFile(path, "UTF-8")) {
      file => file.mkString
    }
  }

  def formatException(exception: scala.Throwable) = {
    usage(new StringWriter()){
      sw =>
        usage(new PrintWriter(sw)){
          pw =>
            exception.printStackTrace(pw)
            exception.getMessage + "\n" + sw.toString
        }
    }
  }

  def copyArray[A](array: Array[A], start: Int, end: Int)(implicit manifest: Manifest[A]): Array[A] = {
    val subArray = new Array(end - start)
    Array.copy(array, start, subArray, 0, end - start)
    subArray
  }
}
