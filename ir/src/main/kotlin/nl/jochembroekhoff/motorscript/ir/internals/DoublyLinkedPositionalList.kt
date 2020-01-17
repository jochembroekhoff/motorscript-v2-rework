package nl.jochembroekhoff.motorscript.ir.internals

class DoublyLinkedPositionalList<T> {

    private class PositionImpl<T>(val listRef: DoublyLinkedPositionalList<*>, val node: Node<T>) : Position<T> {
        override val value
            get() = node.value
    }

    private class Node<T>(val value: T, var prev: Node<T>? = null, var next: Node<T>? = null)

    private val header = Node<T>(null!!)
    private val trailer = Node<T>(null!!)

    init {
        header.next = trailer
        trailer.prev = header
    }

    private var head = header
    private var tail = trailer

    var size = 0
        private set

    // empty <-> header == head <-> tail == trailer
    val empty
        get() = size == 0

    /* Addition */

    fun addFirst(element: T) {
        val newNode = Node(element, header, head)
        insertBetween(newNode, head, head.next)
        head = newNode
        size++
    }

    fun addLast(element: T) {
        val newNode = Node(element)
        insertBetween(newNode, tail.prev, tail)
        tail = newNode
        size++
    }

    private fun insertBetween(node: Node<T>, prev: Node<T>?, next: Node<T>?) {
        // Should not be happening, because of the header and trailer
        if (prev == null || next == null) return

        node.prev = prev
        node.next = next

        prev.next = node
        next.prev = node
    }

    /* Removal */

    fun removeFirst(): T? {
        if (empty) return null
        val removed = remove(head)
        size--
        return removed
    }

    fun removeLast(): T? {
        if (empty) return null
        val removed = remove(tail)
        size--
        return removed
    }

    fun remove(pos: Position<T>): T? {
        val node = validate(pos) ?: return null
        val removed = remove(node)
        size--
        return removed
    }

    private fun remove(node: Node<T>): T {
        val prev = node.prev
        val next = node.next
        // Update prev/next
        if (prev != null) {
            prev.next = next
        }
        if (next != null) {
            next.prev = prev
        }
        // Help GC
        node.prev = null
        node.next = null
        return node.value
    }

    /* Retrieval */

    fun get(element: T): Position<T>? {
        if (empty) return null
        var iter: Node<T>? = head
        while (iter != null && iter != trailer) {
            if (iter.value == element) {
                return PositionImpl(this, iter)
            }
            iter = iter.next
        }
        return null
    }

    /* Utils */

    private fun validate(pos: Position<T>): Node<T>? {
        if (pos !is PositionImpl) return null
        if (pos.listRef != this) return null
        return pos.node
    }

}
