package cn.tenflake.vblock.command

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.wrappers.BlockPosition
import com.comphenix.protocol.wrappers.WrappedBlockData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.toString

data class WorldBlockPosition(
    val world: World,
    val position: BlockPosition
)
class Visible: CommandExecutor {
    override fun onCommand(sender: CommandSender?, command: Command?, p2: String?, args: Array<out String?>?): Boolean {
        if (args == null || sender == null) return false
        when (command?.name?.lowercase()) {
            "visibleblock" -> handleblock(sender, args)
            "visibleblocks" -> handleblocks(sender, args)
            else -> sender.sendMessage("error")
        }
        return true
    }

    private fun handleblock(sender: CommandSender?, args: Array<out String?>) {
        if (args.size < 6) {
            sender?.sendMessage("/visible x y z Block player true/false")
            return
        }
        try {
            val x: Int = args[0]?.toInt()!!
            val y: Int = args[1]?.toInt()!!
            val z: Int = args[2]?.toInt()!!
            val blockType = Material.valueOf(args[3]?.uppercase().toString())
            val playerName = args[4]
            val notTemp = args[5]?.toBooleanStrict()!!

            val targetPlayer = Bukkit.getPlayer(playerName) ?: run {
                sender?.sendMessage("'$playerName' is offonline")
                return
            }
            updateBlocktoPlayer(targetPlayer, WorldBlockPosition(targetPlayer.world,BlockPosition(x, y, z)), blockType, notTemp)
        } catch (e: Exception) {
            sender?.sendMessage("§c请检查语法: ${e.message}")
        }
    }

    private fun handleblocks(sender: CommandSender?, args: Array<out String?>) {
//        sender?.sendMessage("Received ${args.size} arguments: ${args.joinToString(", ")}")
        if (args.size < 9) {
            sender?.sendMessage("/visibleblocks x1 y1 z1 x2 y2 z2 Block player true/false")
            return
        }

        try {
            val x1 = args[0]?.toInt()!!
            val y1 = args[1]?.toInt()!!
            val z1 = args[2]?.toInt()!!
            val x2 = args[3]?.toInt()!!
            val y2 = args[4]?.toInt()!!
            val z2 = args[5]?.toInt()!!

            val blockType = Material.valueOf(args[6]?.uppercase().toString())
            val playerName = args[7]
            val vi = args[8]?.toBooleanStrict()!!

            val targetPlayer = Bukkit.getPlayer(playerName) ?: run {
                sender?.sendMessage("'$playerName' offonline")
                return
            }

            val change = updateBlockstoPlayer(targetPlayer, x1, y1, z1, x2, y2, z2, blockType, vi)
            println("发送了 ${change} 个方块")
        } catch (e: Exception) {
            sender?.sendMessage("§c请检查语法: ${e.message}")
        }
    }

    private fun updateBlockstoPlayer(
        targetPlayer: Player,
        x1: Int,
        y1: Int,
        z1: Int,
        x2: Int,
        y2: Int,
        z2: Int,
        blockType: Material,
        vi: Boolean
    ): Int {
        var updatedBlocks = 0

        val minX = minOf(x1, x2)
        val maxX = maxOf(x1, x2)
        val minY = minOf(y1, y2).coerceAtLeast(0)
        val maxY = maxOf(y1, y2).coerceAtMost(255)
        val minZ = minOf(z1, z2)
        val maxZ = maxOf(z1, z2)

        // 遍历
        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    updateBlocktoPlayer(targetPlayer, WorldBlockPosition(targetPlayer.world,BlockPosition(x, y, z)), blockType, vi)
                    updatedBlocks++
                }
            }
        }

        return updatedBlocks
    }
    //    private fun updateBlocktoPlayer(player: Player,pos: BlockPosition,type: Material,visible: Boolean) {
//        val playerWorld = player.world
//
//        val actualBlockType = if (visible) type else Material.AIR
//        val packet = ProtocolLibrary.getProtocolManager()
//            .createPacket(PacketType.Play.Server.BLOCK_CHANGE)
//            .apply {
//                blockPositionModifier.write(0, pos)
//                blockData.write(0, WrappedBlockData.createData(type))
//            }
//        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
//    }
    private fun updateBlocktoPlayer(
        player: Player,
        wPos: WorldBlockPosition,
        type: Material,
        visible: Boolean
    ) {
        if (player.world != wPos.world) return player.sendMessage("222")

        val actualType = if (visible) type else Material.AIR
        val packet = ProtocolLibrary.getProtocolManager()
            .createPacket(PacketType.Play.Server.BLOCK_CHANGE)
            .apply {
                blockPositionModifier.write(0, wPos.position)
                blockData.write(0, WrappedBlockData.createData(actualType))
            }

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet)
    }
}