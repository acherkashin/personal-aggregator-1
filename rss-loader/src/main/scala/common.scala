
package object common {
  def using[T <: { def close() }] (resource: T) (func: T => Unit) =
    try { func(resource) } finally { if (resource != null) resource.close() }

  def usage[A, B <: {def close(): Unit}] (closeable: B) (f: B => A): A =
    try { f(closeable) } finally { closeable.close() }

  def copyArray[A](array: Array[A], start: Int, end: Int)(implicit manifest: Manifest[A]): Array[A] = {
    val subArray = new Array(end - start)
    Array.copy(array, start, subArray, 0, end - start)
    subArray
  }
}
