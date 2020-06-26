package nl.jochembroekhoff.motorscript.check

import nl.jochembroekhoff.motorscript.common.execution.internalAssert
import nl.jochembroekhoff.motorscript.ir.graph.IRExpressionVertex
import nl.jochembroekhoff.motorscript.ir.graph.edge.IRDependencyEdge
import nl.jochembroekhoff.motorscript.ir.graph.edgemeta.Slot
import java.util.*
import kotlin.collections.HashSet

class SlotMapping {
    class SlotCollectionHolder {
        val indexMapping: SortedMap<Int, Pair<IRDependencyEdge, IRExpressionVertex>> = TreeMap()
        val nameMapping: SortedMap<String, Pair<IRDependencyEdge, IRExpressionVertex>> = TreeMap()
        val allSet: MutableSet<Pair<IRDependencyEdge, IRExpressionVertex>> = HashSet()

        fun byIndex(): Sequence<Pair<IRDependencyEdge, IRExpressionVertex>> {
            return indexMapping.values.asSequence()
        }

        fun byName(): Sequence<Pair<IRDependencyEdge, IRExpressionVertex>> {
            return nameMapping.values.asSequence()
        }

        fun all(): Sequence<Pair<IRDependencyEdge, IRExpressionVertex>> {
            return allSet.asSequence()
        }
    }

    private val typeToHolder: MutableMap<Slot.Category, SlotCollectionHolder> = EnumMap(Slot.Category::class.java)

    fun add(e: IRDependencyEdge, v: IRExpressionVertex) {
        val p = e to v
        val slot = e.meta.slot ?: return
        val collectionHolder = typeToHolder.computeIfAbsent(slot.category) { SlotCollectionHolder() }

        if (slot.name.isNotBlank()) {
            val old = collectionHolder.nameMapping.put(slot.name, p)
            internalAssert(old == null) { "Slot with name ${slot.name} already seen before" }
        } else {
            val old = collectionHolder.indexMapping.put(slot.index, p)
            internalAssert(old == null) { "Slot with index ${slot.index} already seen before" }
        }

        collectionHolder.allSet.add(p).also { internalAssert(it) { "Pair already present" } }
    }

    fun byCategory(category: Slot.Category): SlotCollectionHolder {
        return typeToHolder.computeIfAbsent(category) { SlotCollectionHolder() }
    }
}
