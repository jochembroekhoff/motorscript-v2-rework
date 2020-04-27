package nl.jochembroekhoff.motorscript.gen

import nl.jochembroekhoff.motorscript.common.execution.ExecutionException
import nl.jochembroekhoff.motorscript.common.execution.ExecutionUnit
import nl.jochembroekhoff.motorscript.common.extensions.executorservice.supply
import nl.jochembroekhoff.motorscript.common.extensions.path.div
import nl.jochembroekhoff.motorscript.common.pack.PackEntry
import nl.jochembroekhoff.motorscript.common.ref.NSID
import nl.jochembroekhoff.motorscript.common.result.Result
import nl.jochembroekhoff.motorscript.def.DefContainer
import nl.jochembroekhoff.motorscript.ir.container.IRDefEntryMeta
import java.nio.file.Files
import java.nio.file.Path

class GenExecutionUnit(private val outDir: Path, private val input: List<DefContainer<PackEntry, IRDefEntryMeta>>) :
    ExecutionUnit<Unit>() {

    private val dataDir = outDir / "data"

    override fun execute(): Result<Unit, List<ExecutionException>> {
        logger.debug { "Executing :gen" }

        logger.debug { "Creating pack.mcmeta" }
        Files.writeString(
            outDir / "pack.mcmta",
            "{\n" +
                "  \"pack\": {\n" +
                "    \"pack_format\": 5,\n" +
                // TODO: Escape this & use Kotlinx Serialization
                "    \"description\": \"${ectx.execution.buildSpec.description}\"\n" +
                "  }\n" +
                "}\n"
        )

        // TODO: Also need edef containers (probz passed from :check), in order to construct generators for items provided by edefs, need signatures

        val futs = input.map { defContainer ->
            ectx.executor.supply {
                val publisher =
                    Publisher() // Shared between container items to hopefully also cache some things
                defContainer.functions.forEach { (id, meta) ->
                    logger.trace { "Generating function $id" }
                    val dispatcher =
                        Dispatcher(id, meta.g, publisher)
                    val gctx = GenContext(
                        dispatcher,
                        GenOutput.createRoot()
                    )
                    dispatcher.generateStatementsStartingFrom(gctx, meta.entry)
                    dispatcher.collectOutput().forEach { (nsid, content) ->
                        val file = mcfunctionPathFor(nsid)
                        logger.trace { "Outputting commands to $file" }
                        Files.createDirectories(file.parent)
                        Files.write(file, content)
                    }
                    // TODO: Write special handlers if this function has the "user" modifier
                }
            }
        }.toTypedArray()

        // TODO: Write tags
        // TODO: Write init function that ensures that e.g. a scoreboard objective called "MOS" is present

        return gatherSafe(*futs).mapOk { Unit }
    }

    private fun mcfunctionPathFor(nsid: NSID): Path {
        require(nsid.name.isNotEmpty()) { "Given NSID has empty name" }
        return dataDir / nsid.namespace / "functions" / nsid.name.dropLast(1) / (nsid.name.last() + ".mcfunction")
    }
}
